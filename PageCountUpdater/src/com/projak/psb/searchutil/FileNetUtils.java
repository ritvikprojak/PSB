package com.projak.psb.searchutil;

import java.io.IOException;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.Properties;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;

public class FileNetUtils {

	public static Connection connection = null;
	public static Domain domain = null;
	public static ObjectStore objectStore = null;
	
	public Connection getCEConnection(){
		
		String uri = PropertyReader.getProperty("DMS_CE_URI");
		String userName = PropertyReader.getProperty("DMS_CE_USERNAME");
		String password=PropertyReader.getProperty("DMS_CE_PASSWORD");
		try {
			// Make connection.
		    connection = Factory.Connection.getConnection(uri);
		    Subject subject = UserContext.createSubject(connection, userName, password, null);
		    UserContext.get().pushSubject(subject);
		} catch (Exception e) {
			System.out.println("Exception in getCEConnection: "+e.getMessage());
		}
	    
		return connection;
	
	}
	public Domain getDomain(Connection connection){
		try {
			// Get default domain.
		       domain = Factory.Domain.fetchInstance(connection, null, null);
		       System.out.println("Domain: " + domain.get_Name());
		} catch (Exception e) {
			System.out.println("Exception in getDomain: "+e.getMessage());
		}
		return domain;
		
	}
	
	public ObjectStore getObjectStore(Connection connection, Domain domain, String objectStoreName) {
		try {
			objectStore = Factory.ObjectStore.fetchInstance(domain, objectStoreName, null);
			System.out.println("Retrieved ObjectStore Name: "+objectStore.get_Name());
		} catch (Exception e) {
			System.out.println("Exception in getObjectStore: "+e.getMessage());
		}
		return objectStore;
	}
	public void searchDocuments(ObjectStore objectStore){
		
		SearchScope search = new SearchScope(objectStore);
		SearchSQL sqlObject = new SearchSQL();
		//sqlObject.setSelectList("d.DocumentTitle, d.Id, d.MimeType, d.ContentElements,d.FN_PageCount");
		sqlObject.setSelectList(PropertyReader.getProperty("DMS_QUERY_SELECTLIST"));
		sqlObject.setMaxRecords(Integer.parseInt(PropertyReader.getProperty("DMS_QUERY_MAXRECORDS")));
		sqlObject.setFromClauseInitialValue(PropertyReader.getProperty("DMS_QUERY_FROMCLAUSE"), "d", true);
		// Specify the WHERE clause using the setWhereClause method.
		//String whereClause = "d.DocumentTitle LIKE '%T%'";
		String whereClause = PropertyReader.getProperty("DMS_QUERY_WHERECLAUSE");
		sqlObject.setWhereClause(whereClause);
		// Check the SQL statement.  
		System.out.println("SQL: " + sqlObject.toString());
		// Set the page size (Long) to use for a page of query result data. This value is passed 
		// in the pageSize parameter. If null, this defaults to the value of 
		// ServerCacheConfiguration.QueryPageDefaultSize.
		Integer myPageSize = new Integer(Integer.parseInt(PropertyReader.getProperty("DMS_QUERY_PAGESIZE")));

		// Specify a property filter to use for the filter parameter, if needed. 
		// This can be null if you are not filtering properties.
//		PropertyFilter myFilter = new PropertyFilter();
//		int myFilterLevel = 1;
//		myFilter.setMaxRecursion(myFilterLevel);
//		myFilter.addIncludeType(new FilterElement(null, null, null, FilteredPropertyType.ANY, null)); 

		// Set the (Boolean) value for the continuable parameter. This indicates 
		// whether to iterate requests for subsequent pages of result data when the end of the 
		// first page of results is reached. If null or false, only a single page of results is 
		// returned.
		Boolean continuable = new Boolean(true);

		// Execute the fetchObjects method using the specified parameters.
		IndependentObjectSet myObjects = search.fetchObjects(sqlObject, myPageSize, null, continuable);
		System.out.println("is query returned empty objects?: "+myObjects.isEmpty());
		
		Iterator<Document> myDocument = myObjects.iterator();
		while (myDocument.hasNext()) {
			Document document = (Document) myDocument.next();
			System.out.println("document: "+document.getProperties().getStringValue("DocumentTitle"));
			Properties properties = document.getProperties();
	        
	        PDDocument pdDocument = null;
	        int pageCount = 0;
	      //  System.out.println("doc.get_MimeType():" +document.get_MimeType());
	        try {
	        	if(document.get_MimeType().equalsIgnoreCase("application/pdf")){
	        		//System.out.println("Yes");
	        		pdDocument = PDDocument.load(document.accessContentStream(0));
	        		//System.out.println("No");
	                pageCount = pdDocument.getNumberOfPages();
	                System.out.println("Number of Pages: " + pageCount);
	                properties.putValue("FN_PageCount", pageCount);
	                document.save(RefreshMode.NO_REFRESH);
	                System.out.println("Document saved.");
	        	}
	            
	        }catch(Exception e){
	        	System.out.println("Exception::::"+e.getMessage());
	        } finally {
	            if (pdDocument != null) {
	                try {
						pdDocument.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                System.out.println("PDF closed");
	            }
	        }
	        
		}
	}
	public static void main(String[] args) {
		FileNetUtils utils = new FileNetUtils();
		Connection conn = utils.getCEConnection();
		Domain dom = utils.getDomain(conn);
		//get the OS names from the properties file and iterate
		String[] osNames = PropertyReader.getProperty("DMS_OSNames").split(",");
		System.out.println("OS Names count: "+osNames.length);
		for (String objStore : osNames) {
			System.out.println("Passing ObjectStore Name is: "+objStore);
			ObjectStore os = utils.getObjectStore(conn, dom,objStore);
			utils.searchDocuments(os);
		}
		
		
	}
}
