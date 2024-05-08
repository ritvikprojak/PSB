package com.projak.usersyncutil.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class UserList {
    @JacksonXmlProperty(localName = "Count")
    private int count;

    @JacksonXmlProperty(localName = "Users")
    @JacksonXmlElementWrapper(useWrapping = false)
    private Users users;

	public UserList() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserList(int count, Users users) {
		super();
		this.count = count;
		this.users = users;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

    // Getters and setters
}