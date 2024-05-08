package com.projak.psb.dms.reports.plugin.utils;

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
        				"SI. No", "Date of Uploading", "Branch Name", "Branch Code", "No. of Documents Uploaded", "No Of Pages Uploaded"
            		};
        	} else if(reportType.equalsIgnoreCase("Summary/ Monthly Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
            			"SI. No", "Month", "Date of Uploading", "Branch Name", "Branch Code", "No. of Documents Uploaded", "No Of Pages Uploaded"	
            		};
        	} else if(reportType.equalsIgnoreCase("User Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Date of Uploading", "Department Name", "Branch Name", "Branch Code", "Uploaded By", "No. of Documents Uploaded", "No Of Pages Uploaded"	
            		};
        	} else if(reportType.equalsIgnoreCase("Department Wise Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		columnOrder = new String[] {
        				"SI. No", "Date of Uploading", "Department Name", "Branch Name", "Branch Code", "No. of Documents Uploaded", "No Of Pages Uploaded"	
            		};
        	} else if(reportType.equalsIgnoreCase("Department Wise Metadata Report")) {
        		System.out.println("I'm on the " + reportType + " Column Order");
        		switch (repositoryId) {
                case "HOIT":
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               		  "SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               	    "Branch Name", "Branch Code", "Tender No", "Date", "HO Department Code",
               	    "RFP Subject", "Eligibility Criteria", "Scope of RFP", "Project Timelines",
               	    "Terms & Condition", "General Instructions", "Evaluation Process",
               	    "Service Level and Penalties", "Bid System", "Tendering Process",
               	    "In-principal Note for Floating of RFP", "Note for Floating of RFP",
               	    "Pre-bid Queries/Response from bidder", "Bid Documents",
               	    "Note for Technical Evaluation", "Note for Commercial Evaluation",
               	    "Note for Reverse Auction", "Note for Declaring L-1 bidder",
               	    "Financial Approval for Final TCO", "Other Notes", "Notes for Floating Addendum",
               	    "Addendum No", "Addendum Dated", "Addendum Subject",
               	    "Addendum related queries/response", "Annexures No", "Annexures Subject",
               	    "Document Type", "Remark", "MEETING FOR THE MONTH OF", "MEETING DATE", "Sr.No.",
               	    "Purpose Code", "Agenda Items", "Purpose", "Reference Number", "Agenda Item No",
               	    "Action Taken Report", "ATR Date", "Note Dated", "Minutes of Meeting",
               	    "Note Type", "Date of Receipt", "Type", "Subject", "From",
               	    "Date of submission", "TO", "FINANCIAL YEAR", "FREQUENCY", "AUDIT TYPE",
               	    "Audit Date", "Audit Copy Ref.No.", "Auditor name", "YEAR", "RECEIVED FROM",
               	    "DATE OF RTI APPLICATION", "NAME OF APPLICANT", "SENT TO", "DATE OF DISPOSAL",
               	    "Remarks", "Meeting No.", "Uploaded By", "No Of Pages Uploaded"	
            		};
                    break;
                case "HOPD":
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               		    "Branch Name", "Branch Code", "Note Date", "Note Type", "HO Department Code",
               		    "Note Subject", "Note F.Y.", "Note Dispatch/Sr.No.", "Competent Authority",
               		    "IFSC CODE", "BRANCH LICENCE NO", "MICR NO", "Opening NAME", "Closing NAME",
               		    "Merging NAME", "Branch open date", "RTI DATE", "RTI NAME", "REPLY DATE",
               		    "Reply F.Y.", "Tender No.", "Date", "In-principal Note for Floating of RFP",
               		    "Pre-bid Queries/Response from bidder", "Bid Documents", "Note for Technical Evaluation",
               		    "Notes for Floating Addendum","Note for Floating RFP","Note for Commercial Evaluation","Misc notes regrading Tender","Notes for Floating Addendum", "Addendum No", "Addendum Dated", "Addendum Subject",
               		    "Addendum related queries/response", "Annexures No", "Annexures Subject", "Document Type",
               		    "Remarks", "Uploaded By", "No Of Pages Uploaded"	
            		};
                    break;
                case "CASA"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI.No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                  		    "Department Code", "Branch Name", "Branch Code", "Uploaded By",
                  		    "No Of Pages Uploaded", "Document Type","CIF",
                  		    "Account Number", "File Name", "Customer Name", "Identity Proof Of Firm",
                  		    "Address Proof Of", "Address Proof Of Firm", "Photo Identity", "Identity Proof Of", "SOLID",
                  		    "Nomination REG Number", "Address Proof Of Authorized Signature", "ID Proof Of Authorized Signatory",
                  		    "Account Open Date", "DOB", "KYC", "Staff Employee No", "Mobile", "Email ID",
                  		    "ATM Card Number", "Account Type", "ID Proof Documents", "Address Proof Documents"
               		};
                    break;
                case "HOCA"://added by vara
                 	 System.out.println("Processing the " + repositoryId + " Column Order");
                 	columnOrder = new String[] {
                 			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                 		    "Branch Name", "Branch Code", "Date", "HO Department Code", "Subject",
                 		    "Document Type", "Remark", "Uploaded By", "No Of Pages Uploaded"	
              		};
                   break;
                case "HOB"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"S.No", "Class Name", "Document Title", "Document Type", "Document Created Date", "HO Department Code",
                		    "Department Name", "Department Code", "Branch Name", "Branch Code", "Subject", "Created By",
                		    "Page Count", "Addendum Subject", "Addendum Related Queries Response", "Date", "Annexures No",
                		    "Annexures Subject", "Notes for Floating Addendum", "Committee Name", "Meeting Date", "Type of Meeting",
                		    "Department", "Meeting Date Date", "Meeting Name", "Director Name", "Date of Appointment", "Resolution Number",
                		    "Payment Date", "Payment Type", "Date of Reconstitution", "Month", "Note Subject", "Year", "Financial Year",
                		    "Tender Number", "In-Principal Note for Floating of RFP", "Prebid Queries Response from Bidder",
                		    "Bid Documents", "Note for Floating of RFP", "Note for Technical Evaluation", "Note for Commercial Evaluation",
                		    "Note for Reverse Auction", "Note for Declaring L1 Bidder", "Financial Approval for Final TCO",
                		    "Misc Notes Regarding Tender", "Addendum Number", "Addendum Dated"	
             		};
                  break;
                case "HOT"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                  		    "Branch Name", "Branch Code", "Note Date", "Note Type", "HO Department Code",
                  		    "Note Subject", "Note FY", "Note Dispatch Sr No", "Competent Authority", "Document Type",
                  		    "Reporting Date", "Reporting Period", "Report Name", "Last Date for Reporting", "Audit Date",
                  		    "Audit Type", "Audit Subject", "Audit For Period", "Audit Copy SR No", "Audit Conducted By",
                  		    "Auditor Firm If Any", "Data Receiving Submitting Date", "Data Period", "Data Details",
                  		    "Data Collected From Submitted To", "Data Rectification If Any", "Revised Data Details",
                  		    "Data Receiving Date", "Data Period Date", "Uploaded By", "No Of Pages Uploaded"	
             		};
                  break;
                case "HOATM"://added by vara
                 	 System.out.println("Processing the " + repositoryId + " Column Order");
                 	columnOrder = new String[] {
                 			 "S.No", "Class Name", "Document Title", "Date of Uploading", "Department Name", "Branch Name", "Branch Code",
                 		    "Note Date", "Note Type", "HO Department Code", "Note Subject", "Note F.Y.", "Note Dispatch/Sr.No.", 
                 		    "Competent Authority", "Audit Date", "Audit Type", "Audit Subject", "Audit For Period", "Audit Copy SR No", 
                 		    "Audit Conducted By", "Auditor Firm If Any", "Reporting Date", "Reporting Period", "Report Name", "Last Date for Reporting",
                 		    "Data Receiving Date", "Data Period", "Data Details", "Data Collected From", "Data Rectification If Any", 
                 		    "Revised Data Receiving Date", "Revised Data Details", "Tender No", "Date", "In-principal Note for Floating of RFP", 
                 		    "Pre-bid Queries/Response from bidder", "Bid Documents", "Note for Technical Evaluation", "Note for Commercial Evaluation", 
                 		    "Note for Reverse Auction", "Note for Declaring L1 Bidder", "Financial Approval for Final TCO", "Notes for Floating Addendum", 
                 		    "Addendum No", "Addendum Dated", "Addendum Subject", "Addendum related queries/response", "Annexures No", "Annexures Subject", 
                 		    "Document Type", "Remarks", "Uploaded By", "No Of Pages Uploaded"
              		};
                   break;
                case "HOCPPC"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                		    "Branch Name", "Branch Code", "PPO No", "Account No", "REF Number",
                		    "Document Type", "Month Year", "Treasury Type", "Type", "Uploaded By",
                		    "No Of Pages Uploaded"
             		};
                	break;
                case "HOCOMP"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               		 "SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               	    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
               	    "No Of Pages Uploaded", "Document Type", "File Name", "Subject", "SOPName",
               	    "SOPNumber", "Received From", "Reference Number", "Type", "Regulators", "Year",
               	    "Role Holder Department", "RARDate", "RMP DATE", "Timeline", "Date of Receipt",
               	    "Date of Compliance", "Under Power", "Date", "positionofCompliance", "Received from",
               	    "Release Date", "Visit Date", "Reply of Compliance", "SRNo", "From"
            		};
               	break;
                case "HOMI"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
               		    "Department Name", "Branch Name", "Branch Code", "Account Number", "Customer Name",
               		    "DP Client ID", "Gram of Gold Applied", "Mobile Number", "SOL ID", "Name",
               		    "Date of Birth", "Date of Proposal Form", "Document Number", "Document Type",
               		    "Nominee Name", "Note Reference Number", "Note Subject", "Note Type"
            		};
               	break;
                case "HOOL"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
               		    "Department Name", "Branch Name", "Branch Code", "Financial Year", "Region",
               		    "Subject", "Date", "Quarter"
            		};
               	break;
                case "HOPF"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Uploaded By",
                		    "No Of Pages Uploaded", "Department Name", "Branch Name", "Branch Code", "PRAN Number",
                		    "PF Code", "Name", "Date of Birth", "Date of Joining", "Date", "Case No",
                		    "Jurisdiction", "Status", "Hearing Date", "Employee Name", "Zone", "Financial Year",
                		    "Audit Type", "Auditor Name", "Department", "Trust Name", "Date of Issue", "Subject",
                		    "Relation", "Assessment Year", "Notice Subject", "Notice No", "Note Date", "Resolution No",
                		    "Amount", "Account Number", "Investment Type", "Account Name", "Issuing Date", "Maturity Date",
                		    "ROI", "Minute Number", "Meeting Date", "Minute Name", "Nomination Type", "Nominee Name",
                		    "Withdrawal Amount", "SOL ID", "Pension Account Number", "Family Pensioner's Name", "Date of Leaving",
                		    "Date of Birth", "Date of Payment", "DOR", "Retirement Type", "Trust Documents", "Trust Date",
                		    "Agenda Item No", "Agenda Item"
             		};
                  break;
                case "HOPSA"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
               		    "Department Name", "Branch Name", "Branch Code", "Document Type", "Date of Audit",
               		    "Audit Type", "Time of Audit", "Letter Type", "Date of Letter", "Subject", "Sent To",
               		    "Received From", "Legal Type", "Name of Party Vendor", "Date of Agreement/Addendum/Voucher",
               		    "Invoice/Voucher Amount", "Note Type", "Reference No", "Staff Name", "Amount",
               		    "Account Number", "Purpose", "Voucher Type", "Subsidy Name"	
            		};
                 break;
                case "HOPSAII"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                		    "Branch Name", "Branch Code", "Zone Name", "Zone Code", "Loan Account Number",
                		    "Application Number", "Customer Name", "Customer ID", "Guarantor Name", "Scheme Type",
                		    "Document Type", "Document SubType Pre-Sancation Documents", "Document SubType Post-Sancation Documents",
                		    "Uploaded By", "No Of Pages Uploaded"	
             		};
                  break;
                case "HOFOREX"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Uploaded By",
                  		    "No Of Pages Uploaded", "Department Name", "Branch Name", "Branch Code", "FY",
                  		    "Frequency", "Document Type", "Tender RFP No", "Date", "HO Department Code",
                  		    "RFP Subject", "Selected Bidders Office Note Date", "PO Date", "SLA Date", "BG Date",
                  		    "BG Amount", "Audit Type", "Audit Date", "Audit Subject", "Auditor Firm If Any",
                  		    "Data Details", "Data Receiving Date", "Data Collected from", "Data Rectification If Any",
                  		    "Revised Data Received on", "Revised Data Details", "Note Upto", "Subject", "Invoice No",
                  		    "Vendor Name", "Reporting Date", "Reporting to", "Last Date for Reporting", "Scope of RFP",
                  		    "Tender Type", "Bid System", "Initial Bidders Name", "Doc Type", "Bid Submission Date",
                  		    "Bid Query", "Bid Query Type", "Bid Query Date", "RA Amt", "RA Date", "RA Note Date",
                  		    "No of Final Vendors", "PO Date", "Addendum No", "Addendum Subject", "Addendum Date",
                  		    "Project Concluding Year"
               		};
                    break;
                case "HOGBM"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               		    "Branch Name", "Branch Code", "Account Number", "CIF", "PRAN", "Document Type",
               		    "Note Reference Number", "Account OpenDate", "NoteSubject", "PPFAcNo", "PAN",
               		    "AccountHolderName", "NomineeName", "JointHolderName", "SCSSAcNo", "SCSSCustId",
               		    "NomineeAcNo", "SOPReferenceNumber", "SOPSchemeSubject", "GirlChildName",
               		    "ParentGaurdianName", "DateofBirthofGirlChild", "GirlChildNameGuardianName",
               		    "Mobile", "Uploaded By", "No Of Pages Uploaded"
            		};
                 break;
                case "HOGA"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                		    "Branch Name", "Branch Code", "Zone Name", "Type of Meeting", "Minutes Date", "Date",
                		    "Document Type", "Subject", "Tender No", "HO Department Code",
                		    "In-principal Note for Floating of RFP", "Note for Floating RFP", "Pre-bid Queries Response",
                		    "Bid Documents", "Note for Technical Evaluation", "Note for Commercial Evaluation",
                		    "Note for Reverse Auction", "Note for Declaring L1 Bidder", "Financial Approval for Final TCO",
                		    "Notes for Floating Addendum", "Addendum No", "Addendum Dated", "Addendum Subject",
                		    "Addendum Queries Response", "Annexures No", "Annexures Subject", "Remarks",
                		    "Uploaded By", "No Of Pages Uploaded"
             		};
                  break;
                case "HOAA"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                		    "Branch Name", "Branch Code", "Date", "HODepartment Code", "Subject",
                		    "Document Type", "Remark", "Uploaded By", "No. of  Pages Uploaded"	
             		};
                  break;
                case "EDS1"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI.No", "Class Name", "Document Title", "Document Type", "Date of Uploading",
               		    "HODepartment Code", "Department Name", "Department Code", "Branch Name", "Branch Code",
               		    "Subject", "Uploaded By", "Page Count", "Diary Number", "Department", "Note Date",
               		    "MOM Date", "Date In", "Date Out", "Received Note Type", "Sent Note Type", "Approving Authority",
               		    "Approval Type", "Remarks", "Note Type", "File Name", "Received Letter Type", "Sent Letter Type",
               		    "Document Date"	
            		};
                 break;
                case "EDS2"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI.No", "Class Name", "Document Title", "Document Type", "Date of Uploading",
                  		    "HODepartment Code", "Department Name", "Department Code", "Branch Name", "Branch Code",
                  		    "Subject", "Uploaded By", "No Of Pages Uploaded", "Document Date", "Sent Letter Type",
                  		    "Received Letter Type", "Remarks", "Note Type", "Approval Type", "Approving Authority",
                  		    "File Name", "Sent Note Type", "Received Note Type", "Date Out", "Date In", "Department",
                  		    "Note Date", "MOM Date", "Diary Number"	
               		};
                    break;
                case "HOCM"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
               		    "No Of Pages Uploaded", "Document Type", "Date", "Title", "Ref Number", "Audit Report Type"
            		};
                 break;
                case "HOC"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                  		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
                  		    "No Of Pages Uploaded", "Document Type", "Resolution Number", "Title", "Date",
                  		    "Sanctioning Authority", "Type", "Periodicity", "Sub Type"
               		};
                    break;
                case "HODB"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
               		    "No Of Pages Uploaded", "Document Type", "SUBJECT", "REPORT NAME", "REPORTING DATE",
               		    "Report Type", "OFFICE NOTE NAME", "OFFICE NOTE NUMBER", "NOTE TYPE", "Note Date",
               		    "Remark", "SOP NAME", "SOP NUMBER", "RECEIVED FROM", "REFERENCE NUMBER", "Type",
               		    "Tender No.", "In-principal Note for Floating of RFP", "Note for Floating of RFP",
               		    "Pre-bid Queries/Response from bidder", "Bid Documents", "Note for Technical Evaluation",
               		    "Note fpr Commercial Evaluation", "Note for Reverse Auction", "Note for Declaring L-1 bidder",
               		    "Financial Approval for Final TCO", "Notes for Floating Addendum", "Addendum No",
               		    "Misc notes regrading Tender", "Addendum Dated", "Addendum Subject",
               		    "Addendum related queries/response", "Annexures No", "Annexures Subject", "Merchant ID",
               		    "AccountNumber", "Terminal ID", "Party Name", "Installation Date", "Product",
               		    "DATE", "Deactivation Date"	
            		};
                 break;
                case "HOFI"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                		    "Branch Name", "Branch Code", "Tender No", "Date", "HODepartment Code", "RFP Subject",
                		    "Scope Of RFP", "Project Timelines", "Terms And Conditions", "Evaluation Process",
                		    "Bid System", "Addendum No", "Addendum Subject", "Addendum Date", "Reference No",
                		    "Subject", "Staff Name", "Note Type", "Name Of Party Vendor",
                		    "Date Of Agreement/Addendum Voucher", "Invoice Voucher Amount", "Document Type",
                		    "Remarks", "Date Of Audit", "Audit Type", "Time Of Audit", "Amount", "Products",
                		    "Account Number", "Date Of Letter", "Sent To", "Letter Type", "Received From",
                		    "Uploaded By", "No Of Pages Uploaded"	
             		};
                  break;
                case "HOHRD"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               		    "Branch Name", "Branch Code", "Employee PFCode", "DoctorName", "Airlines Name",
               		    "Month", "FinancialYear", "Board ResolutionNo", "NoteType", "Date",
               		    "Subject of the Correspondence", "Issuing Department", "Designation OfficeName",
               		    "DocumentType", "Subject", "CaseType", "CourtPremises", "Case Number",
               		    "Case Reg Year", "Petitioner Applicant", "Respondent", "Advocate", "Brief",
               		    "InterimOrder", "FinalOrder", "Vakalatnama", "Affidavit", "Counter Affidavit",
               		    "Rejoinder", "Correspondence", "OldCaseType", "CaseType2", "OfficeNote", "DA",
               		    "ReplyDate", "UnionType", "UnionName", "Quarter", "IRMeetingDate", "Minutes",
               		    "Type", "SubType", "Authority", "order", "NameOfInsuranceCompany", "NameOfTPA",
               		    "PolicyYear", "RefNo", "AgendaItemNo", "MeetingDated", "AgendaItem", "Dated",
               		    "HO CircularNo", "Dept RefNo", "Subject of the Circular", "PF Code", "Current Posting",
               		    "Personal FilesName", "CircularNo", "Related Field", "Father Name", "Designation",
               		    "Subject Of Guidelines Letters", "Promotion ProcessYear", "Promotion ProcessName",
               		    "Qualification Marks", "CLEARANCES", "CHALLAN NO", "Policy Subject",
               		    "Uploaded By", "No Of Pages Uploaded"
            		};
                 break;
                case "HOI"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                		    "Branch Name", "Branch Code", "Credit Audit", "Financial Year",
                		    "Financial Year VAPT Audit", "Financial Year BoardACBNotes",
                		    "Financial Year ApplicationAudit", "Quarter", "Month", "Report Date",
                		    "Document Type", "Date", "Remark", "Reference Documents", "ZO Department",
                		    "MISC Audit", "VOR Type", "Zone Name", "CCA", "MAI", "RFP Tender",
                		    "Is Audit General Controls", "VAPT Audit", "Application Audit",
                		    "Other Audits", "Board ACB Notes", "Committee IT Advisory Board Notes",
                		    "Branch Is Audit", "Data of RBIA", "File Name", "Uploaded By",
                		    "No Of Pages Uploaded"
             		};
                  break;
                case "HOKYC"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
                  		    "Department Name", "Branch Name", "Branch Code", "Advisory Subject", "Date",
                  		    "Issued To", "Month", "Year", "Agenda No", "Board Note Subject", "Audit Type",
                  		    "Subject Ref", "Report Date", "Status", "Bill Type", "Vendor Name", "Vendor GST No",
                  		    "Bill Number", "Bill Date", "Bill Payment Date", "HO Department Code", "Institution",
                  		    "Subject", "Return Type", "Provisional Receipt No", "LEA Name", "Notice Type",
                  		    "Notice Date", "PAN No", "Aadhar No", "Account No", "Freeze Date", "Unfreeze Date",
                  		    "Due Date", "Compliance Status", "Directions", "Compliance Date"
               		};
                    break;
                case "HOLR"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               		    "Branch Name", "Branch Code", "HO Department Code", "Customer Name", "Account No",
               		    "Guarantor Name", "Customer Address", "Guarantor Address", "Date of NPA", "Securities Type",
               		    "Securities Details", "Balance Outstanding", "Document Type", "Account Name", "Security Details",
               		    "Subject Matter", "Enforcement Agent Name", "Party Name", "Account Name and Party Name",
               		    "Borrower Name", "Customer ID", "Uploaded By", "No Of Pages Uploaded"
            		};
                 break;
                case "HOPSBSEC"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               		 "SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
               	    "Department Name", "Branch Name", "Branch Code", "Note Reference Number", "Guideline Name",
               	    "Note Subject", "Date", "Note Type", "Policy Name", "Policy Version", "Policy Year",
               	    "Review Date", "SOP Name", "SOP Number"
            		};
                 break;
                case "HORL"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			 "SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
                			    "Department Name", "Branch Name", "Branch Code", "Name of Party DDA", "Date of Agreement",
                			    "Zone", "Applicant Name", "Application Number", "IFSC Code", "Loan Amount", "LOAN_AMOUNT",
                			    "Dates", "Reference Number", "Account Number", "Customer ID", "Customer Name", "Guarantee Scheme",
                			    "Invoice Dates", "Invoice No", "Voucher Dates", "Issue Date", "Certificate Date", "Office Notes",
                			    "Subject", "Invoice Date", "Invoice Number", "Date of Letter", "Sent To", "Received From",
                			    "Document SubType", "Document Type", "Loan Account Number", "Scheme Type", "Office Note",
                			    "Payment Amount", "Purpose", "Amount", "Voucher Amount", "Voucher Date"
             		};
                  break;
                case "HORM"://added by vara
                 	 System.out.println("Processing the " + repositoryId + " Column Order");
                 	columnOrder = new String[] {
                 			"Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
                 		    "Department Name", "Department Code", "Branch Name", "Branch Code",
                 		    "Note Type", "Agenda No", "Date", "Board Note Subject", "Month", "Year",
                 		    "Daily Notes", "Return RBI Notes", "Credit Notes Name", "Agenda No",
                 		    "Dates", "Board Note Subject", "Policy Reference", "Company Name", "Rating Group"
              		};
                   break;
                case "HOS"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
                  		    "Department Name", "Branch Name", "Branch Code", "Tender No", "Date", "HO Department Code",
                  		    "Notes for Floating Addendum", "Addendum No", "Addendum Dated", "Addendum Subject",
                  		    "Addendum Related Queries Response", "Annexures No", "Annexures Subject", "Date of Audit",
                  		    "Audit Type", "Document Type", "Time of Audit", "Service Name", "Service Provider", "Subject",
                  		    "Sent To", "Received From", "General Stationery", "Security", "Type", "Name of Party Vendor",
                  		    "Item Code", "Item Name", "Invoice Number", "Challan No", "Reference No", "Staff Name",
                  		    "Note Type", "Account No", "In-principal Note for Floating of RFP", "Note for Floating of RFP",
                  		    "Pre-bid Queries Response from Bidder", "Bid Documents", "Note for Technical Evaluation",
                  		    "Note for Commercial Evaluation", "Note for Reverse Auction", "Note for Declaring L1 Bidder",
                  		    "Financial Approval for Final TCO", "Miscellaneous Notes Regarding Tender", "Remarks",
                  		    "Printer Name", "Item No"
               		};
                    break;
                case "HOV"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No", "Document Title", "Date of Uploading", "Uploaded By", "No Of Pages Uploaded",
                  		    "Department Name", "Branch Name", "Branch Code", "Name of Account", "Amount in Rs (in crores)",
                  		    "Date of Reporting to RBI", "Date of Reporting to ABBFF", "Date of Complaint Lodged with CBI",
                  		    "Date of Receipt to FIAC", "Date of Reference to ABBFF", "Date of Receipt to ABBFF Advice",
                  		    "ABBFF Advice", "Remarks", "Year", "Name of Investigating Agency",
                  		    "Date of Receipt from CBI Investigating Agency", "CBIRC No", "No of Individuals for Whom Approval Sought",
                  		    "Designation Thereof", "Case Referred to ABBFF (Yes/No)", "ABBFF Advice", "Approval Granted",
                  		    "Approval Denied", "Complaint No", "Date of Complaint", "Date of Receipt of Complaint",
                  		    "Mode of Receipt", "Received Through", "Complainant Name", "Date of Approval Sent to FMD After Approval",
                  		    "Date of Complaint with CBI", "Date of FIR", "Lead Bank", "Zone", "Branch", "Name of the Tender Contract",
                  		    "Office Note Letter", "Office Note Letter Date", "Office Note Type", "Office Note Date", "Date of Receipt",
                  		    "Date of Occurrence", "Date of Deduction", "Database ID", "IAC Number", "IAC FSA Name", "Date of Minutes",
                  		    "PF Code", "SCL", "Irregularities at BO", "ZO", "DA", "Whether FSA Required", "File Number", "Nature of PP",
                  		    "Date of Charge Sheet", "Charge Sheet Issued without FSA", "Office Note Letter No", "Subject",
                  		    "Name of Investigating Agency", "CBIRC No", "No of Individuals for Whom SOP Sought", "SOP Granted",
                  		    "Case Referred to CVC (Yes/No)", "SOP Denied/Granted by CVC", "Case Referred to DFS (Yes/No)",
                  		    "SOP Denied/Granted by DFS", "QPR for Quarter", "RNO", "Folder", "Document Date", "Reporting Date",
                  		    "Reporting Period", "Report Name", "Competent Authority", "Date for Reporting", "Received From",
                  		    "Date of RTI Application", "Date of Disposal", "Name of Applicant", "Designation", "Summon On",
                  		    "Visited By VO Name", "Date of Visit"
               		};
                    break;
                case "MDSECTT"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No","Class Name","Document Title", "Date of Uploading", "Department Name", "Diary Number", "Dept Code",
                  		    "Branch Name", "Branch Code", "HO Department Code", "Uploaded By", "Date", "Received Letter Type",
                  		    "Sent Letter Type","ReceivedNoteType", "MOM Date", "Sent Note Type",
                  		    "ApprovingAuthority", "Remarks", "Note type", "Note Date", "DateOut", "Department",
                  		    "No Of Pages Uploaded", "Document Type", "DateIn"	
               		};
                    break;
                case "ZOHR"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                  		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
                  		    "No Of Pages Uploaded", "Document Type", "Date", "NAME"
               		};
                    break;
                case "ZOATM"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
                		    "No Of Pages Uploaded", "Document Type", "TRANSACTION DATE", "AMOUNT", "NOTE TYPE"
             		};
                  break;
                case "ZOGA"://added by vara
                 	 System.out.println("Processing the " + repositoryId + " Column Order");
                 	columnOrder = new String[] {
                 			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                 		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
                 		    "No Of Pages Uploaded", "Document Type", "Date", "NAME"
              		};
                   break;
                case "ZOI"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	columnOrder = new String[] {
               			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
               		    "No Of Pages Uploaded", "Document Type", "Date of AUDIT", "Financial Year"
            		};
                 break;
                case "ZOLR"://added by vara
                	 System.out.println("Processing the " + repositoryId + " Column Order");
                	columnOrder = new String[] {
                			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name", "Dept Code",
                		    "Branch Name", "Branch Code", "HO Department Code", "Uploaded By", "No Of Pages Uploaded",
                		    "Document Type", "Documents", "BILL AMOUNT", "BILL DATE", "ADVOCATE NAME", "REGION",
                		    "Enf. Agent Name", "LOAN ACCOUNT NUMBER", "CUSTOMER ID", "DATE OF NPA", "Account Number",
                		    "Borrower Name"
             		};
                  break;
                case "ZOIT"://added by vara
               	 System.out.println("Processing the " + repositoryId + " Column Order");
               	 columnOrder =new String[] {
               			    "SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
               			    "Branch Name", "Branch Code", "Account No", "Customer Id", "Borrower Name",
               			    "Document Type", "Date of NPA", "Advocate Name", "Region", "Enforcement Agent Name",
               			    "Bill Date", "Bill Amount", "Uploaded By", "No Of Pages Uploaded"
               			};

                 break;
                case "ZOLOS"://added by vara
                 	 System.out.println("Processing the " + repositoryId + " Column Order");
                 	columnOrder = new String[] {
                 			"SI. No", "Class Name","Document Title", "Date of Uploading", "Department Name", "Dept Code", "Branch Name",
                 		    "Branch Code", "HO Department Code", "Uploaded By",  "LOAN ACCOUNT NUMBER", "CUSTOMER ID", "CUSTOMER NAME", "GUARANTOR NAME", "DOCUMENT SUBTYPE",
               		      "No Of Pages Uploaded", "Document Type","SCHEME TYPE", "DOCUMENT SUBTYPE (1. POST SANCTION DOCUMENTS)", "DOCUMENT SUBTYPE (1. PRE SANCTION DOCUMENTS)",
                 		   "Documents"	
              		};
                   break;
                case "ZORAJ"://added by vara
                  	 System.out.println("Processing the " + repositoryId + " Column Order");
                  	columnOrder = new String[] {
                  			"SI. No", "Class Name", "Document Title", "Date of Uploading", "Department Name",
                  		    "Dept Code", "Branch Name", "Branch Code", "HO Department Code", "Uploaded By",
                  		    "No Of Pages Uploaded", "Document Type"	
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