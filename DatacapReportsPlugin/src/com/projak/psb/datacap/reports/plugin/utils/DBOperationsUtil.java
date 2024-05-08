package com.projak.psb.datacap.reports.plugin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ibm.json.java.JSONObject;

public class DBOperationsUtil {
    private static Connection dbConnection = null;

    public Connection getDBConnection() {
    	System.out.println("<<< ::: Trying to Connect with the Database ::: >>>");
    	String url = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_URL");
    	String username =PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_USERNAME");
    	String password = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_PASSWORD");
        try {
            if (dbConnection == null || dbConnection.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                dbConnection = DriverManager.getConnection(url, username, password);
                String dbSchemaName = dbConnection.getSchema();
                System.out.println("::: Connected to the " + dbSchemaName + " Oracle database! Successfully :::");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC driver not found!"+e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to the Oracle database failed! :: "+e.getMessage());
            e.printStackTrace();
        }
        return dbConnection;
    }
    
    public static JSONObject retrieveDataFromTable(Connection connection,String date, String date4,String repoID,String reportName) {
       System.out.println("<<< ::: In the DBOperationsUtil Class retrieveDataFromTable Method ::: >>>");
       System.out.println("Executing the ReportName: " + reportName + " & forthe Repository ID: " + repoID);
    	PreparedStatement preparedStatement = null;
        ResultSet resultSetData = null;
        JSONObject jsonObject = null;
        try {
        	 System.out.println("Start Date As: " + date + " & End Date As: " + date4);
        	 String query = "";
        	 System.out.println("Initialized the empty Query...");
        	 
        	 if(reportName.equalsIgnoreCase("LoginSessionReport") || reportName.equalsIgnoreCase("AuditLogReport")) {
        		 if(reportName.equalsIgnoreCase("LoginSessionReport")){
        			 System.out.println("Report Name ===> " + reportName);
            		 preparedStatement = connection.prepareStatement(PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_LOGIN_SELECTQUERY"));
            		 System.out.println("Processing the " + reportName + "...");
            	 }else if(reportName.equalsIgnoreCase("AuditLogReport")){
            		 System.out.println("Report Name ===> " + reportName);
            		 preparedStatement = connection.prepareStatement(PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_AUDIT_SELECTQUERY"));
            		 System.out.println("Processing the " + reportName + "...");
            	 }
        		 preparedStatement.setString(1, date);	
                 preparedStatement.setString(2, date4);
                 preparedStatement.setString(3, repoID);
                 resultSetData = preparedStatement.executeQuery();
                System.out.println("<<< ::: Execute Query ::: >>>");
                System.out.println("Boolean Condition::  " + resultSetData.isBeforeFirst());
        	 }else {
        		 if(reportName.equalsIgnoreCase("DailyReport")) {
        			 System.out.println("Report Name ===> " + reportName);
        			 System.out.println("Checking the the Daily Report Repository Name...");
        			 query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_DAILY_SELECTQUERY_" + repoID);
        			 
            	 }else if(reportName.equalsIgnoreCase("SummaryMonthlyReport")){
            		 System.out.println("Report Name ===> " + reportName);
        			 System.out.println("Checking the the Summary/ Monthly Report Repository Name...");
        			 query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_MONTHLY_SELECTQUERY_" + repoID);           		 
            	 }else if(reportName.equalsIgnoreCase("UserReport")){
            		 System.out.println("Report Name ===> " + reportName);
        			 System.out.println("Checking the the Summary/ Monthly Report Repository Name...");
        			 query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_USER_SELECTQUERY_" + repoID);            		  
            	 }else if(reportName.equalsIgnoreCase("DepartmentWiseReport")){
            		 System.out.println("Report Name ===> " + reportName);
        			 System.out.println("Checking the the Summary/ Monthly Report Repository Name...");
        			 query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_DEPTWISE_SELECTQUERY_" + repoID);
            	 }else if(reportName.equalsIgnoreCase("DepartmentWiseMetadataReport")){
            		 System.out.println("Report Name ===> " + reportName);
        			 System.out.println("Checking the the Summary/ Monthly Report Repository Name...");
        			 query = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_DEPTWISEMETADATA_SELECTQUERY_" + repoID);
            	 }else if(reportName.equalsIgnoreCase("DMS Daily Report")){//datacap report
            		 System.out.println("Report Name ===> " + reportName);
        			 System.out.println("Checking the the DMS Daily Report Repository Name...");
        			 query = PSBPropertyReader.getResourceBundle().getString("DATACAP_DMS_REPORT_DB_DAILYREPORT_SELECTQUERY_" + repoID);
            	 }else if(reportName.equalsIgnoreCase("DMS Monthly Report")){//datacap report
            		 System.out.println("Report Name ===> " + reportName);
        			 System.out.println("Checking the the DMS Daily Report Repository Name...");
        			 query = PSBPropertyReader.getResourceBundle().getString("DATACAP_DMS_REPORT_DB_MONTHLYREPORT_SELECTQUERY_" + repoID);
            	 }
        		 if (!query.isEmpty()) {
                     preparedStatement = connection.prepareStatement(query);
                     preparedStatement.setString(1, date);
                     preparedStatement.setString(2, date4);
                     resultSetData = preparedStatement.executeQuery();
                     System.out.println("<<< ::: I'm Loaded with the Execute Query ::: >>>");
                     System.out.println("Boolean Condition" + resultSetData.isBeforeFirst());
                 } else {
                     System.out.println("Oops! No query selected for Repository ID:: " + repoID);
                 }
        	 }
            if(!resultSetData.isBeforeFirst()){
            	System.out.println("Oops! I'm having the Empty Result");
            	jsonObject = new JSONObject();
            	jsonObject.put("key", "empty");
            }else{
            	System.out.println("Hmm... I'm with the Full of Data");
            	jsonObject = new JSONObject();
                jsonObject.put("key", "full");
            }
        } catch (SQLException e) {
        	System.out.println("SQL Exception:: "+e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSetData != null) {
                	resultSetData.close();
                }
                if (preparedStatement != null) {
                	preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
		return jsonObject ;
    }
    
    public int insertDataToLoginTable(Connection dbConnection, String userName, String branchName, String solId, String repositoryName, String ipServer, String event, String action) {
        System.out.println("DBOperationsUtil.insertDataToTable()");
        System.out.println(event);
        int rowsAffected = 0;

        Timestamp logonTime = null;
        Timestamp logoffTime = null;

        if (action.equalsIgnoreCase("login")) {
            logonTime = new Timestamp(System.currentTimeMillis());
        } else if (action.equalsIgnoreCase("logoff")) {
            logoffTime = new Timestamp(System.currentTimeMillis());
        }

        String selectQuery = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_LOGIN_ORDERQUERY");
        String updateQuery = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_LOGIN_UPDATEQUERY");
        String insertQuery = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_LOGIN_INSERTQUERY");
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        PreparedStatement insertStatement = null;
        ResultSet resultSet = null;

        try {
            selectStatement = dbConnection.prepareStatement(selectQuery);
            updateStatement = dbConnection.prepareStatement(updateQuery);
            insertStatement = dbConnection.prepareStatement(insertQuery);

            selectStatement.setString(1, repositoryName);
            selectStatement.setString(2, userName);

            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                Timestamp lastLogonTime = resultSet.getTimestamp("LOGONDATE");
                Timestamp lastLogoutTime = resultSet.getTimestamp("LOGOUTDATE");

                if (action.equalsIgnoreCase("login")) {
                	System.out.println("@@@login@@@");
                    if (lastLogonTime != null && lastLogoutTime == null) {
                        System.out.println("User already logged in. Nothing to do.");
                    } else {
                        logoffTime = null;
                        logonTime = new Timestamp(System.currentTimeMillis());
                        insertStatement.setString(1, repositoryName);
                        insertStatement.setString(2, userName);
                        insertStatement.setString(3, branchName);
                        insertStatement.setString(4, solId);
                        insertStatement.setTimestamp(5, logonTime);
                        insertStatement.setTimestamp(6, logoffTime);
                        insertStatement.setString(7, ipServer);
                        insertStatement.setString(8, event);
                        rowsAffected = insertStatement.executeUpdate();
                        System.out.println("LoginRowsUpdated"+rowsAffected);
                    }
                } else if (action.equalsIgnoreCase("logoff")) {
                    if (lastLogonTime == null || lastLogoutTime != null) {
                        System.out.println("User already logged out or never logged in today. Nothing to do.");
                    } else {
                        logoffTime = new Timestamp(System.currentTimeMillis());
                        updateStatement.setTimestamp(1, logoffTime);
                        updateStatement.setString(2, event);
                        updateStatement.setString(3, repositoryName);
                        updateStatement.setString(4, userName);
                        rowsAffected = updateStatement.executeUpdate();
                        System.out.println("LogoutRowsUpdated: "+rowsAffected);
                    }
                }
            } else {
                if (action.equalsIgnoreCase("login")) {
                	System.out.println("New user or no previous login/logout record found for the given repositoryName and userName");
                    logonTime = new Timestamp(System.currentTimeMillis());
                    insertStatement.setString(1, repositoryName);
                    insertStatement.setString(2, userName);
                    insertStatement.setString(3, branchName);
                    insertStatement.setString(4, solId);
                    insertStatement.setTimestamp(5, logonTime);
                    insertStatement.setTimestamp(6, logoffTime);
                    insertStatement.setString(7, ipServer);
                    insertStatement.setString(8, event);
                    rowsAffected = insertStatement.executeUpdate();
                    System.out.println("FirstLoginRecodeInserted: "+rowsAffected);
                } else if (action.equalsIgnoreCase("logoff")) {
                    System.out.println("User has never logged in today. Nothing to do.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the resultSet: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the selectStatement: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (updateStatement != null) {
                try {
                    updateStatement.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the updateStatement: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the insertStatement: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the database connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return rowsAffected;
    }
    
    public int insertDataToAuditTable(Connection dbConnection, String userName, String branchName, String solId,String repositoryName, String ipServer, String eventStatus, String eventType,String fileName) {
        System.out.println("DBOperationsUtil.insertDataToAuditTable()");
        int rowsAffected = 0;

        Timestamp auditTime = null;

        auditTime = new Timestamp(System.currentTimeMillis());
        
        String insertQuery = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_AUDIT_INSERTQUERY");

        PreparedStatement insertStatement = null;

        try {
            
        	insertStatement = dbConnection.prepareStatement(insertQuery);

            insertStatement.setString(1, repositoryName);
            
            insertStatement.setString(2, userName);
            
            insertStatement.setString(3, branchName);
            
            insertStatement.setString(4, solId);
            
            insertStatement.setString(5, eventType);
            
            insertStatement.setTimestamp(6, auditTime);
            
            insertStatement.setString(7, fileName);
            
            insertStatement.setString(8, eventStatus);
            
            insertStatement.setString(8, ipServer);

            rowsAffected = insertStatement.executeUpdate();
            
            System.out.println("RowsInsertedToAuditTable: " + rowsAffected);
        
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the insertStatement: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    System.out.println("Error closing the database connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return rowsAffected;
    }
    
public void getTableColumnNames(Connection dbConnection) {
	System.out.println("<<< ::: In the DBOperations Class getTableColumnNames Method ::: >>>");
	String selectQuery = "SELECT * FROM DMSDATA.loginsession";
    PreparedStatement stmt;
	try {
		stmt = dbConnection.prepareStatement(selectQuery);
		ResultSet resultSet = stmt.executeQuery();
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		    
		    for (int i = 1; i <= columnCount; i++) {
		        String columnName = metaData.getColumnName(i);
		        System.out.println("Column Name:: " + columnName);
		    }
	} catch (SQLException e) {
		System.out.println("SQL Exception:: "+e.getMessage());
		e.printStackTrace();
	}
}
    public static void main(String[] args) {
    }
}
