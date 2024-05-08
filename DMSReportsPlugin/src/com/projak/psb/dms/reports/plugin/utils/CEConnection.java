package com.projak.psb.dms.reports.plugin.utils;

import java.util.Iterator;
import java.util.Vector;

import javax.security.auth.Subject;


import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.serviceability.Logger;

public class CEConnection {

	
	private Connection con;
	private Domain dom;
	private String domainName;
	private ObjectStoreSet ost;
	private Vector osnames;
	private boolean isConnected;
	private UserContext uc;
	static String objectStore;	
	/*
	 * constructor
	 */
	public CEConnection()
	{
		con = null;
		uc = UserContext.get();
		dom = null;
		domainName = null;
		ost = null;
		osnames = new Vector();
		isConnected = false;
	}
	public Connection establishConnection()
    {
		System.out.println("CEConnection.establishConnection()");
		
		try {
			String uri = PSBPropertyReader.getResourceBundle().getString("CE_URI");
			
			System.out.println("uri:"+uri);
			
			con = Factory.Connection.getConnection(uri);
        
			String userName = PSBPropertyReader.getResourceBundle().getString("CE_USERNAME");
        
			String password = PSBPropertyReader.getResourceBundle().getString("CE_PASSWORD");
			
			Subject sub = UserContext.createSubject(con,userName,password,null);
        
			uc.pushSubject(sub);
        
			isConnected = true;
			
			/*dom = fetchDomain();
        
			System.out.println("Domain is :"+dom);
        
			domainName = dom.get_Name();
        
			System.out.println("domainName is :"+domainName);
        
			ost = getOSSet();
        
			System.out.println("ost is :"+ost);
       
			
        
        objectStore = PropertyReader.getProperty("objectStore");
        System.out.println("objectStore is :"+ objectStore);
        ObjectStore os= Factory.ObjectStore.fetchInstance(dom, objectStore, null);*/
        
		}
		catch(Exception e) {
			System.out.println("Exception caught in establishConnection method"+ e);
		}
       
		return con;
        
    }

	/*
	 * Returns Domain object.
	 */
	public Domain fetchDomain()
    {
        dom = Factory.Domain.fetchInstance(con, null, null);
        return dom;
    }
    
    /*
     * Returns ObjectStoreSet from Domain
     */
	public ObjectStoreSet getOSSet()
    {
        ost = dom.get_ObjectStores();
        return ost;
    }
    
    /*
     * Returns vector containing ObjectStore
     * names from object stores available in
     * ObjectStoreSet.
     */
	public Vector getOSNames()
    {
    	if(osnames.isEmpty())
    	{
    		Iterator it = ost.iterator();
    		while(it.hasNext())
    		{
    			ObjectStore os = (ObjectStore) it.next();
    			osnames.add(os.get_DisplayName());
    		}
    	}
        return osnames;
    }

	/*
	 * Checks whether connection has established
	 * with the Content Engine or not.
	 */
	public boolean isConnected() 
	{
		return isConnected;
	}
	
	/*
	 * Returns ObjectStore object for supplied
	 * object store name.
	 */
	public ObjectStore fetchOS(String name)
    {
        ObjectStore os = Factory.ObjectStore.fetchInstance(dom, name, null);
        return os;
    }
	
	/*
	 * Returns the domain name.
	 */
	public String getDomainName()
    {
        return domainName;
    }
}

