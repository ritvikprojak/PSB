package com.projak.usersyncutil.util;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class User {
    @JacksonXmlProperty(localName = "description")
    private String description;

    @JacksonXmlProperty(localName = "index")
    private int index;

    @JacksonXmlProperty(localName = "name")
    private String name;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String description, int index, String name) {
		super();
		this.description = description;
		this.index = index;
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    // Getters and setters
}