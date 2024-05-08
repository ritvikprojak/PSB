package com.projak.usersyncutil.controller;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.projak.usersyncutil.impl.BulkEmpDetailsImpl;
import com.projak.usersyncutil.impl.DMSImpl;
import com.projak.usersyncutil.impl.DatacapImpl;
import com.projak.usersyncutil.impl.LdapImpl;
import com.projak.usersyncutil.util.BulkEmployeeDetails;
import com.projak.usersyncutil.util.PropertyReader;

public class AppController {
	
	static Logger log = Logger.getLogger(AppController.class.getName());
	
    private DatacapImpl datacapImpl;
    
    private DMSImpl dmsImpl;
    
    private LdapImpl ldapImpl;
    
    private BulkEmpDetailsImpl bulkEmpDetailsImpl;
    
    public AppController() {
    	
        datacapImpl = new DatacapImpl();
        
        dmsImpl = new DMSImpl();
        
        ldapImpl = new LdapImpl();
        
        bulkEmpDetailsImpl = new BulkEmpDetailsImpl();
        
    }

    public void startApplication() {

    	BulkEmpDetailsImpl detailsImpl = new BulkEmpDetailsImpl();
		
		 
		String[] deptNames = PropertyReader.getProperty("DEPT_NAMES").split(",");
		
		log.info("DeptNames Length: "+deptNames.length);
		
		String deptName = null;
		
		for (String value : deptNames) {

			log.info("DepartmentName: " + value);
			
			if (value != null) {
	            // Replace spaces with underscores
	            deptName = value.replace(" ", "_");
	            
	            // Print the modified value
	            log.info("modified value: "+deptName);
	            
	            log.info("RepositoryName: " + PropertyReader.getProperty(deptName));
	            
	        } else {
	        	
	        	log.info("DEPT_NAMES property is not found or is null.");
	            
	        }
			
			String repoName = PropertyReader.getProperty(deptName);
			
			log.info("DeptNameFromPropertyFile:"+repoName);
			
			log.info("Passing DepartmentName to API: " + value);
			
			ArrayList<BulkEmployeeDetails> empArrayList = detailsImpl.getBulkEmployeeDetails(value);
			
			if (empArrayList != null && !empArrayList.isEmpty()) {
				
				dmsImpl.syncWithDMS(empArrayList, repoName);
				
				ldapImpl.syncWithLDAP(empArrayList, repoName);
		
			}else{
			
				log.info("Data not found for "+value);
			
			}

		}
		
		//datacapImpl.performDatacapSync();
		
       // dmsImpl.syncWithDMS(empArrayList,"HFOS");//get the OS Name/s from the property file
        
       // ldapImpl.syncWithLDAP(empArrayList,"HFOS");
    	
        
       
    }
}
