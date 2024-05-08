package com.projak.psb.datacap.reports.plugin.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class UserReportUtil {
	  
	  ResultSet reportData = null;
	  public JSONArray searchSignInSignOffReport(Connection connection, String fromDate, String todate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchSignInSignOffReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", todate: " + todate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
		    try {
		    	PreparedStatement preparedStatement = connection.prepareStatement(PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_LOGIN_SELECTQUERY"));
		         preparedStatement.setString(1, fromDate);
		         preparedStatement.setString(2, todate);
		         preparedStatement.setString(3, repositoryId);
		      this.reportData = preparedStatement.executeQuery();
		      System.out.println("reportData ===> " + this.reportData.toString());
		      
		      int serialNumber = 1;
		      
		      while (this.reportData.next()) {
		    	  System.out.println("Adding the Report Data into Variables...");
		    	  String repoName = this.reportData.getString("REPOSITORYID");
	              String userName = this.reportData.getString("USER_NAME");
	              String branchName = this.reportData.getString("BRANCH_NAME");
	                String branchCode = this.reportData.getString("BRANCH_CODE");
	                String loginTime = this.reportData.getString("LOGONDATE");
	                String logoutTime = this.reportData.getString("LOGOUTDATE");
	                String userItAddress = this.reportData.getString("USER_IT_ADDRESS");
	                String event = this.reportData.getString("EVENT");
	                
	                loginTime = formatLogonOutDate(loginTime);
	                logoutTime = formatLogonOutDate(logoutTime);
	                
	                System.out.println("---------- Adding the Data into JSONObject ----------");
	                
		        JSONObject jsonObject = new JSONObject();
		        jsonObject.put("SI. No", serialNumber);
		        jsonObject.put("Repository Name", repoName);
		        jsonObject.put("User Name", userName);
		        jsonObject.put("Branch Name", branchName);
		        jsonObject.put("Branch Code", branchCode);
		        jsonObject.put("Login DateTime", loginTime);
		        jsonObject.put("Logout DateTime", logoutTime);
		        jsonObject.put("IP Address", userItAddress);
		        jsonObject.put("Event Name", event);
		        
		        jsonArray.add(jsonObject);
		        serialNumber++;
		        System.out.println("---------- Data was successfully added into the JSONObject ----------");
		      } 
		      System.out.println("jsonArray::: " + jsonArray.toString());
		    } catch (Exception e) {
		      e.printStackTrace();
		      System.out.println("Error:: "+ e.getMessage());
		    } finally {
		      try {
		        connection.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		        System.out.println("Error in Closing the Connection:: " + e.getMessage() + e);
		      } 
		      try {
		        this.reportData.close();
		      } catch (SQLException e) {
		        e.printStackTrace();
		        System.out.println("Error in Closing the reportData:: " + e.getMessage() + e);
		      } 
		    } 
		    return jsonArray;
		  }
	  public JSONArray searchAuditLogReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchAuditLogReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
		    try {
		    	PreparedStatement preparedStatement = connection.prepareStatement(PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_AUDIT_SELECTQUERY"));
		        preparedStatement.setString(1, fromDate);
		        preparedStatement.setString(2, toDate);
		        preparedStatement.setString(3, repositoryId);

		        this.reportData = preparedStatement.executeQuery();
		        int serialNumber = 1;
		        System.out.println("reportData ===> " + this.reportData.toString());

		        while (this.reportData.next()) {
		        	System.out.println("Adding the Report Data into Variables...");
		            String repoName = this.reportData.getString("REPOSITORY_ID");
		            String userName = this.reportData.getString("USER_NAME");
		            String branchName = this.reportData.getString("BRANCH_NAME");
		            String branchCode = this.reportData.getString("BRANCH_CODE");
		            String eventType = this.reportData.getString("EVENT_TYPE");
		            String recordTime = this.reportData.getString("RECORD_DATETIME");
		            String fileName = this.reportData.getString("FILENAME_DIRECTORY");
		            String eventStatus = this.reportData.getString("STATUS_OF_THE_ACTIVITY");
		            String ipAddress = this.reportData.getString("IP_ADDRESS");
		            
		            recordTime = formatRecordDate(recordTime);
		            
	                String formattedFileName = formatFileNameValue(fileName);
	                System.out.println("FileNameAfter:: "+formattedFileName);
	                System.out.println("---------- Adding the Data into JSONObject ----------");
		            JSONObject jsonObject = new JSONObject();
		            jsonObject.put("SI. No", serialNumber);
		            jsonObject.put("Repository Name", repoName);
		            jsonObject.put("User Name", userName);
		            jsonObject.put("Branch Name", branchName);
		            jsonObject.put("Branch Code", branchCode);
		            jsonObject.put("Record DateTime", recordTime);
		            jsonObject.put("File Name", formattedFileName);
		            jsonObject.put("Event Type", eventType);
		            jsonObject.put("Status of The Activity", eventStatus);
		            jsonObject.put("IP Address", ipAddress);

		            jsonArray.add(jsonObject);
		            serialNumber++;
		            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		    }
		    return jsonArray;
		}
	  
	  public JSONArray searchDailyReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDailyReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
		    PreparedStatement preparedStatement = null;
		    
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_DAILY_SELECTQUERY_" + repositoryId);
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            int serialNumber = 1;
		            System.out.println("reportData ===> " + this.reportData.toString());

		            while (this.reportData.next()) {
		            	System.out.println("Adding the Report Data into Variables...");
		                String dateOfUploading = this.reportData.getString("DateOfUploading");
		                String branchName = this.reportData.getString("BranchName");
		                String branchCode = this.reportData.getString("BranchCode");
		                String noOfDocumentsUploaded = this.reportData.getString("NoOfDocumentsUploaded");
		                int pageCount = this.reportData.getInt("NoOfPagesUploaded");
		                
		                dateOfUploading = formatDate(dateOfUploading);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
		                JSONObject jsonObject = new JSONObject();
		                jsonObject.put("SI. No", serialNumber);
		                jsonObject.put("Date of Uploading", dateOfUploading);
		                jsonObject.put("Branch Name", branchName);
		                jsonObject.put("Branch Code", branchCode);
		                jsonObject.put("No. of Documents Uploaded", noOfDocumentsUploaded);
		                jsonObject.put("No. of Pages Uploaded", pageCount);

		                jsonArray.add(jsonObject);
		                serialNumber++;
		                System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId:: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  public JSONArray searchDMSDailyReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDMSDailyReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
		    PreparedStatement preparedStatement = null;
		    
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString("DATACAP_DMS_REPORT_DB_DAILYREPORT_SELECTQUERY_" + repositoryId);
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            int serialNumber = 1;
		            System.out.println("reportData ===> " + this.reportData.toString());

		            while (this.reportData.next()) {
		            	System.out.println("Adding the Report Data into Variables...");
		            	String dateOfUploading = this.reportData.getString("Date of Uploading");
		                String departmentName = this.reportData.getString("Department Name");
		                String classificationName = this.reportData.getString("Classification/Subclassification Name");
		                String ownerName = this.reportData.getString("Owner/Uploaded Name");
		                int noOfDocumentsUploaded = this.reportData.getInt("NoOfDocumentsUploaded");

		                // Do something with the retrieved data, e.g., print it
		                System.out.println("Date of Uploading: " + dateOfUploading);
		                System.out.println("Department Name: " + departmentName);
		                System.out.println("Classification/Subclassification Name: " + classificationName);
		                System.out.println("Owner/Uploaded Name: " + ownerName);
		                System.out.println("NoOfDocumentsUploaded: " + noOfDocumentsUploaded);
		            
		                
		                dateOfUploading = formatDate(dateOfUploading);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
		                JSONObject jsonObject = new JSONObject();
		                jsonObject.put("SI. No", serialNumber);
		                jsonObject.put("Date of Uploading", dateOfUploading);
		                jsonObject.put("Department Name", departmentName);
		                jsonObject.put("Classification/Subclassification Name", classificationName);
		                jsonObject.put("Owner/Uploaded Name", ownerName);
		                jsonObject.put("NoOfDocumentsUploaded", noOfDocumentsUploaded);

		                jsonArray.add(jsonObject);
		                serialNumber++;
		                System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId:: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  public JSONArray searchDMSMonthlyReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDMSMonthlyReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
		    PreparedStatement preparedStatement = null;
		    
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString("DATACAP_DMS_REPORT_DB_MONTHLYREPORT_SELECTQUERY_" + repositoryId);
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            int serialNumber = 1;
		            System.out.println("reportData ===> " + this.reportData.toString());

		            while (this.reportData.next()) {
		            	String month = this.reportData.getString("Month");
		                String dateOfUploading = this.reportData.getString("Date_Of_Uploading");
		                String departmentName = this.reportData.getString("Department_Name");
		                int noOfDocumentsUploaded = this.reportData.getInt("No_Of_Documents_Uploaded");
		                int noOfPagesUploaded = this.reportData.getInt("No_Of_Pages_Uploaded");


		                // Do something with the retrieved data, e.g., print it
		                System.out.println("Month: " + month);
		                System.out.println("Date of Uploading: " + dateOfUploading);
		                System.out.println("Department Name: " + departmentName);
		                System.out.println("No Of Documents Uploaded: " + noOfDocumentsUploaded);
		                System.out.println("No Of Pages Uploaded: " + noOfPagesUploaded);
		            
		                
		                dateOfUploading = formatDate(dateOfUploading);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
		                JSONObject jsonObject = new JSONObject();
		                jsonObject.put("SI. No", serialNumber);
		                jsonObject.put("Month", month);
		                jsonObject.put("Date_Of_Uploading", dateOfUploading);
		                jsonObject.put("Department_Name", departmentName);
		                jsonObject.put("No_Of_Documents_Uploaded", noOfDocumentsUploaded);
		                jsonObject.put("No_Of_Pages_Uploaded", noOfPagesUploaded);

		                jsonArray.add(jsonObject);
		                serialNumber++;
		                System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId:: " + repositoryId);
		        }
		    } catch (Exception e) {
		    	System.out.println("SQLException Occured: "+e.getMessage());
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  public JSONArray searchDatacapDailyReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDatacapDailyReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
		    PreparedStatement preparedStatement = null;
		    
		    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
            String dcStartDate="";
            String dcEndDate = "";
            try {
                // Parse the input date string
                Date inputDate = inputFormat.parse(fromDate);
                Date inputDate1 = inputFormat.parse(fromDate);

                // Format the date in the desired output format
                dcStartDate = outputFormat.format(inputDate).toUpperCase();
                dcEndDate = outputFormat.format(inputDate1).toUpperCase();

                // Print the formatted date
                System.out.println("Datacap Date: "+dcStartDate+"\t"+dcEndDate); // Output: 22-SEP-23
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString(repositoryId+"_DATACAP_DAILYREPORT_SELECTQUERY");
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, dcStartDate);
		            preparedStatement.setString(2, dcEndDate);
		            this.reportData = preparedStatement.executeQuery();
		            int serialNumber = 1;
		            System.out.println("reportData ===> " + this.reportData.toString());

		            while (this.reportData.next()) {
		            	System.out.println("Adding the Report Data into Variables...");
		            	 String date = this.reportData.getString("date_of_uploading");
		                 int onHoldCount = this.reportData.getInt("no_of_documents_on_hold");
		                 int rejectedCount = this.reportData.getInt("no_of_rejected_documents");
		                 int verifierCount = this.reportData.getInt("no_of_documents_at_verifier");

		                 // Process or print the retrieved data as needed
		                 System.out.println("Date: " + date + ", On Hold: " + onHoldCount +
		                                    ", Rejected: " + rejectedCount + ", At Verifier: " + verifierCount);
		             
		                
		               // dateOfUploading = formatDate(dateOfUploading);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
		                JSONObject jsonObject = new JSONObject();
		                jsonObject.put("SI. No", serialNumber);
		                jsonObject.put("Date of Uploading", date);
		                jsonObject.put("No. of Documents on Hold", onHoldCount);
		                jsonObject.put("No. of Rejected Documents", rejectedCount);
		                jsonObject.put("No. of Documents at Verifier", verifierCount);

		                jsonArray.add(jsonObject);
		                serialNumber++;
		                System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId:: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  public JSONArray searchMonthlyReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchMonthlyReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
            PreparedStatement preparedStatement = null;
		    
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_MONTHLY_SELECTQUERY_" + repositoryId);
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            System.out.println("reportData ===> " + this.reportData.toString());
		            
		            int serialNumber = 1;

		            while (this.reportData.next()) {
		            	System.out.println("Adding the Report Data into Variables...");
		            	String month = this.reportData.getString("Month");
			            String dateOfUploading = this.reportData.getString("DateOfUploading");
			            String branchName = this.reportData.getString("BranchName");
			            String branchCode = this.reportData.getString("BranchCode");
			            String noOfDocumentsUploaded = this.reportData.getString("NoOfDocumentsUploaded");
			            int pageCount = this.reportData.getInt("NoOfPagesUploaded");
			            
			            dateOfUploading = formatDate(dateOfUploading);
			            
			            //
			            System.out.println("Month: " + month);
			            System.out.println("Date Of Uploading: " + dateOfUploading);
		                System.out.println("Branch Name: " + branchName);
		                System.out.println("Branch Code: " + branchCode);
		                System.out.println("No. of Documents Uploaded: " + noOfDocumentsUploaded);
		                System.out.println("No. of Pages Uploaded: " + pageCount);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
			            JSONObject jsonObject = new JSONObject();
			            jsonObject.put("SI. No", serialNumber);
			            jsonObject.put("Month", month);
			            jsonObject.put("Date of Uploading", dateOfUploading);
			            jsonObject.put("Branch Name", branchName);
			            jsonObject.put("Branch Code", branchCode);
			            jsonObject.put("No. of Documents Uploaded", noOfDocumentsUploaded);
			            jsonObject.put("No. of Pages Uploaded", pageCount);

			            jsonArray.add(jsonObject);
			            serialNumber++;
			            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  public JSONArray searchDatacapSummaryReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDatacapSummaryReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
            PreparedStatement preparedStatement = null;
		    
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
            String dcStartDate="";
            String dcEndDate="";
            try {
                // Parse the input date string
                Date inputDate = inputFormat.parse(fromDate);
                Date inputDate1 = inputFormat.parse(toDate);

                // Format the date in the desired output format
                dcStartDate = outputFormat.format(inputDate).toUpperCase();
                dcEndDate = outputFormat.format(inputDate1).toUpperCase();

                // Print the formatted date
                System.out.println("Datacap Date: "+dcStartDate+"\t"+dcEndDate); // Output: 22-SEP-23
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString(repositoryId+"_DATACAP_SUMMARYREPORT_SELECTQUERY");
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, dcStartDate);
		            preparedStatement.setString(2, dcEndDate);
		            this.reportData = preparedStatement.executeQuery();
		            System.out.println("reportData ===> " + this.reportData.toString());
		            
		            int serialNumber = 1;

		            while (this.reportData.next()) {
		            	System.out.println("Adding the Report Data into Variables...");
		            	// Retrieve data from the result set
		                String date = this.reportData.getString("DATE");
		                String deptCode = this.reportData.getString("PB_DEPTCODE");
		                String task = this.reportData.getString("QU_TASK");
		                String status = this.reportData.getString("QU_STATUS");
		                int batches = this.reportData.getInt("Batches");

		                // Process or print the retrieved data as needed
		                System.out.println("Date: " + date + ", Dept Code: " + deptCode +
		                                   ", Task: " + task + ", Status: " + status +
		                                   ", Batches: " + batches);
			            
			            //
			            System.out.println("date: " + date);
			            System.out.println("deptCode: " + deptCode);
		                System.out.println("task: " + task);
		                System.out.println("status: " + status);
		                System.out.println("Batches " + batches);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
			            JSONObject jsonObject = new JSONObject();
			            jsonObject.put("SI. No", serialNumber);
			            jsonObject.put("DATE", date);
			            jsonObject.put("PB_DEPTCODE", deptCode);
			            jsonObject.put("QU_TASK", task);
			            jsonObject.put("QU_STATUS", status);
			            jsonObject.put("Batches", batches);

			            jsonArray.add(jsonObject);
			            serialNumber++;
			            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  public JSONArray searchDatacapMonthlyReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDatacapMonthlyReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
            PreparedStatement preparedStatement = null;
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
            String dcFromDate="";
            String dcToDate="";
            try {
                // Parse the input date string
                Date inputDate = inputFormat.parse(fromDate);
                Date outDate = inputFormat.parse(toDate);
                // Format the date in the desired output format
                dcFromDate = outputFormat.format(inputDate).toUpperCase();
                dcToDate = outputFormat.format(outDate).toUpperCase();
                // Print the formatted date
                System.out.println("Datacap Date: "+dcFromDate+"\t"+dcToDate); // Output: 22-SEP-23
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString(repositoryId+"_DATACAP_MONTHLYREPORT_SELECTQUERY");
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, dcFromDate);
		            preparedStatement.setString(2, dcToDate);
		            this.reportData = preparedStatement.executeQuery();
		            System.out.println("reportData ===> " + this.reportData.toString());
		            int serialNumber = 1;

		            while (this.reportData.next()) {
		            	   // Retrieve data from the result set
		                String month = this.reportData.getString("month");
		                String dateRange = this.reportData.getString("date_of_uploading");
		                int onHoldCount = this.reportData.getInt("no_of_documents_on_hold");
		                int rejectedCount = this.reportData.getInt("no_of_rejected_documents");
		                int verifierCount = this.reportData.getInt("no_of_documents_at_verifier");

		                // Process or print the retrieved data as needed
		                System.out.println("Month: " + month + ", Date Range: " + dateRange +
		                                   ", On Hold: " + onHoldCount + ", Rejected: " + rejectedCount +
		                                   ", At Verifier: " + verifierCount);
		            
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
			            JSONObject jsonObject = new JSONObject();
			            jsonObject.put("SI. No", serialNumber);
			            jsonObject.put("Month", month);
			            jsonObject.put("Date of Uploading", dateRange);
			            jsonObject.put("No. of Documents on Hold", onHoldCount);
			            jsonObject.put("No. of Rejected Documents", rejectedCount);
			            jsonObject.put("No. of Documents at Verifier", verifierCount);
	
			            jsonArray.add(jsonObject);
			            serialNumber++;
			            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  public JSONArray searchUserReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchUserReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
            PreparedStatement preparedStatement = null;
		    
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_USER_SELECTQUERY_" + repositoryId);
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            System.out.println("reportData ===> " + this.reportData.toString());
		            int serialNumber = 1;

		            while (this.reportData.next()) {
		            	String dateOfUploading = this.reportData.getString("DateOfUploading");
			            String deptName = this.reportData.getString("DepartmentName");
			            String branchName = this.reportData.getString("BranchName");
			            String branchCode = this.reportData.getString("BranchCode");
			            String uploadedBy = this.reportData.getString("UploadedBy");
			            String noOfDocumentsUploaded = this.reportData.getString("NoOfDocumentsUploaded");
			            int pageCount = this.reportData.getInt("NoOfPagesUploaded");
			            
			            dateOfUploading = formatDate(dateOfUploading);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
			            JSONObject jsonObject = new JSONObject();
			            jsonObject.put("SI. No", serialNumber);
			            jsonObject.put("Date of Uploading", dateOfUploading);
			            jsonObject.put("Department Name", deptName);
			            jsonObject.put("Branch Name", branchName);
			            jsonObject.put("Branch Code", branchCode);
			            jsonObject.put("Uploaded By", uploadedBy);
			            jsonObject.put("No. of Documents Uploaded", noOfDocumentsUploaded);
			            jsonObject.put("No. of Pages Uploaded", pageCount);
	
			            jsonArray.add(jsonObject);
			            serialNumber++;
			            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  
	  public JSONArray searchDepartmentWiseReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDepartmentWiseReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
            PreparedStatement preparedStatement = null;
		    
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_DEPTWISE_SELECTQUERY_" + repositoryId);
		        
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            System.out.println("reportData ===> " + this.reportData.toString());
		            int serialNumber = 1;

		            while (this.reportData.next()) {
		            	String dateOfUploading = this.reportData.getString("DateOfUploading");
			            String deptName = this.reportData.getString("DepartmentName");
			            String branchName = this.reportData.getString("BranchName");
			            String branchCode = this.reportData.getString("BranchCode");
			            String noOfDocumentsUploaded = this.reportData.getString("NoOfDocumentsUploaded");
			            int pageCount = this.reportData.getInt("NoOfPagesUploaded");
			            
			            dateOfUploading = formatDate(dateOfUploading);
			            
			            //
			            System.out.println("Date Of Uploading: " + dateOfUploading);
		                System.out.println("Department Name: " + deptName);
		                System.out.println("Branch Name: " + branchName);
		                System.out.println("Branch Code: " + branchCode);
		                System.out.println("No. of Documents Uploaded: " + noOfDocumentsUploaded);
		                System.out.println("No. of Pages Uploaded: " + pageCount);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
			            JSONObject jsonObject = new JSONObject();
			            jsonObject.put("SI. No", serialNumber);
			            jsonObject.put("Date of Uploading", dateOfUploading);
			            jsonObject.put("Department Name", deptName);
			            jsonObject.put("Branch Name", branchName);
			            jsonObject.put("Branch Code", branchCode);
			            jsonObject.put("No. of Documents Uploaded", noOfDocumentsUploaded);
			            jsonObject.put("No. of Pages Uploaded", pageCount);
	
			            jsonArray.add(jsonObject);
			            serialNumber++;
			            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    
		    return jsonArray;
		}
	  
	  public JSONArray searchDepartmentWiseMetadataReport(Connection connection, String fromDate, String toDate, String repositoryId) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDepartmentWiseMetadataReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		    JSONArray jsonArray = new JSONArray();
            PreparedStatement preparedStatement = null;
		    
		    try {
		        String query = "";
		        query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_DEPTWISEMETADATA_SELECTQUERY_" + repositoryId);
		        System.out.println("Metadate Query: "+query);
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            System.out.println("reportData ===> " + this.reportData.toString());
		            int serialNumber = 1;

		            while (this.reportData.next()) {
		            	
		            	if(repositoryId.equalsIgnoreCase("HOIT")) {
		            		System.out.println("::: In the HOIT reportData :::");
		            		String className = this.reportData.getString("ClassName");
		            		String documentTitle = this.reportData.getString("DocumentTitle");
			            	String dateOfUploading = this.reportData.getString("DateOfUploading");
				            String deptName = this.reportData.getString("DepartmentName");
				            String branchName = this.reportData.getString("BranchName");
				            String branchCode = this.reportData.getString("BranchCode");
				            String tenderNo = this.reportData.getString("TenderNo");
				            String date = this.reportData.getString("HOIT_Date");
				            String hODepartmentCode = this.reportData.getString("HODepartmentCode");
				            String rFPSubject = this.reportData.getString("RFPSubject");
				            String eligibilityCriteria = this.reportData.getString("EligibilityCriteria");
				            String scopeofRFP = this.reportData.getString("ScopeofRFP");
				            String projectTimelines = this.reportData.getString("ProjectTimelines");
				            String termsCondition = this.reportData.getString("TermsCondition");
				            String generalInstructions = this.reportData.getString("GeneralInstructions");
				            String evaluationProcess = this.reportData.getString("EvaluationProcess");
				            String serviceLevelandPenalties = this.reportData.getString("ServiceLevelandPenalties");
				            String bidSystem = this.reportData.getString("BidSystem");
				            String tenderingProcess = this.reportData.getString("TenderingProcess");
				            String inprincipalNoteforFloatingofRFP = this.reportData.getString("InprincipalNoteforFloatingofRFP");
				            String noteforFloatingofRFP = this.reportData.getString("NoteforFloatingofRFP");
				            String prebidQueriesResponsefrombidder = this.reportData.getString("PrebidQueriesResponsefrombidder");
				            String bidDocuments = this.reportData.getString("BidDocuments");
				            String noteforTechnicalEvaluation = this.reportData.getString("NoteforTechnicalEvaluation");
				            String noteforCommercialEvaluation = this.reportData.getString("NoteforCommercialEvaluation");
				            String noteforReverseAuction = this.reportData.getString("NoteforReverseAuction");
				            String noteforDeclaringL1bidder = this.reportData.getString("NoteforDeclaringL1bidder");
				            String financialApprovalforFinalTCO = this.reportData.getString("FinancialApprovalforFinalTCO");
				            String miscnotesregradingTender = this.reportData.getString("MiscnotesregradingTender");
				            String notesforFloatingAddendum = this.reportData.getString("NotesforFloatingAddendum");
				            String addendumNo = this.reportData.getString("AddendumNo");
				            String addendumDated = this.reportData.getString("AddendumDated");
				            String addendumSubject = this.reportData.getString("AddendumSubject");
				            String addendumrelatedqueriesresponse = this.reportData.getString("Addendumrelatedqueriesresponse");
				            String annexuresNo = this.reportData.getString("AnnexuresNo");
				            String annexuresSubject = this.reportData.getString("AnnexuresSubject");
				            String documentType = this.reportData.getString("DocumentType");
				            String remark = this.reportData.getString("Remark");
				            String mEETINGFORTHEMONTHOF = this.reportData.getString("MEETINGFORTHEMONTHOF");
				            String mEETINGDATE = this.reportData.getString("MEETINGDATE");
				            String srNo = this.reportData.getString("SrNo");
				            String purposeCode = this.reportData.getString("PurposeCode");
				            String agendaItems = this.reportData.getString("AgendaItems");
				            String purpose = this.reportData.getString("Purpose");
				            String referenceNumber = this.reportData.getString("ReferenceNumber");
				            String agendaItemNo = this.reportData.getString("AgendaItemNo");
				            String actionTakenReport = this.reportData.getString("ActionTakenReport");
				            String aTRDate = this.reportData.getString("ATRDate");
				            String noteDated = this.reportData.getString("NoteDated");
				            String minutesofMeeting = this.reportData.getString("MinutesofMeeting");
				            String noteType = this.reportData.getString("NoteType");
				            String dateofReceipt = this.reportData.getString("DateofReceipt");
				            String type = this.reportData.getString("Type");
				            String subject = this.reportData.getString("Subject");
				            String from = this.reportData.getString("RL_From");
				            String dateofsubmission = this.reportData.getString("Dateofsubmission");
				            String to = this.reportData.getString("RL_TO");
				            String fINANCIALYEAR = this.reportData.getString("FINANCIALYEAR");
				            String fREQUENCY = this.reportData.getString("FREQUENCY");
				            String aUDITTYPE = this.reportData.getString("AUDITTYPE");
				            String auditDate = this.reportData.getString("AuditDate");
				            String auditCopyRefNo = this.reportData.getString("AuditCopyRefNo");
				            String auditorname = this.reportData.getString("Auditorname");
				            String yEAR = this.reportData.getString("YEAR");
				            String rECEIVEDFROM = this.reportData.getString("RECEIVEDFROM");
				            String dATEOFRTIAPPLICATION = this.reportData.getString("DATEOFRTIAPPLICATION");
				            String nAMEOFAPPLICANT = this.reportData.getString("NAMEOFAPPLICANT");
				            String sENTTO = this.reportData.getString("SENTTO");
				            String dATEOFDISPOSAL = this.reportData.getString("DATEOFDISPOSAL");
				            String remarks = this.reportData.getString("Remarks");
				            String meetingNo = this.reportData.getString("MeetingNo");
				            String uploadedBy = this.reportData.getString("UploadedBy");
				            int pageCount = this.reportData.getInt("NoOfPagesUploaded");
				            
				            dateOfUploading = formatDate(dateOfUploading);
				            date = formatCommonDate(date);
				            addendumDated = formatCommonDate(addendumDated);
				            mEETINGDATE = formatCommonDate(mEETINGDATE);
				            aTRDate = formatCommonDate(aTRDate);
				            noteDated = formatCommonDate(noteDated);
				            dateofReceipt = formatCommonDate(dateofReceipt);
				            dateofsubmission = formatCommonDate(dateofsubmission);
				            auditDate = formatCommonDate(auditDate);
				            dATEOFRTIAPPLICATION = formatCommonDate(dATEOFRTIAPPLICATION);
				            dATEOFDISPOSAL = formatCommonDate(dATEOFDISPOSAL);
			                
			                System.out.println("---------- Adding the Data into JSONObject ----------");
				            JSONObject jsonObject = new JSONObject();
				            jsonObject.put("SI. No", serialNumber);
				            jsonObject.put("Class Name", className);
				            jsonObject.put("Document Title", documentTitle);
				            jsonObject.put("Date of Uploading", dateOfUploading);
				            jsonObject.put("Department Name", deptName);
				            jsonObject.put("Branch Name", branchName);
				            jsonObject.put("Branch Code", branchCode);
				            jsonObject.put("Tender No", tenderNo);
				            jsonObject.put("Date", date);
				            jsonObject.put("HO Department Code", hODepartmentCode);
				            jsonObject.put("RFP Subject", rFPSubject);
				            jsonObject.put("Eligibility Criteria", eligibilityCriteria);
				            jsonObject.put("Scope of RFP", scopeofRFP);
				            jsonObject.put("Project Timelines", projectTimelines);
				            jsonObject.put("Terms & Condition", termsCondition);
				            jsonObject.put("General Instructions", generalInstructions);
				            jsonObject.put("Evaluation Process", evaluationProcess);
				            jsonObject.put("Service Level and Penalties", serviceLevelandPenalties);
				            jsonObject.put("Bid System", bidSystem);
				            jsonObject.put("Tendering Process", tenderingProcess);
				            jsonObject.put("In-principal Note for Floating of RFP", inprincipalNoteforFloatingofRFP);
				            jsonObject.put("Note for Floating of RFP", noteforFloatingofRFP);
				            jsonObject.put("Pre-bid Queries/Response from bidder", prebidQueriesResponsefrombidder);
				            jsonObject.put("Bid Documents", bidDocuments);
				            jsonObject.put("Note for Technical Evaluation", noteforTechnicalEvaluation);
				            jsonObject.put("Note for Commercial Evaluation", noteforCommercialEvaluation);
				            jsonObject.put("Note for Reverse Auction", noteforReverseAuction);
				            jsonObject.put("Note for Declaring L-1 bidder", noteforDeclaringL1bidder);
				            jsonObject.put("Financial Approval for Final TCO", financialApprovalforFinalTCO);
				            jsonObject.put("Misc notes regrading Tender", miscnotesregradingTender);
				            jsonObject.put("Notes for Floating Addendum", notesforFloatingAddendum);
				            jsonObject.put("Addendum No", addendumNo);
				            jsonObject.put("Addendum Dated", addendumDated);
				            jsonObject.put("Addendum Subject", addendumSubject);
				            jsonObject.put("Addendum related queries/response", addendumrelatedqueriesresponse);
				            jsonObject.put("Annexures No", annexuresNo);
				            jsonObject.put("Annexures Subject", annexuresSubject);
				            jsonObject.put("Document Type", documentType);
				            jsonObject.put("Remark", remark);
				            jsonObject.put("MEETING FOR THE MONTH OF", mEETINGFORTHEMONTHOF);
				            jsonObject.put("MEETING DATE", mEETINGDATE);
				            jsonObject.put("Sr.No.", srNo);
				            jsonObject.put("Purpose Code", purposeCode);
				            jsonObject.put("Agenda Items", agendaItems);
				            jsonObject.put("Purpose", purpose);
				            jsonObject.put("Reference Number", referenceNumber);
				            jsonObject.put("Agenda Item No", agendaItemNo);
				            jsonObject.put("Action Taken Report", actionTakenReport);
				            jsonObject.put("ATR Date", aTRDate);
				            jsonObject.put("Note Dated", noteDated);
				            jsonObject.put("Minutes of Meeting", minutesofMeeting);
				            jsonObject.put("Note Type", noteType);
				            jsonObject.put("Date of Receipt", dateofReceipt);
				            jsonObject.put("Type", type);
				            jsonObject.put("Subject", subject);
				            jsonObject.put("From", from);
				            jsonObject.put("Date of submission", dateofsubmission);
				            jsonObject.put("TO", to);
				            jsonObject.put("FINANCIAL YEAR", fINANCIALYEAR);
				            jsonObject.put("FREQUENCY", fREQUENCY);
				            jsonObject.put("AUDIT TYPE", aUDITTYPE);
				            jsonObject.put("Audit Date", auditDate);
				            jsonObject.put("Audit Copy Ref.No.", auditCopyRefNo);
				            jsonObject.put("Auditor name", auditorname);
				            jsonObject.put("YEAR", yEAR);
				            jsonObject.put("RECEIVED FROM", rECEIVEDFROM);
				            jsonObject.put("DATE OF RTI APPLICATION", dATEOFRTIAPPLICATION);
				            jsonObject.put("NAME OF APPLICANT", nAMEOFAPPLICANT);
				            jsonObject.put("SENT TO", sENTTO);
				            jsonObject.put("DATE OF DISPOSAL", dATEOFDISPOSAL);
				            jsonObject.put("Remarks", remarks);
				            jsonObject.put("Meeting No.", meetingNo);
				            jsonObject.put("Uploaded By", uploadedBy);
				            jsonObject.put("No. of  Pages Uploaded", pageCount);
		
				            jsonArray.add(jsonObject);
				            serialNumber++;
				            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            	} else if(repositoryId.equalsIgnoreCase("HOPD")) {
		            		System.out.println("::: In the HOPD reportData :::");
						String className = this.reportData.getString("ClassName");
						String documentTitle = this.reportData.getString("DocumentTitle");
						String dateOfUploading = this.reportData.getString("DateOfUploading");
						String deptName = this.reportData.getString("DepartmentName");
						String branchName = this.reportData.getString("BranchName");
						String branchCode = this.reportData.getString("BranchCode");
						String noteDate = this.reportData.getString("NoteDate");
						String noteType = this.reportData.getString("NoteType");
						String hODepartmentCode = this.reportData.getString("HODepartmentCode");
						String noteSubject = this.reportData.getString("NoteSubject");
						String noteFY = this.reportData.getString("NoteFY");
						String noteDispatchSrNo = this.reportData.getString("NoteDispatchSrNo");
						String competentAuthority = this.reportData.getString("CompetentAuthority");
						String iFSCCode = this.reportData.getString("IFSCCode");
						String branchLicenceNo = this.reportData.getString("BranchLicenceNo");
						String mICRNo = this.reportData.getString("MICRNo");
						String openingName = this.reportData.getString("OpeningName");
						String closingName = this.reportData.getString("ClosingName");
						String mergingName = this.reportData.getString("MergingName");
						String branchOpenDate = this.reportData.getString("BranchOpenDate");
						String rTIDate = this.reportData.getString("RTIDate");
						String rTIName = this.reportData.getString("RTIName");
						String replyDate = this.reportData.getString("ReplyDate");
						String replyFY = this.reportData.getString("ReplyFY");
						String tenderNo = this.reportData.getString("TenderNo");
						String dateHOPD = this.reportData.getString("Date_");
						String inPrincipalNoteforFloatingofRFP = this.reportData.getString("InPrincipalNoteforFloatingofRFP");
						String preBidQueries = this.reportData.getString("PreBidQueries");
						String bidDocuments = this.reportData.getString("BidDocuments");
						String noteForTechnical = this.reportData.getString("NoteForTechnical");
						String noteForFloatingAddendum = this.reportData.getString("NotesForFloatingAddendum");
						String addendumNo = this.reportData.getString("AddendumNo");
						String addendumDated = this.reportData.getString("AddendumDated");
						String addendumsubject = this.reportData.getString("Addendumsubject");
						String addendumRelatedQueries = this.reportData.getString("AddendumRelatedQueries");
						String annexuresNo = this.reportData.getString("AnnexuresNo");
						String annexuresSubject = this.reportData.getString("AnnexuresSubject");
						String documentType = this.reportData.getString("DocumentType");
						String remarks = this.reportData.getString("Remarks");
						String uploadedBy = this.reportData.getString("UploadedBy");
						int pageCount = this.reportData.getInt("NoOfPagesUploaded");
		            		
				            
				            dateOfUploading = formatDate(dateOfUploading);
				            branchOpenDate = formatCommonDate(branchOpenDate);
				            noteDate = formatCommonDate(noteDate);
				            rTIDate = formatCommonDate(rTIDate);
				            replyDate = formatCommonDate(replyDate);
				            dateHOPD = formatCommonDate(dateHOPD);
				            addendumDated = formatCommonDate(addendumDated);
			                
			                System.out.println("---------- Adding the Data into JSONObject ----------");
				            JSONObject jsonObject = new JSONObject();
				            jsonObject.put("SI. No", serialNumber);
				            jsonObject.put("Class Name", className);
				            jsonObject.put("Document Title", documentTitle);
		            		jsonObject.put("Date of Uploading", dateOfUploading);
				            jsonObject.put("Department Name", deptName);
				            jsonObject.put("Branch Name", branchName);
				            jsonObject.put("Branch Code", branchCode);
				            jsonObject.put("Note Date", noteDate);
				            jsonObject.put("Note Type", noteType);
		            		jsonObject.put("HO Department Code", hODepartmentCode);
		            		jsonObject.put("Note Subject", noteSubject);
		            		jsonObject.put("Note F.Y.", noteFY);
		            		jsonObject.put("Note Dispatch/Sr.No.", noteDispatchSrNo);
		            		jsonObject.put("Competent Authority", competentAuthority);
		            		jsonObject.put("IFSC CODE", iFSCCode);
		            		jsonObject.put("BRANCH LICENCE NO", branchLicenceNo);
		            		jsonObject.put("MICR NO", mICRNo);
		            		jsonObject.put("Opening NAME", openingName);
		            		jsonObject.put("Closing NAME", closingName);
		            		jsonObject.put("Merging NAME", mergingName);
		            		jsonObject.put("Branch open date", branchOpenDate);
		            		jsonObject.put("RTI DATE", rTIDate);
		            		jsonObject.put("RTI NAME", rTIName);
		            		jsonObject.put("REPLY DATE", replyDate);
		            		jsonObject.put("Reply F.Y.", replyFY);
		            		jsonObject.put("Tender No.", tenderNo);
		            		jsonObject.put("Date", dateHOPD);
		            		jsonObject.put("In-principal Note for Floating of RFP", inPrincipalNoteforFloatingofRFP);
		            		jsonObject.put("Pre-bid Queries/Response from bidder", preBidQueries);
		            		jsonObject.put("Bid Documents", bidDocuments);
		            		jsonObject.put("Note for Technical Evaluation", noteForTechnical);
		            		jsonObject.put("Notes for Floating Addendum", noteForFloatingAddendum);
		            		jsonObject.put("Addendum No", addendumNo);
		            		jsonObject.put("Addendum Dated", addendumDated);
		            		jsonObject.put("Addendum Subject", addendumsubject);
		            		jsonObject.put("Addendum related queries/response", addendumRelatedQueries);
		            		jsonObject.put("Annexures No", annexuresNo);
		            		jsonObject.put("Annexures Subject", annexuresSubject);
		            		jsonObject.put("Document Type", documentType);
		            		jsonObject.put("Remarks", remarks);
				            jsonObject.put("Uploaded By", uploadedBy);
				            jsonObject.put("No. of  Pages Uploaded", pageCount);
				            
				            jsonArray.add(jsonObject);
				            serialNumber++;
				            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            	}
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId: " + repositoryId);
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    } finally {
		        closeResources(connection);
		        if (preparedStatement != null) {
		            try {
		                preparedStatement.close();
		            } catch (SQLException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    return jsonArray;
		}
	  
	  private String formatDate(String inputDate) {
		  System.out.println("UserReportUtil.formatDate():"+inputDate);
		    try {
		        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        Date date = inputFormat.parse(inputDate);

		        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
		        return outputFormat.format(date);
		    } catch (Exception e) {
		        return inputDate;
		    }
	  }
	  
	  private String formatCommonDate(String commonDate) {
		  System.out.println("UserReportUtil.formatCommonDate(): "+commonDate);
		    try {
		        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
		        Date date = inputFormat.parse(commonDate);

		        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
		        return outputFormat.format(date);
		    } catch (Exception e) {
		        return commonDate;
		    }
	  }
	  
	  private String formatRecordDate(String recordDate) {
		    try {
		        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        Date date = inputFormat.parse(recordDate);

		        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mma");
		        return outputFormat.format(date);
		    } catch (Exception e) {
		        return recordDate;
		    }
	  }
	  
	  private String formatLogonOutDate(String logonOutDate) {
		    try {
		    	System.out.println("formatLogonOutDate: "+logonOutDate);
		        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        Date date = inputFormat.parse(logonOutDate);

		        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
		        return outputFormat.format(date);
		    } catch (Exception e) {
		        return logonOutDate;
		    }
	  }
	  
	  private String formatFileNameValue(String fileName) {
		    StringBuilder sentence = new StringBuilder();

		    try {
		        JSONArray jsonArray = JSONArray.parse(fileName);

		        if (jsonArray != null) {
		            for (Object obj : jsonArray) {
		                if (obj instanceof JSONObject) {
		                    JSONObject jsonField = (JSONObject) obj;
		                    String name = jsonField.get("name").toString();
		                    String operator = jsonField.get("operator").toString();
		                    JSONArray values = (JSONArray) jsonField.get("values");

		                    if (!values.isEmpty()) {
		                        sentence.append("Field: ").append(name).append(" Operator: ").append(operator).append(" Values: ");
		                        for (int i = 0; i < values.size(); i++) {
		                            sentence.append(values.get(i));
		                            if (i < values.size() - 1) {
		                                sentence.append(", ");
		                            }
		                        }
		                        sentence.append("\n");
		                    }
		                }
		            }
		        } else {
		            sentence.append(fileName);
		        }

		        if (sentence.length() > 0) {
		            sentence.deleteCharAt(sentence.length() - 1);
		        }
		    } catch (Exception e) {
		        sentence.append(fileName);
		    }

		    return sentence.toString();
		}

	    private void closeResources(Connection connection) {
	        try {
	            if (this.reportData != null) {
	                this.reportData.close();
	            }
	            if (connection != null) {
	                connection.close();
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

}
