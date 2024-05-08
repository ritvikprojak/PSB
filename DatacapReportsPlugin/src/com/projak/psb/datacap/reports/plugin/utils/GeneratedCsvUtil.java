package com.projak.psb.datacap.reports.plugin.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import com.ibm.ecm.extension.PluginLogger;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class GeneratedCsvUtil {
    public String generateCSV(JSONArray jsonResponse, PluginLogger log, String reportType, String repositoryId) {
        System.out.println("<<< ::: In the GeneratedCsvUtil Class generateCSV Method ::: >>>");
        System.out.println("JSON Response:: " + jsonResponse);
        String methodName = "generateCSV";
        boolean isDebugEnabled = log.isDebugLogged();
        log.logEntry(this, methodName);
        log.logDebug(this, "documentPermissionList  is", jsonResponse.toString());
        JSONObject jsonObject = null;
        StringBuffer resultSring = new StringBuffer();
        resultSring.append("");
        System.out.println("JSON Response Size:: " + jsonResponse.size());
        try {
        	String[] columnOrder = null;
        	if (reportType.equalsIgnoreCase("User Login Session Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        	    columnOrder = new String[] {
        	    	"SI. No", "Repository Name", "User Name", "Branch Name", "Branch Code", "Login DateTime", "Logout DateTime", "IP Address", "Event Name"
        	    };
        	} else if (reportType.equalsIgnoreCase("Audit Log Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        	    columnOrder = new String[] {
        	    	"SI. No", "Repository Name", "User Name", "Branch Name", "Branch Code","Event Type", "Record DateTime", "File Name", "Status of The Activity", "IP Address"
        	    };
        	} else if(reportType.equalsIgnoreCase("Daily Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Date of Uploading", "Branch Name", "Branch Code", "No. of Documents Uploaded", "No. of Pages Uploaded"
            		};
        	} else if(reportType.equalsIgnoreCase("Summary/ Monthly Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
            			"SI. No", "Month", "Date of Uploading", "Branch Name", "Branch Code", "No. of Documents Uploaded", "No. of Pages Uploaded"	
            		};
        	} else if(reportType.equalsIgnoreCase("User Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Date of Uploading", "Department Name", "Branch Name", "Branch Code", "Uploaded By", "No. of Documents Uploaded", "No. of Pages Uploaded"	
            		};
        	} else if(reportType.equalsIgnoreCase("Department Wise Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Date of Uploading", "Department Name", "Branch Name", "Branch Code", "No. of Documents Uploaded", "No. of Pages Uploaded"	
            		};
        	} else if(reportType.equalsIgnoreCase("Department Wise Metadata Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		switch (repositoryId) {
                case "HOIT":
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name", "Branch Name", "Branch Code", "Tender No", "Date", "HO Department Code", "RFP Subject", "Eligibility Criteria", "Scope of RFP", "Project Timelines", "Terms & Condition", "General Instructions", "Evaluation Process", "Service Level and Penalties", "Bid System", "Tendering Process", "In-principal Note for Floating of RFP", "Note for Floating of RFP", "Pre-bid Queries/Response from bidder", "Bid Documents", "Note for Technical Evaluation", "Note for Commercial Evaluation", "Note for Reverse Auction", "Note for Declaring L-1 bidder", "Financial Approval for Final TCO", "Misc notes regrading Tender", "Notes for Floating Addendum", "Addendum No", "Addendum Dated", "Addendum Subject", "Addendum related queries/response", "Annexures No", "Annexures Subject", "Document Type", "Remark", "MEETING FOR THE MONTH OF", "MEETING DATE", "Sr.No.", "Purpose Code", "Agenda Items", "Purpose", "Reference Number", "Agenda Item No", "Action Taken Report", "ATR Date", "Note Dated", "Minutes of Meeting", "Note Type", "Date of Receipt", "Type", "Subject", "From", "Date of submission", "TO", "FINANCIAL YEAR", "FREQUENCY", "AUDIT TYPE", "Audit Date", "Audit Copy Ref.No.", "Auditor name", "YEAR", "RECEIVED FROM", "DATE OF RTI APPLICATION", "NAME OF APPLICANT", "SENT TO", "DATE OF DISPOSAL", "Remarks", "Meeting No.", "Uploaded By", "No. of Pages Uploaded"	
            		};
                    break;
                case "HOPD":
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name", "Branch Name", "Branch Code", "Note Date", "Note Type", "HO Department Code", "Note Subject", "Note F.Y.", "Note Dispatch/Sr.No.", "Competent Authority", "IFSC CODE", "BRANCH LICENCE NO", "MICR NO", "Opening NAME", "Closing NAME", "Merging NAME","Branch open date", "RTI DATE", "RTI NAME", "REPLY DATE", "Reply F.Y.", "Tender No.", "Date", "In-principal Note for Floating of RFP", "Pre-bid Queries/Response from bidder", "Bid Documents", "Note for Technical Evaluation", "Notes for Floating Addendum", "Addendum No", "Addendum Dated", "Addendum Subject", "Addendum related queries/response", "Annexures No", "Annexures Subject", "Document Type", "Remarks", "Uploaded By", "No. of Pages Uploaded"	
            		};
                    break;
                default:
                    System.out.println("Oops! I'm In the Default Case, There is nothing to process further...");
                    break;
       		 }
        	} else if(reportType.equalsIgnoreCase("Datacap Daily Report")) { //Datacap Reports
        		
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Date of Uploading", "No. of Documents on Hold", "No. of Rejected Documents", "No. of Documents at Verifier"
            		};
        	} else if(reportType.equalsIgnoreCase("DMS Daily Report")) { //Datacap Reports
        		
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Date of Uploading", "Department Name", "Classification/Subclassification Name", "Owner/Uploaded Name", "NoOfDocumentsUploaded"
            		};
        	} else if(reportType.equalsIgnoreCase("Datacap Summary Status Report")) { //Datacap Reports
        		
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "DATE", "PB_DEPTCODE", "QU_TASK", "QU_STATUS", "Batches"
            		};
        	} else if(reportType.equalsIgnoreCase("Datacap Monthly Report")) { //Datacap Reports
        		
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Month", "Date of Uploading", "No. of Documents on Hold", "No. of Rejected Documents", "No. of Documents at Verifier"
            		};
        	}else if(reportType.equalsIgnoreCase("DMS Monthly Report")) { //Datacap Reports
        		
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Month", "Date_Of_Uploading", "Department_Name", "No_Of_Documents_Uploaded", "No_Of_Pages_Uploaded"
            		};
        	}
        	else {
        	    System.out.println("Oops! It's an unknown report type...");
        	}
            for (String column : columnOrder) {
                resultSring.append('"' + column + '"' + ",");
            }
            resultSring.append("\n");
            for (int i = 0; i < jsonResponse.size(); i++) {
                jsonObject = (JSONObject) jsonResponse.get(i);
                for (String column : columnOrder) {
                    String value = (jsonObject.get(column) == null) ? "" : jsonObject.get(column).toString();
                    if (column.equals("LoginDateTime") || column.equals("LogoutDateTime")) {
                        value = formatDateTime(value);
                    }
                    if (column.equals("RecordDateTime")) {
                        value = formatDateTime(value);
                    }
                    resultSring.append('"' + value + '"' + ",");
                }
                resultSring.append("\n");
            }
            resultSring.deleteCharAt(resultSring.lastIndexOf(","));
            System.out.println("resultSring::::::::::85" + resultSring.toString());
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
            log.logDebug(this, "Error", e.getMessage());
        } finally {
            jsonResponse = null;
            jsonObject = null;
            Iterator iterator = null;
            Set set = null;
        }
        return resultSring.toString();
    }
    
    private String formatDateTime(String dateTimeStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date date = inputFormat.parse(dateTimeStr);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateTimeStr;
        }
    }
}