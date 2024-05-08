package com.projak.psb.dms.reports.plugin.services;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONObject;
import com.projak.psb.dms.reports.plugin.utils.CEConnection;
import com.projak.psb.dms.reports.plugin.utils.DBOperationsUtil;

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
public class UserDBReportCheckService extends PluginService {

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
		return "UserDBReportCheckService";
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
		System.out.println("<<< ::: In the UserDBReportCheckService ::: >>>");
		Connection connection = null;
	    ResultSet reportData = null;
	    ObjectStore objectStore = null;
	    try {
	        String startDate = request.getParameter("startDate");
	        System.out.println("startDate from Request Parameter:: " + startDate);
	        String endDate = request.getParameter("endDate");
	        System.out.println("endDate from Request Parameter:: " + endDate);
	        String repoID = request.getParameter("repoID");
	        System.out.println("repoID from Request Parameter:: " + repoID);
	        String patternChange = "yyyy-MM-dd hh:mm:ss";
	        String reportName = request.getParameter("reportName");
	        System.out.println("repoName from Request Parameter:: " + reportName);
	        SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
	        Date date1 = simpleDateFormatChange.parse(startDate);
	        System.out.println("Pattern Changing for startDate and Adding in Date1 As:: " + date1);
	        String pattern = "yyyy-MM-dd";
	        System.out.println("Initializing the Date Pattern Again for Start Date As:: " + pattern);
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	        String date = simpleDateFormat.format(date1);
	        date = date + " 00:00:00";
	        System.out.println("Final Date Format for startDate with Addition of Time:: " + date);
	        Date date2 = simpleDateFormatChange.parse(endDate);
	        System.out.println("Pattern Changing for endDate and Adding in Date2 As:: " + date2);
	        String pattern1 = "yyyy-MM-dd";
	        System.out.println("Initializing the Date Pattern Again for End Date As:: " + pattern1);
	        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
	        String date3 = simpleDateFormat1.format(date2);
	        String date4 = date3 + " 23:59:59";
	        System.out.println("Final Date Format for endDate with Addition of Time:: " + date4);
	        
	        DBOperationsUtil util = new DBOperationsUtil();
            connection = util.getDBConnection();
            
            if (reportName.equalsIgnoreCase("DepartmentWiseMetadataReport")) {
                try {
                    Subject subject = callbacks.getP8Subject(repoID);
                    if (subject != null) {
                        UserContext.get().pushSubject(subject);
                        objectStore = callbacks.getP8ObjectStore(repoID);
                        System.out.println("ObjectStoreName: " + objectStore.get_DisplayName());
                    } else {
                        System.out.println("Subject is null. User may not be authenticated.");
                        CEConnection ce = new CEConnection();
                        com.filenet.api.core.Connection ceConnection = ce.establishConnection();
                		
                		Domain dom = ce.fetchDomain();
                		
                		objectStore = Factory.ObjectStore.fetchInstance(dom, repoID, null);
                		System.out.println("FileNet ObjectStoreName: " + objectStore.get_DisplayName());
                		System.out.println("connection status is:" + ce.isConnected());
                    }
                } catch (EngineRuntimeException e) {
                    System.out.println("EngineRuntimeException in DepartmentWiseMetadataReport Check:" + e.getMessage());
                }
            }
            
	        System.out.println("Database Schema Name:: " + connection.getSchema());
	        JSONObject jsonObject =  util.retrieveDataFromTable(connection,date,date4,repoID,reportName,objectStore);
	       
	        PrintWriter writer = response.getWriter();
	        writer.print(jsonObject);
			try {
			} catch (Exception e) {
				System.out.println("Exception Writer try Block:: " + e.fillInStackTrace());
			} finally {
				writer.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception in try block:: " + e.getMessage());
		}
	}
}