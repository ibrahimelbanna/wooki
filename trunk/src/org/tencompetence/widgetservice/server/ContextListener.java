/*
 * Copyright (c) 2007, Consortium Board TENCompetence
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the TENCompetence nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY CONSORTIUM BOARD TENCOMPETENCE ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONSORTIUM BOARD TENCOMPETENCE BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.tencompetence.widgetservice.server;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.tencompetence.widgetservice.util.hibernate.HibernateUtil;
/**
 * ContextListener - does some init work and makes certain things are available 
 * to resources under this context
 * 
 * @author Paul Sharples
 * @version $Id: ContextListener.java,v 1.4 2009-02-24 09:56:22 scottwilson Exp $ 
 *
 */
public class ContextListener implements ServletContextListener {
	/*
	 * In the case of the 'log4j.properties' file used within a server environment
	 * there is no need to explicitly load the file.  It will be automatically loaded as
	 * long as it is placed at the root of the source code. This way it eventually is found under...
	 * 
	 * 			'/webappname/WEB-INF/classes'  ...at runtime.
	 */
	static Logger _logger = Logger.getLogger(ContextListener.class.getName());
	
	public void contextInitialized(ServletContextEvent event) {
		// start hibernate now, not on first request
		HibernateUtil.getSessionFactory();
		try {
			ServletContext context = event.getServletContext();
			/* 
			 *  load the widgetserver.properties file and put it into this context
			 *  as an attribute 'properties' available to all resources
			 */
			Configuration configuration = new PropertiesConfiguration("widgetserver.properties");
		 	context.setAttribute("properties", (Configuration) configuration);
			/* 
			 *  load the opensocial.properties file and put it into this context
			 *  as an attribute 'opensocial' available to all resources
			 */
			Configuration opensocialConfiguration = new PropertiesConfiguration("opensocial.properties");
		 	context.setAttribute("opensocial", (Configuration) opensocialConfiguration);	
		} 
		catch (ConfigurationException ex) {
			_logger.error("ConfigurationException thrown: "+ ex.toString());
		}					
	}

	public void contextDestroyed(ServletContextEvent event){
		HibernateUtil.getSessionFactory().close(); // Free all resources
	}
}
