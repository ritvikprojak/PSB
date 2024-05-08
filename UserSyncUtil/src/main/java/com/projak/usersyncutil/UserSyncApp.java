package com.projak.usersyncutil;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.projak.usersyncutil.controller.AppController;

public class UserSyncApp {
	
	static Logger log = Logger.getLogger(UserSyncApp.class.getName());
	
    public static void main(String[] args) {
    	
    	PropertyConfigurator.configure("C:\\UserSync\\log4j.properties");
    	
        // Initialize components
        AppController appController = new AppController();

        // Call controller methods to start the application
        log.info("Calling controller methods to start the UserSync application");
        appController.startApplication();
    }
}
