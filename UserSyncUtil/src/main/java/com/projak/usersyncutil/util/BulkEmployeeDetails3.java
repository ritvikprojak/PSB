package com.projak.usersyncutil.util;


import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
public class BulkEmployeeDetails3{
    @JsonProperty("USER_ID") 
    public String uSER_ID;
    @JsonProperty("EMP_ID") 
    public String eMP_ID;
    @JsonProperty("CRTIME") 
    public String cRTIME;
    @JsonProperty("EXPTIME") 
    public String eXPTIME;
    @JsonProperty("EMP_NAME") 
    public String eMP_NAME;
    @JsonProperty("STATUS") 
    public String sTATUS;
    @JsonProperty("SOL_ID") 
    public String sOL_ID;
    @JsonProperty("ROLE") 
    public String rOLE;
    @JsonProperty("DEPT") 
    public String dEPT;
    @JsonProperty("ROLE_DESC") 
    public String rOLE_DESC;
    
    
	public BulkEmployeeDetails3(String uSER_ID, String eMP_ID, String cRTIME, String eXPTIME, String eMP_NAME,
			String sTATUS, String sOL_ID, String rOLE, String dEPT, String rOLE_DESC) {
		super();
		this.uSER_ID = uSER_ID;
		this.eMP_ID = eMP_ID;
		this.cRTIME = cRTIME;
		this.eXPTIME = eXPTIME;
		this.eMP_NAME = eMP_NAME;
		this.sTATUS = sTATUS;
		this.sOL_ID = sOL_ID;
		this.rOLE = rOLE;
		this.dEPT = dEPT;
		this.rOLE_DESC = rOLE_DESC;
	}
	
	public BulkEmployeeDetails3() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getuSER_ID() {
		return uSER_ID;
	}
	public void setuSER_ID(String uSER_ID) {
		this.uSER_ID = uSER_ID;
	}
	public String geteMP_ID() {
		return eMP_ID;
	}
	public void seteMP_ID(String eMP_ID) {
		this.eMP_ID = eMP_ID;
	}
	public String getcRTIME() {
		return cRTIME;
	}
	public void setcRTIME(String cRTIME) {
		this.cRTIME = cRTIME;
	}
	public String geteXPTIME() {
		return eXPTIME;
	}
	public void seteXPTIME(String eXPTIME) {
		this.eXPTIME = eXPTIME;
	}
	public String geteMP_NAME() {
		return eMP_NAME;
	}
	public void seteMP_NAME(String eMP_NAME) {
		this.eMP_NAME = eMP_NAME;
	}
	public String getsTATUS() {
		return sTATUS;
	}
	public void setsTATUS(String sTATUS) {
		this.sTATUS = sTATUS;
	}
	public String getsOL_ID() {
		return sOL_ID;
	}
	public void setsOL_ID(String sOL_ID) {
		this.sOL_ID = sOL_ID;
	}
	public String getrOLE() {
		return rOLE;
	}
	public void setrOLE(String rOLE) {
		this.rOLE = rOLE;
	}
	public String getdEPT() {
		return dEPT;
	}
	public void setdEPT(String dEPT) {
		this.dEPT = dEPT;
	}
	public String getrOLE_DESC() {
		return rOLE_DESC;
	}
	public void setrOLE_DESC(String rOLE_DESC) {
		this.rOLE_DESC = rOLE_DESC;
	}
    
}



