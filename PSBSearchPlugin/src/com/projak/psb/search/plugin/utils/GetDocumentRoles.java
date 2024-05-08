package com.projak.psb.search.plugin.utils;

import java.util.Iterator;
import java.util.StringTokenizer;

import com.filenet.api.collection.AccessPermissionList;
import com.filenet.api.collection.CmRoleMemberList;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.security.AccessPermission;
import com.filenet.api.security.CmRole;
import com.filenet.api.security.CmRolePermission;
import com.filenet.api.security.CmRolePrincipalMember;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONObject;

public class GetDocumentRoles {

	public JSONObject getEnabledRoles(String repositoryId,String userId, String docInfo,PluginServiceCallbacks callbacks) {
		System.out.println("GetDocumentRoles.getEnabledRoles()");
		System.out.println(repositoryId+"\t"+userId+"\t"+docInfo);
		boolean isDMS_View = false;
		boolean isDMS_Print = false;
		boolean isDMS_Download = false;
		boolean isDMS_Annotation = false;
		JSONObject docRolesObj = null;
		
		Connection connection = callbacks.getP8Connection(repositoryId);
		ObjectStore objectStore = callbacks.getP8ObjectStore(repositoryId);
		StringTokenizer tokenizer = new StringTokenizer(docInfo, ",");
		String[] strTokenArray = null;
		strTokenArray = new String[tokenizer.countTokens()];
		int count = 0;
		while(tokenizer.hasMoreTokens()){
			strTokenArray[count++] = tokenizer.nextToken();
		}
		System.out.println("docId: "+strTokenArray[2].toString());
		String docId = strTokenArray[2].toString();
		Document document = Factory.Document.fetchInstance(objectStore, docId, null);
		System.out.println(document.get_MimeType());
		System.out.println(document.get_ClassDescription().get_DisplayName());
		
		AccessPermissionList docPerms = document.get_Permissions();
		Iterator itr = docPerms.iterator();
		while (itr.hasNext()) {
			AccessPermission object = (AccessPermission) itr.next();
			if(object instanceof CmRolePermission) {
				System.out.println("Role");
				CmRolePermission permission = (CmRolePermission) object;
				CmRole role = permission.get_Role();
				System.out.println(role.get_Id());
				//System.out.println("RoleName: "+role.get_DisplayName());
				com.filenet.api.security.CmStaticRole rolesStatic = Factory.CmStaticRole.fetchInstance(objectStore, role.get_Id(), null);
				if (role.get_DisplayName()==PSBPropertyReader.getResourceBundle().getString("displayName3")) {
					System.out.println("in DMS_Download");
					isDMS_Download = checkUserExist(rolesStatic,userId);
				}else if (role.get_DisplayName()==PSBPropertyReader.getResourceBundle().getString("displayName4")) {
					System.out.println("in DMS_View");
					isDMS_View = checkUserExist(rolesStatic,userId);
				}
			}else{
				System.out.println("Not Role");
			}
		}
		return docRolesObj;
	}
	private static boolean checkUserExist(com.filenet.api.security.CmStaticRole cm, String userId) {
		System.out.println("GetDocumentRoles.checkUserExist()");
		System.out.println("RoleName-->" + cm.get_DisplayName());

		CmRoleMemberList roleMembersList = cm.get_RoleMembers();
		Iterator iterator = roleMembersList.iterator();
		System.out.println("User Contains" + userId);

		Boolean userExist = false;
		while (iterator.hasNext()) {

			CmRolePrincipalMember crp = (CmRolePrincipalMember) iterator.next();
			System.out.println("user" + crp.get_MemberPrincipal().getProperties().getStringValue("Name"));
			String memberName = crp.get_MemberPrincipal().getProperties().getStringValue(PSBPropertyReader.getResourceBundle().getString("memberName2"));
			System.out.println("usercontains" + memberName.toUpperCase().contains(userId.toUpperCase()));
			if (memberName.toUpperCase().contains(userId.toUpperCase())) {
				userExist = true;
				System.out.println("User Contains true" );

				break;
			}
		}
		return userExist;

	}
}
