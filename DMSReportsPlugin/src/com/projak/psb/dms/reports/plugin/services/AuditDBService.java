package com.projak.psb.dms.reports.plugin.services;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.ibm.json.java.JSONObject;
import com.projak.psb.dms.reports.plugin.utils.DBOperationsUtil;
import com.projak.psb.dms.reports.plugin.utils.GetEmployeeDetails;

public class AuditDBService extends PluginService {

    public String getId() {
        return "AuditDBService";
    }

    public String getOverriddenService() {
        return null;
    }

    public void execute(PluginServiceCallbacks callbacks,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        System.out.println("AuditDBService.execute()");
        
        String ipAddress = request.getRemoteAddr();
        String ldaploginid = request.getRemoteUser();
        System.out.println("ipAddress: " + ipAddress);
        String userName = request.getParameter("userName");
        String repoName = request.getParameter("repositoryid");
        String action = request.getParameter("userAction");
        String eventStatus = "";
        String eventType = "";
        String fileName = request.getParameter("fileName");
        
        DBOperationsUtil dbUtil = new DBOperationsUtil();
        Connection connection = dbUtil.getDBConnection();
        PrintWriter writer = response.getWriter();
        JSONResponse responseJ = new JSONResponse();

        try {
            if (connection != null && !connection.isClosed()) {
                GetEmployeeDetails empDetails = new GetEmployeeDetails();
                JSONObject employeeDetails = empDetails.getEmployeeDetails(ldaploginid);

                if (employeeDetails.containsKey("error")) {
                    // Handle the error from GetEmployeeDetails
                    responseJ.put("error", employeeDetails.get("error"));
                } else {
                    System.out.println("userDetailsObj from API: " + employeeDetails);
                    System.out.println("SOLID: " + employeeDetails.get("SOLID"));
                    String solId = employeeDetails.containsKey("SOLID") ? employeeDetails.get("SOLID").toString() : "";
                    System.out.println("SOLID:" + solId);
                    String branchName = employeeDetails.containsKey("BranchName") ? employeeDetails.get("BranchName").toString() : "";
                    System.out.println("BRANCHNAME:: " + branchName);

                    if (action.equalsIgnoreCase("annotSavedEvent")) {
                        eventType = "Annotation";
                        eventStatus = "Annotation updated successfully";

                    } else if (action.equalsIgnoreCase("annotSaveFailedEvent")) {
                        eventType = "Annotation";
                        eventStatus = "Annotation update failed";

                    } else if (action.equalsIgnoreCase("onDocumentLoaded")) {
                        eventType = "View";
                        eventStatus = "Document view successful";

                    } else if (action.equalsIgnoreCase("documentLoadFailedEvent")) {
                        eventType = "View";
                        eventStatus = "Document view failed";

                    } else if (action.equalsIgnoreCase("printFinishEvent")) {
                        eventType = "Print";
                        eventStatus = "Print action successful";

                    }
                    int rowsInserted = dbUtil.insertDataToAuditTable(connection, userName, branchName, solId, repoName, ipAddress, eventStatus, eventType, fileName);

                    if (rowsInserted > 0) {
                        System.out.println("Data inserted successfully.");
                        responseJ.put("success", "rows added");
                    } else {
                        System.out.println("No rows inserted.");
                        responseJ.put("success", "no rows added");
                    }
                }
            } else {
                responseJ.put("error", "Database connection is not available.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseJ.put("error", "An error occurred: " + e.getMessage());
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Finally db connection closed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            
            System.out.println("Final Response: " + responseJ);

            writer.write(responseJ.toString());
            writer.close();
        }
    }
}
