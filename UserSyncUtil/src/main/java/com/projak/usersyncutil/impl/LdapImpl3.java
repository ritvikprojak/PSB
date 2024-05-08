package com.projak.usersyncutil.impl;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.projak.usersyncutil.util.BulkEmployeeDetails;
import com.projak.usersyncutil.util.PropertyReader;
import com.projak.usersyncutil.util.SearchQueries;

public class LdapImpl3 {
	static String ldapURL = null;
	static String adminUser = null;
	static String ldapBindPassword = null;
	static String commonDN = null;
	static String trustStore = null;
	static String trustStorePassword = null;
	static String url = null;
	static String user = null;
	static String password = null;
	static String stanza = null;
	static String objectStore = null;
	
	private static String userObjectClass = "(objectclass=user)";
	private static String groupObjectClass = "(objectclass=group)";
	
	public void setLdap() {

		ldapURL = PropertyReader.getProperty("ldap.ldapURL");
		adminUser = PropertyReader.getProperty("ldap.ldapBindId");
		ldapBindPassword = PropertyReader.getProperty("ldap.ldapBindPassword");
		commonDN = PropertyReader.getProperty("commonDN_LDAP");

	}

	public static LdapContext GetLdapContext(String ldapURL, String adminUser, String ldapBindPassword,
			String trustStore, String trustStorePassword) {

		LdapContext ctx = null;
		
		try {
			
			System.out.println("Inside GetLdapContext method of LdapClient class");
			
			Hashtable<String, String> env = new Hashtable<String, String>();
			
			String principalName = adminUser;
			
			env.put(Context.PROVIDER_URL, ldapURL);
			
			env.put(Context.SECURITY_PRINCIPAL, principalName);
			
			env.put(Context.SECURITY_CREDENTIALS, ldapBindPassword);
			
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			
			env.put(Context.SECURITY_AUTHENTICATION, "Simple");
			
			//env.put("javax.net.ssl.trustStore", trustStore);
			
			//env.put("javax.net.ssl.trustStorePassword", trustStorePassword);
			
			//System.setProperty("javax.net.ssl.trustStore", trustStore);
			
			//System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
			
			ctx = new InitialLdapContext(env, null);
			
			System.out.println("Connected");
		
		} catch (Exception ex) {
		
			System.out.println("Error in ldap Context: " + ex.getMessage());
		
			//throw ex;
		}
		
		return ctx;
	}
	
	public static void AddUserToGroup(String commonDN, LdapContext ctx, String groupName, String userName)
			throws Exception {
		//Boolean userAdded = false;
		try {
			
			System.out.println("Inside AddUserToGroup method of LdapClient class");
			
			ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
			
			ModificationItem[] mods = new ModificationItem[1];
			
			Attribute mod0 = new BasicAttribute("member", GetUserDistinguishedName(commonDN, userName, ctx));
			
			System.out.println("mod0"+ mod0);
			
			if(GetUserDistinguishedName(commonDN, userName, ctx).isEmpty()) {
			
				System.out.println("user not exist");
				
				//return "userNotExist";
			}
			
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod0);
			
			ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
			
			
			//userAdded = true;
		} catch (NameAlreadyBoundException nameEx){
			
			System.out.println(userName + " exists in " + groupName);
		
			//return "userExistInGroup";
		
		} catch (NamingException ex) {
			
			System.out.println("Error: " + ex.getMessage());
			
			System.out.println("Error: " + ex.fillInStackTrace());
			
			ex.printStackTrace();
			//return "groupNotFound";
			
		}
		
		finally {
			ctx.close();
		}
		//return "userAdded";
	}
	
	public static String RemoveUserFromGroup(String commonDN, LdapContext ctx, String groupName, String userName)
			throws Exception {
		//Boolean userRemoved = false;
		Attribute mod0= null;
		try {
			System.out.println("Inside RemoveUserFromGroup method of LdapClient class");
			
			ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
			
			ModificationItem[] mods = new ModificationItem[1];
			
			mod0 = new BasicAttribute("member", GetUserDistinguishedName(commonDN, userName, ctx));
			
			System.out.println("mod0"+ mod0);
			
			if(GetUserDistinguishedName(commonDN, userName, ctx).isEmpty()) {
			
				System.out.println("user not exist");
				
				return "userNotExist";
			
			}
			
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod0);
			
			ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
			
			//userRemoved = true;
	
		} catch (OperationNotSupportedException opEx) {
		
			System.out.println(userName + " not exists in " + groupName);
			
			return "userNotExistInGroup";
		
		} catch (Exception e) {
		
			System.out.println("Error: " + e.getMessage());
			
			System.out.println("Error: " + e.fillInStackTrace());
			
			return "userGroupNotExist";
		
		} finally {
		
			ctx.close();
		
		}
		
		return "userRemoved";
	
	}
	
	static String GetUserDistinguishedName(String commonDN, String userName, LdapContext ctx) throws Exception {
		String userDistinguishedName = "";
		try {
			System.out.println("Inside GetUserDistinguishedName method of LdapClient class");
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + userObjectClass + "(sAMAccountName=" + userName + "))",
					SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				userDistinguishedName = GetUserNameValue(attrs);
			} else {
				System.out.println("User Name '" + userName + "' not found.");
			}
		} catch (NameAlreadyBoundException ex) {
			System.out.println("User Name '" + userName + "'s is NameAlreadyBound.");
			System.out.println("user NAme already Bound");
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
			System.out.println("Error:" + ex.fillInStackTrace());
			System.out.println("userName Not Found");
			ex.printStackTrace();
		}
		return userDistinguishedName;
	}
	
	private static String GetGroupDistinguishedName(String commonDN, String groupName, LdapContext ctx)
			throws Exception {
		String groupDistinguishedName = "";
		try {
			System.out.println("Inside GetGroupDistinguishedName method of LdapClient class");
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + groupObjectClass + "(CN=" + groupName + "))", SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				groupDistinguishedName = GetUserNameValue(attrs);
			} else {
				System.out.println("Group Name '" + groupName + "' not found.");
				
				throw new Exception("Group Name '" + groupName + "' not found.");
			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.fillInStackTrace());
			throw ex;
		}
		return groupDistinguishedName;
	}
	private static String GetUserNameValue(Attributes attrs) throws Exception {
		String userName = "";
		if (attrs != null) {
			try {
				System.out.println("Inside GetUserNameValue method of LdapClient class");
				for (NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();) {
					Attribute attr = (Attribute) ae.next();
					for (NamingEnumeration<?> e = attr.getAll(); e.hasMore();) {
						userName = (String) e.next();
						System.out.println("User Name is :"+userName);
					}
				}
			} catch (Exception ex) {
				System.out.println("exception caught in GetUserNameValue method of LdapClient class"+ex.fillInStackTrace());
				throw ex;
			}
		}
		return userName;
	}

    public void syncWithLDAP(ArrayList<BulkEmployeeDetails> bulkEmpDetails, String objectStoreName) {

    	System.out.println("Syncing with LDAP...");
        
    	setLdap();
        
    	LdapContext ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
        
    	try {
    		
    		String[] groupNames = PropertyReader.getProperty("GROUP_NAME").split(",");
			
			System.out.println("GroupNames: "+groupNames);
			
			//removing all users from the groups ......commenting as this activity has to run only one time 
			
			/*System.out.println("Removing all users from the DMS_Maker/Checker groups");
			
			for (String groupName : groupNames) {

				System.out.println("DMSGroupName: " + groupName);
				
				RemoveAllUsersFromGroup(commonDN, ctx, groupName);
				
			}*/
    		
			System.out.println("Adding users to the LDAP groups");
			
    		for (int i = 0; i < bulkEmpDetails.size(); i++) {
    			
        		BulkEmployeeDetails empData =  bulkEmpDetails.get(i);
    			
    			String userId = empData.getuSER_ID();
    			
    			String dept = empData.getdEPT();
    			
    			String roleDesc = empData.getrOLE_DESC(); 
    			
    			String status = empData.getsTATUS();
    			
    			System.out.println("Passing User Id: "+userId);
    			
    			if (status == null || status.isEmpty()) {
    		        
    				status = ""; // Set it to an empty string if null or empty
    		    }
    			
    			//add users to GROUP_BASE_NAME i.e. p8users,etc
    			
    			String[] groupBaseName = PropertyReader.getProperty("GROUP_BASE_NAME").split(",");//make it dynamic
    			
    			System.out.println("GROUP_BASE_NAME: "+groupBaseName);
    			
    			//add all users to the base groups
    			
    			for (String value : groupBaseName) {

    				System.out.println("DMSGroupName: " + value + "\t" + objectStore);
    				
    				if(status.equalsIgnoreCase("Active")){
    					
    					AddUserToGroup(commonDN, ctx, value, userId);
    				
    				}else{
    					
    					System.out.println(userId + "\t status is either Inactive or empty. Hence user not added to the group "+value+" .");
    			
    				}
    				
    			}
    			
    			System.out.println("add users to respective LDAP group i.e. DMS_Maker,DMS_Checker");
    			
    			//add users to respective LDAP group i.e. DMS_Maker,DMS_Checker
    			
    			String ldapGroupName = null;
    			
    			ldapGroupName = PropertyReader.getProperty("LDAP_"+roleDesc);
    			
    			if(ldapGroupName != null && !ldapGroupName.isEmpty() && userId != null && !userId.isEmpty()){
    				
    				if(status.equalsIgnoreCase("Active")){
    					
    					AddUserToGroup(commonDN, ctx, ldapGroupName, userId);
    				
    				}else{
    					
    					System.out.println(userId + "\t status is either Inactive or empty. Hence user not added to the group "+ldapGroupName+" .");
    			
    				}
    				
    				//AddUserToGroup(commonDN, ctx, ldapGroupName, userId);
    			
    			}else{
    				
    				System.out.println("LDAP Group Name/User ID is null/empty."+ldapGroupName+"\t"+userId);
    				
    			}
    			
        	}
			
		} catch (Exception e) {

			System.out.println("Exception : "+e.getMessage());
			
			e.printStackTrace();
		
		}
        
    }
    
    static Boolean RemoveAllUsersFromGroup(String commonDN, LdapContext ctx, String groupName)
            throws Exception {
    	System.out.println("LdapImpl.RemoveAllUsersFromGroup()");
        Boolean usersRemoved = false;
        try {
        	ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
            // Retrieve the group's distinguished name
            String groupDN = GetGroupDistinguishedName(commonDN, groupName, ctx);

            // Remove all members from the group by setting the "member" attribute to an empty array
            Attribute mod = new BasicAttribute("member");
            ModificationItem[] mods = new ModificationItem[] {
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod)
            };
            ctx.modifyAttributes(groupDN, mods);

            usersRemoved = true;
        } catch (Exception e) {
        	
        	System.out.println("Error: " + e.fillInStackTrace());
        } finally {
            ctx.close();
        }
        return usersRemoved;
    }
    
    public static void main(String[] args) {
		/*LdapImpl impl = new LdapImpl();
		impl.setLdap();
		System.out.println(ldapURL+adminUser+ldapBindPassword);
		LdapContext ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
		try {
			AddUserToGroup(commonDN, ctx, "P8users", "dmstest02");
		} catch (Exception e) {
			System.out.println("RootException: "+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	
       
    		LdapImpl2 impl = new LdapImpl2();
    		//impl.setLdap();
    		String ldapURL = "ldap://13.90.40.147:389/";
    		String adminUser = "p8admin";
    		String ldapBindPassword = "pass@123";
    		System.out.println(ldapURL+adminUser+ldapBindPassword);
    		LdapContext ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
    		
    		
    		try {
    			AddUserToGroup(commonDN, ctx, "DMS_View", "dmstest10");
    		} catch (Exception e) {
    			System.out.println("RootException: "+e.getMessage());
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    
}
