package com.projak.usersyncutil.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.projak.usersyncutil.util.User;
import com.projak.usersyncutil.util.UserList;



public class DatacapImpl {
	 /*private static Properties properties;
	    private String cookie;
	    private Connection connection;

	    // Common properties for both CASA and HOPLDEV
	    private static final String REST_URL = "http://13.90.40.147:8081/ServicewTM.svc/Session/Logon";
	    private static String JSON_BODY = "{\"application\":\"%s\",\"password\":\"admin\",\"station\":\"1\",\"user\":\"admin\"}";

	    private static final String REST_URL_SAVE = "http://13.90.40.147:8081/ServicewTM.svc/Admin/SaveUser";
	    private static String JSON_BODY_SAVE = "{\"application\":\"%s\",\"description\":\"sample user\",\"index\":\"-2\",\"name\":\"%s\",\"password\":\"%d\"}";

	    private static String REST_URL_SET = "http://13.90.40.147:8081/ServicewTM.svc/Admin/SetUserPermissionList/%s/";
	    private static final String maker_permission = "{\"Permissions\":[{\"Job\": \"12\", \"Task\": \"30\"}, {\"Job\": \"12\", \"Task\": \"32\"}, {\"Job\": \"13\", \"Task\": \"34\"}]}";
	    private static final String checker_permission = "{\"Permissions\":[{\"Job\": \"12\", \"Task\": \"36\"}]}";
	    private static String REST_URL_GET_USERS = "http://13.90.40.147:8081/ServicewTM.svc/Admin/GetUserList/%s";

	    public DatacapImpl(String application) {
	        this.JSON_BODY = JSON_BODY.replace("%s", application);
	        this.JSON_BODY_SAVE = JSON_BODY_SAVE.replace("%s", application);
	        this.REST_URL_SET = REST_URL_SET.replace("%s", application);
	        this.REST_URL_GET_USERS = REST_URL_GET_USERS.replace("%s", application);
	    }
	    public Connection getConnection(String dbUrl, String dbUsername, String dbPassword) throws IOException {
	    	System.out.println("DatacapImpl.getConnection()");
	        System.out.println("Calling Database Connection **");
	        try {
	            // Database Connection properties
	            Class.forName("com.ibm.db2.jcc.DB2Driver");
	            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
	            System.out.println("URL: " + dbUrl);
	            System.out.println("Username: " + dbUsername);
	            System.out.println("Connection was successful - " + connection);
	        } catch (ClassNotFoundException exe) {
	            exe.printStackTrace();
	            System.out.println(exe);
	        } catch (SQLException exe) {
	            exe.printStackTrace();
	            System.out.println(exe);
	        }
	        return connection;
	    }

	    public void fetchData() {
	        Statement statement = null;
	        ResultSet resultSet = null;

	        try {
	            // Create a statement
	            statement = connection.createStatement();

	            // Get the database query from the properties file
	            String dbQuery = "SELECT * FROM DB2ADMIN.ADMIN";

	            // Execute the query
	            String query = dbQuery;
	            resultSet = statement.executeQuery(query);

	            // Process the result set
	            while (resultSet.next()) {
	                int id = resultSet.getInt("ID"); // Updated column name
	                String user = resultSet.getString("USER_NAME"); // Updated column name
	                String role = resultSet.getString("ROLE");
	                String message = resultSet.getString("MESSAGE");
	                String status = resultSet.getString("STATUS");
	                Timestamp time = resultSet.getTimestamp("TIME");
	                //Dept
	                //APP
	                
	                System.out.println(
	                    "id: " + id + ", user: " + user + ", role: " + role + ", message: " + message + ", status: "
	                    + status + ", time: " + time);
	            }
	          
	            
	        } catch (SQLException e) {
	            System.out.println("Failed to execute the query.");
	            e.printStackTrace();
	        } finally {
	            // Close the result set and statement
	            if (resultSet != null) {
	                try {
	                    resultSet.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (statement != null) {
	                try {
	                    statement.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
	  
	    
	    
	    
	    private List<String> getDatabaseUsers() {
	        List<String> users = new ArrayList<>();
	        Statement statement = null;
	        ResultSet resultSet = null;

	        try {
	            // Create a statement
	            statement = connection.createStatement();

	            // Get the database query from the properties file
	            String dbQuery = "SELECT USER_NAME FROM DB2ADMIN.ADMIN"; // Replace "ADMIN" with the actual table name

	            // Execute the query
	            resultSet = statement.executeQuery(dbQuery);

	            // Process the result set
	            while (resultSet.next()) {
	                String user = resultSet.getString("USER_NAME");
	                users.add(user);
	            }
	        } catch (SQLException e) {
	            System.out.println("Failed to execute the query.");
	            e.printStackTrace();
	        } finally {
	            // Close the result set, statement, and connection
	            if (resultSet != null) {
	                try {
	                    resultSet.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (statement != null) {
	                try {
	                    statement.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	        return users;
	    }
	    public void logon() throws IOException {
	        // Call the REST API
	        URL restUrl = new URL(REST_URL);
	        HttpURLConnection con = (HttpURLConnection) restUrl.openConnection();
	        con.setRequestMethod("POST");
	        con.setRequestProperty("Content-Type", "application/json");
	        con.setDoOutput(true);
	        con.getOutputStream().write(JSON_BODY.getBytes("UTF-8"));

	        // Get the cookie from the response
	        cookie = con.getHeaderField("Set-Cookie");

	        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String inputLine;
	        StringBuilder response = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();

	        // Print the response
	        System.out.println(response.toString());
	    }


	    
	    public UserList getUserList() throws IOException {
	        URL restUrl = new URL(REST_URL_GET_USERS);
	        HttpURLConnection con = (HttpURLConnection) restUrl.openConnection();
	        con.setRequestMethod("GET");
	        con.setRequestProperty("Content-Type", "application/json");
	        con.setRequestProperty("Authorization", createBasicAuthHeader("admin", "admin"));
	        con.setRequestProperty("Cookie", cookie);

	        StringBuilder response = new StringBuilder();
	        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
	            String inputLine;
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	        } catch (IOException e) {
	            // Handle connection or reading error
	            e.printStackTrace();
	        }

	        String userListResponse = response.toString();
	        
	        XmlMapper objectMapper = new XmlMapper();
	       
	        
	        UserList emp = objectMapper.readValue(userListResponse, UserList.class);
	  


	        return emp;
	    }


	    private String createBasicAuthHeader(String username, String password) {
	        String auth = username + ":" + password;
	        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
	        return "Basic " + new String(encodedAuth);
	    }

	    // Add any other methods that are common between CASA and HOPLDEV classes.
	    public void saveUser() throws UnsupportedEncodingException, IOException {
	        List<String> databaseUsers = getDatabaseUsers();
	        UserList userListResponse = getUserList();
	        List<User> userList = userListResponse.getUsers().getUserList();
	        		

	      //  List<String> newUsers = new ArrayList<>();
	        List<String> newUsers = new ArrayList<>();
	        for (String user : databaseUsers) {
	            boolean found = false;
	            for (User u : userList) {
	                if (u.getName().equals(user)) {
	                    found = true;
	                    break;
	                }
	            }
	            if (!found) {
	                newUsers.add(user);
	            }
	        }

	        for (String newUser : newUsers) {
	            String jsonBodySave = JSON_BODY_SAVE
	                                               .replace("%s", newUser.trim())
	                                               .replace("%d", newUser.trim());

	            String auth = "admin" + ":" + "admin";
	            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes("UTF-8"));
	            String authHeaderValue = "Basic " + new String(encodedAuth);
	            URL restUrl = new URL(REST_URL_SAVE);
	            HttpURLConnection con = (HttpURLConnection) restUrl.openConnection();
	            con.setRequestMethod("POST");
	            con.setRequestProperty("Content-Type", "application/json");
	            con.setRequestProperty("Authorization", authHeaderValue);

	            if (cookie != null) {
	                con.setRequestProperty("Cookie", cookie);
	            }
	            con.setDoOutput(true);
	            con.getOutputStream().write(jsonBodySave.getBytes("UTF-8"));

	            int responseCode = con.getResponseCode();
	            if (responseCode == HttpURLConnection.HTTP_OK) {
	                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	                String inputLine;
	                StringBuilder response = new StringBuilder();
	                while ((inputLine = in.readLine()) != null) {
	                    response.append(inputLine);
	                }
	                in.close();
	                System.out.println(response.toString());
	            } else {
	                System.out.println("Failed to save user: " + newUser);
	                System.out.println("Response code: " + responseCode);
	            }
	        }
	    }

	    public void setPermission() throws UnsupportedEncodingException, IOException {
	        List<String> databaseUsers = getDatabaseUsers();
	        UserList userListResponse = getUserList();
	        List<User> userList = userListResponse.getUsers().getUserList();
	        String jsonBodySet = null;

	        
	        for (String user : databaseUsers) {
	            for (User u : userList) {
	                if (u.getName().equals(user)) {
	                    String dbRole = getRoleFromDatabase(user); // Retrieve the role from the database for the user

	                    if (dbRole.equals("maker")) {
	                        jsonBodySet = maker_permission;
	                    } else if (dbRole.equals("checker")) {
	                        jsonBodySet = checker_permission;
	                    }

	                    if (jsonBodySet != null) {
	                        String auth = "admin" + ":" + "admin";
	                        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes("UTF-8"));
	                        String authHeaderValue = "Basic " + new String(encodedAuth);
	                        URL restUrl = new URL(REST_URL_SET+u.getIndex());
	                        System.out.println(restUrl);
	                        HttpURLConnection con = (HttpURLConnection) restUrl.openConnection();
	                        con.setRequestMethod("POST");
	                        con.setRequestProperty("Content-Type", "application/json");
	                        con.setRequestProperty("Authorization", authHeaderValue);

	                        if (cookie != null) {
	                            con.setRequestProperty("Cookie", cookie);
	                        }
	                        con.setDoOutput(true);
	                        con.getOutputStream().write(jsonBodySet.getBytes("UTF-8"));

	                        System.out.println(jsonBodySet.toString());
	                        int responseCode = con.getResponseCode();
	                        if (responseCode == HttpURLConnection.HTTP_OK) {
	                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	                            String inputLine;
	                            StringBuilder response = new StringBuilder();
	                            while ((inputLine = in.readLine()) != null) {
	                                response.append(inputLine);
	                            }
	                            in.close();
	                            System.out.println(" seTPermission APi executed successuffy ");
	                            System.out.println(response.toString());
	                            System.out.println();
	                        } else {
	                            System.out.println("Failed to set permission for user: " + user);
	                            System.out.println("Response code: " + responseCode);
	                        }
	                    }
	                    break;
	                }
	            }
	        }
	    }

	    private String getRoleFromDatabase(String user) {
	        String role = null;

	        String dbUrl = "jdbc:db2://13.90.40.147:50000/LICENGDB";
	        String dbUsername = "db2admin";
	        String dbPassword = "pass@123";
	        try (Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
	            String query = "SELECT ROLE FROM ADMIN WHERE USERNAME = ?";
	            try (PreparedStatement statement = connection.prepareStatement(query)) {
	                statement.setString(1, user);
	                try (ResultSet resultSet = statement.executeQuery()) {
	                    if (resultSet.next()) {
	                        role = resultSet.getString("ROLE");
	                    }
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return role;
	    }
  
    public void performDatacapSync() {
        // Implement Datacap synchronization logic here
        System.out.println("Performing Datacap synchronization...");
    }*/
}
