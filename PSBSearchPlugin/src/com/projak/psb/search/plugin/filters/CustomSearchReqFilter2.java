package com.projak.psb.search.plugin.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.ecm.extension.PluginRequestFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONMessage;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONArtifact;
import com.ibm.json.java.JSONObject;
import com.projak.psb.search.plugin.utils.DMSENCDECUtil;
import com.projak.psb.search.plugin.utils.GetEmployeeDetails;
import com.projak.psb.search.plugin.utils.PSBPropertyReader;

/**
 * Provides an abstract class that is extended to create a filter for requests to a particular service. The filter is provided with the 
 * request parameters before being examined by the service. The filter can change the parameters or reject the request.
 */
public class CustomSearchReqFilter2 extends PluginRequestFilter {

	/**
	 * Returns the names of the services that are extended by this filter.
	 * 
	 * @return A <code>String</code> array that contains the names of the services.
	 */
	public String[] getFilteredServices() {
		return new String[] { "/p8/search" };
	}

	/**
	 * Filters a request that is submitted to a service.
	 * 
	 * @param callbacks
	 *            An instance of <code>PluginServiceCallbacks</code> that contains several functions that can be used by the
	 *            service. These functions provide access to plug-in configuration and content server APIs.
	 * @param request
	 *            The <code>HttpServletRequest</code> object that provides the request. The service can access the invocation parameters from the
	 *            request. <strong>Note:</strong> The request object can be passed to a response filter to allow a plug-in to communicate 
	 *            information between a request and response filter.
	 * @param jsonRequest
	 *            A <code>JSONArtifact</code> that provides the request in JSON format. If the request does not include a <code>JSON Artifact</code>  
	 *            object, this parameter returns <code>null</code>.
	 * @return A <code>JSONObject</code> object. If this object is not <code>null</code>, the service is skipped and the
	 *            JSON object is used as the response.
	 */
	public JSONObject filter(PluginServiceCallbacks callbacks, HttpServletRequest request, JSONArtifact jsonRequest) throws Exception {
		System.out.println("CustomSearchReqFilter.filter():");
		JSONArray criteria = (JSONArray) ((JSONObject) jsonRequest).get("searchCriteria");
		JSONObject criterion = new JSONObject();
		String user = request.getRemoteUser();
		System.out.println("UserId: "+user);
		//get the access branches from api
				GetEmployeeDetails empDetails = new GetEmployeeDetails();
				JSONObject employeeDetails = empDetails.getEmployeeDetails(user);
				
				System.out.println("userDetailsObj from API: "+employeeDetails);
		
		if(employeeDetails.containsKey(PSBPropertyReader.getResourceBundle().getString("employeeDetailsKey"))){
			if(employeeDetails.get("Status").equals("E") || employeeDetails.get("Error").equals("No Data Found") ){
				System.out.println("Status E");
				JSONObject json=null;
				JSONMessage errorMessage = new JSONMessage(10001, PSBPropertyReader.getResourceBundle().getString("errorMessageText"),
						PSBPropertyReader.getResourceBundle().getString("errorMessageExplanation")+ employeeDetails, PSBPropertyReader.getResourceBundle().getString("errorMessageUserResponse"), "", "");

				JSONArray jsonMessages = new JSONArray();

				jsonMessages.add(errorMessage);

				if (jsonRequest != null) {
					//
					if (jsonRequest instanceof JSONObject) {
						System.out.println("instanceof JSONObject");
					    json = (JSONObject) jsonRequest;
					    json.put(PSBPropertyReader.getResourceBundle().getString("jsonRequestObjectKey"), jsonMessages);
					    System.out.println("end1");
					    return json;
					    // process json object
					} else if (jsonRequest instanceof JSONArray) {
						System.out.println("instanceof JSONArray");
					    JSONArray jsonArray = (JSONArray) jsonRequest;
					    json = (JSONObject) jsonArray.get(0);
					    json.put(PSBPropertyReader.getResourceBundle().getString("jsonRequestObjectKey"), jsonMessages);
					    System.out.println("end2");
					    return json;
					}
					
				}
			
			}
		}else{
			System.out.println("No");
			JSONObject userAccessObj = (JSONObject) employeeDetails.get(PSBPropertyReader.getResourceBundle().getString("userAccessObj"));
			String userStatus = (String) employeeDetails.get(PSBPropertyReader.getResourceBundle().getString("userStatus"));
			String userType = (String) employeeDetails.get(PSBPropertyReader.getResourceBundle().getString("userType")).toString();
			JSONArray accessBranches = (JSONArray) userAccessObj.get(PSBPropertyReader.getResourceBundle().getString("accessBranches"));
			JSONArray accessZones = (JSONArray) userAccessObj.get(PSBPropertyReader.getResourceBundle().getString("accessZones"));
			JSONArray accessDepts = (JSONArray) userAccessObj.get(PSBPropertyReader.getResourceBundle().getString("accessDepts"));
			System.out.println(accessBranches+"\n"+accessZones+"\n"+accessDepts);
			System.out.println("USERSTATUS: "+userStatus);
			
			if (userStatus == null || !(userStatus.equalsIgnoreCase("ACTIVE")) || userStatus == "") {
				System.out.println("UserStatus is: "+userStatus);
				JSONObject json=null;
				JSONMessage errorMessage = new JSONMessage(10001, PSBPropertyReader.getResourceBundle().getString("errorMessageText2"),
						PSBPropertyReader.getResourceBundle().getString("errorMessageExplanation2")+ userStatus, PSBPropertyReader.getResourceBundle().getString("errorMessageUserResponse2"), "", "");

				JSONArray jsonMessages = new JSONArray();

				jsonMessages.add(errorMessage);

				if (jsonRequest != null) {
					//
					if (jsonRequest instanceof JSONObject) {
						System.out.println("instanceof JSONObject");
					    json = (JSONObject) jsonRequest;
					    json.put(PSBPropertyReader.getResourceBundle().getString("jsonRequestObjectKey"), jsonMessages);
					    return json;
					    // process json object
					} else if (jsonRequest instanceof JSONArray) {
						System.out.println("instanceof JSONArray");
					    JSONArray jsonArray = (JSONArray) jsonRequest;
					    json = (JSONObject) jsonArray.get(0);
					    json.put(PSBPropertyReader.getResourceBundle().getString("jsonRequestObjectKey"), jsonMessages);
					    return json;
					}
					
				}
			} else {
				System.out.println("UserStatus is: "+userStatus);
				if(userType.equalsIgnoreCase(PSBPropertyReader.getResourceBundle().getString("userType2"))){
					
					System.out.println("UserType is: "+userType);
					
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey"), PSBPropertyReader.getResourceBundle().getString("criterionValue"));
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey2"), PSBPropertyReader.getResourceBundle().getString("criterionValue2"));
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey3"), accessBranches);
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey4"), PSBPropertyReader.getResourceBundle().getString("criterionValue4"));
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey5"), PSBPropertyReader.getResourceBundle().getString("criterionValue5"));
					criteria.add(criterion);
					
				}else if (userType.equalsIgnoreCase(PSBPropertyReader.getResourceBundle().getString("userType3"))) {
					
					System.out.println("UserType is: "+userType);
					
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey6"), PSBPropertyReader.getResourceBundle().getString("criterionValue6"));
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey7"), PSBPropertyReader.getResourceBundle().getString("criterionValue7"));
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey8"), accessBranches);
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey9"), PSBPropertyReader.getResourceBundle().getString("criterionValue9"));
					criterion.put(PSBPropertyReader.getResourceBundle().getString("criterionKey10"), PSBPropertyReader.getResourceBundle().getString("criterionValue10"));
					criteria.add(criterion);
					
				}else if (userType.equalsIgnoreCase(PSBPropertyReader.getResourceBundle().getString("userType4"))) {
						System.out.println("UserType is: "+userType);
				}

			}
		}
		System.out.println("Final Criteria: "+criteria);
		return null;
		
	}
}
