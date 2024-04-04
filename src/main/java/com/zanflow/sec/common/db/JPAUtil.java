package com.zanflow.sec.common.db;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 * JPA related utility methods
 * 
 * The utility only supports ONE persistence unit per session.
 * Methods are not completely thread-safe but concurrent threads
 * should not cause problems other than wasting CPU cycles. 
 * This is not expected to happen frequently and 
 * is preferred to avoid synchronization overheads.
 * The creator of the EntityManager is responsible to close it.
 * These are typically the service classes.
 *
 */
public final class JPAUtil {
	
	private  EntityManagerFactory emf = null;
	private  EntityManager em = null;
	private static Map<String, EntityManagerFactory> objEmfMap = new Hashtable<String, EntityManagerFactory>();

	//public  Map<String, EntityManager> objEM = new HashMap();		
	
	/**
	 * Returns the Persistence Unit's EntityManager.
	 * Creates one if none exists, throws a RuntimeException if exist but is closed. 
	 * 
	 * @return EntityManager
	 */
	public EntityManager getEntityManager(String unitName) {
			try{
					em = getEntityManagerFactory(unitName).createEntityManager();
					
				}

			catch(Exception e)
			{
				//System.out.println("Problem with EntityManager ");
				e.printStackTrace();
			}			
			return em;
		}


	/**
	 * Returns the Persistence Unit's EntityManagerFactory.
	 * Creates one if none exists, throws a RuntimeException if exist but is closed. 
	 * 
	 * @return EntityManagerFactory
	 */
 
	public synchronized static EntityManagerFactory getEntityManagerFactory(String unitName) {	
	  EntityManagerFactory emf = objEmfMap.get(unitName);
		try{
			
			// check if the entity manager factory is available for the unit else create			
			if (emf == null) {
				emf = Persistence.createEntityManagerFactory(unitName);
				objEmfMap.put(unitName, emf);
			}
			/*else {
				//System.out.println("EntityMangerFactory AV");
			}*/
		}

		catch(Exception e)
		{
			e.printStackTrace();
			//System.out.println("Problem with EntityManagerFactory ");
		}
		return emf;
	}

	 /**
	 * Closes the Persistence Unit EntityManagerFactory
	 * This should only be done at application end.
	 */
	
	public  void closeEntityManagerFactory(String unitName) {
		emf = objEmfMap.get(unitName);
		if (emf != null) {
			emf.close();
			emf = null;
		}
	}

	
	/**
	 * Cleans up and closes the ThreadLocal EntityManager
	 * This should be done for each "session" and not each operation.
	 * A "session" has a similar life span as a http request.
	 * - Rollback any active transactions
	 * - Closes EntityManager in session
	 */
	
	public  void closeEntityManager(String unitName) {
		//em = objEM.get(unitName);		
		if ( em!= null) {			
			  em.close();
			}
		}


	/**
	 * Create an Entity Manager if it is not Exit.
	 *                          
	 * @return the EntityMaager 
	 */


	public  EntityManager createEntityManager(String pUnit) {		 
		return  getEntityManagerFactory(pUnit).createEntityManager();
	}


	public static String getPersistenceUnitUnitName(String companyCode, String countryCode, String processId, String appName) 
	{
		String strUnitName=null;
		
		strUnitName=appName;
		return strUnitName;
	}


	
	}

   
