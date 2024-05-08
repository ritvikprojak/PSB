package com.projak.psb.dms.reports.plugin.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.filenet.api.collection.DateTimeList;
import com.filenet.api.collection.Float64List;
import com.filenet.api.collection.Integer32List;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.collection.StringList;
import com.filenet.api.constants.FilteredPropertyType;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.Id;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class UserReportUtil {
	  
	  ResultSet reportData = null;
	  private static final List<String> CLASSNAMESTOINGORE = Arrays.asList("CmRptSearchResultsCSVDocument", "CodeModule", "EntryTemplate", "PreferencesDocument", "RecordsTemplate" ,"StoredSearch", "WebContentTemplate", "WorkflowDefinition", "XMLPropertyMappingScript");
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
		                jsonObject.put("No Of Pages Uploaded", pageCount);

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
		                System.out.println("No Of Pages Uploaded: " + pageCount);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
			            JSONObject jsonObject = new JSONObject();
			            jsonObject.put("SI. No", serialNumber);
			            jsonObject.put("Month", month);
			            jsonObject.put("Date of Uploading", dateOfUploading);
			            jsonObject.put("Branch Name", branchName);
			            jsonObject.put("Branch Code", branchCode);
			            jsonObject.put("No. of Documents Uploaded", noOfDocumentsUploaded);
			            jsonObject.put("No Of Pages Uploaded", pageCount);

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
		        System.out.println("UserQuery: "+query);
		        if (!query.isEmpty()) {
		            preparedStatement = connection.prepareStatement(query);
		            preparedStatement.setString(1, fromDate);
		            preparedStatement.setString(2, toDate);
		            this.reportData = preparedStatement.executeQuery();
		            System.out.println("Boolean Condition:" + this.reportData.isBeforeFirst());
		            // System.out.println("reportData ===> " + this.reportData.toString());
		            int serialNumber = 1;
		            
		            while (this.reportData.next()) {
		            	String dateOfUploading = this.reportData.getString("DateOfUploading");
			            String deptName = this.reportData.getString("DepartmentName");
			            String branchName = this.reportData.getString("BranchName");
			            String branchCode = this.reportData.getString("BranchCode");
			            String uploadedBy = this.reportData.getString("UploadedBy");
			            int pageCount = this.reportData.getInt("NoOfPagesUploaded");
			            String noOfDocumentsUploaded = this.reportData.getString("NoOfDocumentsUploaded");
			            
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
			            jsonObject.put("No Of Pages Uploaded", pageCount);
	
			            jsonArray.add(jsonObject);
			            serialNumber++;
			            System.out.println("---------- Data was successfully added into the JSONObject ----------");
		            }
		        } else {
		            System.out.println("Oops! No query selected for repositoryId: " + repositoryId);
		        }
		    }
		    catch (SQLException e) {
		        e.printStackTrace();
		        System.out.println("SQLException occured:;;: "+e.getMessage());
		    }catch (Exception e) {
		        e.printStackTrace();
		        System.out.println("Exception occured:;;: "+e.getMessage());
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
		                System.out.println("No Of Pages Uploaded: " + pageCount);
		                
		                System.out.println("---------- Adding the Data into JSONObject ----------");
			            JSONObject jsonObject = new JSONObject();
			            jsonObject.put("SI. No", serialNumber);
			            jsonObject.put("Date of Uploading", dateOfUploading);
			            jsonObject.put("Department Name", deptName);
			            jsonObject.put("Branch Name", branchName);
			            jsonObject.put("Branch Code", branchCode);
			            jsonObject.put("No. of Documents Uploaded", noOfDocumentsUploaded);
			            jsonObject.put("No Of Pages Uploaded", pageCount);
	
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
	  
	  public JSONArray searchDepartmentWiseMetadataReport(Connection connection, String fromDate, String toDate, String repositoryId,ObjectStore os) {
		  System.out.println("<<< ::: In the UserReportUtil Class searchDepartmentWiseMetadataReport Method ::: >>>");
		  System.out.println("fromDate: " + fromDate + ", toDate: " + toDate + " & Repository ID: " + repositoryId);
		  JSONArray jsonArray = new JSONArray();
		  RepositoryRowSet rowSet = null;
		  String searchFnQuery = "";
		  // fromDate = DateConversion.getConvertedDate(fromDate);
		  //toDate = DateConversion.getConvertedDate(toDate);
		  System.out.println("Metadata report dates: "+fromDate+"\t"+toDate);
		  searchFnQuery = "SELECT * FROM Document WHERE [DateCreated] >= "+fromDate+" AND [DateCreated] <= "+toDate;
		  System.out.println("searchFnQuery: "+searchFnQuery);
		  try {
			  if (!searchFnQuery.isEmpty()) {
				  SearchSQL searchSQL = new SearchSQL(searchFnQuery);
				  SearchScope searchScope = new SearchScope(os);
				  // This can be null if you are not filtering properties.
				  PropertyFilter myFilter = new PropertyFilter();
				  int myFilterLevel = 1;
				  myFilter.setMaxRecursion(myFilterLevel);
				  myFilter.addIncludeType(new FilterElement(null, null, null, FilteredPropertyType.ANY, null)); 

				  rowSet = searchScope.fetchRows(searchSQL, null, myFilter, new Boolean(true));
				  System.out.println("isEmpty: "+rowSet.isEmpty());
				  Iterator<RepositoryRow> iter = rowSet.iterator();

				  int serialNumber = 0;
				  while (iter.hasNext()) {
					  RepositoryRow row = (RepositoryRow) iter.next();

					  if(repositoryId.equalsIgnoreCase("HOIT")) {
						  System.out.println("::: In the HOIT reportData :::");
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading = row.getProperties().get("DateCreated").getDateTimeValue();
							  Integer noOfPageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							  int pageCount = noOfPageCount.intValue();
							  System.out.println("pageCount"+pageCount);
							  String deptName = row.getProperties().get("Dept_Name").getStringValue();
							  String branchName = row.getProperties().get("BranchName").getStringValue();
							  String branchCode = row.getProperties().get("BranchCode").getStringValue();
							  String tenderNo = row.getProperties().get("TenderNo").getStringValue();
							  Date date = row.getProperties().get("Date").getDateTimeValue();
							  String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							  String rFPSubject = row.getProperties().get("RFPSubject").getStringValue();
							  String eligibilityCriteria = row.getProperties().get("EligibilityCriteria").getStringValue();
							  String scopeofRFP = row.getProperties().get("ScopeOfRFP").getStringValue();
							  String projectTimelines = row.getProperties().get("ProjectTimelines").getStringValue();
							  String termsCondition = row.getProperties().get("TermsAndConditions").getStringValue();
							  String generalInstructions = row.getProperties().get("GeneralInstructions").getStringValue();
							  String evaluationProcess = row.getProperties().get("Eva").getStringValue();
							  String serviceLevelandPenalties = row.getProperties().get("ServiceLevelPenalties").getStringValue();
							  String bidSystem = row.getProperties().get("BIDSystem").getStringValue();
							  String tenderingProcess = row.getProperties().get("TenderingProcess").getStringValue();
							  String inprincipalNoteforFloatingofRFP = row.getProperties().get("InPrincipalNoteForFloatingRFP").getStringValue();
							  String noteforFloatingofRFP = row.getProperties().get("NoteForFloatingRFP").getStringValue();
							  String prebidQueriesResponsefrombidder = row.getProperties().get("PreBidQueriesResponse").getStringValue();
							  String bidDocuments = row.getProperties().get("BIDDocuments").getStringValue();
							  String noteforTechnicalEvaluation = row.getProperties().get("NoteForTechnicalEvaluation").getStringValue();
							  String noteforCommercialEvaluation = row.getProperties().get("NoteForCommercialEvaluation").getStringValue();
							  String noteforReverseAuction = row.getProperties().get("NoteForReverseAuction").getStringValue();
							  String noteforDeclaringL1bidder = row.getProperties().get("NoteForDeclaringL1Bidder").getStringValue();
							  String financialApprovalforFinalTCO = row.getProperties().get("FinancialApprovalForFinalTCO").getStringValue();
							  String otherNotes = row.getProperties().get("OtherNotes").getStringValue();
							  String notesforFloatingAddendum = row.getProperties().get("NoteForFloatingAddendum").getStringValue();
							  String addendumNo = row.getProperties().get("AddendumNo").getStringValue();
							  Date addendumDated = row.getProperties().get("AddendumDate").getDateTimeValue();
							  String addendumSubject = row.getProperties().get("AddendumSubject").getStringValue();
							  String addendumrelatedqueriesresponse = row.getProperties().get("AddendumQueriesResponse").getStringValue();
							  String annexuresNo = row.getProperties().get("AnnexureNo").getStringValue();
							  String annexuresSubject = row.getProperties().get("AnnexureSubject").getStringValue();
							  String documentType = row.getProperties().get("DocumentType").getStringValue();
							  String remark = row.getProperties().get("Remark").getStringValue();
							  String mEETINGFORTHEMONTHOF = row.getProperties().get("Meetingforthemonthof").getStringValue();
							  Date mEETINGDATE = row.getProperties().get("MeetingDate").getDateTimeValue();
							  String srNo = row.getProperties().get("SrNo").getStringValue();
							  String purposeCode = row.getProperties().get("PurposeCode").getStringValue();
							  String agendaItems = row.getProperties().get("AgendaItems").getStringValue();
							  String purpose = row.getProperties().get("Purpose").getStringValue();
							  String referenceNumber = row.getProperties().get("ReferenceNumber").getStringValue();
							  String agendaItemNo =row.getProperties().get("AgendaItemNo").getStringValue();
							  String actionTakenReport = row.getProperties().get("ActionTakenReport").getStringValue();
							  Date aTRDate = row.getProperties().get("ATRDate").getDateTimeValue();
							  Date noteDated = row.getProperties().get("NoteDate").getDateTimeValue();
							  String minutesofMeeting = row.getProperties().get("MinutesOfMeeting").getStringValue();
							  String noteType = row.getProperties().get("NoteType").getStringValue();
							  Date dateofReceipt = row.getProperties().get("DateOfReceipt").getDateTimeValue();
							  String type = row.getProperties().get("Type").getStringValue();
							  String subject = row.getProperties().get("Subject").getStringValue();
							  String from = row.getProperties().get("From").getStringValue();
							  Date dateofsubmission = row.getProperties().get("DateOfSubmission").getDateTimeValue();
							  String to = row.getProperties().get("To").getStringValue();
							  String fINANCIALYEAR = row.getProperties().get("FinancialYear").getStringValue();
							  String fREQUENCY = row.getProperties().get("Frequency").getStringValue();
							  String aUDITTYPE = row.getProperties().get("AuditType").getStringValue();
							  Date auditDate = row.getProperties().get("AuditDate").getDateTimeValue();
							  String auditCopyRefNo = row.getProperties().get("AuditCopyRefNo").getStringValue();
							  String auditorname = row.getProperties().get("AuditorName").getStringValue();
							  String yEAR = row.getProperties().get("Year").getStringValue();
							  String rECEIVEDFROM = row.getProperties().get("ReceivedFrom").getStringValue();
							  Date dATEOFRTIAPPLICATION = row.getProperties().get("DateOfRTIApplication").getDateTimeValue();
							  String nAMEOFAPPLICANT = row.getProperties().get("NameOfApplicant").getStringValue();
							  String sENTTO = row.getProperties().get("SentTo").getStringValue();
							  Date dATEOFDISPOSAL = row.getProperties().get("DateOfDisposal").getDateTimeValue();
							  String remarks = row.getProperties().get("Remarks").getStringValue();
							  String meetingNo = row.getProperties().get("MeetingNo").getStringValue();
							  System.out.println("");
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  System.out.println("uploadedBy");

							  String converteddateOfUploading = formatFnDate(dateOfUploading);
							  String converteddate = formatFnDate(date);
							  String convertedaddendumDated = formatFnDate(addendumDated);
							  String convertedmEETINGDATE = formatFnDate(mEETINGDATE);
							  String convertedaTRDate = formatFnDate(aTRDate);
							  String convertednoteDated = formatFnDate(noteDated);
							  String converteddateofReceipt = formatFnDate(dateofReceipt);
							  String converteddateofsubmission = formatFnDate(dateofsubmission);
							  String convertedauditDate = formatFnDate(auditDate);
							  String converteddATEOFRTIAPPLICATION = formatFnDate(dATEOFRTIAPPLICATION);
							  String converteddATEOFDISPOSAL = formatFnDate(dATEOFDISPOSAL);

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", converteddateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Tender No", tenderNo);
							  jsonObject.put("Date", converteddate);
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
							  jsonObject.put("Other Notes", otherNotes);
							  jsonObject.put("Notes for Floating Addendum", notesforFloatingAddendum);
							  jsonObject.put("Addendum No", addendumNo);
							  jsonObject.put("Addendum Dated", convertedaddendumDated);
							  jsonObject.put("Addendum Subject", addendumSubject);
							  jsonObject.put("Addendum related queries/response", addendumrelatedqueriesresponse);
							  jsonObject.put("Annexures No", annexuresNo);
							  jsonObject.put("Annexures Subject", annexuresSubject);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Remark", remark);
							  jsonObject.put("MEETING FOR THE MONTH OF", mEETINGFORTHEMONTHOF);
							  jsonObject.put("MEETING DATE", convertedmEETINGDATE);
							  jsonObject.put("Sr.No.", srNo);
							  jsonObject.put("Purpose Code", purposeCode);
							  jsonObject.put("Agenda Items", agendaItems);
							  jsonObject.put("Purpose", purpose);
							  jsonObject.put("Reference Number", referenceNumber);
							  jsonObject.put("Agenda Item No", agendaItemNo);
							  jsonObject.put("Action Taken Report", actionTakenReport);
							  jsonObject.put("ATR Date", convertedaTRDate);
							  jsonObject.put("Note Dated", convertednoteDated);
							  jsonObject.put("Minutes of Meeting", minutesofMeeting);
							  jsonObject.put("Note Type", noteType);
							  jsonObject.put("Date of Receipt", converteddateofReceipt);
							  jsonObject.put("Type", type);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("From", from);
							  jsonObject.put("Date of submission", converteddateofsubmission);
							  jsonObject.put("TO", to);
							  jsonObject.put("FINANCIAL YEAR", fINANCIALYEAR);
							  jsonObject.put("FREQUENCY", fREQUENCY);
							  jsonObject.put("AUDIT TYPE", aUDITTYPE);
							  jsonObject.put("Audit Date", convertedauditDate);
							  jsonObject.put("Audit Copy Ref.No.", auditCopyRefNo);
							  jsonObject.put("Auditor name", auditorname);
							  jsonObject.put("YEAR", yEAR);
							  jsonObject.put("RECEIVED FROM", rECEIVEDFROM);
							  jsonObject.put("DATE OF RTI APPLICATION", converteddATEOFRTIAPPLICATION);
							  jsonObject.put("NAME OF APPLICANT", nAMEOFAPPLICANT);
							  jsonObject.put("SENT TO", sENTTO);
							  jsonObject.put("DATE OF DISPOSAL", dATEOFDISPOSAL);
							  jsonObject.put("Remarks", remarks);
							  jsonObject.put("Meeting No.", meetingNo);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							                  
							  jsonArray.add(jsonObject);
							  serialNumber++;
						  }

						  System.out.println("---------- Data was successfully added into the JSONObject ----------");
					  }else if(repositoryId.equalsIgnoreCase("HOPD")) {
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading = row.getProperties().get("DateCreated").getDateTimeValue();
							  Integer noOfPageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							  int pageCount = noOfPageCount.intValue();
							  System.out.println("pageCount"+pageCount);

							  String deptName = row.getProperties().get("Dept_Name").getStringValue();
							  String branchName = row.getProperties().get("BranchName").getStringValue();
							  String branchCode = row.getProperties().get("BranchCode").getStringValue();
							  Date noteDate = row.getProperties().get("NoteDate").getDateTimeValue();
							  String noteType = row.getProperties().get("NoteType").getStringValue();
							  String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							  String noteSubject = row.getProperties().get("NoteSubject").getStringValue();
							  String noteFY = row.getProperties().get("NoteFY").getStringValue();
							  String noteDispatchSrNo = row.getProperties().get("NoteDispatchSrNo").getStringValue();
							  String competentAuthority = row.getProperties().get("CompetentAuthority").getStringValue();
							  String iFSCCode = row.getProperties().get("IFSCCODE").getStringValue();
							  String branchLicenceNo = row.getProperties().get("BRANCHLICENCENO").getStringValue();
							  String mICRNo = row.getProperties().get("MICRNO").getStringValue();
							  String openingName = row.getProperties().get("OpeningName").getStringValue();
							  String closingName = row.getProperties().get("ClosingName").getStringValue();
							  String mergingName = row.getProperties().get("MergingName").getStringValue();
							  Date branchOpenDate = row.getProperties().get("Branchopendate").getDateTimeValue();
							  Date rTIDate = row.getProperties().get("RTIDATE").getDateTimeValue();
							  String rTIName = row.getProperties().get("RTIName").getStringValue();
							  Date replyDate = row.getProperties().get("REPLYDATE").getDateTimeValue();
							  String replyFY = row.getProperties().get("ReplyFY").getStringValue();
							  String tenderNo = row.getProperties().get("TenderNo").getStringValue();
							  Date dateHOPD = row.getProperties().get("Date").getDateTimeValue();
							  String inPrincipalNoteforFloatingofRFP = row.getProperties().get("InprincipalNoteforFloatingofRFP").getStringValue();
							  StringList preBidQueries = row.getProperties().get("PrebidQueriesResponsefrombidder").getStringListValue();
							  StringList bidDocuments = row.getProperties().get("BidDocuments").getStringListValue();
							  StringList noteForTechnical1 = row.getProperties().get("NoteforTechnicalEvaluation").getStringListValue();
							  String noteForTechnical = formatStringList(noteForTechnical1);
							  StringList noteforCommercialEvaluation1 = row.getProperties().get("NoteforCommercialEvaluation").getStringListValue();
							  String noteforCommercialEvaluation = formatStringList(noteforCommercialEvaluation1);
							  StringList miscnotesregradingTender1 = row.getProperties().get("MiscnotesregradingTender").getStringListValue();
							  String miscnotesregradingTender = formatStringList(miscnotesregradingTender1);
							  String noteForFloatingAddendum = row.getProperties().get("NotesforFloatingAddendum").getStringValue();
							  String addendumNo = row.getProperties().get("AddendumNo").getStringValue();
							  Date addendumDated = row.getProperties().get("AddendumDated").getDateTimeValue();
							  StringList addendumsubject = row.getProperties().get("AddendumSubject").getStringListValue();
							  StringList addendumRelatedQueries = row.getProperties().get("Addendumrelatedqueriesresponse").getStringListValue();
							  StringList noteforFloatingRFP1 = row.getProperties().get("NoteforFloatingRFP").getStringListValue();
							  String noteforFloatingRFP = formatStringList(noteforFloatingRFP1);
							  StringList annexuresNo1 = row.getProperties().get("AnnexuresNo").getStringListValue();
							  String annexuresNo = formatStringList(annexuresNo1);
							  StringList annexuresSubject1 = row.getProperties().get("AnnexuresSubject").getStringListValue();
							  String annexuresSubject = formatStringList(annexuresSubject1);
							  String documentType = row.getProperties().get("DocumentType").getStringValue();
							  Date documentDate1 = row.getProperties().get("DocumentDate").getDateTimeValue();
							  String documentDate = formatFnDate(documentDate1);
							  String remarks = row.getProperties().get("Remarks").getStringValue();
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  String converteddateOfUploading = formatFnDate(dateOfUploading);
							  String convertedBranchOpenDate = formatFnDate(branchOpenDate);
							  String convertedNoteDate = formatFnDate(noteDate);
							  String convertedrTIDate = formatFnDate(rTIDate);
							  String convertedreplyDate = formatFnDate(replyDate);
							  String converteddateHOPD = formatFnDate(dateHOPD);
							  String convertedAddendumDated = formatFnDate(addendumDated);
							  String convertedPreBidQueries = formatStringList(preBidQueries);
							  String convertedbidDocuments = formatStringList(bidDocuments);
							  String convertedaddendumsubject = formatStringList(addendumsubject);
							  String convertedaddendumRelatedQueries = formatStringList(addendumRelatedQueries);
							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", converteddateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Note Date", convertedNoteDate);
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
							  jsonObject.put("Branch open date", convertedBranchOpenDate);
							  jsonObject.put("RTI DATE", convertedrTIDate);
							  jsonObject.put("RTI NAME", rTIName);
							  jsonObject.put("REPLY DATE", convertedreplyDate);
							  jsonObject.put("Reply F.Y.", replyFY);
							  jsonObject.put("Tender No.", tenderNo);
							  jsonObject.put("Date", converteddateHOPD);
							  jsonObject.put("In-principal Note for Floating of RFP", inPrincipalNoteforFloatingofRFP);
							  jsonObject.put("Pre-bid Queries/Response from bidder", convertedPreBidQueries);
							  jsonObject.put("Bid Documents", convertedbidDocuments);
							  jsonObject.put("Note for Technical Evaluation", noteForTechnical);
							  jsonObject.put("Notes for Floating Addendum", noteForFloatingAddendum);
							  jsonObject.put("Note for Floating RFP", noteforFloatingRFP);
							  jsonObject.put("Note for Commercial Evaluation", noteforCommercialEvaluation);
							  jsonObject.put("Misc notes regrading Tender", miscnotesregradingTender);
							  jsonObject.put("Addendum No", addendumNo);
							  jsonObject.put("Addendum Dated", convertedAddendumDated);
							  jsonObject.put("Addendum Subject", convertedaddendumsubject);
							  jsonObject.put("Addendum related queries/response", convertedaddendumRelatedQueries);
							  jsonObject.put("Annexures No", annexuresNo);
							  jsonObject.put("Annexures Subject", annexuresSubject);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Document Date", documentDate);
							  jsonObject.put("Remarks", remarks);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }

					  }else if(repositoryId.equalsIgnoreCase("HOATM")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateCreated = row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateCreated);
							  String deptName = row.getProperties().get("Dept_Name").getStringValue();
							  String branchName = row.getProperties().get("BranchName").getStringValue();
							  String branchCode = row.getProperties().get("BranchCode").getStringValue();
							  Date noteDate1 = row.getProperties().get("NoteDate").getDateTimeValue();
							  String noteDate = formatFnDate(noteDate1);
							  String noteType = row.getProperties().get("NoteType").getStringValue();
							  String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							  String noteSubject = row.getProperties().get("NoteSubject").getStringValue();
							  String noteFY = row.getProperties().get("NoteFY").getStringValue();
							  String noteDispatchSrNo = row.getProperties().get("NoteDispatchSrNo").getStringValue();
							  String competentAuthority = row.getProperties().get("CompetentAuthority").getStringValue();
							  Date auditDate1 = row.getProperties().get("AuditDate").getDateTimeValue();
							  String auditDate = formatFnDate(auditDate1);
							  String auditType = row.getProperties().get("AuditTYPE").getStringValue();
							  String auditSubject = row.getProperties().get("AuditSubject").getStringValue();
							  String auditForPeriod = row.getProperties().get("Auditforperiod").getStringValue();
							  String auditCopySRNo = row.getProperties().get("AuditCopySrNo").getStringValue();
							  String auditConductedBy = row.getProperties().get("AuditConductedby").getStringValue();
							  String auditorFirmIfAny = row.getProperties().get("AuditorFirmIfAny").getStringValue();
							  Date reportingDate1 = row.getProperties().get("ReportingDate").getDateTimeValue();
							  String reportingDate = formatFnDate(reportingDate1);
							  String reportingPeriod = row.getProperties().get("Reportingperiod").getStringValue();
							  String reportName = row.getProperties().get("ReportName").getStringValue();
							  Date lastDateforReporting1 = row.getProperties().get("LastDateforReporting").getDateTimeValue();
							  String lastDateforReporting = formatFnDate(lastDateforReporting1);
							  Date dataReceivingDate1 =row.getProperties().get("DataReceivingDate").getDateTimeValue();
							  String dataReceivingDate = formatFnDate(dataReceivingDate1);
							  String dataPeriod = row.getProperties().get("Dataperiod").getStringValue();
							  String dataDetails = row.getProperties().get("DataDetails").getStringValue();
							  String dataCollectedFrom = row.getProperties().get("DataCollectedfrom").getStringValue();
							  String dataRectificationIfAny = row.getProperties().get("DataRectificationIfany").getStringValue();
							  Date revisedDataReceivingDate1 = row.getProperties().get("RevisedDataReceivingDate").getDateTimeValue();
							  String revisedDataReceivingDate = formatFnDate(revisedDataReceivingDate1);
							  Date revisedDataDetails1 =row.getProperties().get("RevisedDataDetails").getDateTimeValue();
							  String revisedDataDetails = formatFnDate(revisedDataDetails1);
							  String tenderNo = row.getProperties().get("TenderNo").getStringValue();
							  Date date1= row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String inPrincipalNoteforFloatingofRFP = row.getProperties().get("InprincipalNoteforFloatingofRFP").getStringValue();
							  StringList preBidQueriesResponsefrombidderList = row.getProperties().get("PrebidQueriesResponsefrombidder").getStringListValue();
							  String preBidQueriesResponsefrombidder = formatStringList(preBidQueriesResponsefrombidderList);
							  StringList bidDocumentsList = row.getProperties().get("BidDocuments").getStringListValue();
							  String bidDocuments = formatStringList(bidDocumentsList);
							  StringList noteForTechnicalEvaluationList = row.getProperties().get("NoteforTechnicalEvaluation").getStringListValue();
							  String noteForTechnicalEvaluation = formatStringList(noteForTechnicalEvaluationList);
							  StringList noteforCommercialEvaluationList = row.getProperties().get("NotefprCommercialEvaluation").getStringListValue();
							  String noteforCommercialEvaluation = formatStringList(noteforCommercialEvaluationList);
							  StringList noteforReverseAuctionList = row.getProperties().get("NoteforReverseAuction").getStringListValue();
							  String noteforReverseAuction = formatStringList(noteforReverseAuctionList);
							  StringList noteforDeclaringL1bidderList = row.getProperties().get("NoteforDeclaringL1bidder").getStringListValue();
							  String noteforDeclaringL1bidder = formatStringList(noteforDeclaringL1bidderList);
							  StringList financialApprovalforFinalTCOList = row.getProperties().get("FinancialApprovalforFinalTCO").getStringListValue();
							  String financialApprovalforFinalTCO = formatStringList(financialApprovalforFinalTCOList);
							  StringList miscnotesregradingTenderList = row.getProperties().get("MiscnotesregradingTender").getStringListValue();
							  String miscnotesregradingTender = formatStringList(miscnotesregradingTenderList);
							  String notesforFloatingAddendum = row.getProperties().get("NotesforFloatingAddendum").getStringValue();
							  String addendumNo = row.getProperties().get("AddendumNo").getStringValue();
							  Date addendumDated1 = row.getProperties().get("AddendumDated").getDateTimeValue();
							  String addendumDated = formatFnDate(addendumDated1);
							  StringList addendumSubjectList = row.getProperties().get("AddendumSubject").getStringListValue();
							  String addendumSubject = formatStringList(addendumSubjectList);
							  StringList addendumrelatedqueriesresponseList = row.getProperties().get("Addendumrelatedqueriesresponse").getStringListValue();
							  String addendumrelatedqueriesresponse = formatStringList(addendumrelatedqueriesresponseList);
							  StringList annexuresNoList = row.getProperties().get("AnnexuresNo").getStringListValue();
							  String annexuresNo = formatStringList(annexuresNoList);
							  StringList annexuresSubjectList =row.getProperties().get("AnnexuresSubject").getStringListValue();
							  String annexuresSubject = formatStringList(annexuresSubjectList);
							  String documentType = row.getProperties().get("DocumentType").getStringValue();
							  String remarks = row.getProperties().get("Remarks").getStringValue();
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  //int noOfPagesUploaded = row.getProperties().get("DocumentTitle").getStringValue();


							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("S.No", serialNumber);
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
							  jsonObject.put("Audit Date", auditDate);
							  jsonObject.put("Audit Type", auditType);
							  jsonObject.put("Audit Subject", auditSubject);
							  jsonObject.put("Audit For Period", auditForPeriod);
							  jsonObject.put("Audit Copy SR No", auditCopySRNo);
							  jsonObject.put("Audit Conducted By", auditConductedBy);
							  jsonObject.put("Auditor Firm If Any", auditorFirmIfAny);
							  jsonObject.put("Reporting Date", reportingDate);
							  jsonObject.put("Reporting Period", reportingPeriod);
							  jsonObject.put("Report Name", reportName);
							  jsonObject.put("Last Date for Reporting", lastDateforReporting);
							  jsonObject.put("Data Receiving Date", dataReceivingDate);
							  jsonObject.put("Data Period", dataPeriod);
							  jsonObject.put("Data Details", dataDetails);
							  jsonObject.put("Data Collected From", dataCollectedFrom);
							  jsonObject.put("Data Rectification If Any", dataRectificationIfAny);
							  jsonObject.put("Revised Data Receiving Date", revisedDataReceivingDate);
							  jsonObject.put("Revised Data Details", revisedDataDetails);
							  jsonObject.put("Tender No", tenderNo);
							  jsonObject.put("Date", date);
							  jsonObject.put("In-principal Note for Floating of RFP", inPrincipalNoteforFloatingofRFP);
							  jsonObject.put("Pre-bid Queries/Response from bidder", preBidQueriesResponsefrombidder);
							  jsonObject.put("Bid Documents", bidDocuments);
							  jsonObject.put("Note for Technical Evaluation", noteForTechnicalEvaluation);
							  jsonObject.put("Note for Commercial Evaluation", noteforCommercialEvaluation);
							  jsonObject.put("Note for Reverse Auction", noteforReverseAuction);
							  jsonObject.put("Note for Declaring L1 Bidder", noteforDeclaringL1bidder);
							  jsonObject.put("Financial Approval for Final TCO", financialApprovalforFinalTCO);
							  jsonObject.put("Notes for Floating Addendum", notesforFloatingAddendum);
							  jsonObject.put("Addendum No", addendumNo);
							  jsonObject.put("Addendum Dated", addendumDated);
							  jsonObject.put("Addendum Subject", addendumSubject);
							  jsonObject.put("Addendum related queries/response", addendumrelatedqueriesresponse);
							  jsonObject.put("Annexures No", annexuresNo);
							  jsonObject.put("Annexures Subject", annexuresSubject);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Remarks", remarks);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", "");

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  }else if(repositoryId.equalsIgnoreCase("HOCPPC")) {
						  System.out.println("::: In the HOCPPC reportData :::");
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String branchCode = row.getProperties().get("BranchCode").getStringValue();
							  String branchName = row.getProperties().get("BranchName").getStringValue();
							  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							  String deptName = row.getProperties().get("Dept_Name").getStringValue();
							  String ppoNo = row.getProperties().get("PPONo").getStringValue();
							  String accountNo = row.getProperties().get("AccountNo").getStringValue();
							  String refNumber = row.getProperties().get("REFNUMBER").getStringValue();
							  String documentType = row.getProperties().get("DocumentType").getStringValue();
							  Date monthYear1 = row.getProperties().get("MONTHYEAR").getDateTimeValue();
							  String monthYear = formatFnDate(monthYear1);
							  String treasuryType = row.getProperties().get("TreasuryType").getStringValue();
							  String type = row.getProperties().get("Type").getStringValue();
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  System.out.println("FileNet Date:");
							  Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading.toString());
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("PPO No", ppoNo);
							  jsonObject.put("Account No", accountNo);
							  jsonObject.put("REF Number", refNumber);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Month Year", monthYear);
							  jsonObject.put("Treasury Type", treasuryType);
							  jsonObject.put("Type", type);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No. of  Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  }else if (repositoryId.equalsIgnoreCase("HOPSAII")) {

						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  //String className = this.reportData.getString("ClassName");
							  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String deptName = row.getProperties().get("Dept_Name").getStringValue();
							  String branchName = row.getProperties().get("BranchName").getStringValue();
							  String branchCode = row.getProperties().get("BranchCode").getStringValue();
							  String zoneName = row.getProperties().get("Zone_Name").getStringValue();
							  String zoneCode = row.getProperties().get("Zone_Code").getStringValue();
							  String loanAccountNumber = row.getProperties().get("LOANACCOUNTNUMBER").getStringValue();
							  String applicationNumber = row.getProperties().get("APPLICATIONNUMBER").getStringValue();
							  String customerId = row.getProperties().get("CUSTOMERID").getStringValue();
							  StringList customerName1 = row.getProperties().get("CUSTOMERNAME").getStringListValue();
							  String customerName = formatStringList(customerName1);
							  StringList guarantorName1 = row.getProperties().get("GUARANTORNAME").getStringListValue();
							  String guarantorName = formatStringList(guarantorName1);
							  String schemeType = row.getProperties().get("SCHEMETYPE").getStringValue();
							  String documentType = row.getProperties().get("DOCUMENTTYPE").getStringValue();
							  String documentSubTypePreSancationDocuments = row.getProperties().get("DOCUMENTSUBTYPE1PRESANCTIONDOCUMENTS").getStringValue();
							  String documentSubTypePostSancationDocuments = row.getProperties().get("DOCUMENTSUBTYPE1POSTSANCTIONDOCUMENTS").getStringValue();
							  String uploadedBy = row.getProperties().get("BranchCode").getStringValue();
							  int pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Zone Name", zoneName);
							  jsonObject.put("Zone Code", zoneCode);
							  jsonObject.put("Loan Account Number", loanAccountNumber);
							  jsonObject.put("Application Number", applicationNumber);
							  jsonObject.put("Customer Name", customerName);
							  jsonObject.put("Customer ID", customerId);
							  jsonObject.put("Guarantor Name", guarantorName);
							  jsonObject.put("Scheme Type", schemeType);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Document SubType Pre-Sancation Documents", documentSubTypePreSancationDocuments);
							  jsonObject.put("Document SubType Post-Sancation Documents", documentSubTypePostSancationDocuments);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No. of  Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

						  }	
					  }
					  else if(repositoryId.equalsIgnoreCase("HOT")) {

						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{

							  // String className = this.reportData.getString("ClassName");
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  Date noteDate1 =  row.getProperties().get("NoteDate").getDateTimeValue();
							  String noteDate = formatFnDate(noteDate1);
							  String noteType =  row.getProperties().get("NoteType").getStringValue();
							  String hoDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String noteSubject =  row.getProperties().get("NoteSubject").getStringValue();
							  String noteFY =  row.getProperties().get("NoteFY").getStringValue();
							  String noteDispatchSrNo =  row.getProperties().get("NoteDispatchSrNo").getStringValue();
							  String competentAuthority =  row.getProperties().get("CompetentAuthority").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  Date reportingDate1 =  row.getProperties().get("ReportingDate").getDateTimeValue();
							  String reportingDate = formatFnDate(reportingDate1);
							  String reportingPeriod =  row.getProperties().get("Reportingperiod").getStringValue();
							  String reportName =  row.getProperties().get("ReportName").getStringValue();
							  Date lastDateForReporting1 =  row.getProperties().get("LastDateforReporting").getDateTimeValue();
							  String lastDateForReporting = formatFnDate(lastDateForReporting1);
							  String auditDate =  row.getProperties().get("AuditDate").getStringValue();
							  String auditType =  row.getProperties().get("AuditTYPE").getStringValue();
							  String auditSubject =  row.getProperties().get("AuditSubject").getStringValue();
							  String auditForPeriod = row.getProperties().get("Auditforperiod").getStringValue();
							  String auditCopySRNo =  row.getProperties().get("AuditCopySrNo").getStringValue();
							  String auditConductedBy =  row.getProperties().get("AuditConductedby").getStringValue();
							  String auditorFirmIfAny = row.getProperties().get("AuditorFirmIfAny").getStringValue();
							  String dataReceivingSubmittingDate =  row.getProperties().get("DataReceivingsubmittingDate").getStringValue();
							  String dataPeriod =  row.getProperties().get("Dataperiod").getStringValue();
							  String dataDetails =  row.getProperties().get("DataDetails").getStringValue();
							  String dataCollectedFromSubmittedTo =  row.getProperties().get("DataCollectedfromSubmittedto").getStringValue();
							  String dataRectificationIfAny =  row.getProperties().get("DataRectificationIfany").getStringValue();
							  String revisedDataDetails =  row.getProperties().get("RevisedDataDetails").getStringValue();
							  Date dataReceivingDate1 =  row.getProperties().get("RevisedDataReceivingDate").getDateTimeValue();
							  String dataReceivingDate = formatFnDate(dataReceivingDate1);
							  String dataPeriodDate =  row.getProperties().get("DataperiodDate").getStringValue();
							  String uploadedBy =  row.getProperties().get("DocumentTitle").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();

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
							  jsonObject.put("HO Department Code", hoDepartmentCode);
							  jsonObject.put("Note Subject", noteSubject);
							  jsonObject.put("Note FY", noteFY);
							  jsonObject.put("Note Dispatch Sr No", noteDispatchSrNo);
							  jsonObject.put("Competent Authority", competentAuthority);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Reporting Date", reportingDate);
							  jsonObject.put("Reporting Period", reportingPeriod);
							  jsonObject.put("Report Name", reportName);
							  jsonObject.put("Last Date for Reporting", lastDateForReporting);
							  jsonObject.put("Audit Date", auditDate);
							  jsonObject.put("Audit Type", auditType);
							  jsonObject.put("Audit Subject", auditSubject);
							  jsonObject.put("Audit For Period", auditForPeriod);
							  jsonObject.put("Audit Copy SR No", auditCopySRNo);
							  jsonObject.put("Audit Conducted By", auditConductedBy);
							  jsonObject.put("Auditor Firm If Any", auditorFirmIfAny);
							  jsonObject.put("Data Receiving Submitting Date", dataReceivingSubmittingDate);
							  jsonObject.put("Data Period", dataPeriod);
							  jsonObject.put("Data Details", dataDetails);
							  jsonObject.put("Data Collected From Submitted To", dataCollectedFromSubmittedTo);
							  jsonObject.put("Data Rectification If Any", dataRectificationIfAny);
							  jsonObject.put("Revised Data Details", revisedDataDetails);
							  jsonObject.put("Data Receiving Date", dataReceivingDate);
							  jsonObject.put("Data Period Date", dataPeriodDate);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  }else if(repositoryId.equalsIgnoreCase("HOAA")) {
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{	
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String hodDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String subject =  row.getProperties().get("Subject").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String remark =  row.getProperties().get("Remark").getStringValue();
							  String uploadedBy = row.getProperties().get("DataRectificationIfany").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Date", date);
							  jsonObject.put("HODepartment Code", hodDepartmentCode);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Remark", remark);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No. of  Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

						  }
					  }

					  else if(repositoryId.equalsIgnoreCase("HOCA")) {
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String remark =  row.getProperties().get("Remark").getStringValue();
							  String Subject =  row.getProperties().get("Subject").getStringValue();
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String hoDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String subject =  row.getProperties().get("Subject").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Date", date);
							  jsonObject.put("HO Department Code", hoDepartmentCode);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Remark", remark);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  }
					  else if(repositoryId.equalsIgnoreCase("HOFI")) {
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  String DocumentType =  row.getProperties().get("DocumentType").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  Date dATEOFAUDIT1 =  row.getProperties().get("DATEOFAUDIT").getDateTimeValue();
							  String dATEOFAUDIT = formatFnDate(dATEOFAUDIT1);
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String auditType =  row.getProperties().get("AUDITTYPE").getStringValue();
							  String timeOfAudit =  row.getProperties().get("TIMEOFAUDIT").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  Date dateOfLetter1 =  row.getProperties().get("DATEOFLETTER").getDateTimeValue();
							  String dateOfLetter = formatFnDate(dateOfLetter1);
							  StringList subject1 =  row.getProperties().get("SUBJECT").getStringListValue();
							  String subject = formatStringList(subject1);
							  String sentTo =  row.getProperties().get("SENTTO").getStringValue();
							  String letterType =  row.getProperties().get("LETTERTYPE").getStringValue();
							  String addendumNo =  row.getProperties().get("AddendumNo").getStringValue();
							  String receivedFrom =  row.getProperties().get("RECEIVEDFROM").getStringValue();
							  StringList AddendumSubject =  row.getProperties().get("AddendumSubject").getStringListValue();
							  String addendumSubject = formatStringList(AddendumSubject);
							  Date Date =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(Date);
							  Date AddendumDate =  row.getProperties().get("AddendumDate").getDateTimeValue();
							  String addendumDate = formatFnDate(AddendumDate);
							  String NAMEOFPARTYVENDOR =  row.getProperties().get("NAMEOFPARTYVENDOR").getStringValue();
							  String ReferenceNo =  row.getProperties().get("ReferenceNo").getStringValue();
							  String Remarks =  row.getProperties().get("Remarks").getStringValue();
							  String Products =  row.getProperties().get("Products").getStringValue();
							  String STAFFNAME =  row.getProperties().get("STAFFNAME").getStringValue();
							  StringList AccountNumber =  row.getProperties().get("AccountNumber").getStringListValue();
							  String accountNumber = formatStringList(AccountNumber);
							  String NoteType =  row.getProperties().get("NoteType").getStringValue();
							  String tenderNo=  row.getProperties().get("TenderNo").getStringValue();
							  String hodDepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							  StringList RFPSubject=  row.getProperties().get("RFPSubject").getStringListValue();
							  String rFPSubject = formatStringList(RFPSubject);
							  StringList ScopeofRFP=  row.getProperties().get("ScopeofRFP").getStringListValue();
							  String scopeOfRFP = formatStringList(ScopeofRFP);
							  StringList ProjectTimelines=  row.getProperties().get("ProjectTimelines").getStringListValue();
							  String projectTimelines = formatStringList(ProjectTimelines);
							  StringList TermsCondition=  row.getProperties().get("TermsCondition").getStringListValue();
							  String termsCondition = formatStringList(TermsCondition);
							  StringList EvaluationProcess=  row.getProperties().get("EvaluationProcess").getStringListValue();
							  String evaluationProcess = formatStringList(EvaluationProcess);
							  String BidSystem =  row.getProperties().get("BidSystem ").getStringValue();
							  Date DATEOFAGREEMENTADDENDUMVOUCHER1 =  row.getProperties().get("DATEOFAGREEMENTADDENDUMVOUCHER").getDateTimeValue();
							  String DATEOFAGREEMENTADDENDUMVOUCHER = formatFnDate(DATEOFAGREEMENTADDENDUMVOUCHER1);
							  System.out.println("INVOICEVOUCHERAMOUNT");
							  double INVOICEVOUCHERAMOUNT =  row.getProperties().get("INVOICEVOUCHERAMOUNT").getFloat64Value();
							  System.out.println("INVOICEVOUCHERAMOUNT"+INVOICEVOUCHERAMOUNT);
							  double AMOUNT =  row.getProperties().get("AMOUNT").getFloat64Value();
							  String uploadedBy = row.getProperties().get("Creater").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();

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
							  jsonObject.put("HODepartment Code", hodDepartmentCode);
							  jsonObject.put("RFP Subject", rFPSubject);
							  jsonObject.put("Scope Of RFP", scopeOfRFP);
							  jsonObject.put("Project Timelines", projectTimelines);
							  jsonObject.put("Terms And Conditions", termsCondition);
							  jsonObject.put("Evaluation Process", evaluationProcess);
							  jsonObject.put("Bid System", BidSystem);
							  jsonObject.put("Addendum No", addendumNo);
							  jsonObject.put("Addendum Subject", addendumSubject);
							  jsonObject.put("Addendum Date", addendumDate);
							  jsonObject.put("Reference No", ReferenceNo);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Staff Name", STAFFNAME);
							  jsonObject.put("Note Type", NoteType);
							  jsonObject.put("Name Of Party Vendor", NAMEOFPARTYVENDOR);
							  jsonObject.put("Date Of Agreement/Addendum Voucher", DATEOFAGREEMENTADDENDUMVOUCHER);
							  jsonObject.put("Invoice Voucher Amount", INVOICEVOUCHERAMOUNT);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Remarks", Remarks);
							  jsonObject.put("Date Of Audit", dATEOFAUDIT);
							  jsonObject.put("Audit Type", auditType);
							  jsonObject.put("Time Of Audit", timeOfAudit);
							  jsonObject.put("Amount", AMOUNT);
							  jsonObject.put("Products", Products);
							  jsonObject.put("Account Number", accountNumber);
							  jsonObject.put("Date Of Letter", dateOfLetter);
							  jsonObject.put("Sent To", sentTo);
							  jsonObject.put("Letter Type", letterType);
							  jsonObject.put("Received From", receivedFrom);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  }

					  else if(repositoryId.equalsIgnoreCase("HOGA")) {
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String zoneName =  row.getProperties().get("Zone_Name").getStringValue();
							  String AddendumNo =  row.getProperties().get("AddendumNo").getStringValue();
							  StringList AddendumSubject =  row.getProperties().get("AddendumSubject").getStringListValue();
							  String addendumSubject = formatStringList(AddendumSubject);
							  StringList NoteforFloatingofRFP =  row.getProperties().get("NoteforFloatingofRFP").getStringListValue();
							  String noteforFloatingofRFP = formatStringList(NoteforFloatingofRFP);
							  StringList BidDocuments =  row.getProperties().get("BidDocuments").getStringListValue();
							  String bidDocuments = formatStringList(BidDocuments);
							  StringList NoteforDeclaringL1bidder =  row.getProperties().get("NoteforDeclaringL1bidder").getStringListValue();
							  String noteforDeclaringL1bidder = formatStringList(NoteforDeclaringL1bidder);
							  StringList MiscnotesregradingTender =  row.getProperties().get("MiscnotesregradingTender").getStringListValue();
							  String miscnotesregradingTender = formatStringList(MiscnotesregradingTender);
							  StringList FinancialApprovalforFinalTCO =  row.getProperties().get("FinancialApprovalforFinalTCO").getStringListValue();
							  String financialApprovalforFinalTCO = formatStringList(FinancialApprovalforFinalTCO);
							  StringList NoteforReverseAuction =  row.getProperties().get("NoteforReverseAuction").getStringListValue();
							  String noteforReverseAuction = formatStringList(NoteforReverseAuction);
							  StringList NoteforTechnicalEvaluation =  row.getProperties().get("NoteforTechnicalEvaluation").getStringListValue();
							  String noteforTechnicalEvaluation = formatStringList(NoteforTechnicalEvaluation);
							  StringList PrebidQueriesResponsefrombidder =  row.getProperties().get("PrebidQueriesResponsefrombidder").getStringListValue();
							  String prebidQueriesResponsefrombidder = formatStringList(PrebidQueriesResponsefrombidder);
							  StringList NotefprCommercialEvaluation =  row.getProperties().get("NotefprCommercialEvaluation").getStringListValue();
							  String notefprCommercialEvaluation = formatStringList(NotefprCommercialEvaluation);
							  StringList AnnexuresNo =  row.getProperties().get("AnnexuresNo").getStringListValue();
							  String annexuresNo = formatStringList(AnnexuresNo);
							  StringList AnnexuresSubject =  row.getProperties().get("AnnexuresSubject").getStringListValue();
							  String annexuresSubject = formatStringList(AnnexuresSubject);
							  StringList Addendumrelatedqueriesresponse =  row.getProperties().get("Addendumrelatedqueriesresponse").getStringListValue();
							  String addendumrelatedqueriesresponse = formatStringList(Addendumrelatedqueriesresponse);
							  Date AddendumDated =  row.getProperties().get("AddendumDated").getDateTimeValue();
							  String addendumDated = formatFnDate(AddendumDated);
							  String NotesforFloatingAddendum =  row.getProperties().get("NotesforFloatingAddendum").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String TenderNo =  row.getProperties().get("TenderNo").getStringValue();
							  String TypeofMeeting =  row.getProperties().get("TypeofMeeting").getStringValue();
							  String InprincipalNoteforFloatingofRFP =  row.getProperties().get("InprincipalNoteforFloatingofRFP").getStringValue();
							  String Subject =  row.getProperties().get("Subject").getStringValue();
							  Date Minutesdate =  row.getProperties().get("Minutesdate").getDateTimeValue();
							  String minutesdate = formatFnDate(Minutesdate);
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String hodDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String subject =  row.getProperties().get("Subject").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String HODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String uploadedBy = row.getProperties().get("creator").getStringValue();
							  String remarks = row.getProperties().get("Remarks").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Zone Name", zoneName);
							  jsonObject.put("Type of Meeting", TypeofMeeting);
							  jsonObject.put("Minutes Date", minutesdate);
							  jsonObject.put("Date", date);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Tender No", TenderNo);
							  jsonObject.put("HO Department Code", HODepartmentCode);
							  jsonObject.put("In-principal Note for Floating of RFP", InprincipalNoteforFloatingofRFP);
							  jsonObject.put("Note for Floating RFP", noteforFloatingofRFP);
							  jsonObject.put("Pre-bid Queries Response", prebidQueriesResponsefrombidder);
							  jsonObject.put("Bid Documents", bidDocuments);
							  jsonObject.put("Note for Technical Evaluation", noteforTechnicalEvaluation);
							  jsonObject.put("Note for Commercial Evaluation", notefprCommercialEvaluation);
							  jsonObject.put("Note for Reverse Auction", noteforReverseAuction);
							  jsonObject.put("Note for Declaring L1 Bidder", noteforDeclaringL1bidder);
							  jsonObject.put("Financial Approval for Final TCO", financialApprovalforFinalTCO);
							  jsonObject.put("Notes for Floating Addendum", NoteforFloatingofRFP);
							  jsonObject.put("Addendum No", AddendumNo);
							  jsonObject.put("Addendum Dated", addendumDated);
							  jsonObject.put("Addendum Subject", addendumSubject);
							  jsonObject.put("Addendum Queries Response", addendumrelatedqueriesresponse);
							  jsonObject.put("Annexures No", annexuresNo);
							  jsonObject.put("Annexures Subject", annexuresSubject);
							  jsonObject.put("Remarks", remarks);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  } else if(repositoryId.equalsIgnoreCase("HOI")) {
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String Subject =  row.getProperties().get("Subject").getStringValue();
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String HODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String creditAudit = row.getProperties().get("CreditAudit").getStringValue();
							  String financialYear = row.getProperties().get("FinancialYear").getStringValue();
							  String quarter = row.getProperties().get("Quarter").getStringValue();
							  String month = row.getProperties().get("Month").getStringValue();
							  Date reportDate1 =  row.getProperties().get("ReportDated").getDateTimeValue();
							  String reportDate = formatFnDate(reportDate1);
							  String branchIsAudit = row.getProperties().get("BRANCHISAUDIT").getStringValue();
							  String rfpTender = row.getProperties().get("RFPTENDER").getStringValue();
							  String isAuditGeneralControls = row.getProperties().get("ISAUDITGENERALCONTROLS").getStringValue();
							  String FINANCIALYEAR_APPLICATIONAUDIT = row.getProperties().get("FINANCIALYEAR_APPLICATIONAUDIT").getStringValue();
							  String vaptAudit = row.getProperties().get("VAPTAUDIT").getStringValue();
							  String FINANCIALYEAR_VAPTAUDIT = row.getProperties().get("FINANCIALYEAR_VAPTAUDIT").getStringValue();
							  String applicationAudit = row.getProperties().get("APPLICATIONAUDIT").getStringValue();
							  String boardACBNotes = row.getProperties().get("BOARDACBNOTES").getStringValue();
							  String FINANCIALYEAR_BOARDACBNOTES = row.getProperties().get("FINANCIALYEAR_BOARDACBNOTES").getStringValue();
							  String committeeITAdvisoryBoardNotes = row.getProperties().get("COMMITTEEITADVISORYBOARDNOTES").getStringValue();
							  Date dataOfRBIA1 =  row.getProperties().get("DateofRBIA").getDateTimeValue();
							  String dataOfRBIA = formatFnDate(dataOfRBIA1);
							  String cca = row.getProperties().get("CCA").getStringValue();
							  String zoDepartment = row.getProperties().get("ZODEPARTMENT").getStringValue();
							  String mai = row.getProperties().get("MAI").getStringValue();
							  String otherAudits = row.getProperties().get("OTHERAUDITS").getStringValue();
							  String ApplicationName = row.getProperties().get("ApplicationName").getStringValue();
							  String remark = row.getProperties().get("Remark").getStringValue();
							  String referenceDocuments = row.getProperties().get("ReferenceDocument").getStringValue();
							  String  miscAudit= row.getProperties().get("MiscAudit").getStringValue();
							  String vorType = row.getProperties().get("VORType").getStringValue();
							  String  zoneName= row.getProperties().get("Zone_Name").getStringValue();
							  String  fileName= row.getProperties().get("FileName").getStringValue();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Credit Audit", creditAudit);
							  jsonObject.put("Financial Year", financialYear);
							  jsonObject.put("Financial Year VAPT Audit", FINANCIALYEAR_VAPTAUDIT);
							  jsonObject.put("Financial Year BoardACBNotes", FINANCIALYEAR_BOARDACBNOTES);
							  jsonObject.put("Financial Year ApplicationAudit", FINANCIALYEAR_APPLICATIONAUDIT);
							  jsonObject.put("Quarter", quarter);
							  jsonObject.put("Month", month);
							  jsonObject.put("Report Date", reportDate);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Date", date);
							  jsonObject.put("Remark", remark);
							  jsonObject.put("Reference Documents", referenceDocuments);
							  jsonObject.put("ZO Department", zoDepartment);
							  jsonObject.put("MISC Audit", miscAudit);
							  jsonObject.put("VOR Type", vorType);
							  jsonObject.put("Zone Name", zoneName);
							  jsonObject.put("CCA", cca);
							  jsonObject.put("MAI", mai);
							  jsonObject.put("RFP Tender", rfpTender);
							  jsonObject.put("Is Audit General Controls", isAuditGeneralControls);
							  jsonObject.put("VAPT Audit", vaptAudit);
							  jsonObject.put("Application Audit", applicationAudit);
							  jsonObject.put("Other Audits", otherAudits);
							  jsonObject.put("Board ACB Notes", boardACBNotes);
							  jsonObject.put("Committee IT Advisory Board Notes", committeeITAdvisoryBoardNotes);
							  jsonObject.put("Branch Is Audit", branchIsAudit);
							  jsonObject.put("Data of RBIA", dataOfRBIA);
							  jsonObject.put("File Name", fileName);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  }

					  else if(repositoryId.equalsIgnoreCase("HOLR")) {
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String Subject =  row.getProperties().get("Subject").getStringValue();
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  StringList guarantorName1 =  row.getProperties().get("GUARANTORNAME").getStringListValue();
							  String guarantorName = formatStringList(guarantorName1);
							  StringList customerName1 =  row.getProperties().get("CUSTOMERNAME").getStringListValue();
							  String customerName = formatStringList(customerName1);
							  StringList accountNo1 =  row.getProperties().get("AccountNo").getStringListValue();
							  String accountNo = formatStringList(accountNo1);
							  StringList accountName1 =  row.getProperties().get("ACCOUNTNAME").getStringListValue();
							  String accountName = formatStringList(accountName1);
							  String customerAddress =  row.getProperties().get("CUSTOMERADDRESS").getStringValue();
							  String guarantorAddress =  row.getProperties().get("GUARANTORADDRESS").getStringValue();
							  Date dateOfNPA1 =  row.getProperties().get("DATEOFNPA").getDateTimeValue();
							  String dateOfNPA = formatFnDate(dateOfNPA1);
							  String securitiesType =  row.getProperties().get("SECURITIESTYPE").getStringValue();
							  String securityDetails =  row.getProperties().get("SECURITYDETAILS").getStringValue();
							  String securitiesDetails =  row.getProperties().get("SECURITIESDETAILS").getStringValue();
							  System.out.println("balanceOutstanding");
							  Double balanceOutstanding =  row.getProperties().get("BalanceOutstanding").getFloat64Value();
							  System.out.println("balanceOutstanding"+balanceOutstanding);
							  String subjectMatter =  row.getProperties().get("SUBJECTMATTER").getStringValue();
							  String enforcementAgentName =  row.getProperties().get("ENFORCEMENTAGENCYNAME").getStringValue();
							  StringList partyName1 =  row.getProperties().get("PARTYNAME").getStringListValue();
							  String partyName = formatStringList(partyName1);
							  StringList accountNameAndPartyName1 =  row.getProperties().get("ACCOUNTNAMEPARTYNAME").getStringListValue();
							  String accountNameAndPartyName = formatStringList(accountNameAndPartyName1);
							  StringList borrowerName1 =  row.getProperties().get("BorrowerName").getStringListValue();
							  String borrowerName = formatStringList(borrowerName1);
							  String customerId =  row.getProperties().get("CustomerID").getStringValue();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("HO Department Code", hODepartmentCode);
							  jsonObject.put("Customer Name", customerName);
							  jsonObject.put("Account No", accountNo);
							  jsonObject.put("Guarantor Name", guarantorName);
							  jsonObject.put("Customer Address", customerAddress);
							  jsonObject.put("Guarantor Address", guarantorAddress);
							  jsonObject.put("Date of NPA", dateOfNPA);
							  jsonObject.put("Securities Type", securitiesType);
							  jsonObject.put("Securities Details", securitiesDetails);
							  jsonObject.put("Balance Outstanding", balanceOutstanding);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Account Name", accountName);
							  jsonObject.put("Security Details", securityDetails);
							  jsonObject.put("Subject Matter", subjectMatter);
							  jsonObject.put("Enforcement Agent Name", enforcementAgentName);
							  jsonObject.put("Party Name", partyName);
							  jsonObject.put("Account Name and Party Name", accountNameAndPartyName);
							  jsonObject.put("Borrower Name", borrowerName);
							  jsonObject.put("Customer ID", customerId);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

						  }

					  }else if(repositoryId.equalsIgnoreCase("HOGBM")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String accountNumber =  row.getProperties().get("AccountNumber").getStringValue();
							  StringList customerName1 =  row.getProperties().get("CustomerName").getStringListValue();
							  String customerName = formatStringList(customerName1);
							  String cif =  row.getProperties().get("CIF").getStringValue();
							  Date accountOpenDate1 =  row.getProperties().get("AccountOpenDate").getDateTimeValue();
							  String accountOpenDate = formatFnDate(accountOpenDate1);
							  StringList mobile1 =  row.getProperties().get("Mobile").getStringListValue();
							  String mobile = formatStringList(mobile1);
							  String pran =  row.getProperties().get("PRAN").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String noteReferenceNumber =  row.getProperties().get("NoteReferenceNumber").getStringValue();
							  String noteSubject =  row.getProperties().get("NoteSubject").getStringValue();
							  String pPFAcNo =  row.getProperties().get("PPFAcNo").getStringValue();
							  String pan =  row.getProperties().get("PAN").getStringValue();
							  String accountHolderName =  row.getProperties().get("AccountHolderName").getStringValue();
							  StringList nomineeName1 =  row.getProperties().get("NomineeName").getStringListValue();
							  String nomineeName = formatStringList(nomineeName1);
							  String jointHolderName =  row.getProperties().get("JointHolderName").getStringValue();
							  String scssAcNo =  row.getProperties().get("SCSSAcNo").getStringValue();
							  String scssCustId =  row.getProperties().get("SCSSCustId").getStringValue();
							  String nomineeAcNo =  row.getProperties().get("NomineeAcNo").getStringValue();
							  String sopReferenceNumber =  row.getProperties().get("SOPReferenceNumber").getStringValue();
							  String sopSchemeSubject =  row.getProperties().get("SOPSchemeSubject").getStringValue();
							  String girlChildName =  row.getProperties().get("GirlChildName").getStringValue();
							  String parentGaurdianName =  row.getProperties().get("ParentGaurdianName").getStringValue();
							  Date dateofBirthofGirlChild1 =  row.getProperties().get("DateofBirthofGirlChild").getDateTimeValue();
							  String dateofBirthofGirlChild = formatFnDate(dateofBirthofGirlChild1);
							  StringList girlChildNameGuardianName1 =  row.getProperties().get("GirlChildNameGuardianName").getStringListValue();
							  String girlChildNameGuardianName = formatStringList(girlChildNameGuardianName1);

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Account Number", accountNumber);
							  jsonObject.put("CIF", cif);
							  jsonObject.put("PRAN", pran);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Note Reference Number", noteReferenceNumber);
							  jsonObject.put("Account OpenDate", accountOpenDate);
							  jsonObject.put("NoteSubject", noteSubject);
							  jsonObject.put("PPFAcNo", pPFAcNo);
							  jsonObject.put("PAN", pan);
							  jsonObject.put("AccountHolderName", accountHolderName);
							  jsonObject.put("NomineeName", nomineeName);
							  jsonObject.put("JointHolderName", jointHolderName);
							  jsonObject.put("SCSSAcNo", scssAcNo);
							  jsonObject.put("SCSSCustId", scssCustId);
							  jsonObject.put("NomineeAcNo", nomineeAcNo);
							  jsonObject.put("SOPReferenceNumber", sopReferenceNumber);
							  jsonObject.put("SOPSchemeSubject", sopSchemeSubject);
							  jsonObject.put("GirlChildName", girlChildName);
							  jsonObject.put("ParentGaurdianName", parentGaurdianName);
							  jsonObject.put("DateofBirthofGirlChild", dateofBirthofGirlChild);
							  jsonObject.put("GirlChildNameGuardianName", girlChildNameGuardianName);
							  jsonObject.put("Mobile", mobile);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

						  }

					  }else if(repositoryId.equalsIgnoreCase("HOHRD")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String employeePFCode =  row.getProperties().get("EMPLOYEEPFCODE").getStringValue();
							  String doctorName =  row.getProperties().get("DOCTORNAME").getStringValue();
							  String airlinesName =  row.getProperties().get("AIRLINESNAME").getStringValue();
							  String month =  row.getProperties().get("MONTH").getStringValue();
							  String financialYear =  row.getProperties().get("FINANCIALYEAR").getStringValue();
							  String boardResolutionNo =  row.getProperties().get("BoardResolutionNo").getStringValue();
							  String noteType =  row.getProperties().get("NoteType").getStringValue();
							  Date date1 =  row.getProperties().get("DATE").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String subjectoftheCorrespondance =  row.getProperties().get("SubjectoftheCorrespondance").getStringValue();
							  String issuingDepartment =  row.getProperties().get("ISSUINGDEPARTMENT").getStringValue();
							  String designationOfficeName =  row.getProperties().get("DESIGNATIONOFFICENAME").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String subject =  row.getProperties().get("SUBJECT").getStringValue();
							  String caseType =  row.getProperties().get("CASETYPE").getStringValue();
							  String courtPremises =  row.getProperties().get("COURTPREMISES").getStringValue();
							  String caseNumber =  row.getProperties().get("CASENUMBER").getStringValue();
							  String caseRegYear =  row.getProperties().get("CASEREGYEAR").getStringValue();
							  String petitionerApplicant =  row.getProperties().get("PETITIONERAPPLICANT").getStringValue();
							  String respondent =  row.getProperties().get("RESPONDENT").getStringValue();
							  String advocate =  row.getProperties().get("ADVOCATE").getStringValue();
							  String brief =  row.getProperties().get("BRIEF").getStringValue();
							  Date interimOrder1 =  row.getProperties().get("INTERIMORDER").getDateTimeValue();
							  String interimOrder = formatFnDate(interimOrder1);
							  Date finalOrder1 =  row.getProperties().get("FINALORDER").getDateTimeValue();
							  String finalOrder = formatFnDate(finalOrder1);
							  Date vakalatnama1 =  row.getProperties().get("VAKALATNAMA").getDateTimeValue();
							  String vakalatnama = formatFnDate(vakalatnama1);
							  Date affidavit1 =  row.getProperties().get("AFFIDAVIT").getDateTimeValue();
							  String affidavit = formatFnDate(affidavit1);
							  Date counterAffidavit1 =  row.getProperties().get("COUNTERAFFIDAVIT").getDateTimeValue();
							  String counterAffidavit = formatFnDate(counterAffidavit1);
							  Date rejoinder1 =  row.getProperties().get("REJOINDER").getDateTimeValue();
							  String rejoinder = formatFnDate(rejoinder1);
							  Date correspondance1 =  row.getProperties().get("CORRESPONDANCE").getDateTimeValue();
							  String correspondance = formatFnDate(correspondance1);
							  String oldCaseType =  row.getProperties().get("OLDCASETYPE").getStringValue();
							  String caseType2 =  row.getProperties().get("CASETYPE2").getStringValue();
							  String officeNote =  row.getProperties().get("OFFICENOTE").getStringValue();
							  String da =  row.getProperties().get("DA").getStringValue();
							  Date replyDate1 =  row.getProperties().get("REPLYDATE").getDateTimeValue();
							  String replyDate = formatFnDate(replyDate1);
							  String unionType =  row.getProperties().get("UNIONTYPE").getStringValue();
							  String unionName =  row.getProperties().get("UNIONNAME").getStringValue();
							  String quarter =  row.getProperties().get("QUARTER").getStringValue();
							  Date irMeetingDate =  row.getProperties().get("IRMEETINGDATE").getDateTimeValue();
							  Date minutes1 =  row.getProperties().get("MINUTES").getDateTimeValue();
							  String minutes = formatFnDate(minutes1);
							  String type =  row.getProperties().get("TYPE").getStringValue();
							  String subType =  row.getProperties().get("SUBTYPE").getStringValue();
							  String authority =  row.getProperties().get("AUTHORITY").getStringValue();
							  Date order1 =  row.getProperties().get("ORDER").getDateTimeValue();
							  String order = formatFnDate(order1);
							  String nameOfInsuranceCompany =  row.getProperties().get("NAMEOFINSURANCECOMPANY").getStringValue();
							  String nameOfTPA =  row.getProperties().get("NAMEOFTPA").getStringValue();
							  String policyYear =  row.getProperties().get("POLICYYEAR").getStringValue();
							  String refNo =  row.getProperties().get("RefNo").getStringValue();
							  String agendaItemNo =  row.getProperties().get("AgendaItemNo").getStringValue();
							  Date meetingDated1 =  row.getProperties().get("MeetingDated").getDateTimeValue();
							  String meetingDated = formatFnDate(meetingDated1);
							  String agendaItem =  row.getProperties().get("AgendaItem").getStringValue();
							  Date dated1 =  row.getProperties().get("Dated").getDateTimeValue();
							  String dated = formatFnDate(dated1);
							  String hoCircularNo =  row.getProperties().get("HOCircularNo").getStringValue();
							  String deptRefNo =  row.getProperties().get("DeptRefNo").getStringValue();
							  String subjectoftheCircular =  row.getProperties().get("SubjectoftheCircular").getStringValue();
							  String pfCode =  row.getProperties().get("PFCODE").getStringValue();
							  String currentPosting =  row.getProperties().get("CURRENTPOSTING").getStringValue();
							  String name =  row.getProperties().get("PERSONALFILESNAME").getStringValue();
							  String circularNo =  row.getProperties().get("CIRCULARNO").getStringValue();
							  String relatedField =  row.getProperties().get("RELATEDFIELD").getStringValue();
							  String fatherName =  row.getProperties().get("FATHERNAME").getStringValue();
							  String designation =  row.getProperties().get("DESIGNATION").getStringValue();
							  String subjectOfGuidelinesLetters =  row.getProperties().get("SUBJECTOFGUIDELINESLETTERS").getStringValue();
							  String promotionProcessYear =  row.getProperties().get("PROMOTIONPROCESSYEAR").getStringValue();
							  String promotionProcessName =  row.getProperties().get("PROMOTIONPROCESSNAME").getStringValue();
							  String qualificationMarks =  row.getProperties().get("QUALIFICATIONMARKS").getStringValue();
							  String clearances =  row.getProperties().get("CLEARANCES").getStringValue();
							  String challanNo =  row.getProperties().get("CHALLANNO").getStringValue();
							  String policySubject =  row.getProperties().get("PolicySubject").getStringValue();

							  System.out.println("---------- Adding the Data into JSONObject ----------");
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Employee PFCode", employeePFCode);
							  jsonObject.put("DoctorName", doctorName);
							  jsonObject.put("Airlines Name", airlinesName);
							  jsonObject.put("Month", month);
							  jsonObject.put("FinancialYear", financialYear);
							  jsonObject.put("Board ResolutionNo", boardResolutionNo);
							  jsonObject.put("NoteType", noteType);
							  jsonObject.put("Date", date);
							  jsonObject.put("Subject of the Correspondance", subjectoftheCorrespondance);
							  jsonObject.put("Issuing Department", issuingDepartment);
							  jsonObject.put("Designation OfficeName", designationOfficeName);
							  jsonObject.put("DocumentType", documentType);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("CaseType", caseType);
							  jsonObject.put("CourtPremises", courtPremises);
							  jsonObject.put("Case Number", caseNumber);
							  jsonObject.put("Case Reg Year", caseRegYear);
							  jsonObject.put("Petitioner Applicant", petitionerApplicant);
							  jsonObject.put("Respondent", respondent);
							  jsonObject.put("Advocate", advocate);
							  jsonObject.put("Brief", brief);
							  jsonObject.put("InterimOrder", interimOrder);
							  jsonObject.put("FinalOrder", finalOrder);
							  jsonObject.put("Vakalatnama", vakalatnama);
							  jsonObject.put("Affidavit", affidavit);
							  jsonObject.put("Counter Affidavit", counterAffidavit);
							  jsonObject.put("Rejoinder", rejoinder);
							  jsonObject.put("Correspondance", correspondance);
							  jsonObject.put("OldCaseType", oldCaseType);
							  jsonObject.put("CaseType2", caseType2);
							  jsonObject.put("OfficeNote", officeNote);
							  jsonObject.put("DA", da);
							  jsonObject.put("ReplyDate", replyDate);
							  jsonObject.put("UnionType", unionType);
							  jsonObject.put("UnionName", unionName);
							  jsonObject.put("Quarter", quarter);
							  jsonObject.put("IRMeetingDate", irMeetingDate);
							  jsonObject.put("Minutes", minutes);
							  jsonObject.put("Type", type);
							  jsonObject.put("SubType", subType);
							  jsonObject.put("Authority", authority);
							  jsonObject.put("order", order);
							  jsonObject.put("NameOfInsuranceCompany", nameOfInsuranceCompany);
							  jsonObject.put("NameOfTPA", nameOfTPA);
							  jsonObject.put("PolicyYear", policyYear);
							  jsonObject.put("RefNo", refNo);
							  jsonObject.put("AgendaItemNo", agendaItemNo);
							  jsonObject.put("MeetingDated", meetingDated);
							  jsonObject.put("AgendaItem", agendaItem);
							  jsonObject.put("Dated", dated);
							  jsonObject.put("HO CircularNo", hoCircularNo);
							  jsonObject.put("Dept RefNo", deptRefNo);
							  jsonObject.put("Subject of the Circular", subjectoftheCircular);
							  jsonObject.put("PF Code", pfCode);
							  jsonObject.put("Current Posting", currentPosting);
							  jsonObject.put("Personal FilesName", name);
							  jsonObject.put("CircularNo", circularNo);
							  jsonObject.put("Related Field", relatedField);
							  jsonObject.put("Father Name", fatherName);
							  jsonObject.put("Designation", designation);
							  jsonObject.put("Subject Of Guidelines Letters", subjectOfGuidelinesLetters);
							  jsonObject.put("Promotion ProcessYear", promotionProcessYear);
							  jsonObject.put("Promotion ProcessName", promotionProcessName);
							  jsonObject.put("Qualification Marks", qualificationMarks);
							  jsonObject.put("CLEARANCES", clearances);
							  jsonObject.put("CHALLAN NO", challanNo);
							  jsonObject.put("Policy Subject", policySubject);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

						  }

					  }else if(repositoryId.equalsIgnoreCase("HOFOREX")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String fy =  row.getProperties().get("FY").getStringValue();
							  String frequency =  row.getProperties().get("FREQUENCY").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String tenderRFPNo =  row.getProperties().get("TenderRFPNo").getStringValue();
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String hoDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String rfpSubject =  row.getProperties().get("RFPSubject").getStringValue();
							  Date selectedBiddersOfficeNoteDate1 =  row.getProperties().get("SelectedBiddersOfficeNoteDate").getDateTimeValue();
							  String SelectedBiddersOfficeNoteDate = formatFnDate(selectedBiddersOfficeNoteDate1);
							  Date poDate1 =  row.getProperties().get("PODate").getDateTimeValue();
							  String poDate = formatFnDate(poDate1);
							  Date slaDate1 =  row.getProperties().get("SLADate").getDateTimeValue();
							  String SLADate = formatFnDate(slaDate1);
							  Date bgDate1 =  row.getProperties().get("BGDate").getDateTimeValue();
							  String bgDate = formatFnDate(bgDate1);
							  Float64List bgAmount1 = row.getProperties().get("BGAmount").getFloat64ListValue();
							  String bgAmount = formatFloatList(bgAmount1);
							  String auditType =  row.getProperties().get("AUDITTYPE").getStringValue();
							  Date auditDate1 =  row.getProperties().get("AuditDate").getDateTimeValue();
							  String auditDate = formatFnDate(auditDate1);
							  String auditSubject =  row.getProperties().get("AuditSubject").getStringValue();
							  String auditorFirmIfAny =  row.getProperties().get("AuditorFirmIfAny").getStringValue();
							  String dataDetails =  row.getProperties().get("DataDetails").getStringValue();
							  Date dataReceivingDate1 =  row.getProperties().get("DataReceivingDate").getDateTimeValue();
							  String dataReceivingDate = formatFnDate(dataReceivingDate1);
							  String dataCollectedfrom =  row.getProperties().get("DataCollectedfrom").getStringValue();
							  String dataRectificationIfany =  row.getProperties().get("DataRectificationIfany").getStringValue();
							  Date revisedDataReceivedon1 =  row.getProperties().get("RevisedDataReceivedon").getDateTimeValue();
							  String revisedDataReceivedon = formatFnDate(revisedDataReceivedon1);
							  String revisedDataDetails =  row.getProperties().get("RevisedDataDetails").getStringValue();
							  String noteUpto =  row.getProperties().get("NOTEUPTO").getStringValue();
							  String subject =  row.getProperties().get("SUBJECT").getStringValue();
							  String invoiceNo =  row.getProperties().get("INVOICENO").getStringValue();
							  String venderName =  row.getProperties().get("VENDORNAME").getStringValue();
							  Date reportingDate1 =  row.getProperties().get("ReportingDate").getDateTimeValue();
							  String reportingDate = formatFnDate(reportingDate1);
							  String reportingto =  row.getProperties().get("Reportingto").getStringValue();

							  Date lastDateforReporting1 =  row.getProperties().get("LastDateforReporting").getDateTimeValue();
							  String lastDateforReporting = formatFnDate(lastDateforReporting1);
							  StringList scopeofRFP1 =  row.getProperties().get("ScopeofRFP").getStringListValue();
							  String scopeofRFP = formatStringList(scopeofRFP1);
							  String tenderType =  row.getProperties().get("TenderType").getStringValue();
							  String bidSystem =  row.getProperties().get("BidSystem").getStringValue();
							  StringList initialBiddersName1 =  row.getProperties().get("InitialBiddersName").getStringListValue();
							  String initialBiddersName = formatStringList(initialBiddersName1);
							  StringList docType1 =  row.getProperties().get("DocType").getStringListValue();
							  String docType = formatStringList(docType1);
							  Date bidSubmissionDate1 =  row.getProperties().get("BidSubmissionDate").getDateTimeValue();
							  String bidSubmissionDate = formatFnDate(bidSubmissionDate1);
							  StringList bidQuery1 =  row.getProperties().get("BidQuery").getStringListValue();
							  String bidQuery = formatStringList(bidQuery1);
							  StringList bidQueryType1 =  row.getProperties().get("BidQueryType").getStringListValue();
							  String bidQueryType = formatStringList(bidQueryType1);
							  Date bidQueryDate1 =  row.getProperties().get("BidQueryDate").getDateTimeValue();
							  String bidQueryDate = formatFnDate(bidQueryDate1);
							  Integer32List rAAmt1 = row.getProperties().get("RAAmt").getInteger32ListValue();
							  String rAAmt = formatIntegerList(rAAmt1);
							  Date rADate1 =  row.getProperties().get("RADate").getDateTimeValue();
							  String rADate = formatFnDate(rADate1);
							  Date rANoteDate1 =  row.getProperties().get("RANoteDate").getDateTimeValue();
							  String rANoteDate = formatFnDate(rANoteDate1);
							  String oofFinalvendors =  row.getProperties().get("NoofFinalvendors").getStringValue();
							  Date pODate1 =  row.getProperties().get("PODate").getDateTimeValue();
							  String pODate = formatFnDate(pODate1);
							  String addendumNo =  row.getProperties().get("AddendumNo").getStringValue();
							  StringList addendumSubject1 =  row.getProperties().get("AddendumSubject").getStringListValue();
							  String addendumSubject = formatStringList(addendumSubject1);
							  Date addendumDate1 =  row.getProperties().get("AddendumDate").getDateTimeValue();
							  String addendumDate = formatFnDate(addendumDate1);
							  String projectConcludingYear =  row.getProperties().get("ProjectConcludingYear").getStringValue();

							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Class Name", className);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("FY", fy);
							  jsonObject.put("Frequency", frequency);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Tender RFP No", tenderRFPNo);
							  jsonObject.put("Date", date);
							  jsonObject.put("HO Department Code", hoDepartmentCode);
							  jsonObject.put("RFP Subject", rfpSubject);
							  jsonObject.put("Selected Bidders Office Note Date", SelectedBiddersOfficeNoteDate);
							  jsonObject.put("PO Date", poDate);
							  jsonObject.put("SLA Date", SLADate);
							  jsonObject.put("BG Date", bgDate);
							  jsonObject.put("BG Amount", bgAmount);
							  jsonObject.put("Audit Type", auditType);
							  jsonObject.put("Audit Date", auditDate);
							  jsonObject.put("Audit Subject", auditSubject);
							  jsonObject.put("Auditor Firm If Any", auditorFirmIfAny);
							  jsonObject.put("Data Details", dataDetails);
							  jsonObject.put("Data Receiving Date", dataReceivingDate);
							  jsonObject.put("Data Collected from", dataCollectedfrom);
							  jsonObject.put("Data Rectification If Any", dataRectificationIfany);
							  jsonObject.put("Revised Data Received on", revisedDataReceivedon);
							  jsonObject.put("Revised Data Details", revisedDataDetails);
							  jsonObject.put("Note Upto", noteUpto);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Invoice No", invoiceNo);
							  jsonObject.put("Vendor Name", venderName);
							  jsonObject.put("Reporting Date", reportingDate);
							  jsonObject.put("Reporting to", reportingto);
							  jsonObject.put("Last Date for Reporting", lastDateforReporting);
							  jsonObject.put("Scope of RFP", scopeofRFP);
							  jsonObject.put("Tender Type", tenderType);
							  jsonObject.put("Bid System", bidSystem);
							  jsonObject.put("Initial Bidders Name", initialBiddersName);
							  jsonObject.put("Doc Type", docType);
							  jsonObject.put("Bid Submission Date", bidSubmissionDate);
							  jsonObject.put("Bid Query", bidQuery);
							  jsonObject.put("Bid Query Type", bidQueryType);
							  jsonObject.put("Bid Query Date", bidQueryDate);
							  jsonObject.put("RA Amt", rAAmt);
							  jsonObject.put("RA Date", rADate);
							  jsonObject.put("RA Note Date", rANoteDate);
							  jsonObject.put("No of Final Vendors", oofFinalvendors);
							  jsonObject.put("PO Date", pODate);
							  jsonObject.put("Addendum No", addendumNo);
							  jsonObject.put("Addendum Subject", addendumSubject);
							  jsonObject.put("Addendum Date", addendumDate);
							  jsonObject.put("Project Concluding Year", projectConcludingYear);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");
						  }
					  }else if(repositoryId.equalsIgnoreCase("HOKYC")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String advisorySubject =  row.getProperties().get("ADVISORYsubject").getStringValue();
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String issuedTo =  row.getProperties().get("ISSUEDTO").getStringValue();
							  String month =  row.getProperties().get("MONTH").getStringValue();
							  String year =  row.getProperties().get("YEAR").getStringValue();
							  String agendaNo =  row.getProperties().get("AGENDANO").getStringValue();
							  String boardNoteSubject =  row.getProperties().get("BOARDNOTEsubject").getStringValue();
							  String auditType =  row.getProperties().get("AUDITTYPE").getStringValue();
							  String subjectRef =  row.getProperties().get("SUBJECTREF").getStringValue();
							  Date reportDate1 =  row.getProperties().get("REPORTDATE").getDateTimeValue();
							  String reportDate = formatFnDate(reportDate1);
							  String status =  row.getProperties().get("STATUS").getStringValue();
							  String billType =  row.getProperties().get("BILLTYPE").getStringValue();
							  String vendorName =  row.getProperties().get("VENDORNAME").getStringValue();
							  String vendorGSTNo =  row.getProperties().get("VENDORGSTNO").getStringValue();
							  String billNumber =  row.getProperties().get("BILLNUMBER").getStringValue();
							  Date billDate1 =  row.getProperties().get("BILLDATE").getDateTimeValue();
							  String billDate = formatFnDate(billDate1);
							  Date billPaymentDate1 =  row.getProperties().get("BILLPAYMENTDATE").getDateTimeValue();
							  String billPaymentDate = formatFnDate(billPaymentDate1);
							  String hoDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
							  String institution =  row.getProperties().get("INSTITUTION").getStringValue();
							  String subject =  row.getProperties().get("SUBJECT").getStringValue();
							  String returnType =  row.getProperties().get("RETURNTYPE").getStringValue();
							  String provisionalReceiptNo =  row.getProperties().get("PROVISIONALRECEIPTNO").getStringValue();
							  String leaName =  row.getProperties().get("LEANAME").getStringValue();
							  String noticeType =  row.getProperties().get("NOTICETYPE").getStringValue();
							  Date noticeDate1 =  row.getProperties().get("NOTICEDATE").getDateTimeValue();
							  String noticeDate = formatFnDate(noticeDate1);
							  String panNo =  row.getProperties().get("PANNO").getStringValue();
							  String aadharNo =  row.getProperties().get("AADHARNO").getStringValue();
							  String accountNo =  row.getProperties().get("ACCOUNTNO").getStringValue();
							  Date freezeDate1 =  row.getProperties().get("FREEZEDATE").getDateTimeValue();
							  String freezeDate = formatFnDate(freezeDate1);
							  Date unFreezeDate1 =  row.getProperties().get("UNFREEZEDATE").getDateTimeValue();
							  String unFreezeDate = formatFnDate(unFreezeDate1);
							  Date dueDate1 =  row.getProperties().get("DUEDATE").getDateTimeValue();
							  String dueDate = formatFnDate(dueDate1);
							  String complianceStatus =  row.getProperties().get("COMPLIANCESTATUS").getStringValue();
							  String directions =  row.getProperties().get("DIRECTIONS").getStringValue();
							  Date complianceDate1 =  row.getProperties().get("COMPLIANCEDATE").getDateTimeValue();
							  String complianceDate = formatFnDate(complianceDate1);
							  
							// Continue building the JSON object
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Advisory Subject", advisorySubject);
							  jsonObject.put("Date", date);
							  jsonObject.put("Issued To", issuedTo);
							  jsonObject.put("Month", month);
							  jsonObject.put("Year", year);
							  jsonObject.put("Agenda No", agendaNo);
							  jsonObject.put("Board Note Subject", boardNoteSubject);
							  jsonObject.put("Audit Type", auditType);
							  jsonObject.put("Subject Ref", subjectRef);
							  jsonObject.put("Report Date", reportDate);
							  jsonObject.put("Status", status);
							  jsonObject.put("Bill Type", billType);
							  jsonObject.put("Vendor Name", vendorName);
							  jsonObject.put("Vendor GST No", vendorGSTNo);
							  jsonObject.put("Bill Number", billNumber);
							  jsonObject.put("Bill Date", billDate);
							  jsonObject.put("Bill Payment Date", billPaymentDate);
							  jsonObject.put("HO Department Code", hoDepartmentCode);
							  jsonObject.put("Institution", institution);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Return Type", returnType);
							  jsonObject.put("Provisional Receipt No", provisionalReceiptNo);
							  jsonObject.put("LEA Name", leaName);
							  jsonObject.put("Notice Type", noticeType);
							  jsonObject.put("Notice Date", noticeDate);
							  jsonObject.put("PAN No", panNo);
							  jsonObject.put("Aadhar No", aadharNo);
							  jsonObject.put("Account No", accountNo);
							  jsonObject.put("Freeze Date", freezeDate);
							  jsonObject.put("Unfreeze Date", unFreezeDate);
							  jsonObject.put("Due Date", dueDate);
							  jsonObject.put("Compliance Status", complianceStatus);
							  jsonObject.put("Directions", directions);
							  jsonObject.put("Compliance Date", complianceDate);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

							  
						  }
					  }else if(repositoryId.equalsIgnoreCase("HOMI")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String accountNumber =  row.getProperties().get("AccountNumber").getStringValue();
							  String customerName =  row.getProperties().get("CustomerName").getStringValue();
							  String dpClientId =  row.getProperties().get("DPCLIENTID").getStringValue();
							  String gramOfGoldApplied =  row.getProperties().get("GRAMOFGOLDAPPLIED").getStringValue();
							  StringList mobileNumber1 =  row.getProperties().get("Mobilenumber").getStringListValue();
							  String mobileNumber = formatStringList(mobileNumber1);
							  String solId =  row.getProperties().get("SolID").getStringValue();
							  String name =  row.getProperties().get("Names").getStringValue();
							  Date date1 =  row.getProperties().get("DOB").getDateTimeValue();
							  String date = formatFnDate(date1);
							  Date dateofproposalform1 =  row.getProperties().get("Dateofproposalform").getDateTimeValue();
							  String dateofproposalform = formatFnDate(dateofproposalform1);
							  String documentNo =  row.getProperties().get("DocumentNo").getStringValue();
							  String documentType =  row.getProperties().get("DocumentType").getStringValue();
							  String nomineeName =  row.getProperties().get("NomineeName").getStringValue();
							  String noteRefernceNumber =  row.getProperties().get("NoteRefernceNumber").getStringValue();
							  String noteSubject =  row.getProperties().get("NoteSubject").getStringValue();
							  String noteType =  row.getProperties().get("NoteType").getStringValue();
							  
							// Continue building the JSON object
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Account Number", accountNumber);
							  jsonObject.put("Customer Name", customerName);
							  jsonObject.put("DP Client ID", dpClientId);
							  jsonObject.put("Gram of Gold Applied", gramOfGoldApplied);
							  jsonObject.put("Mobile Number", mobileNumber);
							  jsonObject.put("SOL ID", solId);
							  jsonObject.put("Name", name);
							  jsonObject.put("Date of Birth", date);
							  jsonObject.put("Date of Proposal Form", dateofproposalform);
							  jsonObject.put("Document Number", documentNo);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Nominee Name", nomineeName);
							  jsonObject.put("Note Reference Number", noteRefernceNumber);
							  jsonObject.put("Note Subject", noteSubject);
							  jsonObject.put("Note Type", noteType);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

						  }
					  }else if(repositoryId.equalsIgnoreCase("HOOL")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String financialYear =  row.getProperties().get("FINANCIALYEAR").getStringValue();
							  String region =  row.getProperties().get("REGION").getStringValue();
							  String subject =  row.getProperties().get("SUBJECT").getStringValue();
							  Date date1 =  row.getProperties().get("DATE").getDateTimeValue();
							  String date = formatFnDate(date1);
							  String quarter =  row.getProperties().get("QUARTER").getStringValue();
							  
							// Continue building the JSON object
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Financial Year", financialYear);
							  jsonObject.put("Region", region);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Date", date);
							  jsonObject.put("Quarter", quarter);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

							  
						  }
					  }else if(repositoryId.equalsIgnoreCase("HOPSA")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String documentType =  row.getProperties().get("DOCUMENTTYPE").getStringValue();
							  Date dateOfAudit1 =  row.getProperties().get("DATEOFAUDIT").getDateTimeValue();
							  String dateOfAudit = formatFnDate(dateOfAudit1);
							  String auditType =  row.getProperties().get("AUDITTYPE").getStringValue();
							  String timeOfAudit =  row.getProperties().get("TIMEOFAUDIT").getStringValue();
							  String letterType =  row.getProperties().get("LETTERTYPE").getStringValue();
							  Date dateOfLetter1 =  row.getProperties().get("DATEOFLETTER").getDateTimeValue();
							  String dateOfLetter = formatFnDate(dateOfLetter1);
							  StringList subject1 =  row.getProperties().get("SUBJECT").getStringListValue();
							  String subject = formatStringList(subject1);
							  String sentTo =  row.getProperties().get("SENTTO").getStringValue();
							  String receivedFrom =  row.getProperties().get("RECEIVEDFROM").getStringValue();
							  String legalType =  row.getProperties().get("LEGALTYPE").getStringValue();
							  String nameOfPartyVendor =  row.getProperties().get("NAMEOFPARTYVENDOR").getStringValue();
							  Date dateOfAgreementAddendumVoucher1 =  row.getProperties().get("DATEOFAGREEMENTADDENDUMVOUCHER").getDateTimeValue();
							  String dateOfAgreementAddendumVoucher = formatFnDate(dateOfAgreementAddendumVoucher1);
							  System.out.println("invoiceVoucherAmount");
							  Double invoiceVoucherAmount = row.getProperties().get("INVOICEVOUCHERAMOUNT").getFloat64Value();
							  System.out.println("invoiceVoucherAmount"+invoiceVoucherAmount);
							  String noteType =  row.getProperties().get("NoteType").getStringValue();
							  String referenceNo =  row.getProperties().get("ReferenceNo").getStringValue();
							  String staffName =  row.getProperties().get("STAFFNAME").getStringValue();
							  Double amount = row.getProperties().get("AMOUNT").getFloat64Value();
							  String accountNumber =  row.getProperties().get("ACCOUNTNUMBER").getStringValue();
							  String purpose =  row.getProperties().get("PURPOSE").getStringValue();
							  String voucherType =  row.getProperties().get("VoucherType").getStringValue();
							  StringList subsidyName1 =  row.getProperties().get("SUBSIDYNAME").getStringListValue();
							  String subsidyName = formatStringList(subsidyName1);
							  
							// Continue building the JSON object
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Date of Audit", dateOfAudit);
							  jsonObject.put("Audit Type", auditType);
							  jsonObject.put("Time of Audit", timeOfAudit);
							  jsonObject.put("Letter Type", letterType);
							  jsonObject.put("Date of Letter", dateOfLetter);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Sent To", sentTo);
							  jsonObject.put("Received From", receivedFrom);
							  jsonObject.put("Legal Type", legalType);
							  jsonObject.put("Name of Party Vendor", nameOfPartyVendor);
							  jsonObject.put("Date of Agreement/Addendum/Voucher", dateOfAgreementAddendumVoucher);
							  jsonObject.put("Invoice/Voucher Amount", invoiceVoucherAmount);
							  jsonObject.put("Note Type", noteType);
							  jsonObject.put("Reference No", referenceNo);
							  jsonObject.put("Staff Name", staffName);
							  jsonObject.put("Amount", amount);
							  jsonObject.put("Account Number", accountNumber);
							  jsonObject.put("Purpose", purpose);
							  jsonObject.put("Voucher Type", voucherType);
							  jsonObject.put("Subsidy Name", subsidyName);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

						  }
					  }else if(repositoryId.equalsIgnoreCase("HOPSBSEC")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  String noteReferenceNumber =  row.getProperties().get("NoteRefernceNumber").getStringValue();
							  String guidelineName =  row.getProperties().get("GuidelineName").getStringValue();
							  String noteSubject =  row.getProperties().get("NoteSubject").getStringValue();
							  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
							  String date = formatFnDate(date1);
							  StringList noteType1 =  row.getProperties().get("NoteType").getStringListValue();
							  String noteType = formatStringList(noteType1);
							  String policyName =  row.getProperties().get("PolicyName").getStringValue();
							  String policyVersion =  row.getProperties().get("PolicyVersion").getStringValue();
							  String policyYear =  row.getProperties().get("PolicyYear").getStringValue();
							  Date reviewDate1 =  row.getProperties().get("ReviewDate").getDateTimeValue();
							  String reviewDate = formatFnDate(reviewDate1);
							  String sopName =  row.getProperties().get("SOPName").getStringValue();
							  String sopNumber =  row.getProperties().get("SOPNumber").getStringValue();
							  
							// Continue building the JSON object
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Note Reference Number", noteReferenceNumber);
							  jsonObject.put("Guideline Name", guidelineName);
							  jsonObject.put("Note Subject", noteSubject);
							  jsonObject.put("Date", date);
							  jsonObject.put("Note Type", noteType);
							  jsonObject.put("Policy Name", policyName);
							  jsonObject.put("Policy Version", policyVersion);
							  jsonObject.put("Policy Year", policyYear);
							  jsonObject.put("Review Date", reviewDate);
							  jsonObject.put("SOP Name", sopName);
							  jsonObject.put("SOP Number", sopNumber);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

							  
						  }
					  }else if(repositoryId.equalsIgnoreCase("HORL")){
						  Id docId = row.getProperties().get("Id").getIdValue();
						  Document document = Factory.Document.fetchInstance(os, docId, null);
						  String className = document.getClassName();
						  System.out.println("className: "+className);
						  if (CLASSNAMESTOINGORE.contains(className)) {
							  // Skip processing for this class
							  continue;
						  }else{
							  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
							  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
							  String dateOfUploading = formatFnDate(dateOfUploading1);
							  String uploadedBy = row.getProperties().get("Creator").getStringValue();
							  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
							  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
							  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
							  String branchName =  row.getProperties().get("BranchName").getStringValue();
							  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
							  StringList nameOfPartyDDA1 =  row.getProperties().get("NAMEOFPARTYDDA").getStringListValue();
							  String nameOfPartyDDA = formatStringList(nameOfPartyDDA1);
							  Date dateOfAgreement1 =  row.getProperties().get("DATEOFAGREEMENT").getDateTimeValue();
							  String dateOfAgreement = formatFnDate(dateOfAgreement1);
							  String zone =  row.getProperties().get("ZONE").getStringValue();
							  StringList applicantName1 =  row.getProperties().get("APPLICANTNAME").getStringListValue();
							  String applicantName = formatStringList(applicantName1);
							  Integer applicationNumber =  row.getProperties().get("APPLICATIONNUMBER").getInteger32Value();
							  String ifscCode =  row.getProperties().get("IFSCCODE").getStringValue();
							  System.out.println("loanAmount");
							  Double loanAmount = row.getProperties().get("LoanAmount").getFloat64Value();
							  System.out.println("loanAmount"+loanAmount);
							  Double loan_Amount =  row.getProperties().get("LOAN_AMOUNT").getFloat64Value();
							  DateTimeList dates1 = row.getProperties().get("DATES").getDateTimeListValue();
							  String dates = formatDateList(dates1);
							  String referenceNumber =  row.getProperties().get("REFERENCENUMBER").getStringValue();
							  String accountNumber =  row.getProperties().get("ACCOUNTNUMBER").getStringValue();
							  String customerId =  row.getProperties().get("CUSTOMERID").getStringValue();
							  StringList customerName1 =  row.getProperties().get("CUSTOMERNAME").getStringListValue();
							  String customerName = formatStringList(customerName1);
							  Date guaranteeScheme1 =  row.getProperties().get("GUARANTEESCHEME").getDateTimeValue();
							  String guaranteeScheme = formatFnDate(guaranteeScheme1);
							  StringList invoiceDates1 =  row.getProperties().get("INVOICEDATES").getStringListValue();
							  String invoiceDates = formatStringList(invoiceDates1);
							  StringList invoiceNo1 =  row.getProperties().get("INVOICENO").getStringListValue();
							  String invoiceNo = formatStringList(invoiceNo1);
							  Date voucherDates1 =  row.getProperties().get("VOUCHERDATES").getDateTimeValue();
							  String voucherDates = formatFnDate(voucherDates1);
							  Date issueDate1 =  row.getProperties().get("ISSUEDATE").getDateTimeValue();
							  String issueDate = formatFnDate(issueDate1);
							  Date certificateDate1 =  row.getProperties().get("CERTIFICATEDATE").getDateTimeValue();
							  String certificateDate = formatFnDate(certificateDate1);
							  StringList officeNotes1 =  row.getProperties().get("OFFICENOTES").getStringListValue();
							  String officeNotes = formatStringList(officeNotes1);
							  StringList subject1 =  row.getProperties().get("SUBJECT").getStringListValue();
							  String subject = formatStringList(subject1);
							  Date invoiceDate1 =  row.getProperties().get("INVOICEDATE").getDateTimeValue();
							  String invoiceDate = formatFnDate(invoiceDate1);
							  StringList invoiceNumber1 =  row.getProperties().get("INVOICENUMBER").getStringListValue();
							  String invoiceNumber = formatStringList(invoiceNumber1);
							  DateTimeList dateOfLetter1 = row.getProperties().get("DATEOFLETTER").getDateTimeListValue();
							  String dateOfLetter = formatDateList(dateOfLetter1);
							  String sentTo =  row.getProperties().get("SENTTO").getStringValue();
							  String receivedFrom =  row.getProperties().get("RECEIVEDFROM").getStringValue();
							  String documentSubType =  row.getProperties().get("DOCUMENTSUBTYPE").getStringValue();
							  String documentType =  row.getProperties().get("DOCUMENTTYPE").getStringValue();
							  String loanAccountNumber =  row.getProperties().get("LOANACCOUNTNUMBER").getStringValue();
							  String schemaType =  row.getProperties().get("SCHEMETYPE").getStringValue();
							  String officeNote =  row.getProperties().get("OFFICENOTE").getStringValue();
							  Double paymentAmount = row.getProperties().get("PAYMENTAMOUNT").getFloat64Value();
							  StringList purpose1 =  row.getProperties().get("PURPOSE").getStringListValue();
							  String purpose = formatStringList(purpose1);
							  Double amount =  row.getProperties().get("PAYMENTSAMOUNT").getFloat64Value();
							  Double voucherAmount =  row.getProperties().get("VOUCHERAMOUNT").getFloat64Value();
							  Date voucherDate1 =  row.getProperties().get("VOUCHERDATE").getDateTimeValue();
							  String voucherDate = formatFnDate(voucherDate1);
							  
							// Continue building the JSON object
							  JSONObject jsonObject = new JSONObject();
							  jsonObject.put("SI. No", serialNumber);
							  jsonObject.put("Document Title", documentTitle);
							  jsonObject.put("Date of Uploading", dateOfUploading);
							  jsonObject.put("Uploaded By", uploadedBy);
							  jsonObject.put("No Of Pages Uploaded", pageCount);
							  jsonObject.put("Department Name", deptName);
							  jsonObject.put("Branch Name", branchName);
							  jsonObject.put("Branch Code", branchCode);
							  jsonObject.put("Name of Party DDA", nameOfPartyDDA);
							  jsonObject.put("Date of Agreement", dateOfAgreement);
							  jsonObject.put("Zone", zone);
							  jsonObject.put("Applicant Name", applicantName);
							  jsonObject.put("Application Number", applicationNumber);
							  jsonObject.put("IFSC Code", ifscCode);
							  jsonObject.put("Loan Amount", loanAmount);
							  jsonObject.put("LOAN_AMOUNT", loan_Amount);
							  jsonObject.put("Dates", dates);
							  jsonObject.put("Reference Number", referenceNumber);
							  jsonObject.put("Account Number", accountNumber);
							  jsonObject.put("Customer ID", customerId);
							  jsonObject.put("Customer Name", customerName);
							  jsonObject.put("Guarantee Scheme", guaranteeScheme);
							  jsonObject.put("Invoice Dates", invoiceDates);
							  jsonObject.put("Invoice No", invoiceNo);
							  jsonObject.put("Voucher Dates", voucherDates);
							  jsonObject.put("Issue Date", issueDate);
							  jsonObject.put("Certificate Date", certificateDate);
							  jsonObject.put("Office Notes", officeNotes);
							  jsonObject.put("Subject", subject);
							  jsonObject.put("Invoice Date", invoiceDate);
							  jsonObject.put("Invoice Number", invoiceNumber);
							  jsonObject.put("Date of Letter", dateOfLetter);
							  jsonObject.put("Sent To", sentTo);
							  jsonObject.put("Received From", receivedFrom);
							  jsonObject.put("Document SubType", documentSubType);
							  jsonObject.put("Document Type", documentType);
							  jsonObject.put("Loan Account Number", loanAccountNumber);
							  jsonObject.put("Scheme Type", schemaType);
							  jsonObject.put("Office Note", officeNote);
							  jsonObject.put("Payment Amount", paymentAmount);
							  jsonObject.put("Purpose", purpose);
							  jsonObject.put("Amount", amount);
							  jsonObject.put("Voucher Amount", voucherAmount);
							  jsonObject.put("Voucher Date", voucherDate);

							  jsonArray.add(jsonObject);
							  serialNumber++;
							  System.out.println("---------- Data was successfully added into the JSONObject ----------");

							  
					  }
				  }else if(repositoryId.equalsIgnoreCase("HORM")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
						  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
						  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
						  String branchName =  row.getProperties().get("BranchName").getStringValue();
						  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
						  String noteType =  row.getProperties().get("NoteType").getStringValue();
						  String agendaNo =  row.getProperties().get("AGENDANO").getStringValue();
						  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
						  String date = formatFnDate(date1);
						  String boardNoteSubject =  row.getProperties().get("BOARDNOTEsubject").getStringValue();
						  String month =  row.getProperties().get("MONTH").getStringValue();
						  String year =  row.getProperties().get("YEAR").getStringValue();
						  String dailyNotes =  row.getProperties().get("DAILYNOTES").getStringValue();
						  String returnRBINotes =  row.getProperties().get("RETURNRBINOTES").getStringValue();
						  StringList creditNotes_Name1 =  row.getProperties().get("CreditNotes_Name").getStringListValue();
						  String creditNotes_Name = formatStringList(creditNotes_Name1);
						  StringList agenda_No1 =  row.getProperties().get("AGENDA_NO").getStringListValue();
						  String agenda_No = formatStringList(agenda_No1);
						  DateTimeList dates1 = row.getProperties().get("Dates").getDateTimeListValue();
						  String dates = formatDateList(dates1);
						  StringList boardNote_Subject1 =  row.getProperties().get("BOARDNOTE_subject").getStringListValue();
						  String boardNote_Subject = formatStringList(boardNote_Subject1);
						  StringList policyReference1 =  row.getProperties().get("Policyreference").getStringListValue();
						  String policyReference = formatStringList(policyReference1);
						  String companyName =  row.getProperties().get("CompanyName").getStringValue();
						  String rattingGroup =  row.getProperties().get("RattingGroup").getStringValue();
						  
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("No Of Pages Uploaded", pageCount);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Department Code", deptCode);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Note Type", noteType);
						  jsonObject.put("Agenda No", agendaNo);
						  jsonObject.put("Date", date);
						  jsonObject.put("Board Note Subject", boardNoteSubject);
						  jsonObject.put("Month", month);
						  jsonObject.put("Year", year);
						  jsonObject.put("Daily Notes", dailyNotes);
						  jsonObject.put("Return RBI Notes", returnRBINotes);
						  jsonObject.put("Credit Notes Name", creditNotes_Name);
						  jsonObject.put("Agenda No", agenda_No);
						  jsonObject.put("Dates", dates);
						  jsonObject.put("Board Note Subject", boardNote_Subject);
						  jsonObject.put("Policy Reference", policyReference);
						  jsonObject.put("Company Name", companyName);
						  jsonObject.put("Rating Group", rattingGroup);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");

					  }
				  }else if(repositoryId.equalsIgnoreCase("HOS")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
						  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
						  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
						  String branchName =  row.getProperties().get("BranchName").getStringValue();
						  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
						  String tenderNo =  row.getProperties().get("TenderNo").getStringValue();
						  Date date1 =  row.getProperties().get("DATE").getDateTimeValue();
						  String date = formatFnDate(date1);
						  String hoDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
						  String notesforFloatingAddendum =  row.getProperties().get("NotesforFloatingAddendum").getStringValue();
						  String addendumNo =  row.getProperties().get("AddendumNo").getStringValue();
						  Date addendumDated1 =  row.getProperties().get("AddendumDated").getDateTimeValue();
						  String addendumDated = formatFnDate(addendumDated1);
						  StringList addendumSubject1 =  row.getProperties().get("AddendumSubject").getStringListValue();
						  String addendumSubject = formatStringList(addendumSubject1);
						  StringList addendumRelatedQueriesResponse1 =  row.getProperties().get("Addendumrelatedqueriesresponse").getStringListValue();
						  String addendumRelatedQueriesResponse = formatStringList(addendumRelatedQueriesResponse1);
						  StringList annexuresNo1 =  row.getProperties().get("AnnexuresNo").getStringListValue();
						  String annexuresNo = formatStringList(annexuresNo1);
						  StringList annexuresSubject1 =  row.getProperties().get("AnnexuresSubject").getStringListValue();
						  String annexuresSubject = formatStringList(annexuresSubject1);
						  Date dateOfAudit1 =  row.getProperties().get("DATEOFAUDIT").getDateTimeValue();
						  String dateOfAudit = formatFnDate(dateOfAudit1);
						  String auditType =  row.getProperties().get("AUDITTYPE").getStringValue();
						  String documentType =  row.getProperties().get("DocumentType").getStringValue();
						  String timeOfAudit =  row.getProperties().get("TIMEOFAUDIT").getStringValue();
						  String serviceName =  row.getProperties().get("SERVICENAME").getStringValue();
						  String serviceProvider =  row.getProperties().get("SERVICEPROVIDER").getStringValue();
						  StringList subject1 =  row.getProperties().get("SUBJECT").getStringListValue();
						  String subject = formatStringList(subject1);
						  String sentTo =  row.getProperties().get("SENTTO").getStringValue();
						  String receivedFrom =  row.getProperties().get("RECEIVEDFROM").getStringValue();
						  String generalStationery =  row.getProperties().get("GENERALSTATIONERY").getStringValue();
						  String security =  row.getProperties().get("SECURITY").getStringValue();
						  String type =  row.getProperties().get("Type").getStringValue();
						  String nameOfPartyVendor =  row.getProperties().get("NAMEOFPARTYVENDOR").getStringValue();
						  String itemCode =  row.getProperties().get("ITEMCODE").getStringValue();
						  String itemName =  row.getProperties().get("ITEMNAME").getStringValue();
						  String invoiceNumber =  row.getProperties().get("INVOICENUMBER").getStringValue();
						  String challanNo =  row.getProperties().get("CHALLANNO").getStringValue();
						  String referenceNo =  row.getProperties().get("ReferenceNo").getStringValue();
						  String staffName =  row.getProperties().get("STAFFNAME").getStringValue();
						  String noteType =  row.getProperties().get("NoteType").getStringValue();
						  String accountNo =  row.getProperties().get("AccountNo").getStringValue();
						  String inprincipalNoteforFloatingofRFP =  row.getProperties().get("InprincipalNoteforFloatingofRFP").getStringValue();
						  StringList noteforFloatingofRFP1 =  row.getProperties().get("NoteforFloatingofRFP").getStringListValue();
						  String noteforFloatingofRFP = formatStringList(noteforFloatingofRFP1);
						  StringList prebidQueriesResponsefrombidder1 =  row.getProperties().get("PrebidQueriesResponsefrombidder").getStringListValue();
						  String prebidQueriesResponsefrombidder = formatStringList(prebidQueriesResponsefrombidder1);
						  StringList bidDocuments1 =  row.getProperties().get("BidDocuments").getStringListValue();
						  String bidDocuments = formatStringList(bidDocuments1);
						  StringList noteforTechnicalEvaluation1 =  row.getProperties().get("NoteforTechnicalEvaluation").getStringListValue();
						  String noteforTechnicalEvaluation = formatStringList(noteforTechnicalEvaluation1);
						  StringList notefprCommercialEvaluation1 =  row.getProperties().get("NotefprCommercialEvaluation").getStringListValue();
						  String notefprCommercialEvaluation = formatStringList(notefprCommercialEvaluation1);
						  StringList noteforReverseAuction1 =  row.getProperties().get("NoteforReverseAuction").getStringListValue();
						  String noteforReverseAuction = formatStringList(noteforReverseAuction1);
						  StringList noteforDeclaringL1bidder1 =  row.getProperties().get("NoteforDeclaringL1bidder").getStringListValue();
						  String noteforDeclaringL1bidder = formatStringList(noteforDeclaringL1bidder1);
						  StringList financialApprovalforFinalTCO1 =  row.getProperties().get("FinancialApprovalforFinalTCO").getStringListValue();
						  String financialApprovalforFinalTCO = formatStringList(financialApprovalforFinalTCO1);
						  StringList miscnotesregradingTender1 =  row.getProperties().get("MiscnotesregradingTender").getStringListValue();
						  String miscnotesregradingTender = formatStringList(miscnotesregradingTender1);
						  String remarks =  row.getProperties().get("Remarks").getStringValue();
						  String printerName =  row.getProperties().get("PRINTERNAME").getStringValue();
						  String itemNo =  row.getProperties().get("ITEMNO").getStringValue();
						  
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("SI. No", serialNumber);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("No Of Pages Uploaded", pageCount);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Tender No", tenderNo);
						  jsonObject.put("Date", date);
						  jsonObject.put("HO Department Code", hoDepartmentCode);
						  jsonObject.put("Notes for Floating Addendum", notesforFloatingAddendum);
						  jsonObject.put("Addendum No", addendumNo);
						  jsonObject.put("Addendum Dated", addendumDated);
						  jsonObject.put("Addendum Subject", addendumSubject);
						  jsonObject.put("Addendum Related Queries Response", addendumRelatedQueriesResponse);
						  jsonObject.put("Annexures No", annexuresNo);
						  jsonObject.put("Annexures Subject", annexuresSubject);
						  jsonObject.put("Date of Audit", dateOfAudit);
						  jsonObject.put("Audit Type", auditType);
						  jsonObject.put("Document Type", documentType);
						  jsonObject.put("Time of Audit", timeOfAudit);
						  jsonObject.put("Service Name", serviceName);
						  jsonObject.put("Service Provider", serviceProvider);
						  jsonObject.put("Subject", subject);
						  jsonObject.put("Sent To", sentTo);
						  jsonObject.put("Received From", receivedFrom);
						  jsonObject.put("General Stationery", generalStationery);
						  jsonObject.put("Security", security);
						  jsonObject.put("Type", type);
						  jsonObject.put("Name of Party Vendor", nameOfPartyVendor);
						  jsonObject.put("Item Code", itemCode);
						  jsonObject.put("Item Name", itemName);
						  jsonObject.put("Invoice Number", invoiceNumber);
						  jsonObject.put("Challan No", challanNo);
						  jsonObject.put("Reference No", referenceNo);
						  jsonObject.put("Staff Name", staffName);
						  jsonObject.put("Note Type", noteType);
						  jsonObject.put("Account No", accountNo);
						  jsonObject.put("In-principal Note for Floating of RFP", inprincipalNoteforFloatingofRFP);
						  jsonObject.put("Note for Floating of RFP", noteforFloatingofRFP);
						  jsonObject.put("Pre-bid Queries Response from Bidder", prebidQueriesResponsefrombidder);
						  jsonObject.put("Bid Documents", bidDocuments);
						  jsonObject.put("Note for Technical Evaluation", noteforTechnicalEvaluation);
						  jsonObject.put("Note for Commercial Evaluation", notefprCommercialEvaluation);
						  jsonObject.put("Note for Reverse Auction", noteforReverseAuction);
						  jsonObject.put("Note for Declaring L1 Bidder", noteforDeclaringL1bidder);
						  jsonObject.put("Financial Approval for Final TCO", financialApprovalforFinalTCO);
						  jsonObject.put("Miscellaneous Notes Regarding Tender", miscnotesregradingTender);
						  jsonObject.put("Remarks", remarks);
						  jsonObject.put("Printer Name", printerName);
						  jsonObject.put("Item No", itemNo);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");

					  }
				  }else if(repositoryId.equalsIgnoreCase("HOT")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
						  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
						  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
						  String branchName =  row.getProperties().get("BranchName").getStringValue();
						  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
						  String auditDate =  row.getProperties().get("AuditDate").getStringValue();
						  String auditTYPE =  row.getProperties().get("AuditTYPE").getStringValue();
						  String hoDepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
						  String auditSubject =  row.getProperties().get("AuditSubject").getStringValue();
						  String auditforperiod =  row.getProperties().get("Auditforperiod").getStringValue();
						  String auditCopySrNo =  row.getProperties().get("AuditCopySrNo").getStringValue();
						  String auditConductedby =  row.getProperties().get("AuditConductedby").getStringValue();
						  String auditorFirmIfAny =  row.getProperties().get("AuditorFirmIfAny").getStringValue();
						  Date reportingDate1 =  row.getProperties().get("ReportingDate").getDateTimeValue();
						  String reportingDate = formatFnDate(reportingDate1);
						  String reportingperiod =  row.getProperties().get("Reportingperiod").getStringValue();
						  String reportName =  row.getProperties().get("ReportName").getStringValue();
						  String competentAuthority =  row.getProperties().get("CompetentAuthority").getStringValue();
						  Date lastDateforReporting1 =  row.getProperties().get("LastDateforReporting").getDateTimeValue();
						  String lastDateforReporting = formatFnDate(lastDateforReporting1);
						  Date noteDate1 =  row.getProperties().get("NoteDate").getDateTimeValue();
						  String noteDate = formatFnDate(noteDate1);
						  String noteType =  row.getProperties().get("NoteType").getStringValue();
						  String noteSubject =  row.getProperties().get("NoteSubject").getStringValue();
						  String noteFY =  row.getProperties().get("NoteFY").getStringValue();
						  String noteDispatchSrNo =  row.getProperties().get("NoteDispatchSrNo").getStringValue();
						  String documentType =  row.getProperties().get("DocumentType").getStringValue();
						  String dataReceivingsubmittingDate =  row.getProperties().get("DataReceivingsubmittingDate").getStringValue();
						  String dataperiod =  row.getProperties().get("Dataperiod").getStringValue();
						  String dataDetails =  row.getProperties().get("DataDetails").getStringValue();
						  String dataCollectedfromSubmittedto =  row.getProperties().get("DataCollectedfromSubmittedto").getStringValue();
						  String dataRectificationIfany =  row.getProperties().get("DataRectificationIfany").getStringValue();
						  Date revisedDataReceivingDate1 =  row.getProperties().get("RevisedDataReceivingDate").getDateTimeValue();
						  String revisedDataReceivingDate = formatFnDate(revisedDataReceivingDate1);
						  String revisedDataDetails =  row.getProperties().get("RevisedDataDetails").getStringValue();
						  Date dataReceivingDate1 =  row.getProperties().get("DataReceivingDate").getDateTimeValue();
						  String dataReceivingDate = formatFnDate(dataReceivingDate1);
						  String dataperiodDate =  row.getProperties().get("DataperiodDate").getStringValue();
						  
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("SI. No", serialNumber);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("No Of Pages Uploaded", pageCount);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Audit Date", auditDate);
						  jsonObject.put("Audit Type", auditTYPE);
						  jsonObject.put("HO Department Code", hoDepartmentCode);
						  jsonObject.put("Audit Subject", auditSubject);
						  jsonObject.put("Audit for Period", auditforperiod);
						  jsonObject.put("Audit Copy Sr No", auditCopySrNo);
						  jsonObject.put("Audit Conducted by", auditConductedby);
						  jsonObject.put("Auditor Firm If Any", auditorFirmIfAny);
						  jsonObject.put("Reporting Date", reportingDate);
						  jsonObject.put("Reporting Period", reportingperiod);
						  jsonObject.put("Report Name", reportName);
						  jsonObject.put("Competent Authority", competentAuthority);
						  jsonObject.put("Last Date for Reporting", lastDateforReporting);
						  jsonObject.put("Note Date", noteDate);
						  jsonObject.put("Note Type", noteType);
						  jsonObject.put("Note Subject", noteSubject);
						  jsonObject.put("Note FY", noteFY);
						  jsonObject.put("Note Dispatch Sr No", noteDispatchSrNo);
						  jsonObject.put("Document Type", documentType);
						  jsonObject.put("Data Receiving/Submitting Date", dataReceivingsubmittingDate);
						  jsonObject.put("Data Period", dataperiod);
						  jsonObject.put("Data Details", dataDetails);
						  jsonObject.put("Data Collected from/Submitted to", dataCollectedfromSubmittedto);
						  jsonObject.put("Data Rectification If Any", dataRectificationIfany);
						  jsonObject.put("Revised Data Receiving Date", revisedDataReceivingDate);
						  jsonObject.put("Revised Data Details", revisedDataDetails);
						  jsonObject.put("Data Receiving Date", dataReceivingDate);
						  jsonObject.put("Data Period Date", dataperiodDate);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");

					  }
					} else if (repositoryId.equalsIgnoreCase("HODB")) {
						Id docId = row.getProperties().get("Id").getIdValue();
						Document document = Factory.Document.fetchInstance(os, docId, null);
						String className = document.getClassName();
						System.out.println("className: " + className);
						if (CLASSNAMESTOINGORE.contains(className)) {
							// Skip processing for this class
							continue;
						} else {
							String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							String documentType = row.getProperties().get("DocumentType").getStringValue();
							Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							String dateOfUploading = formatFnDate(dateOfUploading1);
							String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							String deptName = row.getProperties().get("Dept_Name").getStringValue();
							String deptCode = row.getProperties().get("Dept_Code").getStringValue();
							String branchName = row.getProperties().get("BranchName").getStringValue();
							String branchCode = row.getProperties().get("BranchCode").getStringValue();
							String uploadedBy = row.getProperties().get("Creator").getStringValue();
							Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							String referenceNumber = row.getProperties().get("REFERENCENUMBER").getStringValue();
							String officeNoteName = row.getProperties().get("OFFICENOTENAME").getStringValue();
							Date DATE1 = row.getProperties().get("DATE").getDateTimeValue();
							String date = formatFnDate(DATE1);
							String officeNoteNumber = row.getProperties().get("OFFICENOTENUMBER").getStringValue();
							String noteType = row.getProperties().get("NOTETYPE").getStringValue();
							String remark = row.getProperties().get("Remark").getStringValue();
							String receivedFrom = row.getProperties().get("RECEIVEDFROM").getStringValue();
							String type = row.getProperties().get("Type").getStringValue();
							String subject = row.getProperties().get("SUBJECT").getStringValue();
							Date NoteDate1 = row.getProperties().get("NoteDate").getDateTimeValue();
							String noteDate = formatFnDate(NoteDate1);
							String sopName = row.getProperties().get("SOPNAME").getStringValue();
							String sopNumber = row.getProperties().get("SOPNUMBER").getStringValue();
							Date installationDate1 = row.getProperties().get("InstallationDate").getDateTimeValue();
							String installationDate = formatFnDate(installationDate1);
							String product = row.getProperties().get("Product").getStringValue();
							String reportName = row.getProperties().get("REPORTNAME").getStringValue();
							String reportType = row.getProperties().get("ReportType").getStringValue();
							String tenderNo = row.getProperties().get("TenderNo").getStringValue();
							Date reportingDate1 = row.getProperties().get("REPORTINGDATE").getDateTimeValue();
							String reportingDate = formatFnDate(reportingDate1);
							String inprincipalNoteForFloatingOfRFP = row.getProperties().get("InprincipalNoteforFloatingofRFP").getStringValue();
							StringList noteForFloatingRFP1 = row.getProperties().get("NoteforFloatingofRFP").getStringListValue();
							String noteForFloatingRFP = formatStringList(noteForFloatingRFP1);
							StringList roleHolderDepartment1 = row.getProperties().get("RoleHolderDepartment").getStringListValue();
							String roleHolderDepartment = formatStringList(roleHolderDepartment1);
							StringList prebidQueriesResponsefrombidder1 = row.getProperties().get("PrebidQueriesResponsefrombidder").getStringListValue();
							String prebidQueriesResponsefrombidder = formatStringList(prebidQueriesResponsefrombidder1);
							StringList bidDocuments1 = row.getProperties().get("BidDocuments").getStringListValue();
							String bidDocuments = formatStringList(bidDocuments1);
							StringList noteforReverseAuction1 = row.getProperties().get("NoteforReverseAuction").getStringListValue();
							String noteforReverseAuction = formatStringList(noteforReverseAuction1);
							StringList noteforDeclaringL1bidder1 = row.getProperties().get("NoteforDeclaringL1bidder").getStringListValue();
							String noteforDeclaringL1bidder = formatStringList(noteforDeclaringL1bidder1);
							StringList financialApprovalforFinalTCO1 = row.getProperties().get("FinancialApprovalforFinalTCO").getStringListValue();
							String financialApprovalforFinalTCO = formatStringList(financialApprovalforFinalTCO1);
							StringList miscnotesregradingTender1 = row.getProperties().get("MiscnotesregradingTender")
									.getStringListValue();
							String miscnotesregradingTender = formatStringList(miscnotesregradingTender1);
							StringList noteforTechnicalEvaluation1 = row.getProperties()
									.get("NoteforTechnicalEvaluation").getStringListValue();
							String noteforTechnicalEvaluation = formatStringList(noteforTechnicalEvaluation1);
							StringList notefprCommercialEvaluation1 = row.getProperties()
									.get("NotefprCommercialEvaluation").getStringListValue();
							String notefprCommercialEvaluation = formatStringList(notefprCommercialEvaluation1);
							Date addendumDate1 = row.getProperties().get("AddendumDated").getDateTimeValue();
							String addendumDate = formatFnDate(addendumDate1);
							String partyName = row.getProperties().get("PartyName").getStringValue();
							String merchantId = row.getProperties().get("MerchantID").getStringValue();
							String notesforFloatingAddendum = row.getProperties().get("NotesforFloatingAddendum")
									.getStringValue();
							String addendumNo = row.getProperties().get("AddendumNo").getStringValue();
							StringList addendumSubject1 = row.getProperties().get("AddendumSubject")
									.getStringListValue();
							String addendumSubject = formatStringList(addendumSubject1);
							StringList addendumrelatedqueriesresponse1 = row.getProperties()
									.get("Addendumrelatedqueriesresponse").getStringListValue();
							String addendumrelatedqueriesresponse = formatStringList(addendumrelatedqueriesresponse1);
							String accountNumber = row.getProperties().get("AccountNumber").getStringValue();

							StringList annexuresNo1 = row.getProperties().get("AnnexuresNo").getStringListValue();
							String annexuresNo = formatStringList(annexuresNo1);
							StringList annexuresSubject1 = row.getProperties().get("AnnexuresSubject")
									.getStringListValue();
							String annexuresSubject = formatStringList(annexuresSubject1);
							StringList terminalId1 = row.getProperties().get("TerminalID").getStringListValue();
							String terminalId = formatStringList(terminalId1);
							Date deactivationDate1 = row.getProperties().get("DeactivationDate").getDateTimeValue();
							String deactivationDate = formatFnDate(deactivationDate1);

							System.out.println("---------- Adding the Data into JSONObject ----------");
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("SI. No", serialNumber);
							jsonObject.put("Class Name", className);
							jsonObject.put("Document Title", documentTitle);
							jsonObject.put("Date of Uploading", dateOfUploading);
							jsonObject.put("Department Name", deptName);
							jsonObject.put("Dept Code", deptCode);
							jsonObject.put("Branch Name", branchName);
							jsonObject.put("Branch Code", branchCode);
							jsonObject.put("HO Department Code", hODepartmentCode);
							jsonObject.put("Uploaded By", uploadedBy);
							jsonObject.put("No Of Pages Uploaded", pageCount);
							jsonObject.put("Document Type", documentType);

							jsonObject.put("SUBJECT", subject);
							jsonObject.put("REPORT NAME", reportName);
							jsonObject.put("REPORTING DATE", reportingDate);
							jsonObject.put("Report Type", reportType);
							jsonObject.put("OFFICE NOTE NAME", officeNoteName);
							jsonObject.put("OFFICE NOTE NUMBER", officeNoteNumber);
							jsonObject.put("NOTE TYPE", noteType);
							jsonObject.put("Note Date", noteDate);
							jsonObject.put("Remark", remark);
							jsonObject.put("SOP NAME", sopName);
							jsonObject.put("SOP NUMBER", sopNumber);
							jsonObject.put("RECEIVED FROM", receivedFrom);

							jsonObject.put("REFERENCE NUMBER", referenceNumber);
							jsonObject.put("Type", type);

							jsonObject.put("Tender No.", tenderNo);
							jsonObject.put("In-principal Note for Floating of RFP", inprincipalNoteForFloatingOfRFP);
							jsonObject.put("Note for Floating of RFP", noteForFloatingRFP);
							jsonObject.put("Pre-bid Queries/Response from bidder", prebidQueriesResponsefrombidder);
							jsonObject.put("Bid Documents", bidDocuments);
							jsonObject.put("Note for Technical Evaluation", noteforTechnicalEvaluation);
							jsonObject.put("Note fpr Commercial Evaluation", notefprCommercialEvaluation);
							jsonObject.put("Note for Reverse Auction", noteforReverseAuction);
							jsonObject.put("Note for Declaring L-1 bidder", noteforDeclaringL1bidder);
							jsonObject.put("Financial Approval for Final TCO", financialApprovalforFinalTCO);
							jsonObject.put("Notes for Floating Addendum", notesforFloatingAddendum);
							jsonObject.put("Addendum No", addendumNo);
							jsonObject.put("Misc notes regrading Tender", miscnotesregradingTender);
							jsonObject.put("Addendum Dated", addendumDate);
							jsonObject.put("Addendum Subject", addendumSubject);
							jsonObject.put("Addendum related queries/response", addendumrelatedqueriesresponse);
							jsonObject.put("Annexures No", annexuresNo);
							jsonObject.put("Annexures Subject", annexuresSubject);
							jsonObject.put("Merchant ID", merchantId);
							jsonObject.put("AccountNumber", accountNumber);
							jsonObject.put("Terminal ID", terminalId);
							jsonObject.put("Party Name", partyName);
							jsonObject.put("Installation Date", installationDate);
							jsonObject.put("Product", product);
							// jsonObject.put("",zone );
							jsonObject.put("DATE", date);
							jsonObject.put("Deactivation Date", deactivationDate);

							jsonArray.add(jsonObject);
							serialNumber++;
							System.out.println("---------- Data was successfully added into the JSONObject ----------");

						}

					}else if(repositoryId.equalsIgnoreCase("HOV")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
						  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
						  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
						  String branchName =  row.getProperties().get("BranchName").getStringValue();
						  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
						  String nameOfAccount =  row.getProperties().get("NAMEOFACCOUNT").getStringValue();
						  String AMOUNTRsincrores =  row.getProperties().get("AMOUNTRsincrores").getStringValue();
						  Date dateOfReportingToRBI1 =  row.getProperties().get("DATEOFREPORTINGTORBI").getDateTimeValue();
						  String dateOfReportingToRBI = formatFnDate(dateOfReportingToRBI1);
						  Date dateOfReportingToABBFF1 =  row.getProperties().get("DATEOFREPORTINGTOABBFF").getDateTimeValue();
						  String dateOfReportingToABBFF = formatFnDate(dateOfReportingToABBFF1);
						  Date dateOfComplaintLodgeWithCBI1 =  row.getProperties().get("DATEOFCOMPLAINTLODGEDWITHCBI").getDateTimeValue();
						  String dateOfComplaintLodgeWithCBI = formatFnDate(dateOfComplaintLodgeWithCBI1);
						  Date dateOfReceiptToFIAC1 =  row.getProperties().get("DATEOFRECEIPTOFIAC").getDateTimeValue();
						  String dateOfReceiptTofIAC = formatFnDate(dateOfReceiptToFIAC1);
						  Date dateOfReferenceToABBFF1 =  row.getProperties().get("DATEOFREFERENCETOABBFF").getDateTimeValue();
						  String dateOfReferenceToABBFF = formatFnDate(dateOfReferenceToABBFF1);
						  Date dateOfReceiptToABBFFAdvice1 =  row.getProperties().get("DATEOFRECEIPTOFABBFFADVICE").getDateTimeValue();
						  String dateOfReceiptToABBFFAdvice = formatFnDate(dateOfReceiptToABBFFAdvice1);
						  String aBBFFAdvice =  row.getProperties().get("ABBFFADVICE").getStringValue();
						  String remarks =  row.getProperties().get("REMARKS").getStringValue();
						  String year =  row.getProperties().get("YEAR").getStringValue();
						  String nameofInvestigatingAgency =  row.getProperties().get("NAMEOFINVESTIGATINGAGENCY").getStringValue();
						  Date dateOfReceiptFromCBIInvestigatingAgency1 =  row.getProperties().get("DATEOFRECEIPTFROMCBIINVESTIGATINGAGENCY").getDateTimeValue();
						  String dateOfReceiptFromCBIInvestigatingAgency = formatFnDate(dateOfReceiptFromCBIInvestigatingAgency1);
						  String cbircNo =  row.getProperties().get("CBIRCNO").getStringValue();
						  String noOfIndividualsForWhomApprovalSought =  row.getProperties().get("NOOFINDIVIDUALSFORWHOMAPPROVALSOUGHT").getStringValue();
						  String designationThereOf =  row.getProperties().get("DESIGNATIONTHEREOF").getStringValue();
						  String caseReferredToABBFFyn =  row.getProperties().get("CASEREFERREDTOABBFFYN").getStringValue();
						  String abbffAdvice =  row.getProperties().get("ABBFFADVICE").getStringValue();
						  String approvalGranted =  row.getProperties().get("APPROVALGRNATED").getStringValue();
						  String approvalDenied =  row.getProperties().get("APPROVALDENIED").getStringValue();
						  String complaintNo =  row.getProperties().get("ComplaintNo").getStringValue();
						  Date dateOfComplaint1 =  row.getProperties().get("DateofComplaint").getDateTimeValue();
						  String dateOfComplaint = formatFnDate(dateOfComplaint1);
						  String dateOfReceiptOfComplaint =  row.getProperties().get("DateofReceiptofComplaint").getStringValue();
						  String modeOfReceipt =  row.getProperties().get("ModeofReceipt").getStringValue();
						  String receivedThru =  row.getProperties().get("ReceivedThru").getStringValue();
						  String complainantName =  row.getProperties().get("ComplainantName").getStringValue();
						  Date dateOfApprovalSentToFMDAfterApproval1 =  row.getProperties().get("DATEOFAPPROVALSENTTOFMDAFTERAPPROVAL").getDateTimeValue();
						  String dateOfApprovalSentToFMDAfterApproval = formatFnDate(dateOfApprovalSentToFMDAfterApproval1);
						  Date dateOfCompliantWithCBI1 =  row.getProperties().get("DATEOFCOMPLAINTWITHCBI").getDateTimeValue();
						  String dateOfCompliantWithCBI = formatFnDate(dateOfCompliantWithCBI1);
						  Date dateOfFIR1 =  row.getProperties().get("DATEOFFIR").getDateTimeValue();
						  String dateOfFIR = formatFnDate(dateOfFIR1);
						  String leadBank =  row.getProperties().get("LEADBANK").getStringValue();
						  String zone =  row.getProperties().get("ZONE").getStringValue();
						  String branch =  row.getProperties().get("BRANCH").getStringValue();
						  String nameoftheTenderContract =  row.getProperties().get("NameoftheTenderContract").getStringValue();
						  StringList officeNoteLetter1 =  row.getProperties().get("OfficeNoteLetter").getStringListValue();
						  String officeNoteLetter = formatStringList(officeNoteLetter1);
						  Date officeNoteLetterDate1 =  row.getProperties().get("OfficeNoteLetterDate").getDateTimeValue();
						  String officeNoteLetterDate = formatFnDate(officeNoteLetterDate1);
						  StringList officeNoteType1 =  row.getProperties().get("OfficeNoteType").getStringListValue();
						  String officeNoteType = formatStringList(officeNoteType1);
						  DateTimeList officeNoteDate1 = row.getProperties().get("OfficeNoteDate").getDateTimeListValue();
						  String officeNoteDate = formatDateList(officeNoteDate1);
						  Date dateOfReceipt1 =  row.getProperties().get("DATEOFRECEIPT").getDateTimeValue();
						  String dateOfReceipt = formatFnDate(dateOfReceipt1);
						  Date dateOfOccurence1 =  row.getProperties().get("DATEOFOCCURRENCE").getDateTimeValue();
						  String dateOfOccurence = formatFnDate(dateOfOccurence1);
						  Date dateOfDeduction1 =  row.getProperties().get("DATEOFDETECTION").getDateTimeValue();
						  String dateOfDeduction = formatFnDate(dateOfDeduction1);
						  String dataBaseID =  row.getProperties().get("DataBaseID").getStringValue();
						  String iacNumber =  row.getProperties().get("IACNumber").getStringValue();
						  String iACFSA_Name =  row.getProperties().get("IACFSA_Name").getStringValue();
						  Date dateofMinutes1 =  row.getProperties().get("DateofMinutes").getDateTimeValue();
						  String dateofMinutes = formatFnDate(dateofMinutes1);
						  String pfCode =  row.getProperties().get("PFCode").getStringValue();
						  String scl =  row.getProperties().get("SCL").getStringValue();
						  String irrgularitiesAtBO =  row.getProperties().get("IrrgularitiesatBO").getStringValue();
						  String zo =  row.getProperties().get("ZO").getStringValue();
						  String da =  row.getProperties().get("DA").getStringValue();
						  String whetherFSArequired =  row.getProperties().get("WhetherFSArequired").getStringValue();
						  String fileNumber =  row.getProperties().get("FileNumber").getStringValue();
						  String natureofPP =  row.getProperties().get("NatureofPP").getStringValue();
						  Date dateofChargeSheet1 =  row.getProperties().get("DateofChargeSheet").getDateTimeValue();
						  String dateofChargeSheet = formatFnDate(dateofChargeSheet1);
						  String chargeSheetIssuedwithoutFSA =  row.getProperties().get("ChargeSheetissuedwithoutFSA").getStringValue();
						  String officeNoteLetterNo =  row.getProperties().get("OfficeNoteLetterNo").getStringValue();
						  String subject =  row.getProperties().get("SUBJECT").getStringValue();
						  String nameOfInvestigatingAgency =  row.getProperties().get("NAMEOFINVESTIGATINGAGENCY").getStringValue();
						  String cbiRcNo =  row.getProperties().get("CBIRCNO").getStringValue();
						  String noOfIndividualsForWhomSopSought =  row.getProperties().get("NOOFINDIVIDUALSFORWHOMSOPSOUGHT").getStringValue();
						  String sopGranted =  row.getProperties().get("SOPGRANTED").getStringValue();
						  String caseRefererredToCVCyn =  row.getProperties().get("CASEREFERREDTOCVCYN").getStringValue();
						  String sopDeniedGrantedByCVC=  row.getProperties().get("SOPDENIEDGRANTEDBYCVC").getStringValue();
						  String caseReferredToDFSyn =  row.getProperties().get("CASEREFERREDTODFSYN").getStringValue();
						  String sopDeniedGrantedbyDFS =  row.getProperties().get("SOPDENIEDGRANTEDBYDFS").getStringValue();
						  String qprForQuarter =  row.getProperties().get("QPRforQuarter").getStringValue();
						  String rno =  row.getProperties().get("RNO").getStringValue();
						  String folder =  row.getProperties().get("FOLDER").getStringValue();
						  Date doc1 =  row.getProperties().get("DOC").getDateTimeValue();
						  String doc = formatFnDate(doc1);  
						  Date reportingDate1 =  row.getProperties().get("ReportingDate").getDateTimeValue();
						  String reportingDate = formatFnDate(reportingDate1);
						  String reportingperiod =  row.getProperties().get("Reportingperiod").getStringValue();
						  String reportName =  row.getProperties().get("ReportName").getStringValue();
						  String competentAuthority =  row.getProperties().get("CompetentAuthority").getStringValue();
						  Date dateforReporting1 =  row.getProperties().get("DateforReporting").getDateTimeValue();
						  String dateforReporting = formatFnDate(dateforReporting1);
						  String receivedFrom =  row.getProperties().get("RECEIVEDFROM").getStringValue();
						  Date dateOfRTIApplication1 =  row.getProperties().get("DATEOFRTIAPPLICATION").getDateTimeValue();
						  String dateOfRTIApplication = formatFnDate(dateOfRTIApplication1);
						  Date dateOfDisposal1 =  row.getProperties().get("DATEOFDISPOSAL").getDateTimeValue();
						  String dateOfDisposal = formatFnDate(dateOfDisposal1);
						  String nameOfApplicant =  row.getProperties().get("NAMEOFAPPLICANT").getStringValue();
						  String designation =  row.getProperties().get("DESIGNATION").getStringValue();
						  String summonOn =  row.getProperties().get("SUMMONON").getStringValue();
						  String visitedByVOName =  row.getProperties().get("VisitedByVOName").getStringValue();
						  Date dateofVisit1 =  row.getProperties().get("DateofVisit").getDateTimeValue();
						  String dateofVisit = formatFnDate(dateofVisit1);
						  
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("SI. No", serialNumber);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("No Of Pages Uploaded", pageCount);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Name of Account", nameOfAccount);
						  jsonObject.put("Amount in Rs (in crores)", AMOUNTRsincrores);
						  jsonObject.put("Date of Reporting to RBI", dateOfReportingToRBI);
						  jsonObject.put("Date of Reporting to ABBFF", dateOfReportingToABBFF);
						  jsonObject.put("Date of Complaint Lodged with CBI", dateOfComplaintLodgeWithCBI);
						  jsonObject.put("Date of Receipt to FIAC", dateOfReceiptTofIAC);
						  jsonObject.put("Date of Reference to ABBFF", dateOfReferenceToABBFF);
						  jsonObject.put("Date of Receipt to ABBFF Advice", dateOfReceiptToABBFFAdvice);
						  jsonObject.put("ABBFF Advice", aBBFFAdvice);
						  jsonObject.put("Remarks", remarks);
						  jsonObject.put("Year", year);
						  jsonObject.put("Name of Investigating Agency", nameofInvestigatingAgency);
						  jsonObject.put("Date of Receipt from CBI Investigating Agency", dateOfReceiptFromCBIInvestigatingAgency);
						  jsonObject.put("CBIRC No", cbircNo);
						  jsonObject.put("No of Individuals for Whom Approval Sought", noOfIndividualsForWhomApprovalSought);
						  jsonObject.put("Designation Thereof", designationThereOf);
						  jsonObject.put("Case Referred to ABBFF (Yes/No)", caseReferredToABBFFyn);
						  jsonObject.put("ABBFF Advice", abbffAdvice);
						  jsonObject.put("Approval Granted", approvalGranted);
						  jsonObject.put("Approval Denied", approvalDenied);
						  jsonObject.put("Complaint No", complaintNo);
						  jsonObject.put("Date of Complaint", dateOfComplaint);
						  jsonObject.put("Date of Receipt of Complaint", dateOfReceiptOfComplaint);
						  jsonObject.put("Mode of Receipt", modeOfReceipt);
						  jsonObject.put("Received Through", receivedThru);
						  jsonObject.put("Complainant Name", complainantName);
						  jsonObject.put("Date of Approval Sent to FMD After Approval", dateOfApprovalSentToFMDAfterApproval);
						  jsonObject.put("Date of Complaint with CBI", dateOfCompliantWithCBI);
						  jsonObject.put("Date of FIR", dateOfFIR);
						  jsonObject.put("Lead Bank", leadBank);
						  jsonObject.put("Zone", zone);
						  jsonObject.put("Branch", branch);
						  jsonObject.put("Name of the Tender Contract", nameoftheTenderContract);
						  jsonObject.put("Office Note Letter", officeNoteLetter);
						  jsonObject.put("Office Note Letter Date", officeNoteLetterDate);
						  jsonObject.put("Office Note Type", officeNoteType);
						  jsonObject.put("Office Note Date", officeNoteDate);
						  jsonObject.put("Date of Receipt", dateOfReceipt);
						  jsonObject.put("Date of Occurrence", dateOfOccurence);
						  jsonObject.put("Date of Deduction", dateOfDeduction);
						  jsonObject.put("Database ID", dataBaseID);
						  jsonObject.put("IAC Number", iacNumber);
						  jsonObject.put("IAC FSA Name", iACFSA_Name);
						  jsonObject.put("Date of Minutes", dateofMinutes);
						  jsonObject.put("PF Code", pfCode);
						  jsonObject.put("SCL", scl);
						  jsonObject.put("Irregularities at BO", irrgularitiesAtBO);
						  jsonObject.put("ZO", zo);
						  jsonObject.put("DA", da);
						  jsonObject.put("Whether FSA Required", whetherFSArequired);
						  jsonObject.put("File Number", fileNumber);
						  jsonObject.put("Nature of PP", natureofPP);
						  jsonObject.put("Date of Charge Sheet", dateofChargeSheet);
						  jsonObject.put("Charge Sheet Issued without FSA", chargeSheetIssuedwithoutFSA);
						  jsonObject.put("Office Note Letter No", officeNoteLetterNo);
						  jsonObject.put("Subject", subject);
						  jsonObject.put("Name of Investigating Agency", nameOfInvestigatingAgency);
						  jsonObject.put("CBIRC No", cbiRcNo);
						  jsonObject.put("No of Individuals for Whom SOP Sought", noOfIndividualsForWhomSopSought);
						  jsonObject.put("SOP Granted", sopGranted);
						  jsonObject.put("Case Referred to CVC (Yes/No)", caseRefererredToCVCyn);
						  jsonObject.put("SOP Denied/Granted by CVC", sopDeniedGrantedByCVC);
						  jsonObject.put("Case Referred to DFS (Yes/No)", caseReferredToDFSyn);
						  jsonObject.put("SOP Denied/Granted by DFS", sopDeniedGrantedbyDFS);
						  jsonObject.put("QPR for Quarter", qprForQuarter);
						  jsonObject.put("RNO", rno);
						  jsonObject.put("Folder", folder);
						  jsonObject.put("Document Date", doc);
						  jsonObject.put("Reporting Date", reportingDate);
						  jsonObject.put("Reporting Period", reportingperiod);
						  jsonObject.put("Report Name", reportName);
						  jsonObject.put("Competent Authority", competentAuthority);
						  jsonObject.put("Date for Reporting", dateforReporting);
						  jsonObject.put("Received From", receivedFrom);
						  jsonObject.put("Date of RTI Application", dateOfRTIApplication);
						  jsonObject.put("Date of Disposal", dateOfDisposal);
						  jsonObject.put("Name of Applicant", nameOfApplicant);
						  jsonObject.put("Designation", designation);
						  jsonObject.put("Summon On", summonOn);
						  jsonObject.put("Visited By VO Name", visitedByVOName);
						  jsonObject.put("Date of Visit", dateofVisit);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");

					  }
				  }else if(repositoryId.equalsIgnoreCase("HOPF")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
						  Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
						  String deptName =  row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
						  String branchName =  row.getProperties().get("BranchName").getStringValue();
						  String branchCode =  row.getProperties().get("BranchCode").getStringValue();
						  String pranNumber =  row.getProperties().get("PRANNUMBER").getStringValue();
						  String pfCode =  row.getProperties().get("PFCode").getStringValue();
						  StringList name1 =  row.getProperties().get("Names").getStringListValue();
						  String name = formatStringList(name1);
						  Date dob1 =  row.getProperties().get("DOB").getDateTimeValue();
						  String dob = formatFnDate(dob1);
						  Date doj1 =  row.getProperties().get("DOJ").getDateTimeValue();
						  String doj = formatFnDate(doj1);
						  Date date1 =  row.getProperties().get("Date").getDateTimeValue();
						  String date = formatFnDate(date1);
						  String caseNo =  row.getProperties().get("CaseNo").getStringValue();
						  String juridisction =  row.getProperties().get("Juridisction").getStringValue();
						  String status =  row.getProperties().get("Status").getStringValue();
						  Date hearingDate1 =  row.getProperties().get("HearingDate").getDateTimeValue();
						  String hearingDate = formatFnDate(hearingDate1);
						  String employeeName =  row.getProperties().get("EmployeeName").getStringValue();
						  String zone =  row.getProperties().get("Zone").getStringValue();
						  String financialYear =  row.getProperties().get("FinancialYear").getStringValue();
						  String auditType =  row.getProperties().get("AuditType").getStringValue();
						  String auditorName =  row.getProperties().get("AuditorName").getStringValue();
						  String department =  row.getProperties().get("Department").getStringValue();
						  String trustName =  row.getProperties().get("TrustName").getStringValue();
						  Date dateofIssue1 =  row.getProperties().get("DateofIssue").getDateTimeValue();
						  String dateofIssue = formatFnDate(dateofIssue1);
						  String subject =  row.getProperties().get("Subject").getStringValue();
						  String ralation =  row.getProperties().get("Ralation").getStringValue();
						  String assessmentyear =  row.getProperties().get("Assessmentyear").getStringValue();
						  String noticeSubject =  row.getProperties().get("NoticeSubject").getStringValue();
						  String noticeNo =  row.getProperties().get("NoticeNo").getStringValue();
						  Date noteDate1 =  row.getProperties().get("NoteDate").getDateTimeValue();
						  String noteDate = formatFnDate(noteDate1);
						  String resolutionNo =  row.getProperties().get("ResolutionNo").getStringValue();
						  String amount =  row.getProperties().get("Amount").getStringValue();
						  String accountNumber =  row.getProperties().get("AccountNumber").getStringValue();
						  String investmentType =  row.getProperties().get("InvestmentType").getStringValue();
						  String accountName =  row.getProperties().get("AccountName").getStringValue();
						  Date issuingDate1 =  row.getProperties().get("IssuingDate").getDateTimeValue();
						  String issuingDate = formatFnDate(issuingDate1);
						  Date maturityDate1 =  row.getProperties().get("MaturityDate").getDateTimeValue();
						  String maturityDate = formatFnDate(maturityDate1);
						  String roi =  row.getProperties().get("ROI").getStringValue();
						  String minuteNumber =  row.getProperties().get("MinuteNumber").getStringValue();
						  Date meetingDate1 =  row.getProperties().get("MaturityDate").getDateTimeValue();
						  String meetingDate = formatFnDate(meetingDate1);
						  String minuteName =  row.getProperties().get("MinuteName").getStringValue();
						  String nominationType =  row.getProperties().get("NOMINATIONTYPE").getStringValue();
						  String nomineeName =  row.getProperties().get("NOMINEENAME").getStringValue();
						  String withdrawalAmount =  row.getProperties().get("WITHDRAWALAMOUNT").getStringValue();
						  String solId =  row.getProperties().get("SOLID").getStringValue();
						  String pensionAccountNumber =  row.getProperties().get("PensionAccountNumber").getStringValue();
						  String familyPensionersName =  row.getProperties().get("FamilyPensionersName").getStringValue();
						  Date dateofLeaving1 =  row.getProperties().get("DateofLeaving").getDateTimeValue();
						  String dateofLeaving = formatFnDate(dateofLeaving1);
						  Date dateofBirth1 =  row.getProperties().get("DateofBirth").getDateTimeValue();
						  String dateofBirth = formatFnDate(dateofBirth1);
						  Date dateofPayment1 =  row.getProperties().get("DateofPayment").getDateTimeValue();
						  String dateofPayment = formatFnDate(dateofPayment1);
						  String dor =  row.getProperties().get("DOR").getStringValue();
						  String retirementType =  row.getProperties().get("RetirementType").getStringValue();
						  String trustDocuments =  row.getProperties().get("RetirementType").getStringValue();
						  Date trustDate1 =  row.getProperties().get("TrustDate").getDateTimeValue();
						  String trustDate = formatFnDate(trustDate1);
						  String agendaItemNo =  row.getProperties().get("AgendaItemNo").getStringValue();
						  String agendaItem =  row.getProperties().get("AgendaItem").getStringValue();
						  
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("SI. No", serialNumber);
						  jsonObject.put("Class Name", className);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("No Of Pages Uploaded", pageCount);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("PRAN Number", pranNumber);
						  jsonObject.put("PF Code", pfCode);
						  jsonObject.put("Name", name);
						  jsonObject.put("Date of Birth", dob);
						  jsonObject.put("Date of Joining", doj);
						  jsonObject.put("Date", date);
						  jsonObject.put("Case No", caseNo);
						  jsonObject.put("Jurisdiction", juridisction);
						  jsonObject.put("Status", status);
						  jsonObject.put("Hearing Date", hearingDate);
						  jsonObject.put("Employee Name", employeeName);
						  jsonObject.put("Zone", zone);
						  jsonObject.put("Financial Year", financialYear);
						  jsonObject.put("Audit Type", auditType);
						  jsonObject.put("Auditor Name", auditorName);
						  jsonObject.put("Department", department);
						  jsonObject.put("Trust Name", trustName);
						  jsonObject.put("Date of Issue", dateofIssue);
						  jsonObject.put("Subject", subject);
						  jsonObject.put("Relation", ralation);
						  jsonObject.put("Assessment Year", assessmentyear);
						  jsonObject.put("Notice Subject", noticeSubject);
						  jsonObject.put("Notice No", noticeNo);
						  jsonObject.put("Note Date", noteDate);
						  jsonObject.put("Resolution No", resolutionNo);
						  jsonObject.put("Amount", amount);
						  jsonObject.put("Account Number", accountNumber);
						  jsonObject.put("Investment Type", investmentType);
						  jsonObject.put("Account Name", accountName);
						  jsonObject.put("Issuing Date", issuingDate);
						  jsonObject.put("Maturity Date", maturityDate);
						  jsonObject.put("ROI", roi);
						  jsonObject.put("Minute Number", minuteNumber);
						  jsonObject.put("Meeting Date", meetingDate);
						  jsonObject.put("Minute Name", minuteName);
						  jsonObject.put("Nomination Type", nominationType);
						  jsonObject.put("Nominee Name", nomineeName);
						  jsonObject.put("Withdrawal Amount", withdrawalAmount);
						  jsonObject.put("SOL ID", solId);
						  jsonObject.put("Pension Account Number", pensionAccountNumber);
						  jsonObject.put("Family Pensioner's Name", familyPensionersName);
						  jsonObject.put("Date of Leaving", dateofLeaving);
						  jsonObject.put("Date of Birth", dateofBirth);
						  jsonObject.put("Date of Payment", dateofPayment);
						  jsonObject.put("DOR", dor);
						  jsonObject.put("Retirement Type", retirementType);
						  jsonObject.put("Trust Documents", trustDocuments);
						  jsonObject.put("Trust Date", trustDate);
						  jsonObject.put("Agenda Item No", agendaItemNo);
						  jsonObject.put("Agenda Item", agendaItem);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");

					  }
				  }else if(repositoryId.equalsIgnoreCase("HOB")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
						  String documentType = row.getProperties().get("DocumentType").getStringValue();
						  Date DateCreatedDate = row.getProperties().get("DateCreated").getDateTimeValue();
						  String formattedDateCreatedDate = formatFnDate(DateCreatedDate);
						  String hoDepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
						  String departmentName = row.getProperties().get("Dept_Name").getStringValue();
						  String departmentCode = row.getProperties().get("Dept_Code").getStringValue();
						  String branchName = row.getProperties().get("BranchName").getStringValue();
						  String branchCode = row.getProperties().get("BranchCode").getStringValue();
						  String subject = row.getProperties().get("Subject").getStringValue();
						  String createdBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
						  StringList addendumSubjectList = row.getProperties().get("AddendumSubject").getStringListValue();
						  String formattedAddendumSubject = formatStringList(addendumSubjectList);
						  StringList addendumRelatedQueriesResponseList = row.getProperties().get("Addendumrelatedqueriesresponse").getStringListValue();
						  String formattedAddendumRelatedQueriesResponse = formatStringList(addendumRelatedQueriesResponseList);
						  Date formattedDate = row.getProperties().get("Date").getDateTimeValue();
						  String formattedDateString = formatFnDate(formattedDate);
						  StringList annexuresNoList = row.getProperties().get("AnnexuresNo").getStringListValue();
						  String formattedAnnexuresNo = formatStringList(annexuresNoList);
						  StringList annexuresSubjectList = row.getProperties().get("AnnexuresSubject").getStringListValue();
						  String formattedAnnexuresSubject = formatStringList(annexuresSubjectList);
						  String notesForFloatingAddendum = row.getProperties().get("NotesforFloatingAddendum").getStringValue();
						  String committeeName = row.getProperties().get("NameofCommittee").getStringValue();
						  Date meetingDateDate = row.getProperties().get("DateofMeeting").getDateTimeValue();
						  String formattedMeetingDate = formatFnDate(meetingDateDate);
						  String typeOfMeeting = row.getProperties().get("TypeofMeeting").getStringValue();
						  String department = row.getProperties().get("Department").getStringValue();
						  Date formattedMeetingDateDate = row.getProperties().get("MeetingDate").getDateTimeValue();
						  String formattedMeetingDateDateString = formatFnDate(formattedMeetingDateDate);
						  String meetingName = row.getProperties().get("NameofMeeting").getStringValue();
						  String directorName = row.getProperties().get("DirectorName").getStringValue();
						  Date dateOfAppointmentDate = row.getProperties().get("DateofAppointment").getDateTimeValue();
						  String formattedDateOfAppointment = formatFnDate(dateOfAppointmentDate);
						  String resolutionNumber = row.getProperties().get("ResolutionNumber").getStringValue();
						  Date paymentDateDate = row.getProperties().get("PaymentDate").getDateTimeValue();
						  String formattedPaymentDate = formatFnDate(paymentDateDate);
						  String paymentType = row.getProperties().get("PaymentType").getStringValue();
						  Date dateOfReconstitutionDate = row.getProperties().get("DateofReconsitution").getDateTimeValue();
						  String formattedDateOfReconstitution = formatFnDate(dateOfReconstitutionDate);
						  String month = row.getProperties().get("MONTH").getStringValue();
						  String noteSubject = row.getProperties().get("NOTEsubject").getStringValue();
						  String year = row.getProperties().get("YEAR").getStringValue();
						  String financialYear = row.getProperties().get("FinancialYear").getStringValue();
						  String tenderNumber = row.getProperties().get("TenderNo").getStringValue();
						  String inPrincipalNoteForFloatingOfRFP = row.getProperties().get("InprincipalNoteforFloatingofRFP").getStringValue();
						  StringList prebidQueriesResponseFromBidderList = row.getProperties().get("PrebidQueriesResponsefrombidder").getStringListValue();
						  String formattedPrebidQueriesResponseFromBidder = formatStringList(prebidQueriesResponseFromBidderList);
						  StringList bidDocumentsList = row.getProperties().get("BidDocuments").getStringListValue();
						  String formattedBidDocuments = formatStringList(bidDocumentsList);
						  StringList noteForFloatingOfRFPList = row.getProperties().get("NoteforFloatingofRFP").getStringListValue();
						  String formattedNoteForFloatingOfRFP = formatStringList(noteForFloatingOfRFPList);
						  StringList noteForTechnicalEvaluationList = row.getProperties().get("NoteforTechnicalEvaluation").getStringListValue();
						  String formattedNoteForTechnicalEvaluation = formatStringList(noteForTechnicalEvaluationList);
						  StringList noteForCommercialEvaluationList = row.getProperties().get("NotefprCommercialEvaluation").getStringListValue();
						  String formattedNoteForCommercialEvaluation = formatStringList(noteForCommercialEvaluationList);
						  StringList noteForReverseAuctionList = row.getProperties().get("NoteforReverseAuction").getStringListValue();
						  String formattedNoteForReverseAuction = formatStringList(noteForReverseAuctionList);
						  StringList noteForDeclaringL1BidderList = row.getProperties().get("NoteforDeclaringL1bidder").getStringListValue();
						  String formattedNoteForDeclaringL1Bidder = formatStringList(noteForDeclaringL1BidderList);
						  StringList financialApprovalForFinalTCOList = row.getProperties().get("FinancialApprovalforFinalTCO").getStringListValue();
						  String formattedFinancialApprovalForFinalTCO = formatStringList(financialApprovalForFinalTCOList);
						  StringList miscNotesRegardingTenderList = row.getProperties().get("MiscnotesregradingTender").getStringListValue();
						  String formattedMiscNotesRegardingTender = formatStringList(miscNotesRegardingTenderList);
						  String addendumNumber = row.getProperties().get("AddendumNo").getStringValue();
						  Date addendumDatedDate = row.getProperties().get("AddendumDated").getDateTimeValue();
						  String formattedAddendumDated = formatFnDate(addendumDatedDate);
						  
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("S.No", serialNumber);
						  jsonObject.put("Class Name", className);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Document Type", documentType);
						  jsonObject.put("Document Created Date", formattedDateCreatedDate);
						  jsonObject.put("HO Department Code", hoDepartmentCode);
						  jsonObject.put("Department Name", departmentName);
						  jsonObject.put("Department Code", departmentCode);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Subject", subject);
						  jsonObject.put("Created By", createdBy);
						  jsonObject.put("Page Count", pageCount);
						  jsonObject.put("Addendum Subject", formattedAddendumSubject);
						  jsonObject.put("Addendum Related Queries Response", formattedAddendumRelatedQueriesResponse);
						  jsonObject.put("Date", formattedDateString);
						  jsonObject.put("Annexures No", formattedAnnexuresNo);
						  jsonObject.put("Annexures Subject", formattedAnnexuresSubject);
						  jsonObject.put("Notes for Floating Addendum", notesForFloatingAddendum);
						  jsonObject.put("Committee Name", committeeName);
						  jsonObject.put("Meeting Date", formattedMeetingDate);
						  jsonObject.put("Type of Meeting", typeOfMeeting);
						  jsonObject.put("Department", department);
						  jsonObject.put("Meeting Date Date", formattedMeetingDateDateString);
						  jsonObject.put("Meeting Name", meetingName);
						  jsonObject.put("Director Name", directorName);
						  jsonObject.put("Date of Appointment", formattedDateOfAppointment);
						  jsonObject.put("Resolution Number", resolutionNumber);
						  jsonObject.put("Payment Date", formattedPaymentDate);
						  jsonObject.put("Payment Type", paymentType);
						  jsonObject.put("Date of Reconstitution", formattedDateOfReconstitution);
						  jsonObject.put("Month", month);
						  jsonObject.put("Note Subject", noteSubject);
						  jsonObject.put("Year", year);
						  jsonObject.put("Financial Year", financialYear);
						  jsonObject.put("Tender Number", tenderNumber);
						  jsonObject.put("In-Principal Note for Floating of RFP", inPrincipalNoteForFloatingOfRFP);
						  jsonObject.put("Prebid Queries Response from Bidder", formattedPrebidQueriesResponseFromBidder);
						  jsonObject.put("Bid Documents", formattedBidDocuments);
						  jsonObject.put("Note for Floating of RFP", formattedNoteForFloatingOfRFP);
						  jsonObject.put("Note for Technical Evaluation", formattedNoteForTechnicalEvaluation);
						  jsonObject.put("Note for Commercial Evaluation", formattedNoteForCommercialEvaluation);
						  jsonObject.put("Note for Reverse Auction", formattedNoteForReverseAuction);
						  jsonObject.put("Note for Declaring L1 Bidder", formattedNoteForDeclaringL1Bidder);
						  jsonObject.put("Financial Approval for Final TCO", formattedFinancialApprovalForFinalTCO);
						  jsonObject.put("Misc Notes Regarding Tender", formattedMiscNotesRegardingTender);
						  jsonObject.put("Addendum Number", addendumNumber);
						  jsonObject.put("Addendum Dated", formattedAddendumDated);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");
					  }
				  }else if(repositoryId.equalsIgnoreCase("EDS1")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
						  String documentType = row.getProperties().get("DocumentType").getStringValue();
						  Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
						  String deptName = row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode = row.getProperties().get("Dept_Code").getStringValue();
						  String branchName = row.getProperties().get("BranchName").getStringValue();
						  String branchCode = row.getProperties().get("BranchCode").getStringValue();
						  String subject = row.getProperties().get("Subject").getStringValue();
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
						  String diaryNumber = row.getProperties().get("DiaryNumber").getStringValue();
						  String department = row.getProperties().get("Department").getStringValue();
						  Date noteDate1 = row.getProperties().get("NoteDate").getDateTimeValue();
						  String noteDate = formatFnDate(noteDate1);
						  Date momDate1 = row.getProperties().get("MOMDate").getDateTimeValue();
						  String momDate = formatFnDate(momDate1);
						  Date dateIn1 = row.getProperties().get("DateIn").getDateTimeValue();
						  String dateIn = formatFnDate(dateIn1);
						  Date dateOut1 = row.getProperties().get("DateOut").getDateTimeValue(); // Fixed variable name
						  String dateOut = formatFnDate(dateOut1);
						  String receivedNoteType = row.getProperties().get("ReceivedNoteType").getStringValue();
						  String sentNoteType = row.getProperties().get("SentNoteType").getStringValue();
						  String approvingAuthority = row.getProperties().get("ApprovingAuthority").getStringValue();
						  String approvalType = row.getProperties().get("ApprovalType").getStringValue();
						  String remarks = row.getProperties().get("Remarks").getStringValue();
						  String noteType = row.getProperties().get("Notetype").getStringValue(); // Fixed variable name
						  String fileName = row.getProperties().get("FileName").getStringValue();
						  String receivedLetterType = row.getProperties().get("ReceivedLetterType").getStringValue();
						  String sentLetterType = row.getProperties().get("SentLetterType").getStringValue();
						  Date documentDate1 = row.getProperties().get("DocumentDate").getDateTimeValue();
						  String documentDate = formatFnDate(documentDate1);
						  

						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("SI.No", serialNumber);
						  jsonObject.put("Class Name", className);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Document Type", documentType);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("HODepartment Code", hODepartmentCode);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Department Code", deptCode);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Subject", subject);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("Page Count", pageCount);
						  jsonObject.put("Diary Number", diaryNumber);
						  jsonObject.put("Department", department);
						  jsonObject.put("Note Date", noteDate);
						  jsonObject.put("MOM Date", momDate);
						  jsonObject.put("Date In", dateIn);
						  jsonObject.put("Date Out", dateOut);
						  jsonObject.put("Received Note Type", receivedNoteType);
						  jsonObject.put("Sent Note Type", sentNoteType);
						  jsonObject.put("Approving Authority", approvingAuthority);
						  jsonObject.put("Approval Type", approvalType);
						  jsonObject.put("Remarks", remarks);
						  jsonObject.put("Note Type", noteType);
						  jsonObject.put("File Name", fileName);
						  jsonObject.put("Received Letter Type", receivedLetterType);
						  jsonObject.put("Sent Letter Type", sentLetterType);
						  jsonObject.put("Document Date", documentDate);

						  // Add the jsonObject to your existing jsonArray
						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");
					  }
				  }else if(repositoryId.equalsIgnoreCase("EDS2")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
						  String documentType = row.getProperties().get("DocumentType").getStringValue();
						  Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
						  String deptName = row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode = row.getProperties().get("Dept_Code").getStringValue();
						  String branchName = row.getProperties().get("BranchName").getStringValue();
						  String branchCode = row.getProperties().get("BranchCode").getStringValue();
						  String subject = row.getProperties().get("Subject").getStringValue();
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
						  Date documentDate1 = row.getProperties().get("DocumentDate").getDateTimeValue();
						  String documentDate = formatFnDate(documentDate1);
						  String sentLetterType = row.getProperties().get("SentLetterType").getStringValue();
						  String receivedLetterType = row.getProperties().get("ReceivedLetterType").getStringValue();
						  String remarks = row.getProperties().get("Remarks").getStringValue();
						  String noteType = row.getProperties().get("Notetype").getStringValue();
						  String approvalType = row.getProperties().get("ApprovalType").getStringValue();
						  String approvingAuthority = row.getProperties().get("ApprovingAuthority").getStringValue();
						  String fileName = row.getProperties().get("FileName").getStringValue();
						  String sentNoteType = row.getProperties().get("SentNoteType").getStringValue();
						  String receivedNoteType = row.getProperties().get("ReceivedNoteType").getStringValue();
						  Date dateOut1 = row.getProperties().get("DateOut").getDateTimeValue();
						  String dateOut = formatFnDate(dateOut1);
						  Date dateIn1 = row.getProperties().get("DateIn").getDateTimeValue();
						  String dateIn = formatFnDate(dateIn1);
						  String department = row.getProperties().get("Department").getStringValue();
						  Date noteDate1 = row.getProperties().get("NoteDate").getDateTimeValue();
						  String noteDate = formatFnDate(noteDate1);
						  Date momDate1 = row.getProperties().get("MOMDate").getDateTimeValue();
						  String momDate = formatFnDate(momDate1);
						  String diaryNumber = row.getProperties().get("DiaryNumber").getStringValue();
						
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("SI.No", serialNumber);
						  jsonObject.put("Class Name", className);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Document Type", documentType);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("HODepartment Code", hODepartmentCode);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Department Code", deptCode);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Subject", subject);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("No Of Pages Uploaded", pageCount);
						  jsonObject.put("Document Date", documentDate);
						  jsonObject.put("Sent Letter Type", sentLetterType);
						  jsonObject.put("Received Letter Type", receivedLetterType);
						  jsonObject.put("Remarks", remarks);
						  jsonObject.put("Note Type", noteType);
						  jsonObject.put("Approval Type", approvalType);
						  jsonObject.put("Approving Authority", approvingAuthority);
						  jsonObject.put("File Name", fileName);
						  jsonObject.put("Sent Note Type", sentNoteType);
						  jsonObject.put("Received Note Type", receivedNoteType);
						  jsonObject.put("Date Out", dateOut);
						  jsonObject.put("Date In", dateIn);
						  jsonObject.put("Department", department);
						  jsonObject.put("Note Date", noteDate);
						  jsonObject.put("MOM Date", momDate);
						  jsonObject.put("Diary Number", diaryNumber);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");

					  }
				  }else if(repositoryId.equalsIgnoreCase("CASA")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
						  Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
						  String dateOfUploading = formatFnDate(dateOfUploading1);
						  String deptName = row.getProperties().get("Dept_Name").getStringValue();
						  String deptCode = row.getProperties().get("Dept_Code").getStringValue();
						  String branchName = row.getProperties().get("BranchName").getStringValue();
						  String branchCode = row.getProperties().get("BranchCode").getStringValue();
						  String uploadedBy = row.getProperties().get("Creator").getStringValue();
						  Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
						  String CIF = row.getProperties().get("CIF").getStringValue();
						  String accountNumber = row.getProperties().get("AccountNumber").getStringValue(); // Changed to camelCase
						  String fileName = row.getProperties().get("FileName").getStringValue();
						  String customerName = row.getProperties().get("CustomerName").getStringValue(); // Changed to camelCase
						  String docType = row.getProperties().get("DOCType").getStringValue(); // Changed to camelCase
						  String identityProofOfFirm = row.getProperties().get("IdentityProofOfFirm").getStringValue(); // Changed for consistency
						  String addressProofOf = row.getProperties().get("AddressProofOf").getStringValue();
						  String addressProofOfFirm = row.getProperties().get("AddressProofOfFirm").getStringValue();
						  String photoIdentity = row.getProperties().get("PhotoIdentity").getStringValue();
						  String identityProofOf = row.getProperties().get("IdentityProofOf").getStringValue();
						  String solid = row.getProperties().get("SOLID").getStringValue(); // Changed to camelCase
						  String nominationRegNumber = row.getProperties().get("NominationREGNumber").getStringValue(); // Changed for consistency
						  String addressProofOfAuthorizedSignature = row.getProperties().get("AddressProofOfAuthorizedSignature").getStringValue(); // Changed for consistency
						  String idProofOfAuthorizedSignatory = row.getProperties().get("IDProofOfAuthorizedSignatory").getStringValue(); // Changed for consistency

						  Date accountOpenDate1 = row.getProperties().get("AccountOpenDate").getDateTimeValue();
						  String accountOpenDate = formatFnDate(accountOpenDate1);

						  Date dob1 = row.getProperties().get("DOB").getDateTimeValue();
						  String dob = formatFnDate(dob1);

						  String kyc = row.getProperties().get("KYC").getStringValue();
						  String staffEmployeeNo = row.getProperties().get("StaffEmployeeNo").getStringValue();
						  String mobile = row.getProperties().get("Mobile").getStringValue();
						  String emailId = row.getProperties().get("EMAILID").getStringValue(); // Changed to camelCase
						  String atmCardNumber = row.getProperties().get("ATMCardNumber").getStringValue(); // Changed for consistency
						  String accountType = row.getProperties().get("AccountType").getStringValue(); // Changed to camelCase
						  String idProofDocuments = row.getProperties().get("IDProofDocuments").getStringValue(); // Changed for consistency
						  String addressProofDocuments = row.getProperties().get("AddressProofDocuments").getStringValue(); // Changed for consistency
						  
						// Continue building the JSON object
						  JSONObject jsonObject = new JSONObject();
						  jsonObject.put("SI.No", serialNumber);
						  jsonObject.put("Class Name", className);
						  jsonObject.put("Document Title", documentTitle);
						  jsonObject.put("Date of Uploading", dateOfUploading);
						  jsonObject.put("Department Name", deptName);
						  jsonObject.put("Department Code", deptCode);
						  jsonObject.put("Branch Name", branchName);
						  jsonObject.put("Branch Code", branchCode);
						  jsonObject.put("Uploaded By", uploadedBy);
						  jsonObject.put("No Of Pages Uploaded", pageCount);
						  jsonObject.put("CIF", CIF);
						  jsonObject.put("Account Number", accountNumber);
						  jsonObject.put("File Name", fileName);
						  jsonObject.put("Customer Name", customerName);
						  jsonObject.put("Document Type", docType);
						  jsonObject.put("Identity Proof Of Firm", identityProofOfFirm);
						  jsonObject.put("Address Proof Of", addressProofOf);
						  jsonObject.put("Address Proof Of Firm", addressProofOfFirm);
						  jsonObject.put("Photo Identity", photoIdentity);
						  jsonObject.put("Identity Proof Of", identityProofOf);
						  jsonObject.put("SOLID", solid);
						  jsonObject.put("Nomination REG Number", nominationRegNumber);
						  jsonObject.put("Address Proof Of Authorized Signature", addressProofOfAuthorizedSignature);
						  jsonObject.put("ID Proof Of Authorized Signatory", idProofOfAuthorizedSignatory);
						  jsonObject.put("Account Open Date", accountOpenDate);
						  jsonObject.put("DOB", dob);
						  jsonObject.put("KYC", kyc);
						  jsonObject.put("Staff Employee No", staffEmployeeNo);
						  jsonObject.put("Mobile", mobile);
						  jsonObject.put("Email ID", emailId);
						  jsonObject.put("ATM Card Number", atmCardNumber);
						  jsonObject.put("Account Type", accountType);
						  jsonObject.put("ID Proof Documents", idProofDocuments);
						  jsonObject.put("Address Proof Documents", addressProofDocuments);

						  jsonArray.add(jsonObject);
						  serialNumber++;
						  System.out.println("---------- Data was successfully added into the JSONObject ----------");
					  }
				  }else if(repositoryId.equalsIgnoreCase("ZOI")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						  	String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                   	    	String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		            		String financialYear =  row.getProperties().get("FinancialYear").getStringValue();
		            		Date dateofAUDIT1 =  row.getProperties().get("DateofAUDIT").getDateTimeValue();
		            		String dateofAUDIT = formatFnDate(dateofAUDIT1);
		        
		            		System.out.println("---------- Adding the Data into JSONObject ----------");
		            		JSONObject jsonObject = new JSONObject();
		            		jsonObject.put("SI. No", serialNumber);
		            		jsonObject.put("Class Name", className);
		            		jsonObject.put("Document Title", documentTitle);
		            		jsonObject.put("Date of Uploading", dateOfUploading);
		            		jsonObject.put("Department Name", deptName);
		            		jsonObject.put("Dept Code", deptCode);
		            		jsonObject.put("Branch Name", branchName);
		            		jsonObject.put("Branch Code", branchCode);
		            		jsonObject.put("HO Department Code", hODepartmentCode);
		            		jsonObject.put("Uploaded By", uploadedBy);
		            		jsonObject.put("No Of Pages Uploaded", pageCount);
		            		jsonObject.put("Document Type", documentType);
		            		jsonObject.put("Date of AUDIT", dateofAUDIT);
		            		jsonObject.put("Financial Year", financialYear);
		            		jsonArray.add(jsonObject);
		            		serialNumber++;
		            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
					  }
				  }else if(repositoryId.equalsIgnoreCase("ZOIT")){
					  Id docId = row.getProperties().get("Id").getIdValue();
					  Document document = Factory.Document.fetchInstance(os, docId, null);
					  String className = document.getClassName();
					  System.out.println("className: "+className);
					  if (CLASSNAMESTOINGORE.contains(className)) {
						  // Skip processing for this class
						  continue;
					  }else{
						    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                   	        String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		            		String noteType =  row.getProperties().get("NOTETYPE").getStringValue();
		            		Date date1 =  row.getProperties().get("Date").getDateTimeValue();
		            		String date = formatFnDate(date1);
		            		
		            		System.out.println("---------- Adding the Data into JSONObject ----------");
   		            		JSONObject jsonObject = new JSONObject();
   		            		jsonObject.put("SI. No", serialNumber);
   		            		jsonObject.put("Class Name", className);
   		            		jsonObject.put("Document Title", documentTitle);
   		            		jsonObject.put("Date of Uploading", dateOfUploading);
   		            		jsonObject.put("Department Name", deptName);
   		            		jsonObject.put("Dept Code", deptCode);
   		            		jsonObject.put("Branch Name", branchName);
   		            		jsonObject.put("Branch Code", branchCode);
   		            		jsonObject.put("HO Department Code", hODepartmentCode);
   		            		jsonObject.put("Uploaded By", uploadedBy);
   		            		jsonObject.put("No Of Pages Uploaded", pageCount);
   		            		jsonObject.put("Document Type", documentType);
   		            		jsonObject.put("Note Type", noteType);
   		            		jsonObject.put("Date", date);
   		            		jsonArray.add(jsonObject);
   		            		serialNumber++;
   		            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
   		            		
					  }
				  }else if(repositoryId.equalsIgnoreCase("ZOLOS")){
					  Id docId = row.getProperties().get("Id").getIdValue();
          		    Document document = Factory.Document.fetchInstance(os, docId, null);
          		    String className = document.getClassName();
          		    System.out.println("className: "+className);
          		    if (CLASSNAMESTOINGORE.contains(className)) {
                          // Skip processing for this class
                          continue;
                      }else{
                   	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                   	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		            		String schemeType =  row.getProperties().get("SCHEMETYPE").getStringValue();
		            		String documents =  row.getProperties().get("Documents").getStringValue();
		            		String documentSubtype1PostSanctionDocuments =  row.getProperties().get("DOCUMENTSUBTYPE1POSTSANCTIONDOCUMENTS").getStringValue();
		            		String  documentSubtype1PreSanctionDocuments=  row.getProperties().get("DOCUMENTSUBTYPE1PRESANCTIONDOCUMENTS").getStringValue();
		            		String loanAccountNumber =  row.getProperties().get("LOANACCOUNTNUMBER").getStringValue();
		            		String customerId =  row.getProperties().get("CUSTOMERID").getStringValue();
		            		String documentSubType =  row.getProperties().get("DOCUMENTSUBTYPE").getStringValue();
		            		
		            		
		            		StringList customerName1 =  row.getProperties().get("CUSTOMERNAME").getStringListValue();
		            		String customerName = formatStringList(customerName1);
		            		StringList guarantorName1 =  row.getProperties().get("GUARANTORNAME").getStringListValue();
		            		String guarantorName = formatStringList(guarantorName1);

		            		System.out.println("---------- Adding the Data into JSONObject ----------");
		            		JSONObject jsonObject = new JSONObject();
		            		jsonObject.put("SI. No", serialNumber);
		            		jsonObject.put("DOCUMENT SUBTYPE (1. POST SANCTION DOCUMENTS)", documentSubtype1PostSanctionDocuments);
		            		jsonObject.put("DOCUMENT SUBTYPE (1. PRE SANCTION DOCUMENTS)", documentSubtype1PreSanctionDocuments);
		            		jsonObject.put("LOAN ACCOUNT NUMBER", loanAccountNumber);
		            		jsonObject.put("CUSTOMER ID", customerId);
		            		jsonObject.put("CUSTOMER NAME", customerName);
		            		jsonObject.put("GUARANTOR NAME", guarantorName);
		            		jsonObject.put("DOCUMENT SUBTYPE", documentSubType);
		            		jsonObject.put("Class Name", className);
		            		jsonObject.put("Document Title", documentTitle);
		            		jsonObject.put("Date of Uploading", dateOfUploading);
		            		jsonObject.put("Department Name", deptName);
		            		jsonObject.put("Dept Code", deptCode);
		            		jsonObject.put("Branch Name", branchName);
		            		jsonObject.put("Branch Code", branchCode);
		            		jsonObject.put("HO Department Code", hODepartmentCode);
		            		jsonObject.put("Uploaded By", uploadedBy);
		            		jsonObject.put("No Of Pages Uploaded", pageCount);
		            		jsonObject.put("Document Type", documentType);
		            		jsonObject.put("SCHEME TYPE", schemeType);
		            		jsonObject.put("Documents",documents );
		            		jsonArray.add(jsonObject);
		            		serialNumber++;
		            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
                      }
				  }else if(repositoryId.equalsIgnoreCase("ZOLR")){
					  Id docId = row.getProperties().get("Id").getIdValue();
          		    Document document = Factory.Document.fetchInstance(os, docId, null);
          		    String className = document.getClassName();
          		    System.out.println("className: "+className);
          		    if (CLASSNAMESTOINGORE.contains(className)) {
                          // Skip processing for this class
                          continue;
                      }else{
                   	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                   	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		            		String accountNumber =  row.getProperties().get("AccountNumber").getStringValue();
		            		String documents =  row.getProperties().get("Documents").getStringValue();
		            		String customerId =  row.getProperties().get("CUSTOMERID").getStringValue();
		            		String loanAccountNumber =  row.getProperties().get("LOANACCOUNTNUMBER").getStringValue();
		            		String borrowerName =  row.getProperties().get("BorrowerName").getStringValue();
		            		String dateofNPA =  row.getProperties().get("DATEOFNPA").getStringValue();
		            		String enfAgentName =  row.getProperties().get("EnfAgentName").getStringValue();
		            		String region =  row.getProperties().get("REGION").getStringValue();
		            		String advocateName =  row.getProperties().get("ADVOCATENAME").getStringValue();
		            		String billAmount =  row.getProperties().get("BILLAMOUNT").getStringValue();
		            		Date  billDate1=  row.getProperties().get("BILLDATE").getDateTimeValue();
		            		String billDate = formatFnDate(billDate1);

		            		System.out.println("---------- Adding the Data into JSONObject ----------");
		            		JSONObject jsonObject = new JSONObject();
		            		jsonObject.put("SI. No", serialNumber);
		            		jsonObject.put("Class Name", className);
		            		jsonObject.put("Document Title", documentTitle);
		            		jsonObject.put("Date of Uploading", dateOfUploading);
		            		jsonObject.put("Department Name", deptName);
		            		jsonObject.put("Dept Code", deptCode);
		            		jsonObject.put("Branch Name", branchName);
		            		jsonObject.put("Branch Code", branchCode);
		            		jsonObject.put("HO Department Code", hODepartmentCode);
		            		jsonObject.put("Uploaded By", uploadedBy);
		            		jsonObject.put("No Of Pages Uploaded", pageCount);
		            		jsonObject.put("Document Type", documentType);
		            		jsonObject.put("Documents",documents );
		            		jsonObject.put("BILL AMOUNT",billAmount);
		            		jsonObject.put("BILL DATE",billDate );
		            		jsonObject.put("ADVOCATE NAME", advocateName);
		            		jsonObject.put("REGION", region);
		            		jsonObject.put( "Enf. Agent Name",enfAgentName);
		            		jsonObject.put("LOAN ACCOUNT NUMBER", loanAccountNumber);
		            		jsonObject.put("CUSTOMER ID", customerId);
		            		jsonObject.put("DATE OF NPA", dateofNPA);
		            		jsonObject.put("Account Number",accountNumber );
		            		jsonObject.put("Borrower Name", borrowerName);
		            		
		            		jsonArray.add(jsonObject);
		            		serialNumber++;
		            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
                      }
				  }else if(repositoryId.equalsIgnoreCase("")){
					  Id docId = row.getProperties().get("Id").getIdValue();
          		    Document document = Factory.Document.fetchInstance(os, docId, null);
          		    String className = document.getClassName();
          		    System.out.println("className: "+className);
          		    if (CLASSNAMESTOINGORE.contains(className)) {
                          // Skip processing for this class
                          continue;
                      }else{
                   	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                   	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		         
		            		System.out.println("---------- Adding the Data into JSONObject ----------");
		            		JSONObject jsonObject = new JSONObject();
		            		jsonObject.put("SI. No", serialNumber);
		            		jsonObject.put("Class Name", className);
		            		jsonObject.put("Document Title", documentTitle);
		            		jsonObject.put("Date of Uploading", dateOfUploading);
		            		jsonObject.put("Department Name", deptName);
		            		jsonObject.put("Dept Code", deptCode);
		            		jsonObject.put("Branch Name", branchName);
		            		jsonObject.put("Branch Code", branchCode);
		            		jsonObject.put("HO Department Code", hODepartmentCode);
		            		jsonObject.put("Uploaded By", uploadedBy);
		            		jsonObject.put("No Of Pages Uploaded", pageCount);
		            		jsonObject.put("Document Type", documentType);
		            		jsonArray.add(jsonObject);
		            		serialNumber++;
		            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
 		
                      }
				  }else if(repositoryId.equalsIgnoreCase("HOCOMP")){
					  Id docId = row.getProperties().get("Id").getIdValue();
           		    Document document = Factory.Document.fetchInstance(os, docId, null);
           		    String className = document.getClassName();
           		    System.out.println("className: "+className);
           		    if (CLASSNAMESTOINGORE.contains(className)) {
                           // Skip processing for this class
                           continue;
                       }else{
                    	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                    	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		            		String regulators = row.getProperties().get("Regulators").getStringValue();
		            		String year = row.getProperties().get("Year").getStringValue();
		            		String referenceNumber = row.getProperties().get("ReferenceNumber").getStringValue();
		            		StringList roleHolderDepartment1 =  row.getProperties().get("roleHolderDepartment").getStringListValue();
		            		String roleHolderDepartment = formatStringList(roleHolderDepartment1);
		            		String fileName = row.getProperties().get("FileName").getStringValue();
		            		String from = row.getProperties().get("From").getStringValue();
		            		String subject = row.getProperties().get("Subject").getStringValue();
		            		String type = row.getProperties().get("Type").getStringValue();
		            		Date releaseDate1 =  row.getProperties().get("ReleaseDate").getDateTimeValue();
		            		String releaseDate = formatFnDate(releaseDate1);
		            		Date dateofReceipt1 =  row.getProperties().get("DateofReceipt").getDateTimeValue();
		            		String dateofReceipt = formatFnDate(dateofReceipt1);
		            		Date visitDate1 =  row.getProperties().get("VisitDate").getDateTimeValue();
		            		String visitDate = formatFnDate(visitDate1);
		            		Date replyofCompliance1 =  row.getProperties().get("ReplyofCompliance").getDateTimeValue();
		            		String replyOfCompliance = formatFnDate(replyofCompliance1);
		            		String srNo = row.getProperties().get("SrNo").getStringValue();
		            		String receivedfrom = row.getProperties().get("Receivedfrom").getStringValue();
		            		String positionofCompliance = row.getProperties().get("PositionofCompliance").getStringValue();
		            		Date date1 =  row.getProperties().get("Date").getDateTimeValue();
		            		String date = formatFnDate(date1);
		            		Date RARDATE1 =  row.getProperties().get("RARDATE").getDateTimeValue();
		            		String RARDATE = formatFnDate(RARDATE1);
		            		Date dateofCompliance1 =  row.getProperties().get("DateofCompliance").getDateTimeValue();
		            		String dateofCompliance = formatFnDate(dateofCompliance1);
		            		Date RMPDATE1 =  row.getProperties().get("RMPDATE").getDateTimeValue();
		            		String RMPDATE = formatFnDate(RMPDATE1);
		            		Date timeline1 =  row.getProperties().get("Timeline").getDateTimeValue();
		            		String timeline = formatFnDate(timeline1);
		            		String SOPName = row.getProperties().get("SOPName").getStringValue();
		            		String SOPNumber = row.getProperties().get("SOPNumber").getStringValue();
		            		String underPower = row.getProperties().get("UnderPower").getStringValue();
		            		String  receivedFrom= row.getProperties().get("ReceivedFrom").getStringValue();
		            		
		            		System.out.println("---------- Adding the Data into JSONObject ----------");
		            		JSONObject jsonObject = new JSONObject();
		            		jsonObject.put("SI. No", serialNumber);
		            		jsonObject.put("Class Name", className);
		            		jsonObject.put("Document Title", documentTitle);
		            		jsonObject.put("Date of Uploading", dateOfUploading);
		            		jsonObject.put("Department Name", deptName);
		            		jsonObject.put("Dept Code", deptCode);
		            		jsonObject.put("Branch Name", branchName);
		            		jsonObject.put("Branch Code", branchCode);
		            		jsonObject.put("HO Department Code", hODepartmentCode);
		            		jsonObject.put("Uploaded By", uploadedBy);
		            		jsonObject.put("No Of Pages Uploaded", pageCount);
		            		jsonObject.put("Document Type", documentType);
		            		jsonObject.put("File Name", fileName);
		                    jsonObject.put("Subject",subject );
		                    jsonObject.put("SOPName", SOPName);
		                    jsonObject.put("SOPNumber", SOPNumber);
		                    jsonObject.put("Received From", receivedFrom);
		                    jsonObject.put("Reference Number",referenceNumber );
		                    jsonObject.put("Type", type);
		                    jsonObject.put("Regulators", regulators);
		                    jsonObject.put("Year",year );
		                    jsonObject.put("Role Holder Department",roleHolderDepartment);
		                    jsonObject.put("RARDate", RARDATE);
		                    jsonObject.put(" RMP DATE", RMPDATE);
		                    jsonObject.put("Timeline", timeline);
		                    jsonObject.put("Date of Receipt", dateofReceipt);
		                    jsonObject.put("Date of Compliance", dateofCompliance);
		                    jsonObject.put("Under Power",underPower );
		                    jsonObject.put("Date",date );
		                    jsonObject.put("positionofCompliance", positionofCompliance);
		                    jsonObject.put("Received from", receivedfrom);
		                    jsonObject.put("Release Date", releaseDate);
		                    jsonObject.put("Visit Date", visitDate);
		                    jsonObject.put("Reply of Compliance", replyOfCompliance);
		                    jsonObject.put("SRNo", srNo);
		                    jsonObject.put("From", from);
		               
		            		jsonArray.add(jsonObject);
		            		serialNumber++;
		            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
                       }
				  }else if(repositoryId.equalsIgnoreCase("HOC")){
					  Id docId = row.getProperties().get("Id").getIdValue();
           		    Document document = Factory.Document.fetchInstance(os, docId, null);
           		    String className = document.getClassName();
           		    System.out.println("className: "+className);
           		    if (CLASSNAMESTOINGORE.contains(className)) {
                           // Skip processing for this class
                           continue;
                       }else{
                    	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                    	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		            		String resolutionNumber = row.getProperties().get("ResolutionNumber").getStringValue();
		            		String title = row.getProperties().get("Title").getStringValue();
		            		String subType = row.getProperties().get("SubType").getStringValue();
		            		String type = row.getProperties().get("Type").getStringValue();
		            		Date date1 =  row.getProperties().get("Date").getDateTimeValue();
		            		String date = formatFnDate(date1);
		            		String periodicity = row.getProperties().get("Periodicity").getStringValue();
		            		String sanctioningAuthority = row.getProperties().get("SanctioningAuthority").getStringValue();
		       
		            		System.out.println("---------- Adding the Data into JSONObject ----------");
		            		JSONObject jsonObject = new JSONObject();
		            		jsonObject.put("SI. No", serialNumber);
		            		jsonObject.put("Class Name", className);
		            		jsonObject.put("Document Title", documentTitle);
		            		jsonObject.put("Date of Uploading", dateOfUploading);
		            		jsonObject.put("Department Name", deptName);
		            		jsonObject.put("Dept Code", deptCode);
		            		jsonObject.put("Branch Name", branchName);
		            		jsonObject.put("Branch Code", branchCode);
		            		jsonObject.put("HO Department Code", hODepartmentCode);
		            		jsonObject.put("Uploaded By", uploadedBy);
		            		jsonObject.put("No Of Pages Uploaded", pageCount);
		            		jsonObject.put("Document Type", documentType);
		            		jsonObject.put("Resolution Number", resolutionNumber);
		                    jsonObject.put("Title", title);
		                    jsonObject.put("Date", date);
		                    jsonObject.put("Sanctioning Authority", sanctioningAuthority);
		                    jsonObject.put("Type", type);
		                    jsonObject.put("Periodicity", periodicity);
		                    jsonObject.put("Sub Type", subType);
		            		jsonArray.add(jsonObject);
		            		serialNumber++;
		            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
  		
                       }
				  }else if(repositoryId.equalsIgnoreCase("HOCM")) {
       				Id docId = row.getProperties().get("Id").getIdValue();
         		    Document document = Factory.Document.fetchInstance(os, docId, null);
         		    String className = document.getClassName();
         		    System.out.println("className: "+className);
         		    if (CLASSNAMESTOINGORE.contains(className)) {
                         // Skip processing for this class
                         continue;
                     }else{
                  	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                  	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
		            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
		            		String dateOfUploading = formatFnDate(dateOfUploading1);
		            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
		            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
		            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
		            		String branchName =  row.getProperties().get("BranchName").getStringValue();
		            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
		            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
		            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
		            		Date date1 =  row.getProperties().get("Date").getDateTimeValue();
		            		String date = formatFnDate(date1);
		            		String CRM_Title = row.getProperties().get("CRM_Titleeator").getStringValue();
		            		String type = row.getProperties().get("Type").getStringValue();
		            		String refNumber = row.getProperties().get("RefNumber").getStringValue();
		            		String auditReportType = row.getProperties().get("AuditReportType").getStringValue();
		            		
		            		System.out.println("---------- Adding the Data into JSONObject ----------");
	            		JSONObject jsonObject = new JSONObject();
	            		jsonObject.put("SI. No", serialNumber);
	            		jsonObject.put("Class Name", className);
	            		jsonObject.put("Document Title", documentTitle);
	            		jsonObject.put("Date of Uploading", dateOfUploading);
	            		jsonObject.put("Department Name", deptName);
	            		jsonObject.put("Dept Code", deptCode);
	            		jsonObject.put("Branch Name", branchName);
	            		jsonObject.put("Branch Code", branchCode);
	            		jsonObject.put("HO Department Code", hODepartmentCode);
	            		jsonObject.put("Uploaded By", uploadedBy);
	            		jsonObject.put("No Of Pages Uploaded", pageCount);
	            		jsonObject.put("Document Type", documentType);
	            		jsonObject.put("Date", date);
	            		jsonObject.put("Title", CRM_Title);
	            		jsonObject.put("Ref Number", refNumber);
	            		jsonObject.put("Audit Report Type",auditReportType );
	            		jsonArray.add(jsonObject);
	            		serialNumber++;
	            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
		
                     }
		    
				  }else if(repositoryId.equalsIgnoreCase("ZORAJ")) {
      				Id docId = row.getProperties().get("Id").getIdValue();
        		    Document document = Factory.Document.fetchInstance(os, docId, null);
        		    String className = document.getClassName();
        		    System.out.println("className: "+className);
        		    if (CLASSNAMESTOINGORE.contains(className)) {
                        // Skip processing for this class
                        continue;
                    }else{
                 	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                 	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
	            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
	            		String dateOfUploading = formatFnDate(dateOfUploading1);
	            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
	            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
	            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
	            		String branchName =  row.getProperties().get("BranchName").getStringValue();
	            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
	            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
	            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
	         
	            		System.out.println("---------- Adding the Data into JSONObject ----------");
	            		JSONObject jsonObject = new JSONObject();
	            		jsonObject.put("SI. No", serialNumber);
	            		jsonObject.put("Class Name", className);
	            		jsonObject.put("Document Title", documentTitle);
	            		jsonObject.put("Date of Uploading", dateOfUploading);
	            		jsonObject.put("Department Name", deptName);
	            		jsonObject.put("Dept Code", deptCode);
	            		jsonObject.put("Branch Name", branchName);
	            		jsonObject.put("Branch Code", branchCode);
	            		jsonObject.put("HO Department Code", hODepartmentCode);
	            		jsonObject.put("Uploaded By", uploadedBy);
	            		jsonObject.put("No Of Pages Uploaded", pageCount);
	            		jsonObject.put("Document Type", documentType);
	            		jsonArray.add(jsonObject);
	            		serialNumber++;
	            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
		
                    }
        		    
				  }else if(repositoryId.equalsIgnoreCase("ZOLOS")) {
      				Id docId = row.getProperties().get("Id").getIdValue();
        		    Document document = Factory.Document.fetchInstance(os, docId, null);
        		    String className = document.getClassName();
        		    System.out.println("className: "+className);
        		    if (CLASSNAMESTOINGORE.contains(className)) {
                        // Skip processing for this class
                        continue;
                    }else{
                 	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                 	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
	            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
	            		String dateOfUploading = formatFnDate(dateOfUploading1);
	            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
	            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
	            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
	            		String branchName =  row.getProperties().get("BranchName").getStringValue();
	            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
	            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
	            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
	            		String schemeType =  row.getProperties().get("SCHEMETYPE").getStringValue();
	            		String documents =  row.getProperties().get("Documents").getStringValue();
	            		String documentSubtype1PostSanctionDocuments =  row.getProperties().get("DOCUMENTSUBTYPE1POSTSANCTIONDOCUMENTS").getStringValue();
	            		String  documentSubtype1PreSanctionDocuments=  row.getProperties().get("DOCUMENTSUBTYPE1PRESANCTIONDOCUMENTS").getStringValue();
	            		String loanAccountNumber =  row.getProperties().get("LOANACCOUNTNUMBER").getStringValue();
	            		String customerId =  row.getProperties().get("CUSTOMERID").getStringValue();
	            		String documentSubType =  row.getProperties().get("DOCUMENTSUBTYPE").getStringValue();
	            		
	            		
	            		StringList customerName1 =  row.getProperties().get("CUSTOMERNAME").getStringListValue();
	            		String customerName = formatStringList(customerName1);
	            		StringList guarantorName1 =  row.getProperties().get("GUARANTORNAME").getStringListValue();
	            		String guarantorName = formatStringList(guarantorName1);
	            	
	            		System.out.println("---------- Adding the Data into JSONObject ----------");
	            		JSONObject jsonObject = new JSONObject();
	            		jsonObject.put("SI. No", serialNumber);
	            		jsonObject.put("DOCUMENT SUBTYPE (1. POST SANCTION DOCUMENTS)", documentSubtype1PostSanctionDocuments);
	            		jsonObject.put("DOCUMENT SUBTYPE (1. PRE SANCTION DOCUMENTS)", documentSubtype1PreSanctionDocuments);
	            		jsonObject.put("LOAN ACCOUNT NUMBER", loanAccountNumber);
	            		jsonObject.put("CUSTOMER ID", customerId);
	            		jsonObject.put("CUSTOMER NAME", customerName);
	            		jsonObject.put("GUARANTOR NAME", guarantorName);
	            		jsonObject.put("DOCUMENT SUBTYPE", documentSubType);
	            		jsonObject.put("Class Name", className);
	            		jsonObject.put("Document Title", documentTitle);
	            		jsonObject.put("Date of Uploading", dateOfUploading);
	            		jsonObject.put("Department Name", deptName);
	            		jsonObject.put("Dept Code", deptCode);
	            		jsonObject.put("Branch Name", branchName);
	            		jsonObject.put("Branch Code", branchCode);
	            		jsonObject.put("HO Department Code", hODepartmentCode);
	            		jsonObject.put("Uploaded By", uploadedBy);
	            		jsonObject.put("No Of Pages Uploaded", pageCount);
	            		jsonObject.put("Document Type", documentType);
	            		jsonObject.put("SCHEME TYPE", schemeType);
	            		jsonObject.put("Documents",documents );
	            		jsonArray.add(jsonObject);
	            		serialNumber++;
	            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
                    }
				  }else if(repositoryId.equalsIgnoreCase("ZOLR")) {
      				Id docId = row.getProperties().get("Id").getIdValue();
        		    Document document = Factory.Document.fetchInstance(os, docId, null);
        		    String className = document.getClassName();
        		    System.out.println("className: "+className);
        		    if (CLASSNAMESTOINGORE.contains(className)) {
                        // Skip processing for this class
                        continue;
                    }else{
                 	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                 	   Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
	            		String dateOfUploading = formatFnDate(dateOfUploading1);
                 	   String deptName =  row.getProperties().get("Dept_Name").getStringValue();
	            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
	            		String branchName =  row.getProperties().get("BranchName").getStringValue();
	            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
	            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
	            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
	            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
	            		String accountNumber =  row.getProperties().get("AccountNumber").getStringValue();
	            		String documents =  row.getProperties().get("Documents").getStringValue();
	            		String documentType =  row.getProperties().get("DocumentType").getStringValue();
	            		String customerId =  row.getProperties().get("CUSTOMERID").getStringValue();
	            		String loanAccountNumber =  row.getProperties().get("LOANACCOUNTNUMBER").getStringValue();
	            		String borrowerName =  row.getProperties().get("BorrowerName").getStringValue();
	            		String dateofNPA =  row.getProperties().get("DATEOFNPA").getStringValue();
	            		String enfAgentName =  row.getProperties().get("EnfAgentName").getStringValue();
	            		String region =  row.getProperties().get("REGION").getStringValue();
	            		String advocateName =  row.getProperties().get("ADVOCATENAME").getStringValue();
	            		String billAmount =  row.getProperties().get("BILLAMOUNT").getStringValue();
	            		Date  billDate1=  row.getProperties().get("BILLDATE").getDateTimeValue();
	            		String billDate = formatFnDate(billDate1);
	            		
	            		System.out.println("---------- Adding the Data into JSONObject ----------");
	            		JSONObject jsonObject = new JSONObject();
	            		jsonObject.put("SI. No", serialNumber);
	            		jsonObject.put("BILL AMOUNT",billAmount);
	            		jsonObject.put("BILL DATE",billDate );
	            		jsonObject.put("ADVOCATE NAME", advocateName);
	            		jsonObject.put("REGION", region);
	            		jsonObject.put( "Enf. Agent Name",enfAgentName);
	            		jsonObject.put("LOAN ACCOUNT NUMBER", loanAccountNumber);
	            		jsonObject.put("CUSTOMER ID", customerId);
	            		jsonObject.put("DATE OF NPA", dateofNPA);
	            		jsonObject.put("Account Number",accountNumber );
	            		jsonObject.put("Borrower Name", borrowerName);
	            		jsonObject.put("Class Name", className);
	            		jsonObject.put("Document Title", documentTitle);
	            		jsonObject.put("Date of Uploading", dateOfUploading);
	            		jsonObject.put("Department Name", deptName);
	            		jsonObject.put("Dept Code", deptCode);
	            		jsonObject.put("Branch Name", branchName);
	            		jsonObject.put("Branch Code", branchCode);
	            		jsonObject.put("HO Department Code", hODepartmentCode);
	            		jsonObject.put("Uploaded By", uploadedBy);
	            		jsonObject.put("No Of Pages Uploaded", pageCount);
	            		jsonObject.put("Document Type", documentType);
	            		jsonObject.put("Documents",documents );
	            		jsonArray.add(jsonObject);
	            		serialNumber++;
	            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
		
                    }
		    
				  }else if(repositoryId.equalsIgnoreCase("ZOI")) {
      				Id docId = row.getProperties().get("Id").getIdValue();
        		    Document document = Factory.Document.fetchInstance(os, docId, null);
        		    String className = document.getClassName();
        		    System.out.println("className: "+className);
        		    if (CLASSNAMESTOINGORE.contains(className)) {
                        // Skip processing for this class
                        continue;
                    }else{
                 	    String documentTitle =  row.getProperties().get("DocumentTitle").getStringValue();
                 	    String documentType =  row.getProperties().get("DocumentType").getStringValue();
	            		Date dateOfUploading1 =  row.getProperties().get("DateCreated").getDateTimeValue();
	            		String dateOfUploading = formatFnDate(dateOfUploading1);
	            		String hODepartmentCode =  row.getProperties().get("HODepartmentCode").getStringValue();
	            		String deptName =  row.getProperties().get("Dept_Name").getStringValue();
	            		String deptCode =  row.getProperties().get("Dept_Code").getStringValue();
	            		String branchName =  row.getProperties().get("BranchName").getStringValue();
	            		String branchCode =  row.getProperties().get("BranchCode").getStringValue();
	            		String uploadedBy = row.getProperties().get("Creator").getStringValue();
	            		Integer pageCount =  row.getProperties().get("FN_PageCount").getInteger32Value();
	            		String financialYear =  row.getProperties().get("FinancialYear").getStringValue();
	            		Date DateofAUDIT1 =  row.getProperties().get("DateofAUDIT").getDateTimeValue();
	            		String dateofAUDIT = formatFnDate(DateofAUDIT1);
	        
	            		System.out.println("---------- Adding the Data into JSONObject ----------");
	            		JSONObject jsonObject = new JSONObject();
	            		jsonObject.put("SI. No", serialNumber);
	            		jsonObject.put("Class Name", className);
	            		jsonObject.put("Document Title", documentTitle);
	            		jsonObject.put("Date of Uploading", dateOfUploading);
	            		jsonObject.put("Department Name", deptName);
	            		jsonObject.put("Dept Code", deptCode);
	            		jsonObject.put("Branch Name", branchName);
	            		jsonObject.put("Branch Code", branchCode);
	            		jsonObject.put("HO Department Code", hODepartmentCode);
	            		jsonObject.put("Uploaded By", uploadedBy);
	            		jsonObject.put("No Of Pages Uploaded", pageCount);
	            		jsonObject.put("Document Type", documentType);
	            		jsonObject.put("Date of AUDIT", dateofAUDIT);
	            		jsonObject.put("Financial Year", financialYear);
	            		jsonArray.add(jsonObject);
	            		serialNumber++;
	            		System.out.println("---------- Data was successfully added into the JSONObject ----------");
		
                    }
		    
				  }else if(repositoryId.equalsIgnoreCase("ZOHR")) {
       				Id docId = row.getProperties().get("Id").getIdValue();
         		    Document document = Factory.Document.fetchInstance(os, docId, null);
         		    String className = document.getClassName();
         		    System.out.println("className: "+className);
         		    if (CLASSNAMESTOINGORE.contains(className)) {
                         // Skip processing for this class
                         continue;
                     }else{
							String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							String documentType = row.getProperties().get("DocumentType").getStringValue();
							Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							String dateOfUploading = formatFnDate(dateOfUploading1);
							String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							String deptName = row.getProperties().get("Dept_Name").getStringValue();
							String deptCode = row.getProperties().get("Dept_Code").getStringValue();
							String branchName = row.getProperties().get("BranchName").getStringValue();
							String branchCode = row.getProperties().get("BranchCode").getStringValue();
							String uploadedBy = row.getProperties().get("Creator").getStringValue();
							Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							Date date1 = row.getProperties().get("DATE").getDateTimeValue();
							String date = formatFnDate(date1);
							String names = row.getProperties().get("NAMES").getStringValue();
							System.out.println("---------- Adding the Data into JSONObject ----------");
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("SI. No", serialNumber);
							jsonObject.put("Class Name", className);
							jsonObject.put("Document Title", documentTitle);
							jsonObject.put("Date of Uploading", dateOfUploading);
							jsonObject.put("Department Name", deptName);
							jsonObject.put("Dept Code", deptCode);
							jsonObject.put("Branch Name", branchName);
							jsonObject.put("Branch Code", branchCode);
							jsonObject.put("HO Department Code", hODepartmentCode);
							jsonObject.put("Uploaded By", uploadedBy);
							jsonObject.put("No Of Pages Uploaded", pageCount);
							jsonObject.put("Document Type", documentType);
							jsonObject.put("Date", date);
							jsonObject.put("NAME", names);
							jsonArray.add(jsonObject);
							serialNumber++;
							System.out.println("---------- Data was successfully added into the JSONObject ----------");
		
                     }
		    
					} else if (repositoryId.equalsIgnoreCase("ZOGA")) {
						Id docId = row.getProperties().get("Id").getIdValue();
						Document document = Factory.Document.fetchInstance(os, docId, null);
						String className = document.getClassName();
						System.out.println("className: " + className);
						if (CLASSNAMESTOINGORE.contains(className)) {
							// Skip processing for this class
							continue;
						} else {
							String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							String documentType = row.getProperties().get("DocumentType").getStringValue();
							Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							String dateOfUploading = formatFnDate(dateOfUploading1);
							String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							String deptName = row.getProperties().get("Dept_Name").getStringValue();
							String deptCode = row.getProperties().get("Dept_Code").getStringValue();
							String branchName = row.getProperties().get("BranchName").getStringValue();
							String branchCode = row.getProperties().get("BranchCode").getStringValue();
							String uploadedBy = row.getProperties().get("Creator").getStringValue();
							Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							Date date1 = row.getProperties().get("DATE").getDateTimeValue();
							String date = formatFnDate(date1);
							String assetType = row.getProperties().get("ASSETTYPE").getStringValue();
							System.out.println("---------- Adding the Data into JSONObject ----------");
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("SI. No", serialNumber);
							jsonObject.put("Class Name", className);
							jsonObject.put("Document Title", documentTitle);
							jsonObject.put("Date of Uploading", dateOfUploading);
							jsonObject.put("Department Name", deptName);
							jsonObject.put("Dept Code", deptCode);
							jsonObject.put("Branch Name", branchName);
							jsonObject.put("Branch Code", branchCode);
							jsonObject.put("HO Department Code", hODepartmentCode);
							jsonObject.put("Uploaded By", uploadedBy);
							jsonObject.put("No Of Pages Uploaded", pageCount);
							jsonObject.put("Document Type", documentType);
							jsonObject.put("Date", date);
							jsonObject.put("", assetType);
							jsonArray.add(jsonObject);
							serialNumber++;
							System.out.println("---------- Data was successfully added into the JSONObject ----------");

						}

					} else if (repositoryId.equalsIgnoreCase("ZOATM")) {
						Id docId = row.getProperties().get("Id").getIdValue();
						Document document = Factory.Document.fetchInstance(os, docId, null);
						String className = document.getClassName();
						System.out.println("className: " + className);
						if (CLASSNAMESTOINGORE.contains(className)) {
							// Skip processing for this class
							continue;
						} else {
							String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							String documentType = row.getProperties().get("DocumentType").getStringValue();
							Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							String dateOfUploading = formatFnDate(dateOfUploading1);
							String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							String deptName = row.getProperties().get("Dept_Name").getStringValue();
							String deptCode = row.getProperties().get("Dept_Code").getStringValue();
							String branchName = row.getProperties().get("BranchName").getStringValue();
							String branchCode = row.getProperties().get("BranchCode").getStringValue();
							String uploadedBy = row.getProperties().get("Creator").getStringValue();
							Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							Date transactionDate1 = row.getProperties().get("TRANSACTIONDATE").getDateTimeValue();
							String transactionDate = formatFnDate(transactionDate1);
							double amount = row.getProperties().get("AMOUNT").getFloat64Value();
							String noteType = row.getProperties().get("NOTETYPE").getStringValue();

							System.out.println("---------- Adding the Data into JSONObject ----------");
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("SI. No", serialNumber);
							jsonObject.put("Class Name", className);
							jsonObject.put("Document Title", documentTitle);
							jsonObject.put("Date of Uploading", dateOfUploading);
							jsonObject.put("Department Name", deptName);
							jsonObject.put("Dept Code", deptCode);
							jsonObject.put("Branch Name", branchName);
							jsonObject.put("Branch Code", branchCode);
							jsonObject.put("HO Department Code", hODepartmentCode);
							jsonObject.put("Uploaded By", uploadedBy);
							jsonObject.put("No Of Pages Uploaded", pageCount);
							jsonObject.put("Document Type", documentType);
							jsonObject.put("TRANSACTION DATE", transactionDate);
							jsonObject.put("AMOUNT", amount);
							jsonObject.put("NOTE TYPE", noteType);
							jsonArray.add(jsonObject);
							serialNumber++;
							System.out.println("---------- Data was successfully added into the JSONObject ----------");

						}

					} else if (repositoryId.equalsIgnoreCase("ZOHR")) {
						Id docId = row.getProperties().get("Id").getIdValue();
						Document document = Factory.Document.fetchInstance(os, docId, null);
						String className = document.getClassName();
						System.out.println("className: " + className);
						if (CLASSNAMESTOINGORE.contains(className)) {
							// Skip processing for this class
							continue;
						} else {
							String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							String documentType = row.getProperties().get("DocumentType").getStringValue();
							Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							String dateOfUploading = formatFnDate(dateOfUploading1);
							String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							String deptName = row.getProperties().get("Dept_Name").getStringValue();
							String deptCode = row.getProperties().get("Dept_Code").getStringValue();
							String branchName = row.getProperties().get("BranchName").getStringValue();
							String branchCode = row.getProperties().get("BranchCode").getStringValue();
							String uploadedBy = row.getProperties().get("Creator").getStringValue();
							Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							Date date1 = row.getProperties().get("DATE").getDateTimeValue();
							String date = formatFnDate(date1);
							String names = row.getProperties().get("NAMES").getStringValue();
							System.out.println("---------- Adding the Data into JSONObject ----------");
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("SI. No", serialNumber);
							jsonObject.put("Class Name", className);
							jsonObject.put("Document Title", documentTitle);
							jsonObject.put("Date of Uploading", dateOfUploading);
							jsonObject.put("Department Name", deptName);
							jsonObject.put("Dept Code", deptCode);
							jsonObject.put("Branch Name", branchName);
							jsonObject.put("Branch Code", branchCode);
							jsonObject.put("HO Department Code", hODepartmentCode);
							jsonObject.put("Uploaded By", uploadedBy);
							jsonObject.put("No Of Pages Uploaded", pageCount);
							jsonObject.put("Document Type", documentType);
							jsonObject.put("Date", date);
							jsonObject.put("NAME", names);
							jsonArray.add(jsonObject);
							serialNumber++;
							System.out.println("---------- Data was successfully added into the JSONObject ----------");

						}

					} else if (repositoryId.equalsIgnoreCase("MDSECIT")) {
						Id docId = row.getProperties().get("Id").getIdValue();
						Document document = Factory.Document.fetchInstance(os, docId, null);
						String className = document.getClassName();
						System.out.println("className: " + className);
						if (CLASSNAMESTOINGORE.contains(className)) {
							// Skip processing for this class
							continue;
						} else {
							String documentTitle = row.getProperties().get("DocumentTitle").getStringValue();
							String documentType = row.getProperties().get("DocumentType").getStringValue();
							Date dateOfUploading1 = row.getProperties().get("DateCreated").getDateTimeValue();
							String dateOfUploading = formatFnDate(dateOfUploading1);
							String hODepartmentCode = row.getProperties().get("HODepartmentCode").getStringValue();
							String deptName = row.getProperties().get("Dept_Name").getStringValue();
							String deptCode = row.getProperties().get("Dept_Code").getStringValue();
							String branchName = row.getProperties().get("BranchName").getStringValue();
							String branchCode = row.getProperties().get("BranchCode").getStringValue();
							String uploadedBy = row.getProperties().get("Creator").getStringValue();
							Integer pageCount = row.getProperties().get("FN_PageCount").getInteger32Value();
							String diaryNumber = row.getProperties().get("DiaryNumber").getStringValue();
							String subject = row.getProperties().get("Subject").getStringValue();
							String department = row.getProperties().get("Department").getStringValue();
							String receivedNoteType = row.getProperties().get("ReceivedNoteType").getStringValue();
							Date noteDate1 = row.getProperties().get("NoteDate").getDateTimeValue();
							String noteDate = formatFnDate(noteDate1);
							Date mOMDate1 = row.getProperties().get("MOMDate").getDateTimeValue();
							String mOMDate = formatFnDate(mOMDate1);
							Date dateIn1 = row.getProperties().get("DateIn").getDateTimeValue();
							String dateIn = formatFnDate(dateIn1);
							Date dateOut1 = row.getProperties().get("DateOut").getDateTimeValue();
							String dateOut = formatFnDate(dateOut1);
							String sentNoteType = row.getProperties().get("SentNoteType").getStringValue();
							String approvingAuthority = row.getProperties().get("ApprovingAuthority").getStringValue();
							String remarks = row.getProperties().get("Remarks").getStringValue();
							String noteType = row.getProperties().get("Notetype").getStringValue();
							Date date1 = row.getProperties().get("Date").getDateTimeValue();
							String date = formatFnDate(date1);
							String receivedLetterType = row.getProperties().get("ReceivedLetterType").getStringValue();
							String sentLetterType = row.getProperties().get("SentLetterType").getStringValue();

							System.out.println("---------- Adding the Data into JSONObject ----------");
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("Document Title", documentTitle);
							jsonObject.put("Date of Uploading", dateOfUploading);
							jsonObject.put("Department Name", deptName);
							jsonObject.put("Diary Number", diaryNumber);
							jsonObject.put("Dept Code", deptCode);
							jsonObject.put("Branch Name", branchName);
							jsonObject.put("Branch Code", branchCode);
							jsonObject.put("HO Department Code", hODepartmentCode);
							jsonObject.put("Uploaded By", uploadedBy);
							jsonObject.put("Date", date);
							jsonObject.put("Received Letter Type", receivedLetterType);
							jsonObject.put("Sent Letter Type", sentLetterType);
							jsonObject.put("SI. No", serialNumber);
							jsonObject.put("Class Name", className);
							jsonObject.put("ReceivedNoteType", receivedNoteType);
							jsonObject.put("MOM Date", mOMDate);
							jsonObject.put("Sent Note Type", sentNoteType);
							jsonObject.put("ApprovingAuthority", approvingAuthority);
							jsonObject.put("Remarks", remarks);
							jsonObject.put("Note type", noteType);
							jsonObject.put("Note Date", noteDate);
							jsonObject.put("DateOut", dateOut);
							jsonObject.put("Department", department);
							jsonObject.put("No Of Pages Uploaded", pageCount);
							jsonObject.put("Document Type", documentType);
							jsonObject.put("DateIn", dateIn);
							jsonArray.add(jsonObject);
							serialNumber++;
							System.out.println("---------- Data was successfully added into the JSONObject ----------");

						}

					}
			  }
			  }
		  } catch (Exception e) {
			  e.printStackTrace();
			  System.out.println("Exception in: "+e.getMessage());
		  }

		  return jsonArray;
	  }
		  private String formatDateList(DateTimeList stringList){
			  StringBuilder concatenatedString = new StringBuilder();
			  StringBuilder separatedString = new StringBuilder();
			  if(stringList!=null || !stringList.isEmpty()){
				  Iterator sList = stringList.iterator();
				   
				    while (sList.hasNext()) {
						String object = (String) sList.next();
					    concatenatedString.append(object);
					    if (separatedString.length() > 0) {
					        separatedString.append(", ");
					    }
					    separatedString.append(object);
					}
				    if(separatedString.length()>0){
				    	 System.out.println("Separated String: " + separatedString.toString());
				    	 return separatedString.toString();
				    }
			  }else{
				  return null;
			  }
			return null;
			    
			    
		  }
	  private String formatIntegerList(Integer32List stringList){
		  StringBuilder concatenatedString = new StringBuilder();
		  StringBuilder separatedString = new StringBuilder();
		  if(stringList!=null || !stringList.isEmpty()){
			  Iterator sList = stringList.iterator();
			   
			    while (sList.hasNext()) {
					String object = (String) sList.next();
				    concatenatedString.append(object);
				    if (separatedString.length() > 0) {
				        separatedString.append(", ");
				    }
				    separatedString.append(object);
				}
			    if(separatedString.length()>0){
			    	 System.out.println("Separated String: " + separatedString.toString());
			    	 return separatedString.toString();
			    }
		  }else{
			  return null;
		  }
		return null;
		    
		    
	  }
	  private String formatFloatList(Float64List stringList){
		  StringBuilder concatenatedString = new StringBuilder();
		  StringBuilder separatedString = new StringBuilder();
		  if(stringList!=null || !stringList.isEmpty()){
			  Iterator sList = stringList.iterator();
			   
			    while (sList.hasNext()) {
					String object = (String) sList.next();
				    concatenatedString.append(object);
				    if (separatedString.length() > 0) {
				        separatedString.append(", ");
				    }
				    separatedString.append(object);
				}
			    if(separatedString.length()>0){
			    	 System.out.println("Separated String: " + separatedString.toString());
			    	 return separatedString.toString();
			    }
		  }else{
			  return null;
		  }
		return null;
		    
		    
	  }
	  private String formatStringList(StringList stringList){
		  StringBuilder concatenatedString = new StringBuilder();
		  StringBuilder separatedString = new StringBuilder();
		  if(stringList!=null || !stringList.isEmpty()){
			  Iterator sList = stringList.iterator();
			   
			    while (sList.hasNext()) {
					String object = (String) sList.next();
				    concatenatedString.append(object);
				    if (separatedString.length() > 0) {
				        separatedString.append(", ");
				    }
				    separatedString.append(object);
				}
			    if(separatedString.length()>0){
			    	 System.out.println("Separated String: " + separatedString.toString());
			    	 return separatedString.toString();
			    }
		  }else{
			  return null;
		  }
		return null;
		    
		    
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
	  private String formatFnDate(Date inputdate) {
		    System.out.println("UserReportUtil.formatDate():" + inputdate);
		    if (inputdate != null) {
		        try {
		            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		            TimeZone inputTimeZone = TimeZone.getTimeZone("IST");
		            outputDateFormat.setTimeZone(inputTimeZone);
		            String outputDateStr = outputDateFormat.format(inputdate);
		            return outputDateStr;
		        } catch (Exception e) {
		            return inputdate.toString();
		        }
		    }
		    return null;
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
