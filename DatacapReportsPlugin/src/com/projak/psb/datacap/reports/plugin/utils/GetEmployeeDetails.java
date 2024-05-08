package com.projak.psb.datacap.reports.plugin.utils;

import java.io.IOException;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.json.java.JSONObject;

public class GetEmployeeDetails {

    public JSONObject getEmployeeDetails(String ldaploginid) throws IOException {
    	System.out.println("GetEmployeeDetails.getEmployeeDetails()");
        JSONObject responseJson = new JSONObject();
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            // Construct the API URL
            String apiUrl = PSBPropertyReader.getResourceBundle().getString("psb.employee.rapi");
            HttpPost httpPost = new HttpPost(apiUrl);

            // Construct the request JSON
            JSONObject requestJson = new JSONObject();
            requestJson.put(PSBPropertyReader.getResourceBundle().getString("jobjKey2"), ldaploginid);

            // Set the request entity
            StringEntity entity = new StringEntity(requestJson.toString());
            httpPost.setEntity(entity);

            // Execute the HTTP request
            CloseableHttpResponse httpResponse = client.execute(httpPost);

            try {
                // Get the status code
                int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    // If the status code is 200 (OK), parse the response JSON
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String responseString = EntityUtils.toString(httpEntity);
                    responseJson = JSONObject.parse(responseString);
                } else {
                    // Handle other status codes as needed
                    responseJson.put("error", "HTTP Status Code: " + statusCode);
                }
            } finally {
                httpResponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            responseJson.put("error", "Client Protocol Exception: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            responseJson.put("error", "IO Exception: " + e.getMessage());
        } finally {
            client.close();
        }

        return responseJson;
    }
}
