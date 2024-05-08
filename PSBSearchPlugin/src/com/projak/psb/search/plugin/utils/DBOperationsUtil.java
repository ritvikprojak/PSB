package com.projak.psb.search.plugin.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ibm.json.java.JSONObject;

public class DBOperationsUtil {
    private static Connection dbConnection = null;

    public Connection getDBConnection() {
    	String url = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_URL");
    	String username =PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_USERNAME");
    	String password = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_PASSWORD");
        try {
            if (dbConnection == null || dbConnection.isClosed()) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
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
    public static JSONObject retrieveDataFromTable(Connection connection,String date, String date4,String repoID,String reportName) {
       System.out.println("DBOperationsUtil.retrieveDataFromTable()");
       System.out.println("ReportName: "+reportName+"\t"+repoID);
    	PreparedStatement preparedStatement = null;
        ResultSet resultSetData = null;
        JSONObject jsonObject = null;
        try {
            // Create a statement
        	 System.out.println("Execute"+date+"\t"+date4);
        	 if(reportName.equalsIgnoreCase("LoginSessionReport")){
        		 preparedStatement = connection.prepareStatement("SELECT * FROM DMSDATA.loginsession WHERE LOGONDATE >= TO_DATE(?, 'yyyy-MM-dd HH24:mi:ss') AND LOGONDATE <=  TO_DATE(?,'yyyy-MM-dd HH24:mi:ss') AND REPOSITORYID = ?");
        	 }else if(reportName.equalsIgnoreCase("AuditLogReport")){
        		 preparedStatement = connection.prepareStatement("SELECT * FROM DMSDATA.auditsession WHERE RECORD_DATETIME >= TO_DATE(?, 'yyyy-MM-dd HH24:mi:ss') AND RECORD_DATETIME <=  TO_DATE(?,'yyyy-MM-dd HH24:mi:ss') AND REPOSITORY_ID = ?");
        	 }
             preparedStatement.setString(1, date);	
             preparedStatement.setString(2, date4);
             preparedStatement.setString(3, repoID);
             resultSetData = preparedStatement.executeQuery();
            System.out.println("execute query");
            System.out.println("boolean condition" + resultSetData.isBeforeFirst());
           /* while (resultSetData.next()) {
            	String repoName = resultSetData.getString("REPOSITORYID");
                String userName = resultSetData.getString("USER_NAME");
                String branchCode = resultSetData.getString("BRANCH_CODE");
                Timestamp loginTime = resultSetData.getTimestamp("LOGONDATE");
                Timestamp logoutTime = resultSetData.getTimestamp("LOGOUTDATE");
                String userItAddress = resultSetData.getString("USER_IT_ADDRESS");
                String event = resultSetData.getString("EVENT");
                
                // Process the retrieved data as per your requirements
                System.out.println("Repository Name: " + repoName);
                System.out.println("User Name: " + userName);
                System.out.println("Branch Code: " + branchCode);
                System.out.println("Login Time: " + loginTime);
                System.out.println("Logout Time: " + logoutTime);
                System.out.println("User IT Address: " + userItAddress);
                System.out.println("Event: " + event);
                System.out.println("----------------------------------");
            }*/
            // Process the result set
            if(!resultSetData.isBeforeFirst()){
            	System.out.println("empty result");
            	jsonObject = new JSONObject();
            	jsonObject.put("key", "empty");
            	
            }else{
            	System.out.println("it is full");
            	jsonObject = new JSONObject();
                jsonObject.put("key", "full");
            }
        } catch (SQLException e) {
        	System.out.println("SQLException: "+e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the resources in reverse order
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
 /*   public int insertDataToTable(Connection dbConnection, String userName, String repositoryName, String ipServer,
            String event) {
    	System.out.println("DBOperationsUtil.insertDataToTable()");
    	System.out.println("OracleDBConnection.insertDataToTable()");
        int rowsAffected = 0;

        String branchCode = "ABC123";
        Timestamp logonTime = new Timestamp(System.currentTimeMillis());
        Timestamp logoffTime = null; // Initialize logoffTime with null

        String selectQuery = "SELECT LOGONDATE, LOGOUTDATE FROM DMSDATA.loginsession WHERE REPOSITORYID = ? AND USER_NAME = ? ORDER BY LOGONDATE DESC";
        String updateQuery = "UPDATE DMSDATA.loginsession SET LOGOUTDATE = ? WHERE REPOSITORYID = ? AND USER_NAME = ? AND LOGOUTDATE IS NULL";
        String insertQuery = "INSERT INTO DMSDATA.loginsession (REPOSITORYID, USER_NAME, BRANCH_CODE, LOGONDATE, LOGOUTDATE, USER_IT_ADDRESS, EVENT) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        PreparedStatement insertStatement = null;

        try {
            selectStatement = dbConnection.prepareStatement(selectQuery);
            updateStatement = dbConnection.prepareStatement(updateQuery);
            insertStatement = dbConnection.prepareStatement(insertQuery);

            selectStatement.setString(1, repositoryName);
            selectStatement.setString(2, userName);

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                Timestamp lastLogonTime = resultSet.getTimestamp("LOGONDATE");
                Timestamp lastLogoutTime = resultSet.getTimestamp("LOGOUTDATE");

                if (lastLogonTime != null && lastLogoutTime == null) {
                    // If LOGOUTDATE is not present, update it with the current timestamp on the same day
                    logoffTime = new Timestamp(System.currentTimeMillis());
                    rowsAffected = updateLogoutDate(updateStatement, logoffTime, repositoryName, userName);
                }
            }

            if (rowsAffected == 0) {
                // If no row was updated, it means no matching row with LOGOUTDATE=NULL was found
                // So, insert a new row with the current timestamp as LOGONDATE and LOGOUTDATE
                logoffTime = logonTime;
                rowsAffected = insertLoginSession(insertStatement, repositoryName, userName, branchCode, logonTime, logoffTime, ipServer, event);
            }

        } catch (SQLException e) {
        	System.out.println("SQLException: {}"+ e.getMessage()+ e);
        } finally {
            // Close the prepared statements in the finally block
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (SQLException e) {
                	System.out.println("Error closing the selectStatement: {}"+ e.getMessage());
                }
            }

            if (updateStatement != null) {
                try {
                    updateStatement.close();
                } catch (SQLException e) {
                	System.out.println("Error closing the updateStatement: {}"+ e.getMessage());
                }
            }

            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException e) {
                	System.out.println("Error closing the insertStatement: {}"+ e.getMessage());
                }
            }
        }

        return rowsAffected;
    }*/
    public int insertDataToLoginTable(Connection dbConnection, String userName,String solId, String repositoryName, String ipServer, String event, String action) {
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

        String selectQuery = "SELECT LOGONDATE, LOGOUTDATE FROM DMSDATA.loginsession WHERE REPOSITORYID = ? AND USER_NAME = ? ORDER BY LOGONDATE DESC";
        String updateQuery = "UPDATE DMSDATA.loginsession SET LOGOUTDATE = ?, EVENT = ? WHERE REPOSITORYID = ? AND USER_NAME = ? AND LOGOUTDATE IS NULL";
        String insertQuery = "INSERT INTO DMSDATA.loginsession (REPOSITORYID, USER_NAME, BRANCH_CODE, LOGONDATE, LOGOUTDATE, USER_IT_ADDRESS, EVENT) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
                // Existing user, check the login/logout status
                Timestamp lastLogonTime = resultSet.getTimestamp("LOGONDATE");
                Timestamp lastLogoutTime = resultSet.getTimestamp("LOGOUTDATE");

                if (action.equalsIgnoreCase("login")) {
                	System.out.println("@@@login@@@");
                    if (lastLogonTime != null && lastLogoutTime == null) {
                        // User already logged in today, do nothing (already logged in)
                        System.out.println("User already logged in. Nothing to do.");
                    } else {
                        // User logged out earlier or never logged in today
                        // Insert a new row with the current timestamp as logonTime
                        logoffTime = null; // Set logoffTime as null for the new row
                        logonTime = new Timestamp(System.currentTimeMillis());
                        insertStatement.setString(1, repositoryName);
                        insertStatement.setString(2, userName);
                        insertStatement.setString(3, solId);
                        insertStatement.setTimestamp(4, logonTime);
                        insertStatement.setTimestamp(5, logoffTime);
                        insertStatement.setString(6, ipServer);
                        insertStatement.setString(7, event);
                        rowsAffected = insertStatement.executeUpdate();
                        System.out.println("LoginRowsUpdated"+rowsAffected);
                    }
                } else if (action.equalsIgnoreCase("logoff")) {
                    if (lastLogonTime == null || lastLogoutTime != null) {
                        // User already logged out or never logged in today, do nothing
                        System.out.println("User already logged out or never logged in today. Nothing to do.");
                    } else {
                        // User is logged in, update the existing row's LOGOUTDATE with the current timestamp as logoffTime
                        logoffTime = new Timestamp(System.currentTimeMillis());
                        updateStatement.setTimestamp(1, logoffTime);
                        updateStatement.setString(2, event); // Set the EVENT value
                        updateStatement.setString(3, repositoryName);
                        updateStatement.setString(4, userName);
                        rowsAffected = updateStatement.executeUpdate();
                        System.out.println("LogoutRowsUpdated: "+rowsAffected);
                    }
                }
            } else {
                // New user or no previous login/logout record found for the given repositoryName and userName
                if (action.equalsIgnoreCase("login")) {
                	System.out.println("New user or no previous login/logout record found for the given repositoryName and userName");
                    // Insert a new row with the current timestamp as logonTime
                    logoffTime = null; // Set logoffTime as null for the new row
                    logonTime = new Timestamp(System.currentTimeMillis());
                    insertStatement.setString(1, repositoryName);
                    insertStatement.setString(2, userName);
                    insertStatement.setString(3, solId);
                    insertStatement.setTimestamp(4, logonTime);
                    insertStatement.setTimestamp(5, logoffTime);
                    insertStatement.setString(6, ipServer);
                    insertStatement.setString(7, event);
                    rowsAffected = insertStatement.executeUpdate();
                    System.out.println("FirstLoginRecodeInserted: "+rowsAffected);
                } else if (action.equalsIgnoreCase("logoff")) {
                    // Handle this case based on your application's requirements.
                    // For example, you may log an error or perform any other desired action.
                    // Since there is no previous logoff record, there is no need to update anything.
                    System.out.println("User has never logged in today. Nothing to do.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close the result set, prepared statements, and the database connection in the finally block
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
//connection,userName,repoName,ipAddress,eventStatus,eventType,fileName);
    public int insertDataToAuditTable(Connection dbConnection, String userName, String solId,String repositoryName, String ipServer, String eventStatus, String eventType,String fileName) {
        System.out.println("DBOperationsUtil.insertDataToAuditTable()");
        int rowsAffected = 0;

        Timestamp auditTime = null;

        auditTime = new Timestamp(System.currentTimeMillis());
        
        String insertQuery = "INSERT INTO DMSDATA.auditsession (REPOSITORY_ID, USER_NAME, BRANCH_CODE, EVENT_TYPE, RECORD_DATETIME, FILENAME_DIRECTORY,STATUS_OF_THE_ACTIVITY, IP_ADDRESS) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement insertStatement = null;

        try {
        	//REPOSITORYID, USER_NAME, BRANCH_CODE, EVENT_TYPE, 
        	//RECORD_DATETIME, FILENAME_DIRECTORY,STATUS_OF_THE_ACTIVITY, IP_ADDRESS
            insertStatement = dbConnection.prepareStatement(insertQuery);

            insertStatement.setString(1, repositoryName);
            
            insertStatement.setString(2, userName);
            
            insertStatement.setString(3, solId);
            
            insertStatement.setString(4, eventType); // Use action as EVENT_TYPE
            
            insertStatement.setTimestamp(5, auditTime); // Use action as EVENT_TYPE
            
            insertStatement.setString(6, fileName);
            
            insertStatement.setString(7, eventStatus);
            
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

/*public int insertDataToTable(String action, Connection dbConnection, String userName, String repositoryName, String ipServer, String event) {
	System.out.println("OracleDBConnection.insertDataToTable()");
	Timestamp logonTime =null;
	Timestamp logoffTime = null;
	if(action.equalsIgnoreCase("login")){
		logonTime = new Timestamp(System.currentTimeMillis());
		logoffTime =null;
	}else if(action.equalsIgnoreCase("logoff")){
		 logonTime = null;
		 logoffTime = new Timestamp(System.currentTimeMillis());
	}
	int rowsInserted = 0;
	// Data to be inserted
	//String repositoryName = "TestRepo";
    //String userName = "John Sky";
    String branchCode = "ABC123";
    //String userItAddress = "localhost";
    //String event = "Login";
    
    String sql = PSBPropertyReader.getResourceBundle().getString("DMS_REPORT_DB_INSERTQUERY");
	System.out.println("insertQuery: "+sql);
	//INSERT INTO DMSDATA.loginsession(REPOSITORYID,USER_NAME, BRANCH_CODE, LOGONDATE,LOGOUTDATE, USER_IT_ADDRESS, EVENT) VALUES (?, ?, ?, ?, ?, ?, ?)
    //String sql = "INSERT INTO DMSDATA.loginsession(REPOSITORYID,USER_NAME, BRANCH_CODE, LOGONDATE,LOGOFFDATE, USER_IT_ADDRESS, EVENT) VALUES (?, ?, ?, ?, ?, ?)";
	PreparedStatement statement;
	try {
		statement = dbConnection.prepareStatement(sql);
		// Set values for the insert statement
		statement.setString(1, repositoryName);
	    statement.setString(2, userName);
	    statement.setString(3, branchCode);
	    statement.setTimestamp(4, logonTime);
	    statement.setTimestamp(5, logoffTime);
	    statement.setString(6, ipServer);
	    statement.setString(7, event);
	    
	    // Execute the insert statement
	    rowsInserted = statement.executeUpdate();
	    
	    if (rowsInserted > 0) {
	        System.out.println("Data inserted successfully.");
	    } else {
	        System.out.println("No rows inserted.");
	    }
	} catch (SQLException e) {
		System.out.println("SQLException: "+e.getMessage());
		e.printStackTrace();
	}
	return rowsInserted;
	
}*/
public void getTableColumnNames(Connection dbConnection) {
	System.out.println("OracleDBConnection.getTableColumnNames()");
	String selectQuery = "SELECT * FROM DMSDATA.loginsession";
    
    PreparedStatement stmt;
	try {
		stmt = dbConnection.prepareStatement(selectQuery);
		ResultSet resultSet = stmt.executeQuery();
		 // Get the result set metadata
		    ResultSetMetaData metaData = resultSet.getMetaData();
		    
		    // Get the number of columns in the result set
		    int columnCount = metaData.getColumnCount();
		    
		    // Iterate over the columns and retrieve their names
		    for (int i = 1; i <= columnCount; i++) {
		        String columnName = metaData.getColumnName(i);
		        System.out.println(columnName);
		    }
	} catch (SQLException e) {
		System.out.println("SQLException: "+e.getMessage());
		e.printStackTrace();
	}
    
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
