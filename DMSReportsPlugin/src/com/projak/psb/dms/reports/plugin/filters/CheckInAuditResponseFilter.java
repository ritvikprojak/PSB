package com.projak.psb.dms.reports.plugin.filters;

import java.sql.Connection;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginResponseFilter;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONObject;
import com.projak.psb.dms.reports.plugin.utils.CEConnection;
import com.projak.psb.dms.reports.plugin.utils.DBOperationsUtil;
import com.projak.psb.dms.reports.plugin.utils.GetEmployeeDetails;

public class CheckInAuditResponseFilter extends PluginResponseFilter {

    public String[] getFilteredServices() {
        return new String[] { "/p8/checkIn" };
    }

    public void filter(String serverType, PluginServiceCallbacks callbacks,
            HttpServletRequest request, JSONObject jsonResponse) throws Exception {
        System.out.println("CheckInAuditResponseFilter.filter()");
        String userName = request.getRemoteUser();
        String repositoryName = request.getParameter("repositoryId");
        System.out.println("userName&RepoName: "+userName+"\t"+repositoryName);
        String ldaploginid = request.getRemoteUser();
        String ipAddress = request.getRemoteAddr();
        String repositoryId = request.getParameter("repositoryId");
        System.out.println("IPAddress: "+ipAddress);
        String docId = request.getParameter("docid");
        ObjectStore objectStore = null;
        try {
            Subject subject = callbacks.getP8Subject(repositoryId);
            if (subject != null) {
                UserContext.get().pushSubject(subject);
                objectStore = callbacks.getP8ObjectStore(repositoryId);
                System.out.println("ObjectStoreName: " + objectStore.get_DisplayName());
            } else {
                System.out.println("Subject is null. User may not be authenticated.");
                CEConnection ce = new CEConnection();
                com.filenet.api.core.Connection ceConnection = ce.establishConnection();
        		
        		Domain dom = ce.fetchDomain();
        		
        		objectStore = Factory.ObjectStore.fetchInstance(dom, repositoryId, null);
        		System.out.println("FileNet ObjectStoreName: " + objectStore.get_DisplayName());
        		System.out.println("connection status is:" + ce.isConnected());
            }
        } catch (EngineRuntimeException e) {
            System.out.println("EngineRuntimeException in DepartmentWiseMetadataReport Check:" + e.getMessage());
        }
        //ObjectStore objectStore = callbacks.getP8ObjectStore(repositoryId);
        //IPTest,{5CC6CE40-6C08-4EA6-9A04-E8C93648B879},{70BDCF89-0000-C413-A795-AAB02665DAA5}
        String[] values = docId.split(",");
        String objectId = values[2].toString();
        System.out.println(values[2]);
        Document document = Factory.Document.fetchInstance(objectStore, objectId, null);
        String fileName = document.getProperties().getStringValue("DocumentTitle");
        System.out.println("FileName: "+fileName);
        String eventType = "Modify";
        String eventStatus = "Document Modified Successfully.";

        GetEmployeeDetails empDetails = new GetEmployeeDetails();
        JSONObject employeeDetails = null;
        DBOperationsUtil dbOperationsUtil = new DBOperationsUtil();
        Connection dbConnection = null;

        try {
            employeeDetails = empDetails.getEmployeeDetails(userName);
            if (employeeDetails.containsKey("error")) {
                System.out.println("Error fetching employee details: " + employeeDetails.get("error"));
                // Handle the error in fetching employee details
                jsonResponse.put("error", "Error fetching employee details: " + employeeDetails.get("error"));
            } else {
                System.out.println("userDetailsObj from API: "+employeeDetails);
                System.out.println("SOLID: "+employeeDetails.get("SOLID"));
                String solId = employeeDetails.get("SOLID").toString();
                System.out.println("SOLID:"+solId);
                String branchName = employeeDetails.get("BranchName").toString();
                System.out.println("BRANCHNAME:: " + branchName);

                dbConnection = dbOperationsUtil.getDBConnection();
                if (dbConnection != null && !dbConnection.isClosed()) {
                    dbOperationsUtil.insertDataToAuditTable(dbConnection, userName, branchName, solId, repositoryName, ipAddress, eventStatus, eventType, fileName);
                    System.out.println("Audit success for Modify - Document");
                } else {
                    System.out.println("Database connection is not available.");
                    jsonResponse.put("error","Database connection is not available.");
                    // Handle the database connection error here
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions as needed
        } finally {
            try {
                if (dbConnection != null && !dbConnection.isClosed()) {
                    dbConnection.close();
                    System.out.println("Finally db connection closed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions during connection closing
                jsonResponse.put("error", "An error occurred: " + e.getMessage());
            }
        }
    }
}
