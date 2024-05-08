define([
	"dojo/_base/declare",
	"dijit/_TemplatedMixin",
    "dijit/_WidgetsInTemplateMixin",
    "dojo/store/Memory",
    "ecm/model/Desktop",
    "dijit/form/ComboBox",
    "ecm/widget/DatePicker",		
    "ecm/model/Request",
    "ecm/widget/admin/PluginConfigurationPane",
    "ecm/widget/dialog/MessageDialog",
    "dojo/html",
    "dojo/date",
    "dojo/date/locale",
    "dojo/aspect",
    "dojo/topic",
	"ecm/widget/layout/_LaunchBarPane",
	"dojo/text!./templates/DMSReports.html"
],
function(declare,
		 _TemplatedMixin, 
		 _WidgetsInTemplateMixin, 
		 Memory, Desktop, ComboBox,
		 DatePicker,Request,PluginConfigurationPane,
		 MessageDialog,html,date,locale,aspect,topic,
		_LaunchBarPane,
		template) {
	/**
	 * @name dMSReportsPluginDojo.DMSReports
	 * @class 
	 * @augments ecm.widget.layout._LaunchBarPane
	 */
	var item=null;
	var comboBox;
	var startFromDate;
	var endToDate;
	var th;
	var _self;
	var doccomboBox;
	var objStorecomboBox;
	
	function resetFormFields() {
		_self.objStorecomboBox.set('value', '');
		_self.comboBox.set('value', '');
	    
		_self.buttondiv.style.display='none';
		_self.reports.style.visibility='hidden';
		_self.reports.style.display='none';
		_self.tableDates.style.visibility='hidden';
	}
	return declare("dMSReportsPluginDojo.DMSReports", [
		_LaunchBarPane
	], {
		/** @lends dMSReportsPluginDojo.DMSReports.prototype */

		templateString: template,
		
		// Set to true if widget template contains DOJO widgets.
		widgetsInTemplate: true,

		postCreate: function() {
			this.logEntry("postCreate");
			this.inherited(arguments);


			_self=this;
			_self.doccomboBox=null; 
			this.inherited(arguments);
			//console.log("DMS this scope is::::::",th);
			html.set(_self.objStorelabel,"Department Name: ");
			_self.buttondiv.style.display='none';
			_self.reports.style.visibility='hidden';
			_self.reports.style.display='none';
			_self.tableDates.style.visibility='hidden';

			var objStore = new Memory({
				data : [
				        {name:"HOIT"},
				        {name:"HOPD"},

				        ]
			});
			
			_self.objStorecomboBox = new ComboBox({
					placeHolder:"Select Department Name",
					store: objStore,
					searchAttr: "name",
					width:"15em"
				}, _self.objStoreCombo);
			_self.objStorecomboBox.startup();
			
			dojo.connect(_self.objStorecomboBox,"onChange", function(objStore) {
				
				if(_self.comboBox != null ){
					_self.comboBox.set('value',"");
					_self.documentdiv.style.display='none';
					}
				if(_self.doccomboBox != null ){
					_self.doccomboBox.set('value',"");
					}
				_self.documentdiv.style.display='none';
				_self.reports.style.visibility='visible';
				_self.reports.style.display='block';
			var reportStore = new Memory({
				data: [
				       {name:"Audit Log Report"},
				       {name:"User Login Session Report"},
				       {name:"Daily Report"},
				       {name:"Summary/ Monthly Report"},
				       {name:"User Report"},
				       {name:"Department Wise Report"},
				       {name:"Department Wise Metadata Report"},
				       ]
			});

			html.set(_self.reports,"Report Type: ");
			if(_self.comboBox == null){
				_self.comboBox = new ComboBox({

				placeHolder:"Select",
				store: reportStore,
				searchAttr: "name",
				width:"15em"

			}, _self.reportsComboBox);

			_self.comboBox.startup();

			}	
			dojo.connect(_self.comboBox,"onChange", function(item) {

				_self.buttondiv.style.display='block';

				if(item=="User Login Session Report"){
					
					html.set(_self.sdate,"Start Date: ");
					html.set(_self.edate,"End Date: ");
					_self.documentdiv.style.visibility='hidden';
					_self.documentdiv.style.display='none';
					if(_self.tableDates.style.display='none'){
						_self.tableDates.style.visibility = 'visible';
						_self.tableDates.style.display='block';
					}
				}else if( item=="Audit Log Report"){
					html.set(_self.sdate,"Start Date: ");
					html.set(_self.edate,"End Date: ");
					_self.documentdiv.style.visibility='hidden';
					_self.documentdiv.style.display='none';
					if(_self.tableDates.style.display='none'){
						_self.tableDates.style.visibility = 'visible';
						_self.tableDates.style.display='block';
					}
				}else if( item=="Daily Report"){
					html.set(_self.sdate,"Start Date: ");
					html.set(_self.edate,"End Date: ");
					_self.documentdiv.style.visibility='hidden';
					_self.documentdiv.style.display='none';
					if(_self.tableDates.style.display='none'){
						_self.tableDates.style.visibility = 'visible';
						_self.tableDates.style.display='block';
					}
				}else if( item=="Summary/ Monthly Report"){
					html.set(_self.sdate,"Start Date: ");
					html.set(_self.edate,"End Date: ");
					_self.documentdiv.style.visibility='hidden';
					_self.documentdiv.style.display='none';
					if(_self.tableDates.style.display='none'){
						_self.tableDates.style.visibility = 'visible';
						_self.tableDates.style.display='block';
					}
				}else if( item=="User Report"){
					html.set(_self.sdate,"Start Date: ");
					html.set(_self.edate,"End Date: ");
					_self.documentdiv.style.visibility='hidden';
					_self.documentdiv.style.display='none';
					if(_self.tableDates.style.display='none'){
						_self.tableDates.style.visibility = 'visible';
						_self.tableDates.style.display='block';
					}
				}else if( item=="Department Wise Report"){

					html.set(_self.sdate,"Start Date: ");
					html.set(_self.edate,"End Date: ");
					_self.documentdiv.style.visibility='hidden';
					_self.documentdiv.style.display='none';
					if(_self.tableDates.style.display='none'){
						_self.tableDates.style.visibility = 'visible';
						_self.tableDates.style.display='block';
					}
				}else if( item=="Department Wise Metadata Report"){
					html.set(_self.sdate,"Start Date: ");
					html.set(_self.edate,"End Date: ");
					_self.documentdiv.style.visibility='hidden';
					_self.documentdiv.style.display='none';
					if(_self.tableDates.style.display='none'){
						_self.tableDates.style.visibility = 'visible';
						_self.tableDates.style.display='block';
					}
				}
					});
				
			}); //ComboBox connect close
		
			
			
			this.logExit("postCreate");
		},
		
		fromDateValidation: function() {
			if (this.reportToDate < this.reportFromDate) {
				this.reportToDate.set('value', "");
			}
		},
		toDateValidation: function() {
			if (this.reportToDate < this.reportFromDate) {
				alert('To Date cannot be less than From Date' );
				this.reportFromDate.set('value', "");
				this.reportToDate.set('value', "");
			}
		},
		ReportsGeneration: function(){
			console.log("::::: DMS REPORTS GENERATION :::::");
			this.inherited(arguments);
			var reportType = _self.comboBox.value;
			console.log("::::: DMS REPORT TYPE ::::: ",reportType);
			
			if(reportType=="User Login Session Report"){
				var start_Date =  this.reportFromDate.value;
				var end_Date =  this.reportToDate.value;
				console.log("in  start_Date&end_Date::::",start_Date+"\t"+end_Date);
				
				if(start_Date==null){
					alert("Enter the StartDate");
				}
				if(end_Date==null){
					alert("Enter the Enddate");
				}
				
				var d = new Date(start_Date);
				d.setMonth(d.getMonth()+1);
				var startDate = new Date(d);
				var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
				var endDate = new Date(end_Date);
				var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
				
				if(toDate > fromDate){
					alert('The gap between the two selected dates must be within one month');
				}else{
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
				//	console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=_self.objStorecomboBox.value;
					serviceParams.reportName="LoginSessionReport";
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DMSReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us." 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
								console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = _self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DMSReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
								
				                    var successMessage = new MessageDialog({
				                        text: "Success! Your report is ready for download."
				                    });
				                    successMessage.show();
				                    
				                    setTimeout(function() {
				                        successMessage.hide();
				                        resetFormFields();
				                    }, 5000);
							}
						}
					});
				}
				
			}else if(reportType=="Audit Log Report"){
				var start_Date =  this.reportFromDate.value;
				var end_Date =  this.reportToDate.value;
				console.log("in  start_Date::::",start_Date);
				console.log("in  end_Date::::",end_Date);
				if(start_Date==null){
					alert("Enter the StartDate");
				}
				if(end_Date==null){
					alert("Enter the Enddate");
				}
				console.log("ERstartdate;;;;;;;;;;;;",start_Date);
				var d = new Date(start_Date);
				d.setMonth(d.getMonth()+1);
				var startDate = new Date(d);
				var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
				var endDate = new Date(end_Date);
				var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
				if(toDate > fromDate){
					alert('The gap between the two selected dates must be within one month.');
				}else{
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
					console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=_self.objStorecomboBox.value;
					serviceParams.reportName="AuditLogReport";
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DMSReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us." 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
					//			console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = _self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DMSReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
								
								var successMessage = new MessageDialog({
			                        text: "Success! Your report is ready for download."
			                    });
			                    successMessage.show();
			                    
			                    setTimeout(function() {
			                        successMessage.hide();
			                        resetFormFields();
			                    }, 5000);
							}
						}
					});		
				}
			}else if(reportType=="Daily Report"){
				var start_Date =  this.reportFromDate.value;
				var end_Date =  this.reportToDate.value;
				console.log("in  start_Date::::",start_Date);
				console.log("in  end_Date::::",end_Date);
				if(start_Date==null){
					alert("Enter the StartDate");
				}
				if(end_Date==null){
					alert("Enter the Enddate");
				}
				console.log("ERstartdate;;;;;;;;;;;;",start_Date);
				var d = new Date(start_Date);
				d.setDate(d.getDate());
				var startDate = new Date(d);
				var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
				var endDate = new Date(end_Date);
				var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
				if(toDate > fromDate){
					alert('The start and end dates must be the same day.');
				}else{
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
					//console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=_self.objStorecomboBox.value;
					serviceParams.reportName="DailyReport";
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DMSReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us." 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
						//		console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = _self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DMSReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
								
								var successMessage = new MessageDialog({
			                        text: "Success! Your report is ready for download."
			                    });
			                    successMessage.show();
			                    
			                    setTimeout(function() {
			                        successMessage.hide();
			                        resetFormFields();
			                    }, 5000);
							}
						}
					});		
				}
			}else if(reportType=="Summary/ Monthly Report"){
				var start_Date = this.reportFromDate.value;
				var end_Date = this.reportToDate.value;
				console.log("in start_Date::::", start_Date);
				console.log("in end_Date::::", end_Date);

				if (start_Date == null || end_Date == null) {
				    alert("Please choose both the start date (1st of the month) and end date (last day of the month).");
				} else {
				    var startDate = new Date(start_Date);
				    var endDate = new Date(end_Date);

				 // Get the current date
				    var currentDate = new Date();
				    
				    // Check if the selected dates are valid (1st and last day of the same month or last day of the current month)
				    if (startDate.getMonth() === endDate.getMonth() && startDate.getFullYear() === endDate.getFullYear() &&startDate.getDate() !== endDate.getDate()) {
				            // Check if it's either the 1st and last day of the same month or last day of the current month
				            if ((startDate.getDate() === 1 && endDate.getDate() === new Date(endDate.getFullYear(), endDate.getMonth() + 1, 0).getDate()) ||
				                (startDate.getMonth() === currentDate.getMonth() && endDate.getDate() === currentDate.getDate())) {// Proceed with your development.

						
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
					//console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=_self.objStorecomboBox.value;
					serviceParams.reportName="SummaryMonthlyReport";
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DMSReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us." 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
								console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = _self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DMSReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
								
								var successMessage = new MessageDialog({
			                        text: "Success! Your report is ready for download."
			                    });
			                    successMessage.show();
			                    
			                    setTimeout(function() {
			                        successMessage.hide();
			                        resetFormFields();
			                    }, 5000);
							}
						}
					});		
			    } else {
			    	alert("Please select dates within the same month: 1st and last day or last day of the current month.");
			    }
			}else{
				alert("Choose different start and end dates within the same month.");
			}
			}
//
		}else if(reportType=="User Report"){
				var start_Date =  this.reportFromDate.value;
				var end_Date =  this.reportToDate.value;
				console.log("in  start_Date::::",start_Date);
				console.log("in  end_Date::::",end_Date);
				if(start_Date==null){
					alert("Enter the StartDate");
				}
				if(end_Date==null){
					alert("Enter the Enddate");
				}
				console.log("ERstartdate;;;;;;;;;;;;",start_Date);
				var d = new Date(start_Date);
				d.setFullYear(d.getFullYear()+1);
				var startDate = new Date(d);
				var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
				var endDate = new Date(end_Date);
				var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
				if(toDate > fromDate){
					alert('The gap between the two selected dates must be within one month.');
				}else{
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
					//console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=_self.objStorecomboBox.value;
					serviceParams.reportName="UserReport";
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DMSReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us." 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
								console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = _self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DMSReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
								
								var successMessage = new MessageDialog({
			                        text: "Success! Your report is ready for download."
			                    });
			                    successMessage.show();
			                    
			                    setTimeout(function() {
			                        successMessage.hide();
			                        resetFormFields();
			                    }, 5000);
							}
						}
					});		
				}
			}else if(reportType=="Department Wise Report"){
				var start_Date =  this.reportFromDate.value;
				var end_Date =  this.reportToDate.value;
				console.log("in  start_Date::::",start_Date);
				console.log("in  end_Date::::",end_Date);
				if(start_Date==null){
					alert("Enter the StartDate");
				}
				if(end_Date==null){
					alert("Enter the Enddate");
				}
				console.log("ERstartdate;;;;;;;;;;;;",start_Date);
				var d = new Date(start_Date);
				d.setFullYear(d.getFullYear()+1);
				var startDate = new Date(d);
				var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
				var endDate = new Date(end_Date);
				var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
				if(toDate > fromDate){
					alert('The gap between the two selected dates must be within one month.');
				}else{
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
					//console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=_self.objStorecomboBox.value;
					serviceParams.reportName="DepartmentWiseReport";
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DMSReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us." 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
								console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = _self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DMSReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
								
								var successMessage = new MessageDialog({
			                        text: "Success! Your report is ready for download."
			                    });
			                    successMessage.show();
			                    
			                    setTimeout(function() {
			                        successMessage.hide();
			                        resetFormFields();
			                    }, 5000);
							}
						}
					});		
				}
			}else if(reportType=="Department Wise Metadata Report"){
				var start_Date =  this.reportFromDate.value;
				var end_Date =  this.reportToDate.value;
				console.log("in  start_Date::::",start_Date);
				console.log("in  end_Date::::",end_Date);
				if(start_Date==null){
					alert("Enter the StartDate");
				}
				if(end_Date==null){
					alert("Enter the Enddate");
				}
				console.log("ERstartdate;;;;;;;;;;;;",start_Date);
				var d = new Date(start_Date);
				d.setFullYear(d.getFullYear()+1);
				var startDate = new Date(d);
				var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
				var endDate = new Date(end_Date);
				var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
				if(toDate > fromDate){
					alert('The gap between the two selected dates must be within one month.');
				}else{
					startDate = dojo.date.locale.format(start_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("startDate;;;;;;;;;;;;",startDate);
					endDate = dojo.date.locale.format(end_Date, {
						datePattern: "yyyy-MM-dd HH:mm:ss",
						selector: "date"
					});
					console.log("endDate;;;;;;;;;;;;",endDate);
					console.log("-----ReportsGeneration-------------");
					var array=new Array();
					array = Desktop.repositories;
					//console.log("array Id::::::::::::",array);
					var repository=array[0];
					var serviceParams = new Object();
					serviceParams.startDate=startDate;
					serviceParams.endDate=endDate;
					serviceParams.repoID=_self.objStorecomboBox.value;
					serviceParams.reportName="DepartmentWiseMetadataReport";
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DMSReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us." 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
						//		console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = _self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DMSReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
								console.log("url::::",uri);
								var appendSecurityToken=Request.appendSecurityToken(uri);
								console.log("appendSecurityToken::::::::::::",appendSecurityToken);
								window.open(appendSecurityToken);
								
								var successMessage = new MessageDialog({
			                        text: "Success! Your report is ready for download."
			                    });
			                    successMessage.show();
			                    
			                    setTimeout(function() {
			                        successMessage.hide();
			                        resetFormFields();
			                    }, 5000);
							}
						}
					});		
				}
			}
		},
		/**
		 * Optional method that sets additional parameters when the user clicks on the launch button associated with 
		 * this feature.
		 */
		setParams: function(params) {
			this.logEntry("setParams", params);
			
			if (params) {
				
				if (!this.isLoaded && this.selected) {
					this.loadContent();
				}
			}
			
			this.logExit("setParams");
		},

		/**
		 * Loads the content of the pane. This is a required method to insert a pane into the LaunchBarContainer.
		 */
		loadContent: function() {
			this.logEntry("loadContent");
			
			if (!this.isLoaded) {
				/**
				 * Add custom load logic here. The LaunchBarContainer widget will call this method when the user
				 * clicks on the launch button associated with this feature.
				 */
				this.isLoaded = true;
				this.needReset = false;
			}
			
			this.logExit("loadContent");
		},

		/**
		 * Resets the content of this pane.
		 */
		reset: function() {
			this.logEntry("reset");
			
			/**
			 * This is an option method that allows you to force the LaunchBarContainer to reset when the user
			 * clicks on the launch button associated with this feature.
			 */
			this.needReset = false;
			
			this.logExit("reset");
		}
	});
});
