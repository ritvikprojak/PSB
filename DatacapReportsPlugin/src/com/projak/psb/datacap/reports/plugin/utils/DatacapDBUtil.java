package com.projak.psb.datacap.reports.plugin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.ibm.json.java.JSONObject;

public class DatacapDBUtil {
	private static Connection dcdbConnection = null;
	
    public Connection getDatacapDBConnection(String repoId) {
    	System.out.println("<<< ::: Trying to Connect with the Database ::: >>>");
    	String url = PSBPropertyReader.getResourceBundle().getString("DATACAP_REPORT_DB_URL");
    	String username =PSBPropertyReader.getResourceBundle().getString(repoId+"_DATACAP_REPORT_DB_USERNAME");
    	String password = PSBPropertyReader.getResourceBundle().getString(repoId+"_DATACAP_REPORT_DB_PASSWORD");
        try {
            if (dcdbConnection == null || dcdbConnection.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                dcdbConnection = DriverManager.getConnection(url, username, password);
                String dbSchemaName = dcdbConnection.getSchema();
                System.out.println("::: Connected to the " + dbSchemaName + " Oracle database! Successfully :::");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC driver not found!"+e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to the Oracle database failed! :: "+e.getMessage());
            e.printStackTrace();
        }
        return dcdbConnection;
    }
    
    public static JSONObject retrieveDataFromDatacapTable(Connection connection,String date, String date4,String repoID,String reportName) {
        System.out.println("<<< ::: In the DatacapDBUtil Class retrieveDataFromDatacapTable Method ::: >>>");
        System.out.println("Executing the ReportName: " + reportName + " & forthe Repository ID: " + repoID);
     	PreparedStatement preparedStatement = null;
         ResultSet resultSetData = null;
         JSONObject jsonObject = null;
         String formattedStartDate="";
         String formattedEndDate="";
         try {
        	 try {
                 System.out.println("Start Date As:::: " + date + " & End Date As: " + date4);
                 System.out.println("Converting to Datacap date format");

                 SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                 SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);

                 try {
                     // Parse the input date strings
                     Date startDate = inputFormat.parse(date);
                     Date endDate = inputFormat.parse(date4);
                     // Format the dates in the desired output format
                     formattedStartDate = outputFormat.format(startDate).toUpperCase();
                     formattedEndDate = outputFormat.format(endDate).toUpperCase();

                     // Print the formatted dates
                     System.out.println("Start Date As: " + formattedStartDate);
                     System.out.println("End Date As: " + formattedEndDate);
                 } catch (ParseException e) {
                	 System.out.println("ParseException: "+e.getMessage());
                     e.printStackTrace();
                 }

             } catch (Exception e) {
            	 System.out.println("Exception: "+e.getMessage());
                 e.printStackTrace();
             }
             
         	 String query = "";
         	 System.out.println("Datacap Date: "+formattedStartDate+"\t"+formattedEndDate);
         	 System.out.println("Initialized the empty Query...");
         		 if(reportName.equalsIgnoreCase("Datacap Daily Report")) {
         			 System.out.println("Report Name ===> " + reportName);
         			 System.out.println("Checking the the Datacap Daily Report Repository Name...");
         			 query = PSBPropertyReader.getResourceBundle().getString(repoID+"_DATACAP_DAILYREPORT_SELECTQUERY");
         			preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, formattedStartDate);
                    preparedStatement.setString(2, formattedEndDate);
         			 
             	 }else if(reportName.equalsIgnoreCase("Datacap Summary Status Report")) {
         			 System.out.println("Report Name ===> " + reportName);
         			 System.out.println("Checking the the Datacap Summary Status Report Repository Name...");
         			 query = PSBPropertyReader.getResourceBundle().getString(repoID+"_DATACAP_SUMMARYREPORT_SELECTQUERY");
         			 preparedStatement = connection.prepareStatement(query);
         			 preparedStatement.setString(1, formattedStartDate);
         			preparedStatement.setString(2, formattedEndDate);
             	 }else if(reportName.equalsIgnoreCase("Datacap Monthly Report")) {
         			 System.out.println("Report Name ===> " + reportName);
         			 System.out.println("Checking the the Datacap Monthly Report Repository Name");
         			 query = PSBPropertyReader.getResourceBundle().getString(repoID+"_DATACAP_MONTHLYREPORT_SELECTQUERY");
         			 System.out.println("Query: "+query);
         			 preparedStatement = connection.prepareStatement(query);
         			preparedStatement.setString(1, formattedStartDate);
         			preparedStatement.setString(2, formattedEndDate);
             	 }
         		 
                      resultSetData = preparedStatement.executeQuery();
                      System.out.println("<<< ::: I'm Loaded with the Execute Query ::: >>>");
                      System.out.println("Boolean Condition" + resultSetData.isBeforeFirst());
         	 
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
}
