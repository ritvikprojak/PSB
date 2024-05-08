package com.projak.psb.search.plugin.utils;

import java.io.IOException;
import java.util.Date;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ibm.json.java.JSONObject;
public class GetEmployeeDetails2 {

public JSONObject getEmployeeDetails(String ldaploginid) throws ClientProtocolException, IOException {
		
		JSONObject responseJson = null;
		
		try{
			int i = (int) (new Date().getTime()/1000);
			
			CloseableHttpClient client = HttpClients.createDefault();
			
			System.out.println("CustomSearchReqFilter.getUserDetails() URL "+PSBPropertyReader.getResourceBundle().getString("psb.employee.rapi"));
			
			HttpPost httpPost = new HttpPost(PSBPropertyReader.getResourceBundle().getString("psb.employee.rapi"));

			JSONObject jobj = new JSONObject();
			
			jobj.put(PSBPropertyReader.getResourceBundle().getString("jobjKey2"), ldaploginid);
			
			StringEntity entity = new StringEntity(jobj.toString());

			httpPost.setEntity(entity);
			
			try{
				CloseableHttpResponse response = client.execute(httpPost);
				String bodyAsString = EntityUtils.toString(response.getEntity());

				System.out.println("bodyAsString: "+bodyAsString);
				
				responseJson = JSONObject.parse(bodyAsString);

			}catch (Exception e) {
				
				System.out.println("Exception in CloseableHttpResponse: "+e.getMessage());
				
			}finally {
				
				client.close();
			}
			
		}catch (Exception e) {
			System.out.println("Exception in CloseableHttpClient: "+e.getMessage());
		}

		return responseJson;
	}
}
