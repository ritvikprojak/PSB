require(["dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/dom-construct",
    "dojo/aspect",
    "dojo/_base/connect",
    "ecm/model/Desktop"], 
function(declare,lang,domConstruct,aspect,connect,desktop) {		
	aspect.after(desktop, "onLogin", lang.hitch(this, function() {
		console.log("ReportDesktop.onLogin.entry()");
		if(desktop.id != "admin"){
			var array=new Array();

			array = desktop.repositories;
			console.log("array Id::::::::::::",desktop);
			var repository=array[0];
			var repoId = repository.id;
			var userName=desktop.userId;
			var params = new Object();
			params.userName=userName;
			params.repoId=repoId;
			
			ecm.model.Request.invokePluginService("DMSReportsPlugin",
					"UserLoginDBService", {
						requestParams : params,
						requestCompleteCallback : function(response) {
							if (response) {
					            if (response.ERROR) {
					                // Handle errors from the service
					                console.error("Error from the service: " + response.ERROR);
					            } else {
					                // Process the successful response
					                console.log("Response from the service: ", response);

					            }
					        } else {
					            // Handle the case when the response is empty
					            console.error("Empty response from the service");
					        }
						}
					});
		}else{
			console.log("admin desktop");
		}
		
		
		
	})
			    
	);
	
	aspect.before(desktop, "logoff", lang.hitch(this, function() {
	    console.log("On LogOut::::::::");
	    if (desktop.id != "admin") {
	        var array = new Array();
	        array = desktop.repositories;
	        console.log("array Id::::::::::::", desktop);
	        var repository = array[0];
	        var repoId = repository.id;
	        var url = this.location.href;
	        console.log("repoId", repoId);
	        userName = desktop.userId;
	        
	        try {
	            var response = ecm.model.Request.invokeSynchronousPluginService("DMSReportsPlugin", "UserLogoffDBService", {
	                userName: userName,
	                repositoryid: repoId,
	                url: url,
	                userAction: "Logoff"
	            });
	            
	            console.log("logoff", response);
	            
	            if (response && response.error) {
	                // Handle errors from the service
	                console.error("Error from the service: " + response.error);
	            } else {
	                // Process the successful response
	                console.log("Response from the service: ", response);
	            }
	        } catch (error) {
	            // Handle any exceptions that occur
	            console.error("An error occurred: " + error.message);
	        }
	    } else {
	        console.log("this is admin desktop.");
	    }
	}));

	aspect.after(ecm.widget.virtualViewer.ViewoneHTMLViewer.prototype, "annotSavedEvent", function(id, text) {
	    console.log("annotSavedEvent");
	    var array = new Array();
	    array = desktop.repositories;
	    console.log("array Id::::::::::::", desktop);
	    var repository = array[0];
	    var repoId = repository.id;
	    console.log("repoId", repoId);
	    userName = desktop.userId;

	    try {
	        var response = ecm.model.Request.invokeSynchronousPluginService("DMSReportsPlugin", "AuditDBService", {
	            userName: userName,
	            repositoryid: repoId,
	            fileName: this.item.name,
	            userAction: "annotSavedEvent"
	        });

	        console.log("AuditDBService response", response);

	        if (response && response.error) {
	            // Handle errors from the service
	            console.error("Error from the service: " + response.error);
	        } else {
	            // Process the successful response
	            console.log("Response from the service: ", response);
	        }
	    } catch (error) {
	        // Handle any exceptions that occur
	        console.error("An error occurred: " + error.message);
	    }
	}, true);

	aspect.after(ecm.widget.virtualViewer.ViewoneHTMLViewer.prototype, "annotSaveFailedEvent", function(id, text) {
	    console.info("annotSaveFailedEvent");
	    var array = new Array();
	    array = desktop.repositories;
	    console.log("array Id::::::::::::", desktop);
	    var repository = array[0];
	    var repoId = repository.id;
	    console.log("repoId", repoId);
	    userName = desktop.userId;
	    
	    try {
	        var response = ecm.model.Request.invokeSynchronousPluginService("DMSReportsPlugin", "AuditDBService", {
	            userName: userName,
	            repositoryid: repoId,
	            fileName: this.item.name,
	            userAction: "annotSaveFailedEvent"
	        });
	        
	        // Check for errors in the response
	        if (response && response.error) {
	            console.error("Error from the service: " + response.error);
	            // Handle the error as needed
	        } else {
	            // Process the successful response
	            console.log("Response from the service: ", response);
	        }
	    } catch (error) {
	        console.error("An error occurred: " + error);
	        // Handle the error as needed
	    }
	    
	}, true);

	
	aspect.after(ecm.widget.virtualViewer.ViewoneHTMLViewer.prototype, "onDocumentLoaded", function() {
	    console.info("onDocumentLoaded");
	    console.log(this.coldTemplateURL);

	    var array = new Array();
	    array = desktop.repositories;
	    console.log("array Id::::::::::::", desktop);
	    var repository = array[0];
	    var repoId = repository.id;
	    console.log("repoId", repoId);
	    userName = desktop.userId;
	    
	    try {
	        var response = ecm.model.Request.invokeSynchronousPluginService("DMSReportsPlugin", "AuditDBService", {
	            userName: userName,
	            repositoryid: repoId,
	            fileName: this.item.name,
	            userAction: "onDocumentLoaded"
	        });
	        
	        // Check for errors in the response
	        if (response && response.error) {
	            console.error("Error from the service: " + response.error);
	            // Handle the error as needed
	        } else {
	            // Process the successful response
	            console.log("Response from the service: ", response);
	        }
	    } catch (error) {
	        console.error("An error occurred: " + error);
	        // Handle the error as needed
	    }
	    
	}, true);

	
	aspect.after(ecm.widget.virtualViewer.ViewoneHTMLViewer.prototype, "documentLoadFailedEvent", function(id, text) {
	    console.info("documentLoadFailedEvent");
	    var array = new Array();
	    array = desktop.repositories;
	    console.log("array Id::::::::::::", desktop);
	    var repository = array[0];
	    var repoId = repository.id;
	    console.log("repoId", repoId);
	    userName = desktop.userId;

	    try {
	        var response = ecm.model.Request.invokeSynchronousPluginService("DMSReportsPlugin", "AuditDBService", {
	            userName: userName,
	            repositoryid: repoId,
	            fileName: this.item.name,
	            userAction: "documentLoadFailedEvent"
	        });

	        // Check for errors in the response
	        if (response && response.error) {
	            console.error("Error from the service: " + response.error);
	            // Handle the error as needed
	        } else {
	            // Process the successful response
	            console.log("Response from the service: ", response);
	        }
	    } catch (error) {
	        console.error("An error occurred: " + error);
	        // Handle the error as needed
	    }

	}, true);

	
	aspect.after(ecm.widget.virtualViewer.ViewoneHTMLViewer.prototype, "printFinishEvent", function(id, text) {
	    console.info("printFinishEvent");
	    var array = new Array();
	    array = desktop.repositories;
	    console.log("array Id::::::::::::", desktop);
	    var repository = array[0];
	    var repoId = repository.id;
	    console.log("repoId", repoId);
	    userName = desktop.userId;

	    try {
	        var response = ecm.model.Request.invokeSynchronousPluginService("DMSReportsPlugin", "AuditDBService", {
	            userName: userName,
	            repositoryid: repoId,
	            fileName: this.item.name,
	            userAction: "printFinishEvent"
	        });

	        // Check for errors in the response
	        if (response && response.error) {
	            console.error("Error from the service: " + response.error);
	            // Handle the error as needed
	        } else {
	            // Process the successful response
	            console.log("Response from the service: ", response);
	        }
	    } catch (error) {
	        console.error("An error occurred: " + error);
	        // Handle the error as needed
	    }

	}, true);


});
