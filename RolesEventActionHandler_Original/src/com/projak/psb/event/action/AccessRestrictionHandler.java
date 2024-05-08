package com.projak.psb.event.action;

import java.security.AccessController;
import java.security.Principal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Iterator;

import javax.security.auth.Subject;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.CmRoleMemberList;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.core.Versionable;
import com.filenet.api.engine.EventActionHandler;
import com.filenet.api.events.ObjectChangeEvent;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.property.Properties;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.security.CmRole;
import com.filenet.api.security.CmRolePermission;
import com.filenet.api.security.CmRolePrincipalMember;
import com.filenet.api.util.Id;
import com.projak.psb.event.action.exception.DocAccessException;
import com.projak.psb.event.action.utils.DBUtil;

public class AccessRestrictionHandler implements EventActionHandler {

	@Override
	public void onEvent(ObjectChangeEvent arg0, Id arg1) throws EngineRuntimeException {
		System.out.println("AccessRestrictionHandler.onEvent()");
		// added as part of audit
		String eventStatus = "Create Successful";
		
		String ipAddress = null;
		
		String eventType = null;
		
		String fileName = null;
		
		String userName = null;//for reports
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		System.out.println("Getting the DB connection");
		
		DBUtil dbUtil = new DBUtil();
		
		System.out.println("call db connection");
		
		Connection dbConnection = dbUtil.getDBConnection();
		
		System.out.println("got the db connection");
		 //String objectStoreName = "HOIT";
		
		eventType = "Create";
		
		Subject subject = Subject.getSubject(AccessController.getContext());
		
		Principal principal = subject.getPrincipals().iterator().next();
        
		String currentUserId = principal.getName();
        
		System.out.println("currentUserId: "+currentUserId);//currentUserId: ldapauth.psb.co.in:389/dmstest02
		
		String userId = currentUserId.split("/")[1];
		
		System.out.println("userId:"+userId);
		
		boolean isDMS_Create = false;
		
		Document source = (Document) arg0.get_SourceObject();
		
		source.refresh();
		
		ObjectStore O = source.getObjectStore();

		O.refresh();

		String OS = O.get_SymbolicName();

		System.out.println("RepositoryId/OS : " + OS);
		
		System.out.println("Source Id: "+source.get_Id());
		
		Document document = Factory.Document.fetchInstance(O, source.get_Id(), null);
		
		Properties docProps = document.getProperties();
		
		String solId = docProps.getStringValue("BranchCode");
		
		String branchName = docProps.getStringValue("BranchName");
		
		System.out.println("get_MajorVersionNumber: "+document.get_CurrentVersion().get_MajorVersionNumber());
		
		System.out.println("get_MinorVersionNumber: "+document.get_CurrentVersion().get_MinorVersionNumber());
		
		VersionSeries verSeries = document.get_VersionSeries();
		
		Versionable version = verSeries.get_CurrentVersion();
		
		fileName = docProps.getStringValue("DocumentTitle");
		
		ipAddress = docProps.getStringValue("IP_Address");
		
		userName = docProps.getStringValue("Creator");
		
		if(userName == null || userName.isEmpty()){
			userName = "";
		}
		
		if(ipAddress == null || ipAddress.isEmpty()){
			ipAddress = "";
		}
		
		System.out.println("Status of current version: " + version.get_VersionStatus().toString() +
				   "\n Number of current version: " + version.get_MajorVersionNumber() +"."+ version.get_MinorVersionNumber() );

		Integer versionNum = version.get_MajorVersionNumber();
		
		System.out.println("versionNum: "+versionNum);
		
		AccessPermissionList docPerms = document.get_Permissions();
		
		Iterator itr = docPerms.iterator();
		
		while (itr.hasNext()) {
			
			AccessPermission object = (AccessPermission) itr.next();
		
			if(object instanceof CmRolePermission) {
			
				System.out.println("Role");
				
				CmRolePermission permission = (CmRolePermission) object;
				
				CmRole role = permission.get_Role();
				
				System.out.println(role.get_Id()+"\t"+role.get_DisplayName());
				
				com.filenet.api.security.CmStaticRole rolesStatic = Factory.CmStaticRole.fetchInstance(O, role.get_Id(), null);
				
				System.out.println("StaticRole: "+rolesStatic.get_DisplayName());
				//use the below code for Create instance
				
				if (role.get_DisplayName()=="DMS_Create"||rolesStatic.get_DisplayName().equalsIgnoreCase("DMS_Create")) {
				
					System.out.println("in DMS_Create");
					
					isDMS_Create = checkUserExist(rolesStatic,userId);
					
					System.out.println("isDMS_Create?:"+isDMS_Create);
					
					if(isDMS_Create == true){
					
						//insert the audit data
						//ipAddress = "10.0.0.5";
						
						System.out.println("UserName: "+userName+" FileName:"+fileName+"\t IP is "+ipAddress);
						
						try {
						
							dbUtil.insertDataToAuditTable(dbConnection, userName, branchName, solId,OS, ipAddress, eventStatus, eventType, fileName);
							
							System.out.println("operation completed");
						
						} catch (Exception e) {
						
							System.out.println("Exception occured in db record insertion");
						
						}
						
					}
					
					if(isDMS_Create == false){
					
						if(versionNum>1){
						
							System.out.println("Checkin allowed.");
						
						}else{
						
							//insert the audit data
							
							try {
								
								eventStatus = "Creation failed";
								
//								fileName = docProps.getStringValue("DocumentTitle");
//								
//								System.out.println("FileName:"+fileName);
								
								dbUtil.insertDataToAuditTable(dbConnection, userName, branchName, solId, OS, ipAddress, eventStatus, eventType, fileName);
								
								System.out.println("operation completed");
							
							} catch (Exception e) {
							
								System.out.println("Exception occured in db record insertion"+e.getMessage());
							
							}
							
							throw new DocAccessException(String.valueOf( userId +" is not Allowed for the document creation."));
						}
						//throw new DocAccessException(String.valueOf( userId +" is Not Allowed for the document creation."));
					}
				}
				
			}else{
				System.out.println("Not Role");
			}
		}

	}
	private static boolean checkUserExist(com.filenet.api.security.CmStaticRole cm, String userId) {
		
		System.out.println("CustomRolePermission.checkUserExist()");
		
		System.out.println("RoleName-->" + cm.get_DisplayName());

		CmRoleMemberList roleMembersList = cm.get_RoleMembers();
		
		Iterator iterator = roleMembersList.iterator();
		
		System.out.println("User Contains::" + userId);

		Boolean userExist = false;
		
		while (iterator.hasNext()) {

			CmRolePrincipalMember crp = (CmRolePrincipalMember) iterator.next();
		
			System.out.println("user: " + crp.get_MemberPrincipal().getProperties().getStringValue("Name"));
			
			String memberName = crp.get_MemberPrincipal().getProperties().getStringValue("Name");
			
			System.out.println("usercontains :" + memberName.toUpperCase().contains(userId.toUpperCase()));
			
			if (memberName.toUpperCase().contains(userId.toUpperCase())) {
			
				userExist = true;
				
				System.out.println("User Contains true" );
				
				break;
			
			}
		}
		
		return userExist;

	}

}
