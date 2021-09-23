package com.zanflow.bpmn.util;

import java.util.List;
import java.util.Properties;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import com.zanflow.bpmn.exception.JPAIllegalStateException;
import com.zanflow.bpmn.exception.JPAPersistenceException;
import com.zanflow.common.db.Constants;
import com.zanflow.common.db.JPersistenceProvider;

public class AppProperties {
private static AppProperties instance;
    
    private Properties prop = new Properties();
    public static synchronized AppProperties getInstance(){
        if(instance == null){
            try {
				instance = new AppProperties();
			} catch (EntityNotFoundException | EntityExistsException | JPAPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return instance;
    }
    
    private AppProperties() throws EntityNotFoundException, EntityExistsException, JPAPersistenceException {
    	/*Load properties*/
    	
    	JPersistenceProvider provider = new JPersistenceProvider(Constants.DB_PUNIT);
    	
    	try{
    		List<Object[]> property = (List)provider.createNativeQuery("select key,value from {h-schema}zf_cfg_properties").getResultList();
	    	for (Object[] objects : property) {
				prop.setProperty((String)objects[0], (String)objects[1]);
	    	};
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				provider.close("zanflowdb");
			} catch (JPAIllegalStateException e) {
				e.printStackTrace();
			}
		}
    }
    
    public String getPropery(String key) {
    	System.out.println("key#"+key+"#value#"+prop.getProperty(key));
    	return prop.getProperty(key);
    }

	public String getPropery(String string, String string2) {
		// TODO Auto-generated method stub
		return null;
	}

}
