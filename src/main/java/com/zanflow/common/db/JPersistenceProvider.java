package com.zanflow.common.db;


import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.persistence.TransactionRequiredException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;
import org.jasypt.intf.service.JasyptStatelessService;

import com.zanflow.bpmn.exception.JPAIllegalStateException;
import com.zanflow.bpmn.exception.JPAPersistenceException;



public  class JPersistenceProvider {

 private EntityTransaction jPersistenceProvider = null;
 private EntityManager entityManager = null; 
 private static String decryptStr=null;
 private String pUnitName=null;	

	/*
	 * Get an EntityManager If already Exist else Create an EntityManager
	 * @param unitName
	 * 
	 */

	    JPAUtil jpa = new JPAUtil ();
	
	// get the Persistent Entity Manager for unit provided
	public JPersistenceProvider(String pUnit) throws EntityNotFoundException,EntityExistsException,JPAPersistenceException {
		try
		{
	        final String cryptorKey=decryptString("xHgO5bDCd/mgGPFMHHu9u7mTVmMaw9il");
			StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
			strongEncryptor.setPassword(cryptorKey);
			HibernatePBEEncryptorRegistry registry =HibernatePBEEncryptorRegistry.getInstance();			      
			registry.registerPBEStringEncryptor("strongHibernateStringEncryptor", strongEncryptor);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		try
		{				
		entityManager = jpa.getEntityManager(pUnit); 
		if(entityManager == null){			
		 entityManager = jpa.createEntityManager(pUnit);
		}
		jPersistenceProvider = entityManager.getTransaction();
		pUnitName = pUnit;
		}
		catch(EntityNotFoundException e)
		{
			e.printStackTrace();
			throw new EntityNotFoundException("Error in creation of Entity Manager");
		}
		catch(EntityExistsException e)
		{
			e.printStackTrace();
			throw new EntityExistsException("Error in creation of Entity Manager");
		}		
	}
	
	/**
	 * Cleans up and closes the ThreadLocal EntityManager
	 * This should be done for each "session" and not each operation.
	 * A "session" has a similar life span as a http request.
	 * - Rollback any active transactions
	 * - Closes EntityManager in session
	 */

	public void close(String UnitName)  throws JPAIllegalStateException {
	    	//CONN_LEAK_FIX : Start
		try	{		
				jpa.closeEntityManager(pUnitName);
		     }
		catch(Exception e) 
		{
			e.printStackTrace();
			//throw new JPAIllegalStateException();
		}
		//CONN_LEAK_FIX : End
	}

	/*
	 * The Transaction begins for each Session
	 */

	public void begin() throws TransactionRequiredException {
		try{
			jPersistenceProvider.begin();	
		   }
		catch(TransactionRequiredException e ){
			e.printStackTrace();
		   throw new TransactionRequiredException("Error in Transaction Begin");
		}
	}

	/*
	 * save the Entity object of the current Transaction
	 */
	public void commit() throws JPAPersistenceException, JPAIllegalStateException{
		try{
			
			jPersistenceProvider.commit();
			
	      }catch(Exception jpaill){
	    	  jpaill.printStackTrace();
	   	   throw new JPAIllegalStateException("Error in commit ");
			
	      }
		
		}
	/*
	 * Discards the changes of Current Transaction
	 */
	public void rollback() throws RollbackException {
		try{
			jPersistenceProvider.rollback();
		   }
		catch(RollbackException e )
		{
			e.printStackTrace();
			throw new RollbackException("Error in Rollback");
		}

	}
	
	/*
	 * Added By Jai for Eopshub
	 */
	/**
	 * Merge the state of the given entity into the
	 * current persistence context.
	 * @param entity
	 * @return the instance that the state was merged
	 */
	
	public Object merge(Object entity) throws  IllegalArgumentException,TransactionRequiredException
	{
		try
		{
			
			return entityManager.merge(entity);
			
		}

		catch(TransactionRequiredException e)
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in Transaction when merging Entity");
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new IllegalStateException("Error when merging Entity");
		}

	}

	
	/*
	 * End of Code added by Jai for Eopshub
	 */
	
	/**
	 * Merge the state of the given entity into the
	 * current persistence context.
	 * @param entity
	 * @return the instance that the state was merged
	 */
	
	public void save(Object entity) throws  IllegalArgumentException,TransactionRequiredException
	{
		try
		{
			entityManager.persist(entity);	
		}

		catch(TransactionRequiredException e)
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in Transaction when Inserting Data");
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new IllegalStateException("Error when Inserting Data");
		}

	}

	/**
	 * Remove the entity instance.
	 * @param entity
	 **/
	public void delete(Object entity) throws  IllegalArgumentException,TransactionRequiredException
	{	 
		try
		{
			entityManager.remove(entity);	
		}
		catch(TransactionRequiredException e)
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in Transaction when Deleting Data");
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new IllegalStateException("Error when Deleting Data");
		}
	}
	/**
	 * Find by primary key.
	 * @param entityClass
	 * @param primaryKey
	 * @return the found entity instance or null
	 * if the entity does not exist
	 **/

	public Object find(Class clazz, Object primaryKey) throws IllegalArgumentException
	{
		try
		{
			return	entityManager.find(clazz, primaryKey);
			//return objects;
		}
		catch(Exception e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("IllegalArgument in Selecting the Data");
		}
	}

	/**
	 * Synchronize the persistence context to the
	 * underlying database.*/
	public void flush() throws TransactionRequiredException,PersistenceException
	{
		try
		{
			entityManager.flush();
		}
		catch(TransactionRequiredException e )
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in Transactionflush");
		}
		catch(Exception e) 
		{
			e.printStackTrace();
			throw new PersistenceException("Error in Flush");
		}
	}
	/**
	 * Set the lock mode for an entity object contained
	 * in the persistence context.
	 * @param entity
	 * @param lockMode
	 */
	public void lock(Object entity, LockModeType lockMode) throws TransactionRequiredException,PersistenceException,IllegalArgumentException
	{
		try
		{
			entityManager.lock(entity, lockMode);
		}
		catch(TransactionRequiredException e )
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in TransactionLock");
		}
		catch(PersistenceException e )
		{
			e.printStackTrace();
			throw new PersistenceException("Error in PersistenceLock");
		}
		catch(Exception e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("IllegalArgument in Lock");
		}
	}
	/**
	 * Get an instance, whose state may be lazily fetched.
	 * @param entityClass
	 * @param primaryKey
	 * @return the found entity instance
	 */
	public <T> T getReference(Class <T> entityClass, Object  primaryKey) throws IllegalArgumentException,EntityNotFoundException {
		try
		{
			return entityManager.getReference(entityClass, primaryKey);
		}
		catch(EntityNotFoundException e)
		{
			e.printStackTrace();
			throw new EntityNotFoundException("Entity Not Found");
		}
		catch(Exception  e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException ("Error in getReference Data");
		}
	}

	/**
	 * Set the flush mode that applies to all objects contained
	 * in the persistence context.
	 * @param flushMode
	 */


	public void setFlushMode(FlushModeType flushMode) throws SQLException
	{
		try
		{
			entityManager.setFlushMode(flushMode);
		}
		catch(Exception e )
		{
			e.printStackTrace();
			throw new SQLException("Error in FlushMode");
		}
	}

	/**
	 * Get the flush mode that applies to all objects contained
	 * in the persistence context.
	 * @return flushMode
	 */
	public FlushModeType getFlushMode() throws SQLException
	{
		try
		{
			return	entityManager.getFlushMode();
		}
		catch(Exception e )
		{
			e.printStackTrace();
			throw new SQLException("Error in GetFlushMode");
		}		

	}

	/**
	 * Create an instance of Query for executing
	 * a native SQL statement, e.g., for update or delete.
	 * @param sqlString a native SQL query string
	 * @return the new query instance
	 */

	public Query createNamedQuery(String  name) throws IllegalArgumentException {
		try
		{
			return entityManager.createNamedQuery(name);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("IllegalArgumentException in createNamedQuery");
		}
	}
	
			
	/**
	 * Determine whether the EntityManager is open.
	 * @return true until the EntityManager has been closed.
	 */
	public boolean isOpen() throws EntityExistsException {
		try
		{
			return entityManager.isOpen();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw  new  EntityExistsException("Entity manager is Exist");
		}
	}

	/**
	 * Indicate to the EntityManager that a JTA transaction is
	 * active. This method should be called on a JTA application
	 * managed EntityManager that was created outside the scope
	 * of the active transaction to associate it with the current
	 * JTA transaction.**/
	public void joinTransaction() throws TransactionRequiredException {
		try
		{
			entityManager.joinTransaction();
		}
		catch(TransactionRequiredException e )
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in JoinTransaction");
		}

	}

	/**
	 * Refresh the state of the instance from the database,
	 * overwriting changes made to the entity, if any.
	 * @param entity
	 * @throws IllegalArgumentException**/
	public void refresh(Object  entity) throws IllegalArgumentException,TransactionRequiredException,EntityNotFoundException{
		try
		{
			entityManager.refresh(entity);
		}
		catch(TransactionRequiredException e)
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in TransactionRefresh");
		}
		catch(EntityNotFoundException e)
		{		
			e.printStackTrace();
			throw new EntityNotFoundException("Error in Entity");

		}
		catch(IllegalArgumentException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error in refresh");
		}
	}

	/**
	 * Clear the persistence context, causing all managed
	 * entities to become detached. Changes made to entities that
	 * have not been flushed to the database will not be
	 * persisted.
	 */
	public void clear() throws IllegalArgumentException {
		try
		{
			entityManager.clear();
		}
		catch(IllegalArgumentException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error in clear");
		}
	}
	/**
	 * Check if the instance belongs to the current persistence
	 * context.**/

	public boolean contains(Object  entity) throws IllegalArgumentException {
		try
		{
			return entityManager.contains(entity);
		}
		catch(IllegalArgumentException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error in contains");
		}

	}

	/**
	 * Create an instance of Query for executing
	 * a native SQL statement, e.g., for update or delete.
	 * @param sqlString a native SQL query string
	 * @return the new query instance
	 */

	public Query createNativeQuery(String  sqlString, Class  resultClass)  throws IllegalArgumentException{
		try
		{
			return entityManager.createNativeQuery(sqlString, resultClass);
		}
		catch(IllegalArgumentException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error in createNativeQuery");
		}
	}
	
	public Query createNativeQuery(String  sqlString)  throws IllegalArgumentException{
		try
		{
			return entityManager.createNativeQuery(sqlString);
		}
		catch(IllegalArgumentException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error in createNativeQuery");
		}
	}
	/**
	 * Create an instance of Query for executing
	 * a native SQL query.
	 * @param sqlString a native SQL query string
	 * @param resultClass the class of the resulting instance(s)
	 * @return the new query instance
	 */
	public Query createNativeQuery(String  sqlString, String  resultSetMapping)   throws TransactionRequiredException{
		try
		{
			return entityManager.createNativeQuery(sqlString, resultSetMapping);

		}
		catch(TransactionRequiredException e )
		{
			e.printStackTrace();
			throw new TransactionRequiredException("Error in Getting data");
		}
	}

	/**
	 * Merge the state of the given entity into the
	 * current persistence context.
	 * @param entity
	 * @return the instance that the state was merged
	 */

	public void update (Object entity) throws TransactionRequiredException,IllegalArgumentException{
		try{
			entityManager.merge(entity);
		}
		catch(TransactionRequiredException e){
			e.printStackTrace();
			throw new TransactionRequiredException("Error in updating data");
		}
		catch(IllegalArgumentException e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("IllegalArgumentException when updating the data");
		}
	}

	/**
	 * Return the underlying provider object for the EntityManager,
	 * if available. The result of this method is implementation
	 * specific.
	 */


	public Object  getDelegate()  throws IllegalStateException{
		try
		{
			return entityManager.getDelegate();
		}
		catch(IllegalStateException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error in Delegate");
		}
	}
	/**
	 * Create an instance of Query for executing a native SQL statement, e.g.,
	 * for update or delete.
	 * 
	 * @param sqlString
	 *            a native SQL query string
	 * @return the new query instance
	 */

	public Query createQuery(String name)throws IllegalArgumentException
	{
		Query	query;
		try{
			query =  entityManager.createQuery(name);
		}
		catch(IllegalArgumentException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Error in Querycreation");
		}

		return query;
	}

/**
 * Indicate whether a resource transaction is in progress
 * @return boolean
 * @throws IllegalArgumentException
 */
	public boolean isActive()throws PersistenceException
	{
		try{
			return jPersistenceProvider.isActive();
		}catch (Exception e) {
			e.printStackTrace();
			throw new PersistenceException("Problem in Manage the Transaction");
		}
		
	}
	
	private static  String decryptString(String input)
	{
		if(decryptStr==null)
		{
			try
			{
				JasyptStatelessService service = new JasyptStatelessService();
				decryptStr= service.decrypt(input, "e0PS_C0DE", null, null, null, null,null,null, null, null, null,null, null, null, null, null,null,null, null, null, null,null);
				return decryptStr;
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return decryptStr;
	}
	

	public Connection getSQLConnection(){
		Connection con = null;
		   try {
			   Session session = entityManager.unwrap(Session.class);
			   SessionFactory sessionFactory = (SessionFactory) session.getSessionFactory();
			   con = sessionFactory.getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class).getConnection();
	} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	public String getpUnitName() {
		return pUnitName;
	}

	public void setpUnitName(String pUnitName) {
		this.pUnitName = pUnitName;
	}

	
}









