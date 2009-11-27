/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.wookie.server;


import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import org.apache.wookie.Messages;
import org.apache.wookie.beans.Widget;
import org.apache.wookie.exceptions.BadManifestException;
import org.apache.wookie.exceptions.BadWidgetZipFileException;
import org.apache.wookie.manager.impl.WidgetAdminManager;
import org.apache.wookie.manifestmodel.IManifestModel;
import org.apache.wookie.util.WgtWatcher;
import org.apache.wookie.util.WidgetPackageUtils;
import org.apache.wookie.util.hibernate.DBManagerFactory;
import org.apache.wookie.util.hibernate.HibernateUtil;
import org.apache.wookie.util.hibernate.IDBManager;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * ContextListener - does some init work and makes certain things are available 
 * to resources under this context
 * 
 * @author Paul Sharples
 * @version $Id: ContextListener.java,v 1.2 2009-07-28 16:05:23 scottwilson Exp $ 
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
			PropertiesConfiguration configuration;
			File localPropsFile = new File(System.getProperty("user.dir") + File.separator + "local.widgetserver.properties");
			if (localPropsFile.exists()) {
				configuration = new PropertiesConfiguration(localPropsFile);
				_logger.info("Using local widget server properties file: " + localPropsFile.toString());
			} else {
				configuration = new PropertiesConfiguration("widgetserver.properties");
				configuration.setFile(localPropsFile);
				configuration.save();
				_logger.info("Using default widget server properties, configure your local server using: " + localPropsFile.toString());
			}
		 	context.setAttribute("properties", (Configuration) configuration);
		 	/*
		 	 * Initialise the locale handler
		 	 */
		 	LocaleHandler.getInstance().initialize(configuration);
			final Locale locale = new Locale(configuration.getString("widget.default.locale"));
			final Messages localizedMessages = LocaleHandler.getInstance().getResourceBundle(locale);
	
		 	if (configuration.getBoolean("widget.hot_deploy")) {
		 		startWatcher(context, configuration, localizedMessages);
		 	} else {
		 		_logger.info(localizedMessages.getString("WidgetHotDeploy.0"));
		 	}
		 	
		 	/* 
			 *  load the opensocial.properties file and put it into this context
			 *  as an attribute 'opensocial' available to all resources
			 */
			PropertiesConfiguration opensocialConfiguration;
			File localOpenSocialPropsFile = new File(System.getProperty("user.dir") + File.separator + "local.opensocial.properties");
			if (localOpenSocialPropsFile.exists()) {
				opensocialConfiguration = new PropertiesConfiguration(localOpenSocialPropsFile);
				_logger.info("Using local open social properties file: " + localOpenSocialPropsFile.toString());
			} else {
				opensocialConfiguration = new PropertiesConfiguration("opensocial.properties");
				opensocialConfiguration.setFile(localOpenSocialPropsFile);
				opensocialConfiguration.save();
				_logger.info("Using default open social properties, configure your local server using: " + localOpenSocialPropsFile.toString());
			}
			context.setAttribute("opensocial", (Configuration) opensocialConfiguration);
		} 
		catch (ConfigurationException ex) {
			_logger.error("ConfigurationException thrown: "+ ex.toString());
		}					
	}
	
	/**
	 * Starts a watcher thread for hot-deploy of new widgets dropped into the deploy folder
	 * this is controlled using the <code>widget.hot_deploy=true|false</code> property 
	 * and configured to look in the folder specified by the <code>widget.deployfolder</code> property
	 * @param context the current servlet context
	 * @param configuration the configuration properties
	 */
	private void startWatcher(ServletContext context, Configuration configuration, final Messages localizedMessages){
	 	/*
	 	 * Start watching for widget deployment
	 	 */
	 	final File deploy = new File(WidgetPackageUtils.convertPathToPlatform(context.getRealPath(configuration.getString("widget.deployfolder"))));
		final String UPLOADFOLDER = context.getRealPath(configuration.getString("widget.useruploadfolder"));
		final String WIDGETFOLDER = context.getRealPath(configuration.getString("widget.widgetfolder"));
		final String localWidgetFolderPath = configuration.getString("widget.widgetfolder");
		Thread thr = new Thread(){
	 		public void run() {
	 			/** Get a DBManager for this thread. */
	 			final IDBManager dbManager = DBManagerFactory.getDBManager();
	 			int interval = 5000;
	 			WgtWatcher watcher = new WgtWatcher();
	 			watcher.setWatchDir(deploy);
	 			watcher.setListener(new WgtWatcher.FileChangeListener(){
	 				public void fileModified(File f) {
	 					try{
	 						dbManager.beginTransaction();
	 						File upload = WidgetPackageUtils.dealWithDroppedFile(UPLOADFOLDER, f);
	 						IManifestModel model = WidgetPackageUtils.processWidgetPackage(upload, localWidgetFolderPath, WIDGETFOLDER, UPLOADFOLDER);
	 						WidgetAdminManager manager = new WidgetAdminManager(null);
	 						if(!Widget.exists(model.getIdentifier())) {
	 							manager.addNewWidget(model, null);	
	 							_logger.info(model.getLocalName("en") +"' - " + localizedMessages.getString("WidgetAdminServlet.19"));
	 						} else {
	 							_logger.info(model.getLocalName("en") +"' - " + localizedMessages.getString("WidgetAdminServlet.20"));
	 						}
	 						dbManager.commitTransaction();
	 					} catch (IOException e) {
	 						_logger.error(f.getName()+":"+localizedMessages.getString("WidgetHotDeploy.1"));
	 					} catch (BadWidgetZipFileException e) {
	 						_logger.error(f.getName()+":"+localizedMessages.getString("WidgetHotDeploy.2"));
	 					} catch (BadManifestException e) {
	 						_logger.error(f.getName()+":"+localizedMessages.getString("WidgetHotDeploy.3"));
	 					}
	 				}
	 				public void fileRemoved(File f) {
	 					// Not implemented - the .wgt files are removed as part of the deployment process
	 				}
	 			});
	 		    try {
	 		       while (true) {
	 		         watcher.check();
	 		         Thread.sleep(interval);
	 		       }
	 		     } catch (InterruptedException iex) {
	 		    	 dbManager.closeSession();
	 		     }
	 		}	
	 	};
	 	
	 	thr.start();
		
	}

	public void contextDestroyed(ServletContextEvent event){
		HibernateUtil.getSessionFactory().close(); // Free all resources
	}
}