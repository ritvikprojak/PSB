package com.projak.usersyncutil.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.json.java.JSONObject;
import com.projak.usersyncutil.util.AES;
import com.projak.usersyncutil.util.BulkEmployeeDetails;
import com.projak.usersyncutil.util.PropertyReader;
import com.projak.usersyncutil.util.Root;

public class BulkEmpDetailsImpl {
	static Logger log = Logger.getLogger(BulkEmpDetailsImpl.class.getName());
	
    public ArrayList<BulkEmployeeDetails> getBulkEmployeeDetails(String objectStoreName) {
        log.info("BulkEmpDetailsImpl.getBulkEmployeeDetails() " + objectStoreName);

        JSONObject responseJson = null;
        Root root = null;
        String skeyHeaderValue = null;
        CloseableHttpClient client = null;
        String secretKeyText = null;

        byte[] secretKey = AES.generateAESKey(256);
        secretKeyText = AES.encodeKey(secretKey);

        try {
            client = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(PropertyReader.getProperty("BULKEMPLOYEE_API_URL"));

            JSONObject jobj = new JSONObject();
            jobj.put("DEPT", objectStoreName);
            jobj.put("EncData", AES.encryptMessage(jobj.toString(), secretKeyText));
            jobj.remove("DEPT");

            StringEntity entity = new StringEntity(jobj.toString());
            httpPost.setEntity(entity);

            String transactionId = String.valueOf(new Date().getTime());
            
            log.info("DEPTNAME:"+objectStoreName);
            log.info("passing Skey&TransactionId: "+secretKeyText +"  & \t"+transactionId);
            
            httpPost.addHeader("Skey", secretKeyText);
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            httpPost.addHeader("Transactionid", transactionId);
            httpPost.addHeader("Source-Name", PropertyReader.getProperty("psb.employee.api.headers.source-Name"));
            httpPost.addHeader("Source-Ip", PropertyReader.getProperty("psb.employee.api.headers.source-Ip"));
            httpPost.addHeader("Access-Control-Allow-Methods",
                    PropertyReader.getProperty("psb.employee.api.headers.AccessControlAllowMethods"));
            httpPost.addHeader("Access-Control-Allow-Origin",
                    PropertyReader.getProperty("psb.employee.api.headers.AccessControlAllowOrigin"));
            httpPost.addHeader("Content-Security-Policy",
                    PropertyReader.getProperty("psb.employee.api.headers.ContentSecurityPolicy"));
            httpPost.addHeader("X-Frame-Options", PropertyReader.getProperty("psb.employee.api.headers.XFrameOptions"));
            httpPost.addHeader("X-Content-Type-Options",
                    PropertyReader.getProperty("psb.employee.api.headers.XContentTypeOptions"));
            httpPost.addHeader("Referrer-Policy", PropertyReader.getProperty("psb.employee.api.headers.ReferrerPolicy"));
            httpPost.addHeader("X-Xss-Protection",
                    PropertyReader.getProperty("psb.employee.api.headers.XXssProtection"));
            httpPost.addHeader("Token", PropertyReader.getProperty("psb.employee.api.headers.token"));
            httpPost.addHeader("Permissions-Policy", "dummytext");
            httpPost.addHeader("Strict-Transport-Security", String.valueOf(new Date().getTime()));

            CloseableHttpResponse response = client.execute(httpPost);

            String bodyAsString = EntityUtils.toString(response.getEntity());
            responseJson = JSONObject.parse(bodyAsString);

            skeyHeaderValue = response.getFirstHeader("Skey").getValue();
            String encryptedResponse = bodyAsString;
            String jsonResponse = AES.decryptMessage(responseJson.get("EncData").toString(),
                    response.getFirstHeader("Skey").getValue());

            responseJson = JSONObject.parse(jsonResponse);

            // Write the response to a file
            String filename = objectStoreName + "_response.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(responseJson.toString()); // Adjust indentation as needed
                log.info("Response written to: " + filename);
            } catch (IOException e) {
            	log.error("Error writing response to file: " + e.getMessage());
                e.printStackTrace();
            }

            ObjectMapper om = new ObjectMapper();
            root = om.readValue(responseJson.toString(), Root.class);

        } catch (Exception e) {
        	log.error("Exception in CloseableHttpResponse: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
            	log.error("IOException: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (root != null) {
            return root.getEmployeeDetails();
        } else {
            return new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        BulkEmpDetailsImpl detailsImpl = new BulkEmpDetailsImpl();
        ArrayList<BulkEmployeeDetails> jsonArray = detailsImpl.getBulkEmployeeDetails("HFOS");
        System.out.println("Size:" + jsonArray.size());
        JSONObject JsonEmpDetails = new JSONObject();
        for (int i = 0; i < jsonArray.size(); i++) {
            BulkEmployeeDetails empData = jsonArray.get(i);
            System.out.println(empData.getdEPT());
        }
    }
}
