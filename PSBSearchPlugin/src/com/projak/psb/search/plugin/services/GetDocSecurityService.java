package com.projak.psb.search.plugin.services;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.ecm.json.JSONResponse;
import com.ibm.json.java.JSONObject;
import com.projak.psb.search.plugin.utils.PSBPropertyReader;

/**
 * Provides an abstract class that is extended to create a class implementing
 * each service provided by the plug-in. Services are actions, similar to
 * servlets or Struts actions, that perform operations on the IBM Content
 * Navigator server. A service can access content server application programming
 * interfaces (APIs) and Java EE APIs.
 * <p>
 * Services are invoked from the JavaScript functions that are defined for the
 * plug-in by using the <code>ecm.model.Request.invokePluginService</code>
 * function.
 * </p>
 * Follow best practices for servlets when implementing an IBM Content Navigator
 * plug-in service. In particular, always assume multi-threaded use and do not
 * keep unshared information in instance variables.
 */
public class GetDocSecurityService extends PluginService {

	/**
	 * Returns the unique identifier for this service.
	 * <p>
	 * <strong>Important:</strong> This identifier is used in URLs so it must
	 * contain only alphanumeric characters.
	 * </p>
	 * 
	 * @return A <code>String</code> that is used to identify the service.
	 */
	public String getId() {
		return "GetDocSecurityService";
	}

	/**
	 * Returns the name of the IBM Content Navigator service that this service
	 * overrides. If this service does not override an IBM Content Navigator
	 * service, this method returns <code>null</code>.
	 * 
	 * @returns The name of the service.
	 */
	public String getOverriddenService() {
		return null;
	}

	/**
	 * Performs the action of this service.
	 * 
	 * @param callbacks
	 *            An instance of the <code>PluginServiceCallbacks</code> class
	 *            that contains several functions that can be used by the
	 *            service. These functions provide access to the plug-in
	 *            configuration and content server APIs.
	 * @param request
	 *            The <code>HttpServletRequest</code> object that provides the
	 *            request. The service can access the invocation parameters from
	 *            the request.
	 * @param response
	 *            The <code>HttpServletResponse</code> object that is generated
	 *            by the service. The service can get the output stream and
	 *            write the response. The response must be in JSON format.
	 * @throws Exception
	 *             For exceptions that occur when the service is running. If the
	 *             logging level is high enough to log errors, information about
	 *             the exception is logged by IBM Content Navigator.
	 */
	public void execute(PluginServiceCallbacks callbacks,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("GetDocSecurityService.execute()");
		String repositoryId = request.getParameter("repoId");
		System.out.println("repositoryId: "+repositoryId);
		boolean isDMS_Download = false;
		boolean isDMS_View = false;
		Connection connection = callbacks.getP8Connection(repositoryId);
		ObjectStore objectStore = callbacks.getP8ObjectStore(repositoryId);
		System.out.println("ObjectStoreName: "+objectStore.get_DisplayName()+"\t"+objectStore.get_SymbolicName());
		String userId = request.getRemoteUser();
		String docInfo = request.getParameter("docInfo");
		System.out.println("docInfo: "+docInfo);
		//String object = "Audit,{25072E22-E0AD-469D-A194-D21C3A737F64},{306C3687-0000-CF13-AA00-B73A3A315517}";
		StringTokenizer tokenizer = new StringTokenizer(docInfo, ",");
		String[] strTokenArray = null;
		strTokenArray = new String[tokenizer.countTokens()];
		int count = 0;
		while(tokenizer.hasMoreTokens()){
			strTokenArray[count++] = tokenizer.nextToken();
		}
		System.out.println("docId: "+strTokenArray[2].toString());
		String docId = strTokenArray[2].toString();
		System.out.println(docId);
		Document document = Factory.Document.fetchInstance(objectStore, docId, null);
		System.out.println(document.get_MimeType());
		System.out.println(document.get_ClassDescription().get_DisplayName());
		
		AccessPermissionList docPerms = document.get_Permissions();
		Iterator itr = docPerms.iterator();
		while (itr.hasNext()) {
			AccessPermission object = (AccessPermission) itr.next();
			if(object instanceof CmRolePermission) {
				try {
					System.out.println("Inside try catch");
					System.out.println("Role");
					CmRolePermission permission = (CmRolePermission) object;
					CmRole role = permission.get_Role();
					System.out.println("Checking the role");
					if(role!=null){
						System.out.println("Role Id is: "+role.get_Id());
						//System.out.println("RoleName: "+role.get_DisplayName());
						com.filenet.api.security.CmStaticRole rolesStatic = Factory.CmStaticRole.fetchInstance(objectStore, role.get_Id(), null);
						if (role.get_DisplayName().equalsIgnoreCase(PSBPropertyReader.getResourceBundle().getString("displayName"))) {
							System.out.println("in DMS_Download");
							isDMS_Download = checkUserExist(rolesStatic,userId);
							System.out.println("isDMS_Download?:"+isDMS_Download);
						}else if (role.get_DisplayName().equalsIgnoreCase(PSBPropertyReader.getResourceBundle().getString("displayName2"))) {
							System.out.println("in DMS_View");
							isDMS_View = checkUserExist(rolesStatic,userId);
						}
					}else{
						System.out.println("Role is null");
					}
					
				} catch (Exception e) {
					System.out.println("Exception Occured in Role checking: "+e.getMessage());
				}
				
			}else{
				System.out.println("Not Role");
			}
		}
		PrintWriter writer = response.getWriter();

		JSONResponse responseJ = new JSONResponse();

		JSONObject jobj = new JSONObject();

		responseJ.put(PSBPropertyReader.getResourceBundle().getString("responseJkey"), isDMS_Download);
		responseJ.put(PSBPropertyReader.getResourceBundle().getString("responseJkey2"), isDMS_View);
		
		System.out.println("Final Response: "+responseJ);
		
		try {

			writer.write(responseJ.toString());

		} catch (Exception e) {

			System.out.println(e.fillInStackTrace());

		} finally {

			writer.close();

		}

	}
	
	private static boolean checkUserExist(com.filenet.api.security.CmStaticRole cm, String userId) {
		System.out.println("GetDocSecurityService.checkUserExist()");
		System.out.println("RoleName--" + cm.get_DisplayName());

		CmRoleMemberList roleMembersList = cm.get_RoleMembers();
		Iterator iterator = roleMembersList.iterator();
		System.out.println("User Contains" + userId);

		Boolean userExist = false;
		while (iterator.hasNext()) {

			CmRolePrincipalMember crp = (CmRolePrincipalMember) iterator.next();
			System.out.println("user" + crp.get_MemberPrincipal().getProperties().getStringValue("Name"));
			String memberName = crp.get_MemberPrincipal().getProperties().getStringValue(PSBPropertyReader.getResourceBundle().getString("memberName"));
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
