package com.projak.psb.event.action.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DBUtil {
    private static Connection dbConnection = null;

    public Connection getDBConnection() {
    	System.out.println("DBUtil.getDBConnection()");
    	//String url = "jdbc:oracle:thin:@172.24.11.75:1521:DMSPSB";
    	//String username ="icndev";
    	//String password = "oracle123";
//    	String url = "jdbc:oracle:thin:@dcsvrvdmsdb005.psb.org.in:1521:GCDPSB";
//    	String username ="ECM";
//    	String password = "oracle123";
    	String url = "jdbc:oracle:thin:@dcdmscluster04.psb.org.in:2289:DMPPROD";
    	String username ="GCD";
    	String password = "Gcd$123";
    	System.out.println("url: "+url);
        try {
            if (dbConnection == null || dbConnection.isClosed()) {
            	System.out.println("load drivers");
                Class.forName("oracle.jdbc.driver.OracleDriver");
                System.out.println("driver loaded");
                dbConnection = DriverManager.getConnection(url, username, password);
                System.out.println("Connected to the Oracle database!");
                
                //getTableColumnNames(dbConnection);
               // insertDataToTable(dbConnection);
                //retrieveDataFromTable(dbConnection);
                
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC driver not found!"+e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection to the Oracle database failed!"+e.getMessage());
            e.printStackTrace();
        }
        return dbConnection;
    }
    public int insertDataToAuditTable(Connection dbConnection, String userName,String branchName, String solId, String repositoryName, String ipServer, String eventStatus, String eventType,String fileName) {
        System.out.println("DBOperationsUtil.insertDataToAuditTable()");
        int rowsAffected = 0;
        
       // String branchCode = "XYZ1259";
        
        if(branchName == null || branchName.isEmpty()){
        	
        	branchName = "";
        }
        
        Timestamp auditTime = null;

        auditTime = new Timestamp(System.currentTimeMillis());
        
        String insertQuery = "INSERT INTO DMSDATA.auditsession (REPOSITORY_ID, USER_NAME, BRANCH_NAME, BRANCH_CODE, EVENT_TYPE, RECORD_DATETIME, FILENAME_DIRECTORY,STATUS_OF_THE_ACTIVITY, IP_ADDRESS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement insertStatement = null;

        try {
        	//REPOSITORYID, USER_NAME, BRANCH_CODE, EVENT_TYPE, 
        	//RECORD_DATETIME, FILENAME_DIRECTORY,STATUS_OF_THE_ACTIVITY, IP_ADDRESS
            insertStatement = dbConnection.prepareStatement(insertQuery);

            insertStatement.setString(1, repositoryName);
            
            insertStatement.setString(2, userName);
            
            insertStatement.setString(3, branchName);
            
            insertStatement.setString(4, solId);
            
            insertStatement.setString(5, eventType); // Use action as EVENT_TYPE
            
            insertStatement.setTimestamp(6, auditTime); // Use action as EVENT_TYPE
            
            insertStatement.setString(7, fileName);
            
            insertStatement.setString(8, eventStatus);
            
            insertStatement.setString(9, ipServer);

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

    public static void main(String[] args) {
       /* // Database credentials
        String url = "jdbc:oracle:thin:@172.24.11.75:1521:DMSPSB";
        String username = "icn";
        String password = "oracle123";

        DBUtil db = new DBUtil();
        Connection connection = db.getDBConnection(url, username, password);

        // Perform database operations here

        // Close the connection
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from the Oracle database!");
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while closing the database connection!");
            e.printStackTrace();
        }*/
    }
}

