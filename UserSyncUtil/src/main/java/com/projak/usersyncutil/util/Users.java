package com.projak.usersyncutil.util;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Users {
	
    @JacksonXmlProperty(localName = "User")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<User> userList;

	public Users() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Users(List<User> userList) {
		super();
		this.userList = userList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

    // Getters and setters
}