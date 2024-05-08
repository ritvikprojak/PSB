package com.projak.psb.datacap.reports.plugin.services;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.projak.psb.datacap.reports.plugin.utils.GetEmployeeDetails;
import com.projak.psb.datacap.reports.plugin.utils.PSBPropertyReader;

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
public class GetEmployeeDetailsService extends PluginService {

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
		return "GetEmployeeDetailsService";
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
	    System.out.println("GetEmployeeDetailsService.execute()");
	    PrintWriter writer = response.getWriter();
	    JSONResponse responseJ = new JSONResponse();

	    try {
	        String userName = request.getParameter("userName");
	        String repoName = request.getParameter("repoId");
	        String ldaploginid = request.getRemoteUser();

	        if (userName == null || userName.isEmpty() || repoName == null || repoName.isEmpty()) {
	            // Handle missing or invalid parameters
	            responseJ.put("ERROR", "Invalid parameters");
	        } else {
	            GetEmployeeDetails empDetails = new GetEmployeeDetails();
	            JSONObject employeeDetails = empDetails.getEmployeeDetails(userName);

	            if (employeeDetails == null || employeeDetails.isEmpty()) {
	                // Handle empty or invalid employee details
	                responseJ.put("ERROR", "Employee details not found");
	            } else {
	                System.out.println("userDetailsObj from API: " + employeeDetails);
	                String solId = employeeDetails.containsKey("SOLID") ? employeeDetails.get("SOLID").toString() : "";
	                System.out.println(":::SOLID:::" + solId);
	                String branchName = employeeDetails.containsKey("BranchName") ? employeeDetails.get("BranchName").toString() : "";
	                System.out.println(":::BRANCHNAME::: " + branchName);
	                System.out.println("getting the access details");
	                //JSONArray accessDepts = employeeDetails.containsKey("ACCESSS") ? (JSONArray) employeeDetails.get("ACCESSS") : new JSONArray();
	                JSONObject userAccessObj = (JSONObject) employeeDetails.get(PSBPropertyReader.getResourceBundle().getString("userAccessObj"));
	    			JSONArray accessBranches = (JSONArray) userAccessObj.get(PSBPropertyReader.getResourceBundle().getString("accessBranches"));
	    			JSONArray accessZones = (JSONArray) userAccessObj.get(PSBPropertyReader.getResourceBundle().getString("accessZones"));
	    			JSONArray accessDepts = (JSONArray) userAccessObj.get(PSBPropertyReader.getResourceBundle().getString("accessDepts"));
	    			System.out.println(accessBranches+"\n"+accessZones+"\n"+accessDepts);
	                // String deptNames = null;
	                System.out.println("Access Depts: " + accessDepts);
	                System.out.println("JSONArray Size: " + accessDepts.size());
	                
	                JSONArray deptsArray = new JSONArray();
	                JSONObject deptsObj = new JSONObject();
	                if (accessDepts != null && accessDepts.size() > 0) {
	                   // StringBuilder deptNamesBuilder = new StringBuilder();
	                    
	                    String hashMapString = PSBPropertyReader.getResourceBundle().getString("DEPARTMENTS_MAPPING");
	                    
	                 // Parse the string into a HashMap
	                    Map<String, String> hashMapValues = parseHashMapString(hashMapString);
	                    
	                    for (int i = 0; i < accessDepts.size(); i++) {
	                    	
	                    	String dept = hashMapValues.get(accessDepts.get(i));
	                    	
	                    	if(dept!=null){
	                    		
	                    		JSONObject jsonObject = new JSONObject();
	                    		
	                    		jsonObject.put("name", dept);
	                    		
	                    		deptsArray.add(jsonObject);
	                    		
	                    	}else{
	                    		
	                    		System.out.println("Mapping not found in the properties file for "+accessDepts.get(i));
	                    	
	                    	}
	                    	
	                        //deptNamesBuilder.append(accessDepts.get(i));

//	                        if (i < accessDepts.size() - 1) {
//	                            deptNamesBuilder.append(",");
//	                        }
	                    }

	                   // deptNames = deptNamesBuilder.toString();
	                    System.out.println("DeptNames:" + deptsArray);
	                } else {
	                    // Handle the case when accessDepts is null or empty
	                    //deptNames = "No departments found";
	                	System.out.println("No departments found");
	                }

	                responseJ.put("DEPTNAMES", deptsArray);
	                System.out.println("Final Response: " + responseJ);
	            }
	        }
	    } catch (Exception e) {
	        // Handle general exceptions
	        e.printStackTrace();
	        responseJ.put("ERROR", "An error occurred");
	    } finally {
	        // Write the JSON response
	        writer.write(responseJ.toString());
	        writer.close();
	    }
	}
	
	private static Map<String, String> parseHashMapString(String hashMapString) {
        Map<String, String> hashMapValues = new HashMap<>();
        if (hashMapString != null && !hashMapString.isEmpty()) {
            // Remove leading and trailing curly braces
            hashMapString = hashMapString.replaceAll("^\\{(.*)\\}$", "$1");

            // Split by ","
            String[] keyValuePairs = hashMapString.split(",");

            for (String pair : keyValuePairs) {
                // Split by "=" to separate key and value
                String[] keyValue = pair.split("=");

                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    hashMapValues.put(key, value);
                }
            }
        }
        return hashMapValues;
    }

}
