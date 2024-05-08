package com.projak.psb.datacap.reports.plugin.services;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.extension.PluginLogger;
import com.ibm.ecm.extension.PluginService;
import com.ibm.ecm.extension.PluginServiceCallbacks;
import com.ibm.json.java.JSONArray;
import com.projak.psb.datacap.reports.plugin.utils.DatacapDBUtil;
import com.projak.psb.datacap.reports.plugin.utils.GeneratedCsvUtil;
import com.projak.psb.datacap.reports.plugin.utils.UserReportUtil;

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
public class DatacapReportGenerateService extends PluginService {

    private static String DATACAP_GENERATED_DAILY_CSV_NAME = "Datacap_Daily_Report.csv";
    private static String GENERATED_SUMMARY_CSV_NAME = "Datacap_Summary-Report.csv";
    private static String DATACAP_GENERATED_Monthly_CSV_NAME = "Datacap_Monthly_Report.csv";
    private static String CSV_MIME_TYPE = "application/csv";
    
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
		return "DatacapReportGenerateService";
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
		System.out.println("DatacapReportGenerateService.execute()");
		String methodName = "execute";
        PluginLogger log = callbacks.getLogger();
        boolean isDebugEnabled = log.isDebugLogged();
        log.logEntry(this, methodName, (ServletRequest) request);
        Connection dcconnection = null;
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

            DatacapDBUtil util = new DatacapDBUtil();
            dcconnection = util.getDatacapDBConnection(repositoryId);
            
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
            UserReportUtil userReportUtil = new UserReportUtil();
            if(reportType.equalsIgnoreCase("Datacap Daily Report")){
            	dailyReportData = userReportUtil.searchDatacapDailyReport(dcconnection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(dailyReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for Daily Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ DATACAP_GENERATED_DAILY_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("Datacap Summary Status Report")){
            	monthlyReportData = userReportUtil.searchDatacapSummaryReport(dcconnection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(monthlyReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for Summary/ Monthly Report:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ GENERATED_SUMMARY_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else if(reportType.equalsIgnoreCase("Datacap Monthly Report")){
            	userReportData = userReportUtil.searchDatacapMonthlyReport(dcconnection, date, date4,
                        repositoryId);
            	GeneratedCsvUtil generate = new GeneratedCsvUtil();
                String generateCsvString = generate.generateCSV(userReportData, log,reportType,repositoryId);
                System.out.println("generateCsvString for UserReport:: " + generateCsvString);
                response.setContentType(CSV_MIME_TYPE);
                response.setHeader("Content-Disposition", "attachment; filename=" +repositoryId+"_"+ DATACAP_GENERATED_Monthly_CSV_NAME);
                out.print(generateCsvString);
                System.out.println("<<< out.print() >>>");
            }else{
            	System.out.println("Oops! Invalid Report Type.");
            }
        
		} catch (Exception e) {
            e.printStackTrace();
            if (null != out)
                out.print("Exception Response from Server" + e.getMessage());
        }finally {
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
            if (dcconnection != null) {
                try {
                	dcconnection.close();
                    System.out.println("::: Connection closed successfully :::");
                } catch (Exception e) {
                	System.out.println("Exception in closing connection:: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

	}
}
