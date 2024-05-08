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
    "dojo/_base/lang",
    "dojo/dom-construct",
    "dojo/_base/connect",
    "dojo/topic",
	"ecm/widget/layout/_LaunchBarPane",
	"dojo/text!./templates/DatacapReports.html"
],
function(declare,_TemplatedMixin, 
		 _WidgetsInTemplateMixin, 
		 Memory, Desktop, ComboBox,
		 DatePicker,Request,PluginConfigurationPane,
		 MessageDialog,html,date,locale,aspect,lang,domConstruct,connect,topic,
		_LaunchBarPane,
		template) {
	/**
	 * @name datacapReportsPluginDojo.DatacapReports
	 * @class 
	 * @augments ecm.widget.layout._LaunchBarPane
	 */
	var item=null;
	var comboBox;
	var startFromDate;
	var endToDate;
	var th;
	var doccomboBox;
	var objStorecomboBox;
	var self;
	
	function resetFormFields() {
	    self.objStorecomboBox.set('value', '');
	    self.comboBox.set('value', '');
	    
	    self.buttondiv.style.display='none';
		self.reports.style.visibility='hidden';
		self.reports.style.display='none';
		self.tableDates.style.visibility='hidden';
	}
	function clearUI() {
	    // Clear department ComboBox selection
	    if (self.objStorecomboBox) {
	        self.objStorecomboBox.set('value', "");
	        self.objStorecomboBox.store.data=[];
	    }

	    // Clear report type ComboBox selection
	    if (self.comboBox) {
	    	self.comboBox.set('value', "");
	    	self.comboBox.store.data=[];
	    }

	    // Hide or reset other UI elements as needed
	    self.documentdiv.style.visibility = 'hidden';
	    self.documentdiv.style.display = 'none';
	    self.tableDates.style.visibility = 'hidden';
	    self.tableDates.style.display = 'none';
	    self.buttondiv.style.display = 'none';
	}
	return declare("datacapReportsPluginDojo.DatacapReports", [
		_LaunchBarPane
	], {
		/** @lends datacapReportsPluginDojo.DatacapReports.prototype */

		templateString: template,
		
		// Set to true if widget template contains DOJO widgets.
		widgetsInTemplate: true,

		postCreate: function() {
			this.logEntry("postCreate");
			this.inherited(arguments);
			self=this;
			th=this;
			self.doccomboBox=null; 
			this.inherited(arguments);
			html.set(self.objStorelabel,"Department Name: ");
			self.buttondiv.style.display='none';
			self.reports.style.visibility='hidden';
			self.reports.style.display='none';
			self.tableDates.style.visibility='hidden';
			console.log("Datacap OnLogin calling");
			aspect.after(Desktop, "onLogin", lang.hitch(self, function() {
				console.log("inside Datacap OnLogin calling");	
				if(Desktop.id != "admin"){
					var array=new Array();

					array = Desktop.repositories;
					console.log("array Id::::::::::::",Desktop);
					var repository=array[0];
					var repoId = repository.id;
					var userName=Desktop.userId;
					var params = new Object();
					params.userName=userName;
					params.repoId=repoId;
					console.log("about to call the API");
					ecm.model.Request.invokePluginService("DatacapReportsPlugin", "GetEmployeeDetailsService", {
					    requestParams: params,
					    requestCompleteCallback: function (response) {
					        if (response) {
					            if (response.ERROR) {
					                // Handle errors from the service
					                console.error("Error from the GetEmployeeDetailsService service: " + response.ERROR);
					                var errorDialog = ecm.widget.dialog.ErrorDialog();
					    			errorDialog.setTitle("Issue with User Access Service");
					    			errorDialog.setWidth(450);
					    			errorDialog.setMessage("Error from the GetEmployeeDetailsService service. Please check with Administration for more information.");
					    			errorDialog.show();
					            } else {
					                // Process the successful response
					                console.log("Response from the GetEmployeeDetailsService service: ", response);

					                // Access the DEPTNAMES attribute from the response
					                var deptNames = response.DEPTNAMES;
					                self.departments = response.DEPTNAMES;
					                topic.publish("hertz/topic", { name: "lookup event", items: self });
					                // Use deptNames as needed in your JavaScript code
					                console.log("DEPTNAMES: " + deptNames);
					            }
					        } else {
					            // Handle the case when the response is empty
					            console.error("Empty response from the GetEmployeeDetailsService service");
					            var errorDialog = ecm.widget.dialog.ErrorDialog();
				    			errorDialog.setTitle("Error: Issue with GetEmployeeDetailsService");
				    			errorDialog.setWidth(450);
				    			errorDialog.setMessage("Oops! Empty response from the GetEmployeeDetailsService service. Please reach out to the administration for further assistance.");
				    			errorDialog.show();
					        }
					    }
					});
					
				}else{
					console.log("its admin desktop");
				}
				
				
			}));
			
			aspect.after(Desktop, "onLogout", lang.hitch(self, function() {
				clearUI();
			}));
			var handle = topic.subscribe("hertz/topic", function(e) {
				var item = e.items;
				self.deptss=item.departments;
				console.log("item:",item);
				console.log("th::",self.reports.style.visibility)
				var objStore = new Memory({
					data : self.deptss
				});
				self.objStorecomboBox = new ComboBox({
					placeHolder:"Select Department Name",
					store: objStore,
					searchAttr: "name",
					width:"15em"
				}, self.objStoreCombo);
				self.objStorecomboBox.startup();
				
				dojo.connect(self.objStorecomboBox,"onChange", function(objStore) {
					debugger;
					if(self.comboBox != null ){
						self.comboBox.set('value',"");
						self.documentdiv.style.display='none';
						}
					if(self.doccomboBox != null ){
						self.doccomboBox.set('value',"");
						}
					self.documentdiv.style.display='none';
					self.reports.style.visibility='visible';
					self.reports.style.display='block';
				var reportStore = new Memory({
					data: [
					       
					       {name:"Datacap Daily Report"},
					       {name:"DMS Daily Report"},
					       {name:"Datacap Summary Status Report"},
					       {name:"Datacap Monthly Report"},
					       {name:"DMS Monthly Report"}
					       
					       ]
				});

				html.set(self.reports,"Report Type: ");
				if(self.comboBox == null){
				self.comboBox = new ComboBox({

					placeHolder:"Select",
					store: reportStore,
					searchAttr: "name",
					width:"15em"

				}, self.reportsComboBox);

				self.comboBox.startup();
				//console.log("comboBox",th.comboBox);
				}	
				dojo.connect(self.comboBox,"onChange", function(item) {

					//console.log("inside of dates",item);

					self.buttondiv.style.display='block';

					if( item=="Datacap Daily Report"){
						html.set(self.sdate,"Start Date: ");
						html.set(self.edate,"End Date: ");
						self.documentdiv.style.visibility='hidden';
						self.documentdiv.style.display='none';
						if(self.tableDates.style.display='none'){
							self.tableDates.style.visibility = 'visible';
							self.tableDates.style.display='block';
						}
					}else if( item=="DMS Daily Report"){
						html.set(self.sdate,"Start Date: ");
						html.set(self.edate,"End Date: ");
						self.documentdiv.style.visibility='hidden';
						self.documentdiv.style.display='none';
						if(self.tableDates.style.display='none'){
							self.tableDates.style.visibility = 'visible';
							self.tableDates.style.display='block';
						}
					}else if( item=="Datacap Summary Status Report"){

						html.set(self.sdate,"Start Date: ");
						html.set(self.edate,"End Date: ");
						self.documentdiv.style.visibility='hidden';
						self.documentdiv.style.display='none';
						if(self.tableDates.style.display='none'){
							self.tableDates.style.visibility = 'visible';
							self.tableDates.style.display='block';
						}
					}else if( item=="Datacap Monthly Report"){
						html.set(self.sdate,"Start Date: ");
						html.set(self.edate,"End Date: ");
						self.documentdiv.style.visibility='hidden';
						self.documentdiv.style.display='none';
						if(self.tableDates.style.display='none'){
							self.tableDates.style.visibility = 'visible';
							self.tableDates.style.display='block';
						}
					}else if( item=="DMS Monthly Report"){
						html.set(self.sdate,"Start Date: ");
						html.set(self.edate,"End Date: ");
						self.documentdiv.style.visibility='hidden';
						self.documentdiv.style.display='none';
						if(self.tableDates.style.display='none'){
							self.tableDates.style.visibility = 'visible';
							self.tableDates.style.display='block';
						}
					}
						});
					
//				});
				});
			});

			/**
			 * Add custom logic (if any) that should be necessary after the feature pane is created. For example,
			 * you might need to connect events to trigger the pane to update based on specific user actions.
			 */
			
			this.logExit("postCreate");
		},
		fromDateValidation: function() {
			if (this.reportingToDate < this.reportingFromDate) {
				this.reportingToDate.set('value', "");
			}
		},
		toDateValidation: function() {
			if (this.reportingToDate < this.reportingFromDate) {
				alert('To Date cannot be less than From Date' );
				this.reportingFromDate.set('value', "");
				this.reportingToDate.set('value', "");
			}
		},
		
		
		ReportsGeneration: function(){
			console.log("::::: Datacap REPORTS GENERATION :::::");
			this.inherited(arguments);
			var reportType = self.comboBox.value;
			console.log("::::: REPORT TYPE ::::: ",reportType);
			
			if(reportType=="Datacap Daily Report"){
				var start_Date =  this.reportingFromDate.value;
				var end_Date =  this.reportingToDate.value;
				console.log("in  Datacap start_Date::::",start_Date);
				console.log("in  Datacap end_Date::::",end_Date);
				if (!start_Date) {
				    alert("Enter the Start Date");
				} else if (!end_Date) {
				    alert("Enter the End Date");
				}else {
				    var startDate = new Date(start_Date);
				    var endDate = new Date(end_Date);

				    if (startDate.getTime() === endDate.getTime()) {

				        console.log("ERstartdate;;;;;;;;;;;;",start_Date);
						var d = new Date(start_Date);
						d.setMonth(d.getMonth()+1);
						var startDate = new Date(d);
						var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
						var endDate = new Date(end_Date);
						var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
						
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
						serviceParams.repoID=self.objStorecomboBox.value;
						serviceParams.reportName="Datacap Daily Report";
						console.log("-----serviceParams-------------",serviceParams);
						ecm.model.Request.invokePluginService("DatacapReportsPlugin", "DatacapReportDBCheckService",{
							requestParams : serviceParams,
							requestCompleteCallback : function(response) {
								console.log("response::::::::::::::::",response);
								if(response.key=="empty"){
									console.log(":::::::::::inside message dialog::::::::::");
									var errorMessage = new MessageDialog({
										text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us" 
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
									var repoId = self.objStorecomboBox.value;
									//check the below line
									console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
									var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DatacapReportsPlugin&action=DatacapReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
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
				        alert('The start and end dates must be the same day.');
				    }
				
				
				/*if(toDate > fromDate){
					alert('The start and end dates must be the same day.');
				}else{}*/
				}
			}else if(reportType=="DMS Daily Report"){
				var start_Date =  this.reportingFromDate.value;
				var end_Date =  this.reportingToDate.value;
				console.log("in  start_Date::::",start_Date);
				console.log("in  end_Date::::",end_Date);
				if (!start_Date) {
				    alert("Enter the Start Date");
				} else if (!end_Date) {
				    alert("Enter the End Date");
				}else {
				    var startDate = new Date(start_Date);
				    var endDate = new Date(end_Date);

				    if (startDate.getTime() === endDate.getTime()) {

				        console.log("ERstartdate;;;;;;;;;;;;",start_Date);
						var d = new Date(start_Date);
						d.setMonth(d.getMonth()+1);
						var startDate = new Date(d);
						var fromDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
						var endDate = new Date(end_Date);
						var toDate = new Date(endDate.getFullYear(), endDate.getMonth(), endDate.getDate());
						
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
					serviceParams.repoID=self.objStorecomboBox.value;
					serviceParams.reportName=reportType;
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DatacapReportsPlugin", "UserDBReportCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us" 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
								//console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DatacapReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
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
				        alert('The start and end dates must be the same day.');
				    }
				}
			}else if(reportType=="Datacap Summary Status Report"){
				var start_Date =  this.reportingFromDate.value;
				var end_Date =  this.reportingToDate.value;
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
					serviceParams.repoID=self.objStorecomboBox.value;
					serviceParams.reportName=reportType;
					console.log("-----serviceParams-------------",serviceParams);
					ecm.model.Request.invokePluginService("DatacapReportsPlugin", "DatacapReportDBCheckService",{
						requestParams : serviceParams,
						requestCompleteCallback : function(response) {
							console.log("response::::::::::::::::",response);
							if(response.key=="empty"){
								console.log(":::::::::::inside message dialog::::::::::");
								var errorMessage = new MessageDialog({
									text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us" 
								});
								errorMessage.show();
								resetFormFields();
							}
							else if(response.key=="full") {
								console.log("it is full");
								
								var array=new Array();
								array = Desktop.repositories;
								//console.log("array Id::::::::::::",array);
								var repository=array[0];
								var repoId = self.objStorecomboBox.value;
								//check the below line
								console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
								var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DatacapReportsPlugin&action=DatacapReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
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
			}else if(reportType=="Datacap Monthly Report"){
				console.log("executing the Datacap Monthly Report");
				var start_Date = this.reportingFromDate.value;
				var end_Date = this.reportingToDate.value;
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
						console.log("array Id::::::::::::",array);
						var repository=array[0];
						var serviceParams = new Object();
						serviceParams.startDate=startDate;
						serviceParams.endDate=endDate;
						serviceParams.repoID=self.objStorecomboBox.value;
						serviceParams.reportName=reportType;
						console.log("-----serviceParams-------------",serviceParams);
						ecm.model.Request.invokePluginService("DatacapReportsPlugin", "DatacapReportDBCheckService",{
							requestParams : serviceParams,
							requestCompleteCallback : function(response) {
								console.log("response::::::::::::::::",response);
								if(response.key=="empty"){
									console.log(":::::::::::inside message dialog::::::::::");
									var errorMessage = new MessageDialog({
										text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us" 
									});
									errorMessage.show();
									resetFormFields();
								}
								else if(response.key=="full") {
									console.log("it is full");
									
									var array=new Array();
									array = Desktop.repositories;
									//console.log("array Id::::::::::::",array);
									var repository=array[0];
									var repoId = self.objStorecomboBox.value;
									//check the below line
									console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
									var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DatacapReportsPlugin&action=DatacapReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
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
					
				        //end
				    } else {
				    	alert("Please select dates within the same month: 1st and last day or last day of the current month.");
				    }
				}else{
					alert("Choose different start and end dates within the same month.");
				}
				}
//
			}else if(reportType=="DMS Monthly Report"){
				console.log("executing the DMS Monthly Report");
				var start_Date = this.reportingFromDate.value;
				var end_Date = this.reportingToDate.value;
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
						serviceParams.repoID=self.objStorecomboBox.value;
						serviceParams.reportName=reportType;
						console.log("-----serviceParams-------------",serviceParams);
						ecm.model.Request.invokePluginService("DatacapReportsPlugin", "UserDBReportCheckService",{
							requestParams : serviceParams,
							requestCompleteCallback : function(response) {
								console.log("response::::::::::::::::",response);
								if(response.key=="empty"){
									console.log(":::::::::::inside message dialog::::::::::");
									var errorMessage = new MessageDialog({
										text: "We apologize, but it seems there are no results available right now. Please feel free to explore other options with us" 
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
									var repoId = self.objStorecomboBox.value;
									//check the below line
									console.log("Parameters are::::::::::::",Desktop.id,startDate,endDate,repoId,reportType);
									var uri=Desktop.getServicesUrl()+"/plugin.do?desktop="+Desktop.id+"&plugin=DatacapReportsPlugin&action=UserDBReportGenerateService"+"&startDate="+startDate+"&endDate="+endDate+"&repoId="+repoId+"&reportType="+reportType;
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
