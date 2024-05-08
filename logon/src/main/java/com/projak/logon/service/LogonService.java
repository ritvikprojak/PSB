package com.projak.logon.service;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

public interface LogonService {
	
	HttpServletResponse addCookies(String userid, String password, HttpServletResponse response);

}
