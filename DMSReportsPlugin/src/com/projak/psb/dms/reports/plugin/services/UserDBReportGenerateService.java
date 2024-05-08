package com.projak.psb.dms.reports.plugin.services;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.Subject;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.util.UserContext;
import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.projak.psb.dms.reports.plugin.utils.CEConnection;
import com.projak.psb.dms.reports.plugin.utils.DBOperationsUtil;
import com.projak.psb.dms.reports.plugin.utils.DateConversion;
import com.projak.psb.dms.reports.plugin.utils.GeneratedCsvUtil;
import com.projak.psb.dms.reports.plugin.utils.UserReportUtil;

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
public class UserDBReportGenerateService extends PluginService {

    private static String GENERATED_LOGIN_CSV_NAME = "User_LoginSession_Report.csv";
    private static String GENERATED_AUDIT_CSV_NAME = "Audit_Log_Report.csv";
    private static String GENERATED_DAILY_CSV_NAME = "Daily_Report.csv";
    private static String GENERATED_MONTHLY_CSV_NAME = "Summary-Monthly_Report.csv";
    private static String GENERATED_USER_CSV_NAME = "User_Report.csv";
    private static String GENERATED_DEPTWISE_CSV_NAME = "Department-Wise_Report.csv";
    private static String GENERATED_DEPTWISEMETADATA_CSV_NAME = "Department-Wise-Metadata_Report.csv";
    private static String GENERATED_DATACAP_DAILYDATA_CSV_NAME = "DMS_Daily-Report.csv";
    private static String GENERATED_DATACAP_MONTHLYDATA_CSV_NAME = "DMS_Monthly-Report.csv";
    private static String CSV_MIME_TYPE = "application/csv";

    public String getId() {
        return "UserDBReportGenerateService";
    }

    public String getOverriddenService() {
        return null;
    }

    public void execute(PluginServiceCallbacks callbacks, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        System.out.println("<<< ::: In the UserDBReportGenerateService ::: >>>");
        String methodName = "execute";
        PluginLogger log = callbacks.getLogger();
        boolean isDebugEnabled = log.isDebugLogged();
        log.logEntry(this, methodName, (ServletRequest) request);
        Connection connection = null;
        ResultSet reportData = null;
        PrintWriter out = null;

        try {
            out = response.getWriter();
            String reportType = request.getParameter("reportType");
            System.out.println("reportType:: " + reportType);
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            System.out.println("startDate:: " + startDate);
            System.out.println("endDate:: " + endDate);
            String repositoryId = request.getParameter("repoId");
            System.out.println("repositoryId:: " + repositoryId);
            
            DBOperationsUtil dbOperationsUtil = new DBOperationsUtil();
            connection = dbOperationsUtil.getDBConnection();
            
            String patternChange = "yyyy-MM-dd hh:mm:ss";
            System.out.println("startDate:: " + startDate + " ,endDate:: " + endDate + " & repositoryId:: " + repositoryId);
            SimpleDateFormat simpleDateFormatChange = new SimpleDateFormat(patternChange);
            Date date1 = simpleDateFormatChange.parse(startDate);
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(date1);
            date = date + " 00:00:00";
            Date date2 = simpleDateFormatChange.parse(endDate);
            String pattern1 = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
            String date3 = simpleDateFormat1.format(date2);
            String date4 = date3 + " 23:59:59";
            JSONArray signinReportData = new JSONArray();
            JSONArray auditReportData = new JSONArray();
            JSONArray dailyReportData = new JSONArray();
            JSONArray monthlyReportData = new JSONArray();
            JSONArray userReportData = new JSONArray();
            JSONArray departmentWiseReportData = new JSONArray();
            JSONArray departmentWiseMetadataReportData = new JSONArray();
            JSONArray dmsDailyReportData = new JSONArray();
            JSONArray dmsMonthlyReportData = new JSONArray();
            UserReportUtil userReportUtil = new UserReportUtil();
            if(reportType.equalsIgnoreCase("User Login Session Report")){
            	signinReportData = userReportUtil.searchSignInSignOffReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(signinReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for User Login Session Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_LOGIN_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("Audit Log Report")){
            	auditReportData = userReportUtil.searchAuditLogReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(auditReportData, log,reportType,repositoryId);
                System.out.println("119 generateCsvString for Audit Log Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_AUDIT_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("Daily Report")){
            	dailyReportData = userReportUtil.searchDailyReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(dailyReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for Daily Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_DAILY_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("Summary/ Monthly Report")){
            	monthlyReportData = userReportUtil.searchMonthlyReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(monthlyReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for Summary/ Monthly Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_MONTHLY_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("User Report")){
            	userReportData = userReportUtil.searchUserReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(userReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for UserReport:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_USER_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("Department Wise Report")){
            	departmentWiseReportData = userReportUtil.searchDepartmentWiseReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(departmentWiseReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for Department Wise Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_DEPTWISE_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("Department Wise Metadata Report")){
            	 date = DateConversion.getConvertedDate(date);
    			 date4 = DateConversion.getConvertedDate(date4);
    			 System.out.println("Department Metadata report dates: "+date+"\t"+date4);
    			 ObjectStore objectStore = null;
    			 try {
    				 try {
    	                    Subject subject = callbacks.getP8Subject(repositoryId);
    	                    if (subject != null) {
    	                        UserContext.get().pushSubject(subject);
    	                        objectStore = callbacks.getP8ObjectStore(repositoryId);
    	                        System.out.println("ObjectStoreName: " + objectStore.get_DisplayName());
    	                    } else {
    	                        System.out.println("Subject is null. User may not be authenticated.");
    	                        CEConnection ce = new CEConnection();
    	                        com.filenet.api.core.Connection ceConnection = ce.establishConnection();
    	                		
    	                		Domain dom = ce.fetchDomain();
    	                		
    	                		objectStore = Factory.ObjectStore.fetchInstance(dom, repositoryId, null);
    	                		System.out.println("FileNet ObjectStoreName: " + objectStore.get_DisplayName());
    	                		System.out.println("connection status is:" + ce.isConnected());
    	                    }
    	                } catch (EngineRuntimeException e) {
    	                    System.out.println("EngineRuntimeException in DepartmentWiseMetadataReport Check:" + e.getMessage());
    	                }
    	            	departmentWiseMetadataReportData = userReportUtil.searchDepartmentWiseMetadataReport(connection, date, date4,
    	                        repositoryId,objectStore);
    	            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
    	                String generateCsvString = generate.generateCSV(departmentWiseMetadataReportData, log,reportType,repositoryId);
    	                System.out.println("generateCsvString for Department Wise Metadata Report:: " + generateCsvString);
    	                response.setContentType(CSV_MIME_TYPE);
    	                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_DEPTWISEMETADATA_CSV_NAME);
    	                out.print(generateCsvString);
    	                System.out.println("<<< out.print() >>>");
				} catch (EngineRuntimeException e) {
					System.out.println("EngineRuntimeException in Department Wise Metadata Report: "+e.getMessage());
				}
    			
            }else if(reportType.equalsIgnoreCase("DMS Daily Report")){
            	dmsDailyReportData = userReportUtil.searchDMSDailyReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(dmsDailyReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for Department Wise Metadata Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_DATACAP_DAILYDATA_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("DMS Monthly Report")){
            	dmsMonthlyReportData = userReportUtil.searchDMSMonthlyReport(connection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(dmsMonthlyReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for Department Wise Metadata Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_DATACAP_MONTHLYDATA_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else{
            	System.out.println("Oops! Invalid Report Type.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (null != out)
                out.print("Exception Response from Server" + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                    System.out.println("::: PrintWriter closed successfully :::");
                }
            } catch (Exception e) {
            	System.out.println("Exception in closing printwriter:: " + e.getMessage());
                e.printStackTrace();
            }
            if (reportData != null) {
                try {
                    reportData.close();
                    System.out.println("::: reportData closed successfully :::)");
                } catch (Exception e) {
                	System.out.println("Exception in closing reportData:: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("::: Connection closed successfully :::");
                } catch (Exception e) {
                	System.out.println("Exception in closing connection:: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}