package com.projak.logon;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CorsFilter implements Filter {
//	
	@Autowired
	private Environment env;

	/**
	 * Default constructor.
	 */
	public CorsFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		System.out.println("DoFilter Called");
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		String origin = req.getHeader("Origin");
		String originList = env.getProperty("origin");
		if (!originList.isEmpty()) {
			List<String> origins = Arrays.asList(originList.split(","));
			if (origins.contains(origin)) {
				res.addHeader("Access-Control-Allow-Origin", origin);
				res.addHeader("Access-Control-Allow-Credentials", "true");
				
			}
		}
		chain.doFilter(req, res);

	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}