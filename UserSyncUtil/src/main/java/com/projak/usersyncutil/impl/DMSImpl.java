package com.projak.usersyncutil.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.filenet.api.collection.CmRoleMemberList;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.security.CmRolePrincipalMember;
import com.filenet.api.security.SecurityPrincipal;
import com.filenet.api.util.Id;
import com.filenet.apiimpl.core.CmRoleMemberImpl;
import com.projak.usersyncutil.util.BulkEmployeeDetails;
import com.projak.usersyncutil.util.CEConnection;
import com.projak.usersyncutil.util.PropertyReader;

public class DMSImpl {

    static Logger log = Logger.getLogger(DMSImpl.class.getName());

    public void syncWithDMS(ArrayList<BulkEmployeeDetails> bulkEmpDetails, String objectStore) {

        log.info("Starts DMSImpl.syncWithDMS()");

        String[] dmsActions = PropertyReader.getProperty("dmsActions").split(",");

        log.debug("DMSActions: " + dmsActions.length);

        // Create lists for users added and not added
        ArrayList<BulkEmployeeDetails> usersAdded = new ArrayList<>();
        ArrayList<BulkEmployeeDetails> usersNotAdded = new ArrayList<>();

        // removing all users from the roles

        for (String value : dmsActions) {

            log.debug("DMSActionRoleName: " + value + "\t" + objectStore);

            removeAllUserFromRole(value, objectStore);

            log.info("******************Users remove successfully******************************");

        }

        log.info("Adding admin users to all roles");

        String[] dmsAdmins = PropertyReader.getProperty("DMS_Admins").split(",");

        // adding admin users to all roles

        if (dmsAdmins != null && dmsAdmins.length > 0) {

            for (String admin : dmsAdmins) {

                log.info("DMSActionRoleName: " + admin);

                addUserToRole(admin, objectStore, "AllRoles");

            }

        } else {

            log.info("DMS_Admin value is empty. Please update the property file");

        }

        // Adding users to the roles and counting
        int usersAddedCount = 0;
        int usersNotAddedCount = 0;

        // adding users to the roles
        for (int i = 0; i < bulkEmpDetails.size(); i++) {

            BulkEmployeeDetails empData = bulkEmpDetails.get(i);

            System.out.println(empData.getdEPT());

            String userId = empData.getuSER_ID();

            String dept = empData.getdEPT();

            String roleDesc = empData.getrOLE_DESC();

            String status = empData.getsTATUS();

            log.info("Passing Role Description: " + roleDesc);

            if (status == null || status.isEmpty()) {

                status = ""; // Set it to an empty string if null or empty
            }

            if (status.equalsIgnoreCase("Active")) {

                // Update the count when adding user to role
                if (addUserToRole(userId, objectStore, roleDesc)) {
                    usersAdded.add(empData);
                    usersAddedCount++;
                } else {
                    usersNotAdded.add(empData);
                    usersNotAddedCount++;
                }

            } else {

                log.info(userId + "\t status is either Inactive or empty. Hence user not added to the Role " + roleDesc
                        + " .");

            }
        }

        // Print counts and JSON objects
        log.info("Users Added Count: " + usersAddedCount);
        log.info("Users Not Added Count: " + usersNotAddedCount);
        log.info("Users Added JSON: " + usersAdded.toString());
        log.info("Users Not Added JSON: " + usersNotAdded.toString());
    }

    public void removeAllUserFromRole(String roleName, String objectStoreName) {

        log.debug("DMSImpl.removeAllUserFromRole()");

        CEConnection ce = new CEConnection();

        Connection ceConnection = ce.establishConnection();

        Domain dom = ce.fetchDomain();

        ObjectStore os = Factory.ObjectStore.fetchInstance(dom, objectStoreName, null);

        System.out.println("connection status is:" + ce.isConnected());

        if (ce.isConnected()) {

            log.info("roleName " + roleName);

            if (!roleName.isEmpty()) {

                Id id = null;

                String role = roleName;

                try {

                    String appRole = PropertyReader.getProperty(role);

                    log.debug("roleName" + appRole);

                    id = new Id(appRole);

                    log.debug("id" + id);

                } catch (Exception e) {

                    log.info("Role doesn't Exist");

                }

                SecurityPrincipal sp = null;
                //
                try {
                    com.filenet.api.security.CmStaticRole rolesStatic = Factory.CmStaticRole.fetchInstance(os, id,
                            null);

                    if (rolesStatic != null) {

                        CmRolePrincipalMember rlist = Factory.CmRolePrincipalMember.createInstance();

                        rlist.set_MemberPrincipal(sp);

                        CmRoleMemberList s = rolesStatic.get_RoleMembers();

                        log.info("RoleMember Size: " + s.size());

                        Boolean found = false;

                        Iterator iter = s.iterator();

                        while (iter.hasNext()) {

                            CmRoleMemberImpl member = (CmRoleMemberImpl) iter.next();

                            iter.remove();

                            found = true;

                            if (iter.hasNext()) {

                                continue;

                            } else {

                                break;

                            }

                        }

                        // System.out.println(" found after for loop " + found);

                        rolesStatic.set_RoleMembers(s);

                        rolesStatic.save(RefreshMode.REFRESH);

                        log.info("After setting size of role members list: " + s.size());

                        if (found) {

                            log.info("User removed successfully");

                        }

                        else {

                            log.info("Users Already Removed");

                        }

                    } else {
                        log.info("Role not found for id: " + id);
                    }
                } catch (com.filenet.api.exception.EngineRuntimeException e) {

                    if (e.getExceptionCode()
                            .equals(com.filenet.api.exception.ExceptionCode.E_OBJECT_NOT_FOUND)) {

                        log.error("Role not found for id:: " + id);

                    } else {

                        log.error("Exception: " + e.getMessage());

                        e.printStackTrace();

                    }
                }
                //
            } else {

                log.info("Inside else loop of removeUserFromRole");

            }

        } else {
            log.info("Please check your filenet connection");
        }

    }

    public boolean addUserToRole(String userName, String objectStoreName, String roleName) {

        log.debug("DMSImpl.addUserToRole()");

        CEConnection ce = new CEConnection();

        Connection ceconnection = ce.establishConnection();

        log.debug("Connection Successful");

        Domain dom = ce.fetchDomain();

        log.debug("dom" + dom.get_Name());

        ObjectStore os = Factory.ObjectStore.fetchInstance(dom, objectStoreName, null);

        log.debug("objectstore" + os.get_DisplayName());

        log.debug("connection status" + ce.isConnected());

        if (ce.isConnected()) {

            log.info("userName:" + userName + "\t ObjectStoreName: " + objectStoreName + "\t roleName " + roleName);

            if (!userName.isEmpty() && !roleName.isEmpty()) {

                log.debug("User Name and role name not empty");

                Id id = null;

                String role = roleName;

                try {

                    log.debug("passing role:" + role);

                    String appRole = PropertyReader.getProperty("FN_" + role);

                    log.debug("appRole" + appRole);

                    StringTokenizer tokenizer = new StringTokenizer(appRole, ",");

                    // Iterate over the tokens
                    while (tokenizer.hasMoreTokens()) {

                        String token = tokenizer.nextToken();

                        log.debug("Token: " + token);

                        id = new Id(PropertyReader.getProperty(token));

                        log.debug("idtoken: " + id);

                        // Connection con = Factory.Connection.getConnection(url);

                        log.debug("con:::" + ceconnection.getURI());

                        log.debug("userName:" + userName);

                        SecurityPrincipal sp = null;

                        try {

                            sp = Factory.SecurityPrincipal.fetchInstance(ceconnection, userName, null);

                            log.info("retrieved the security principal for " + userName);

                        } catch (Exception e) {

                            log.info("User does not Exist" + e.getMessage());

                            // return "user Not Exist";
                        }

                        com.filenet.api.security.CmStaticRole rolesStatic = Factory.CmStaticRole
                                .fetchInstance(os, id, null);

                        // System.out.println("rolesStatic" + rolesStatic);

                        CmRolePrincipalMember rlist = Factory.CmRolePrincipalMember.createInstance();

                        rlist.set_MemberPrincipal(sp);

                        CmRoleMemberList s = ((com.filenet.api.security.CmStaticRole) rolesStatic).get_RoleMembers();

                        String name = rlist.get_MemberPrincipal().getProperties().get("DisplayName").getStringValue();

                        // RoleComparator comp = new RoleComparator();

                        try {

                            // System.out.println("Inside Add Try");

                            s.add(rlist);

                            rolesStatic.set_RoleMembers(s);

                            // System.out.println("After rolesStatic inside try");

                            rolesStatic.save(RefreshMode.REFRESH);

                            return true;

                        } catch (Exception e) {

                            log.info("user already exist" + e.getMessage());

                        }

                    }

                } catch (Exception e) {

                    log.info("Role doesnot exists !!!");
                    // return "role doesn't exist";
                }

            } else {

                log.info("inside else block of addUserToRole");

            }
        } else {

            log.info("please check your filenet connection");
        }

        return false;
    }
}
