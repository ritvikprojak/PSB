require(["dojo/_base/declare",
         "dojo/_base/lang",
         "ecm/widget/layout/CommonActionsHandler",
         "ecm/model/Action",
         "dojo/aspect",
     	"dojo/topic",
     	"dojo/Deferred",
  		"ecm/model/Desktop",
  		"ecm/widget/search/SearchInCriteria","dojo/dom-style",
 		"dojo/dom",
 		"dojo/_base/connect"], 
function(declare, lang,CommonActionsHandler,Action,aspect,topic,Deferred,desktop,SearchInCriteria,domStyle,dom,connect) {		
	/**
	 * Use this function to add any global JavaScript methods your plug-in requires.
	 */
	_self = this;
	_item = null;
	aspect.after(desktop, "onLogin", lang.hitch(this, function() {

		console.log("Desktop.onLogin.entry()");
		
		ecm.model.desktop.preventCreateNewSearch=true;
		
		ecm.model.desktop.preventCreateNewUnifiedSearch=true;
		
    })
			    
	);
aspect.before(ecm.widget.AddContentItemPropertiesPane.prototype, "onCompleteRendering", function() {
		
		this._addContentItemDialog.addContentItemGeneralPane._contentSourceTypeDiv.hidden=true;
		
		
	},true);
aspect.before(ecm.widget.AddContentItemGeneralPane.prototype, "onFileInputChange", function(evet) {
	console.log("onFileInputChange");
	
	//this._addContentItemDialog.addContentItemGeneralPane._contentSourceTypeDiv.hidden=true;
	this._addContentItemDialog.addContentItemGeneralPane._fileInput.multiple = false;
	this._addContentItemDialog.addContentItemGeneralPane._fileInput.accept = ".tiff,.tif,.pdf,.jpeg,.jpg,.png";
	
	var file = this._fileInput.files[0];
	onFileChange(this._addContentItemDialog.addContentItemGeneralPane._fileInput);
	
},true);
	function onFileChange(files) {
		console.log("onFileChange  :  Entry");
		var mimetype = '';
		var message = 'Only PDF, JPEG,JPG,PNG,TIF & TIFF File Format is accepted';
		if(files.files[0] == undefined){
			mimetype = '';
		}else if(files.files[0].type == '' ){
			mimetype = '';
			console.log(" Mime Type  ", mimetype);
		}else{
			//mimetype = files.files[0].type;
			//console.log(" Mime Type  ", mimetype);
			//
			var fileSize = files.files[0].size;
			if(fileSize == 0){
				mimetype = '';
				message = "Please attach the document once again.";
			}else if(fileSize>0){
				mimetype = '';
				fileSize = fileSize / (1024 * 1024 * 1024); //to check GB
				//fileSize = fileSize / (1024 * 1024); // to check MB
				if(fileSize>=1){
					mimetype = '';
					message = "File size is more than 1024 MB. Please upload the below 1024 MB file size."
				}else{
					var hasDouble = hasDoubleExtension(files.files[0].name);
					console.log("hasDouble: ",hasDouble);
					if(hasDouble){
						mimetype = '';
						message = "double .(dot) extension not allowed. Please upload the correct file extension."
					}else{
						
						mimetype = files.files[0].type;
						console.log(" Mime Type  ", mimetype);
					}
				}
			}
			
			
			
		}
		//var mimetype = files.files[0].type;
		//console.log(" Mime Type  ", mimetype);
		if( mimetype == "image/jpeg" || mimetype == "image/jpg" || mimetype == "image/png" || mimetype == "application/pdf"||mimetype == "image/tiff"||mimetype == "image/tif"){
		}
		else{
			
			files.value = "";
			var errorDialog = ecm.widget.dialog.ErrorDialog();
			errorDialog.setTitle("Issue with the attached document");
			errorDialog.setWidth(450);
			errorDialog.setMessage(message);
			errorDialog.show();
			
			
		}
		console.log("onFileChange  :  Exit ");
		
	}
	function hasDoubleExtension(filename) {
		  // Check for double dots
		  if (filename.includes('..')) {
		    return true;
		  }

		  // Check for double extensions
		  var parts = filename.split('.');
		  if (parts.length > 2) {
		    return true;
		  }

		  return false;
		}
	
	
	lang.extend(Action,{
		isEnabled: function(repository, listType, items, teamspace, resultSet) {
			debugger;
			if(items.length != 0 ){
				if(items[0].template != "Folder" && items[0].template != "StoredSearch"){
					console.log("please handle this scenario only");
					var params = new Object();
					var documentInfo = items[0].id;
					var repositoryId = items[0].repository.repositoryId;
					params.docInfo = documentInfo;
					params.repoId = repositoryId;
					console.log("document details : ",params);
					var th = this;
					//debugger;
					

					//if((this.id == "DownloadAsOriginal" || this.id == "DownloadAsPdf" || this.id == "DownloadAll" || this.id == "DownloadAllAsPdf"))    			  
					if(this.id == "CustomDownloadAction")
					{
						//debugger;
						var request = ecm.model.Request.invokePluginServiceSynchronous("PSBSearchPlugin", "GetDocSecurityService", {
							requestParams : params,
							requestCompleteCallback : lang.hitch(th,function(response) {
								console.log("response",response);
						    })
						    });
						if(Boolean(request.isDMS_Download))
						{
							console.log("inside true");
							return th.canPerformAction(repository, items, listType, teamspace, resultSet);
						}
						else
						{
							console.log("inside false")
							return false;
						}
					}
					else
					{
						return this.canPerformAction(repository, items, listType, teamspace, resultSet);
					}
				}
			}else{
				return this.canPerformAction(repository, items, listType, teamspace, resultSet);
			}
			
		}
	});
	
	lang.setObject("customDownloadAction", function(repository, items, callback, teamspace, resultSet, parameterMap) {
		 debugger;
		 _self = items;
			var docDetails = items[0].id;
		 var docSplit = docDetails.split(',');
		 var docId = docSplit[2];
		 var repoId = items[0].repository.repositoryId;
		 var params = new Object();
		 params.docId = docId;
		 params.repoId = repoId;
		 
			ecm.model.Request.invokePluginService("PSBSearchPlugin",
					"WatermarkService", {
						requestParams : params,
						requestCompleteCallback : function(response) {
							console.log("Response from the service:", response);
								_item = response;
					          // Decode the Base64 data
					          var binaryData = atob(response.pdfData);

					          // Create a Uint8Array from the binary data
					          var uint8Array = new Uint8Array(binaryData.length);
					          for (var i = 0; i < binaryData.length; i++) {
					            uint8Array[i] = binaryData.charCodeAt(i);
					          }

					          // Create a Blob from the Uint8Array
					          var blob = new Blob([uint8Array.buffer], { type: "application/pdf" });

					          // Create a temporary URL for the Blob
					          var url = URL.createObjectURL(blob);

					          // Create a temporary <a> element to trigger the download
					          var link = document.createElement("a");
					          link.href = url;
					          link.download = response.filename;

					          // Programmatically trigger the download
					          link.click();

					          // Cleanup after a small delay to ensure the download has started
					          setTimeout(function () {
					            URL.revokeObjectURL(url);

					            // Check if the link element is a child of the document body
					            if (link.parentNode === document.body) {
					              document.body.removeChild(link);
					            }
					          }, 1000);
					          console.log("call the second service here for Audit");
					          var eventStatus = "Download Successful";
					          callSecondService(_self,eventStatus);
					          console.log("call the second service ends here");
					 },
						requestFailedCallback : function(error){
							debugger;
							console.log("error:",error);
							var eventStatus = "Download failed";
							callSecondService(_self,eventStatus);
							callback();
						}
						
					});
		 function callSecondService(_self,eventStatus){
			 debugger;
			 console.log("From second Service");
			 var params = new Object();
			 params.repoId = _self[0].repository.id;
			 params.docName = _self[0].name;
			 params.eventStatus = eventStatus;
			 ecm.model.Request.invokePluginService("PSBSearchPlugin","DownloadAuditService",{
					requestParams:params,
					requestCompleteCallback:function(response){

					}
				});
			 console.log("From second Service Ends");
		 }
		 
		});
});
