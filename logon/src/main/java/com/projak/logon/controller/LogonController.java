package com.projak.logon.controller;

import java.io.IOException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projak.logon.service.impl.LogonServiceImpl;

@RestController
@RequestMapping("/jaxrs")
public class LogonController {
	
	@Autowired
	private LogonServiceImpl logonImpl;
	
	@GetMapping(value ="/logon.do")
	public void addSameSiteCookies(@RequestParam(name="userid")String userId, @RequestParam(name="password")String password,
			HttpServletResponse response, HttpServletRequest request) throws IOException {
		try {
			String xForwardedForHeader = request.getHeader("X-Forwarded-For");
			System.out.println(xForwardedForHeader);
	        String temp12 = request.getRemoteAddr();
	        System.out.println(temp12);
		}
		catch(Exception ex) {
			System.out.println(ex.getStackTrace());
		}
		System.out.println("Logon.do Called");
		logonImpl.addCookies(userId, password, response);
	}
	
	@GetMapping(value ="/demo")
	public void demo() throws IOException {
		
	}
	
	@GetMapping(value ="/logoff")
	public void logoff(HttpServletResponse response , HttpServletRequest request) throws IOException {
		try {
			String xForwardedForHeader = request.getHeader("X-Forwarded-For");
			System.out.println(xForwardedForHeader);
	        String temp12 = request.getRemoteAddr();
	        System.out.println(temp12);
		}
		catch(Exception ex) {
			System.out.println(ex.getStackTrace());
		}
		System.out.println("Logoff Called");
		logonImpl.removeCookies(response , request);
		
	}

}
