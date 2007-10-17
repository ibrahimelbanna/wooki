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
package org.tencompetence.widgetservice.util.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.tencompetence.widgetservice.beans.AbstractKeyBean;

/**
 * @author sheyenrath
 *
 * @version $Id: DBManagerInterface.java,v 1.2 2007-10-17 23:11:12 ps3com Exp $
 */
public interface DBManagerInterface {

	/**
	 * Create start a transaction.
	 */
	public void beginTransaction();

	/**
	 * Commit the transaction.
	 */
	public void commitTransaction();
	
	/**
	 * Rollback the transaction.
	 */
	public void rollbackTransaction();
	
	/**
	 * Close the session.
	 */
	public void closeSession();
	
	/**
	 * @param baseClass
	 * @return Object
	 */
	public <T extends AbstractKeyBean> Criteria createCriteria(final Class<? extends AbstractKeyBean> baseClass);
	
	/**
	 * @param baseClass
	 * @return Object
	 */
	public Query createQuery(final String query);

	public SQLQuery createSQLQuery(final String query);
	/**
	 * Get an object from the database based on key ID;
	 * 
	 * @param <T>
	 * @param baseClass
	 * @param id Identifier (key) from the obejct to search for.
	 * @return object (or null when not found)
	 */
	public <T extends AbstractKeyBean> T getObject(final Class<T> baseClass, final Integer id)
		throws Exception;

	/**
	 * Get an object from the database based on search criterion.
	 * 
	 * @param <T>
	 * @param baseClass
	 * @param criteria 
	 * @return object (or null when not found)
	 * @throws TENCDatabaseException
	 */
	public <T extends AbstractKeyBean> T getObject(final Class<T> baseClass,
			final Criteria criteria) throws Exception;
	
	/**
	 * Get a list from objects from the database based on search criterion.
	 * 
	 * @param <T>
	 * @param baseClass
	 * @param criteria
	 * @return A list from objects or an empty list when no objects are found.
	 * @throws TENCDatabaseException
	 */
	public <T extends AbstractKeyBean> List<T> getObjects(final Class<T> baseClass,
			final Criteria criteria) throws Exception;
	
	/**
	 * Update an object in the database.
	 * @param <T>
	 * @param baseClass
	 * @param object
	 * @throws TENCDatabaseException
	 * @throws TENCObjectNotFoundException
	 */
	public <T extends AbstractKeyBean> void updateObject(final Class<T> baseClass,
			final AbstractKeyBean object) throws Exception;
	
	/**
	 * Insert an object into the database.
	 * 
	 * @param object The object to save.
	 * @return
	 * @throws TENCServerException
	 */
	public Serializable saveObject(final AbstractKeyBean object) throws Exception;
	
	public Serializable saveGenericObject(final Object obj) throws Exception;
	
	/**
	 * Deletes an object from the database.
	 * 
	 * @param object the object to delete.
	 * @throws TENCServerException
	 */
	public void deleteObject(final AbstractKeyBean object) throws Exception;
}
