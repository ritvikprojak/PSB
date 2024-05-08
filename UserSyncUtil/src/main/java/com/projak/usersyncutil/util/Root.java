package com.projak.usersyncutil.util;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Root{
    @JsonProperty("EmployeeDetails") 
    public ArrayList<BulkEmployeeDetails> employeeDetails;

	public Root(ArrayList<BulkEmployeeDetails> employeeDetails) {
		super();
		this.employeeDetails = employeeDetails;
	}

	public Root() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ArrayList<BulkEmployeeDetails> getEmployeeDetails() {
		return employeeDetails;
	}

	public void setEmployeeDetails(ArrayList<BulkEmployeeDetails> employeeDetails) {
		this.employeeDetails = employeeDetails;
	}
}
