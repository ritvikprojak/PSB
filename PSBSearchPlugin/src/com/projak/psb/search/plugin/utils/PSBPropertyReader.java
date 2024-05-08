package com.projak.psb.search.plugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

public class PSBPropertyReader {
	
	private static String path = "/fn_icn_dev/PSB";
	 public static ResourceBundle getResourceBundle() 
	  {
		 ResourceBundle rsbundle = null;
		 FileInputStream fis = null;
		try {
			File file = new File(path);
			URL[] urls = {file.toURI().toURL()};
			ClassLoader loader = new URLClassLoader(urls);
			ResourceBundle rb = ResourceBundle.getBundle("searchplugin_config", Locale.getDefault(), loader);
			//System.out.println(rb.getString("nhl.user.api"));
			return rb;
		} catch (IOException e) {
			System.out.println("Property File Not Found "+e.fillInStackTrace());
		}finally{
			
			fis=null;
			
		}
		return rsbundle;
	  }

	//private static ResourceBundle rb = ResourceBundle.getBundle("resource.nhlProperties");
	
	public static void main(String[] args) {
	//	System.out.println(PropertyReader.rb.getString("nhl.user.api"));
		try {
			//System.out.println(HOPDPropertyReader.getResourceBundle().getString("age"));
			//System.out.println(rb.getString("nhl.user.api"));
		} catch (Exception e) {

		}
		
	}

}
