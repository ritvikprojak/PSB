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

import org.apache.log4j.Logger;

import com.projak.usersyncutil.util.BulkEmployeeDetails;
import com.projak.usersyncutil.util.PropertyReader;
import com.projak.usersyncutil.util.SearchQueries;

public class LdapImpl {
	
	static Logger log = Logger.getLogger(LdapImpl.class.getName());
	
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
		
		log.info("Setting LdapImpl.setLdap()");
		ldapURL = PropertyReader.getProperty("ldap.ldapURL");
		adminUser = PropertyReader.getProperty("ldap.ldapBindId");
		ldapBindPassword = PropertyReader.getProperty("ldap.ldapBindPassword");
		commonDN = PropertyReader.getProperty("commonDN_LDAP");

	}

	public static LdapContext GetLdapContext(String ldapURL, String adminUser, String ldapBindPassword,
			String trustStore, String trustStorePassword) {

		LdapContext ctx = null;
		
		try {
			
			log.debug("Inside GetLdapContext method of LdapClient class");
			
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
			
			log.info("LDAP Connected");
		
		} catch (Exception ex) {
		
			log.error("Error in ldap Context: " + ex.getMessage());
		
			//throw ex;
		}
		
		return ctx;
	}
	
	public static void AddUserToGroup(String commonDN, LdapContext ctx, String groupName, String userName) {
	    try {
	    	log.debug("Inside AddUserToGroup method of LdapClient class");

	        ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);

	        String userDistinguishedName = GetUserDistinguishedName(commonDN, userName, ctx);

	        if (userDistinguishedName.isEmpty()) {
	        	log.info("User not found: " + userName);
	            return; // Skip the user and continue with the next user
	        }

	        ModificationItem[] mods = new ModificationItem[1];
	        Attribute mod0 = new BasicAttribute("member", userDistinguishedName);
	        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, mod0);
	        ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
	    } catch (NameAlreadyBoundException nameEx) {
	    	log.info(userName + " exists in " + groupName);
	    } catch (Exception ex) {
	    	log.error("Error: " + ex.getMessage());
	    	log.error("Error: " + ex.fillInStackTrace());
	        ex.printStackTrace();
	    } finally {
	        if (ctx != null) {
	            try {
	                ctx.close();
	            } catch (NamingException e) {
	            	log.error("Error closing LDAP context: " + e.getMessage());
	            }
	        }
	    }
	}

	
	public static String RemoveUserFromGroup(String commonDN, LdapContext ctx, String groupName, String userName)
			throws Exception {
		//Boolean userRemoved = false;
		Attribute mod0= null;
		try {
			log.debug("Inside RemoveUserFromGroup method of LdapClient class");
			
			ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
			
			ModificationItem[] mods = new ModificationItem[1];
			
			mod0 = new BasicAttribute("member", GetUserDistinguishedName(commonDN, userName, ctx));
			
			log.debug("mod0"+ mod0);
			
			if(GetUserDistinguishedName(commonDN, userName, ctx).isEmpty()) {
			
				log.info("user not exist");
				
				return "userNotExist";
			
			}
			
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, mod0);
			
			ctx.modifyAttributes(GetGroupDistinguishedName(commonDN, groupName, ctx), mods);
			
			//userRemoved = true;
	
		} catch (OperationNotSupportedException opEx) {
		
			log.info(userName + " not exists in " + groupName);
			
			return "userNotExistInGroup";
		
		} catch (Exception e) {
		
			log.error("Error: " + e.getMessage());
			
			log.error("Error: " + e.fillInStackTrace());
			
			return "userGroupNotExist";
		
		} finally {
		
			ctx.close();
		
		}
		
		return "userRemoved";
	
	}
	
	static String GetUserDistinguishedName(String commonDN, String userName, LdapContext ctx) throws Exception {
		String userDistinguishedName = "";
		try {
			log.debug("Inside GetUserDistinguishedName method of LdapClient class");
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + userObjectClass + "(sAMAccountName=" + userName + "))",
					SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				userDistinguishedName = GetUserNameValue(attrs);
			} else {
				log.info("User Name '" + userName + "' not found.");
			}
		} catch (NameAlreadyBoundException ex) {
			log.info("User Name '" + userName + "'s is NameAlreadyBound.");
			log.info("user NAme already Bound");
		} catch (Exception ex) {
			log.error("Error: " + ex.getMessage());
			log.error("Error:" + ex.fillInStackTrace());
			log.error("userName Not Found");
			ex.printStackTrace();
		}
		return userDistinguishedName;
	}
	
	private static String GetGroupDistinguishedName(String commonDN, String groupName, LdapContext ctx)
			throws Exception {
		String groupDistinguishedName = "";
		try {
			log.debug("Inside GetGroupDistinguishedName method of LdapClient class");
			NamingEnumeration<SearchResult> answer = ctx.search(commonDN,
					"(&" + groupObjectClass + "(CN=" + groupName + "))", SearchQueries.GetDistinguishedName());
			if (answer.hasMore()) {
				Attributes attrs = answer.next().getAttributes();
				groupDistinguishedName = GetUserNameValue(attrs);
			} else {
				log.info("Group Name '" + groupName + "' not found.");
				
				throw new Exception("Group Name '" + groupName + "' not found.");
			}
		} catch (Exception ex) {
			log.error("Error:" + ex.fillInStackTrace());
			throw ex;
		}
		return groupDistinguishedName;
	}
	private static String GetUserNameValue(Attributes attrs) throws Exception {
		String userName = "";
		if (attrs != null) {
			try {
				log.debug("Inside GetUserNameValue method of LdapClient class");
				for (NamingEnumeration<?> ae = attrs.getAll(); ae.hasMore();) {
					Attribute attr = (Attribute) ae.next();
					for (NamingEnumeration<?> e = attr.getAll(); e.hasMore();) {
						userName = (String) e.next();
						log.info("User Name is :"+userName);
					}
				}
			} catch (Exception ex) {
				log.error("exception caught in GetUserNameValue method of LdapClient class"+ex.fillInStackTrace());
				throw ex;
			}
		}
		return userName;
	}

    public void syncWithLDAP(ArrayList<BulkEmployeeDetails> bulkEmpDetails, String objectStoreName) {

    	log.info("Syncing with LDAP...");
        
    	setLdap();
        
    	LdapContext ctx = GetLdapContext(ldapURL, adminUser, ldapBindPassword, trustStore, trustStorePassword);
        
    	try {
    		
    		String[] groupNames = PropertyReader.getProperty("GROUP_NAME").split(",");
			
    		log.info("GroupNames: "+groupNames);
			
			//removing all users from the groups ......commenting as this activity has to run only one time 
			
			/*System.out.println("Removing all users from the DMS_Maker/Checker groups");
			
			for (String groupName : groupNames) {

				System.out.println("DMSGroupName: " + groupName);
				
				RemoveAllUsersFromGroup(commonDN, ctx, groupName);
				
			}*/
    		
    		log.info("Adding users to the LDAP groups");
			
    		for (int i = 0; i < bulkEmpDetails.size(); i++) {
    			
        		BulkEmployeeDetails empData =  bulkEmpDetails.get(i);
    			
    			String userId = empData.getuSER_ID();
    			
    			String dept = empData.getdEPT();
    			
    			String roleDesc = empData.getrOLE_DESC(); 
    			
    			String status = empData.getsTATUS();
    			
    			log.info("Passing User Id: "+userId);
    			
    			if (status == null || status.isEmpty()) {
    		        
    				status = ""; // Set it to an empty string if null or empty
    		    }
    			
    			//add users to GROUP_BASE_NAME i.e. p8users,etc
    			
    			String[] groupBaseName = PropertyReader.getProperty("GROUP_BASE_NAME").split(",");//make it dynamic
    			
    			log.info("GROUP_BASE_NAME: "+groupBaseName);
    			
    			//add all users to the base groups
    			
    			for (String value : groupBaseName) {

    				log.info("DMSGroupName: " + value + "\t" + objectStore);
    				
    				if(status.equalsIgnoreCase("Active")){
    					
    					AddUserToGroup(commonDN, ctx, value, userId);
    				
    				}else{
    					
    					log.info(userId + "\t status is either Inactive or empty. Hence user not added to the group "+value+" .");
    			
    				}
    				
    			}
    			
    			log.info("add users to respective LDAP group i.e. DMS_Maker,DMS_Checker");
    			
    			//add users to respective LDAP group i.e. DMS_Maker,DMS_Checker
    			
    			String ldapGroupName = null;
    			
    			ldapGroupName = PropertyReader.getProperty("LDAP_"+roleDesc);
    			
    			if(ldapGroupName != null && !ldapGroupName.isEmpty() && userId != null && !userId.isEmpty()){
    				
    				if(status.equalsIgnoreCase("Active")){
    					
    					AddUserToGroup(commonDN, ctx, ldapGroupName, userId);
    				
    				}else{
    					
    					log.info(userId + "\t status is either Inactive or empty. Hence user not added to the group "+ldapGroupName+" .");
    			
    				}
    				
    				//AddUserToGroup(commonDN, ctx, ldapGroupName, userId);
    			
    			}else{
    				
    				log.info("LDAP Group Name/User ID is null/empty."+ldapGroupName+"\t"+userId);
    				
    			}
    			
        	}
			
		} catch (Exception e) {

			log.error("Exception : "+e.getMessage());
			
			e.printStackTrace();
		
		}
        
    }
    
    static Boolean RemoveAllUsersFromGroup(String commonDN, LdapContext ctx, String groupName)
            throws Exception {
    	log.info("LdapImpl.RemoveAllUsersFromGroup()");
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
        	
        	log.error("Error: " + e.fillInStackTrace());
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
