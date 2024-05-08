package com.projak.psb.search.plugin.services;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.ibm.json.java.JSONObject;
import com.projak.psb.search.plugin.utils.DBOperationsUtil;
import com.projak.psb.search.plugin.utils.GetEmployeeDetails;

/**
 * Provides an abstract class that is extended to create a class implementing
 * each service provided by the plug-in. Services are actions, similar to
 * servlets or Struts actions, that perform operations on the IBM Content
 * Navigator server. A service can access content server application programming
 * interfaces (APIs) and Java EE APIs.
 * <p>
 * Services are invoked from the JavaScript functions that are defined for the
 * plug-in by using the <code>ecm.model.Request.invokePluginService</code>
 * function.
 * </p>
 * Follow best practices for servlets when implementing an IBM Content Navigator
 * plug-in service. In particular, always assume multi-threaded use and do not
 * keep unshared information in instance variables.
 */
public class DownloadAuditService extends PluginService {

	/**
	 * Returns the unique identifier for this service.
	 * <p>
	 * <strong>Important:</strong> This identifier is used in URLs so it must
	 * contain only alphanumeric characters.
	 * </p>
	 * 
	 * @return A <code>String</code> that is used to identify the service.
	 */
	public String getId() {
		return "DownloadAuditService";
	}

	/**
	 * Returns the name of the IBM Content Navigator service that this service
	 * overrides. If this service does not override an IBM Content Navigator
	 * service, this method returns <code>null</code>.
	 * 
	 * @returns The name of the service.
	 */
	public String getOverriddenService() {
		return null;
	}

	/**
	 * Performs the action of this service.
	 * 
	 * @param callbacks
	 *            An instance of the <code>PluginServiceCallbacks</code> class
	 *            that contains several functions that can be used by the
	 *            service. These functions provide access to the plug-in
	 *            configuration and content server APIs.
	 * @param request
	 *            The <code>HttpServletRequest</code> object that provides the
	 *            request. The service can access the invocation parameters from
	 *            the request.
	 * @param response
	 *            The <code>HttpServletResponse</code> object that is generated
	 *            by the service. The service can get the output stream and
	 *            write the response. The response must be in JSON format.
	 * @throws Exception
	 *             For exceptions that occur when the service is running. If the
	 *             logging level is high enough to log errors, information about
	 *             the exception is logged by IBM Content Navigator.
	 */
	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("DownloadAuditService.execute()");

		String ipAddress = request.getRemoteAddr();
		
		String userName = request.getRemoteUser();
		
		String repoName = request.getParameter("repoId");
		
		String eventStatus = request.getParameter("eventStatus");
		
		String eventType = "Download";
		
		String fileName = request.getParameter("docName");
		
		System.out.println("IPAddress: "+ipAddress);
		
		GetEmployeeDetails empDetails = new GetEmployeeDetails();
		
		JSONObject employeeDetails = empDetails.getEmployeeDetails(userName);
		
		System.out.println("userDetailsObj from API: "+employeeDetails);
		
		String solId = employeeDetails.containsKey("SOLID") ? employeeDetails.get("SOLID").toString() : "";
		
		System.out.println("SOLID:"+solId);
		
		DBOperationsUtil dbUtil = new DBOperationsUtil();
		
		Connection connection = dbUtil.getDBConnection();
		
		if(connection == null || connection.isClosed()){
			//send the reason for not inserting
		}else{
			int rowsInserted = dbUtil.insertDataToAuditTable(connection,userName,solId,repoName,ipAddress,eventStatus,eventType,fileName);
			
			if (rowsInserted > 0) {
				
		        System.out.println("Data inserted successfully.");
		        
		    } else {
		    	
		        System.out.println("No rows inserted.");
		        
		    }
		}
		PrintWriter writer = response.getWriter();

		JSONResponse responseJ = new JSONResponse();

		responseJ.put("success", "rows addded");
		
		System.out.println("Final Response: "+responseJ);
		
		try {

			writer.write(responseJ.toString());

		} catch (Exception e) {

			System.out.println(e.fillInStackTrace());

		} finally {

			writer.close();

		}

	}
}
