

//PageName : currentStep [source, target]
//- = dont care
//multiple sources pipe wrapped
//!source or target - only one value as of now
//if step is not there it doesnt show

//Source
//0 - Flickr
//1 - iCloud
//3 - Local Files
//4 - Google
//3000 - Staged App Files
/*
var viewMatrix = {
	"SelectSourceStep" 			: { "0" : ["-","-"]},

	"FlickrAccountSelect_Source": { "1" : ["0", "-"]},     //as source
	"GoogleAccountSelect_Source": { "1" : ["4", "-"]},     //as source - starts oauth2 flow
	"LocalFolderSelectionPage" 	: { "1" : ["3", "-"]},
	"SelectStagedJobStep"		: { "1" : ["3000","-"]},
	"ICloudAlbumWizardPages"	: { "1" : ["1", "-"],

									"2" : ["1", "-"]},		//this will also preview the ablum when its selected using the SelectIcloudAblumStep sub-component
	"SelectAlbumStep"			: { "2" : ["0", "-"]},
	"GoogleSelectAlbum"			: { "2" : ["4", "-"]},
	"SelectTargetStep"			: { "2" : ["|3|3000|", "-"],

								    "3" : ["|0|1|4|", "-"] },
								    
	"FlickrAccountSelect_Target": { "3" : ["|3|3000|", "0"],     //as target
	
									"4" : ["|0|1|4|","0"] },
	
	"GoogleAccountSelect_Target": { "3" : ["|3|3000|", "4"],     //as target
	
									"4" : ["|0|1|4|","4"] },
	
									
	"SetupJob"					: { "4" : ["|3|3000|", "|0|4|"],
	
									"5" : ["|0|1|4|","|0|4|"]},
	"ExecuteJob"				: { //"4" : ["-", "!0"],			//not sure this is valid

									"5" : ["|3|3000|","|0|4|"],
									
									"6" : ["|0|1|4|","|0|4|"] }

	
}

*/

//New Page Matrix
// currentStep : {source : {target : {component}}}
// - = dont care

const pageMatrix = {
	
	0 : {"-" : 
			{"-" : {component: "SelectSourceStep"}
			}
		},
	1 : {0 : 
	  		{"-" : {component: "FlickrAccountSelect", props : {type: "SOURCE"}},
	  		},
	  	 1 :
	  	 	{"-" : {component: "ICloudAlbumWizardPages"},
	  	 	},
	  	 3 : 
	  	 	{"-" : {component: "LocalFolderSelectionPage"},
	  	 	},
	  	 4 : 
	  	 	{"-" : {component: "GoogleAccountSelect", props : {type: "SOURCE"}},
	  	 	},
	  	 3000: 
	  	 	{"-" : {component: "SelectStagedJobStep"}
	  	 	},
		},
	2 : {0 : 
			{"-" : {component: "SelectAlbumsStep"},
			},
		 1 :
			{"-" : {component: "ICloudAlbumWizardPages"},
			},
		 3 : 
		 	{"-" : {component: "SelectTargetStep"},
		 	},
		 4 : 
		 	{"-" : {component: "GoogleSelectAlbum"},
		 	},
		 3000 :
		 	{"-" : {component: "SelectTargetStep"}	
		 	},
		
		},
		
	3 : {0 : 
			{"-" : {component: "SelectTargetStep"},
			},
		 1 :
			{"-" : {component: "SelectTargetStep"},
			},
		 3 : 
		 	{0 : {component: "FlickrAccountSelect", props : {type: "TARGET"}},
		 	 4 : {component: "GoogleAccountSelect", props : {type: "TARGET"}}
		 	},
		 4 : 
		 	{"-" : {component: "SelectTargetStep"},
		 	},
		 3000 :
		 	{0 : {component: "FlickrAccountSelect", props : {type: "TARGET"}},
		 	 4 : {component: "GoogleAccountSelect", props : {type: "TARGET"}}	
		 	},
		 },
	4 : {0 : 
			{0 : {component: "FlickrAccountSelect", props : {type: "TARGET"}},
			 4 : {component: "GoogleAccountSelect", props : {type: "TARGET"}}
			},
		 1 :
			{0 : {component: "FlickrAccountSelect", props : {type: "TARGET"}},
			 4 : {component: "GoogleAccountSelect", props : {type: "TARGET"}}
			},
		 3 : 
		 	{0 : {component: "FlickrAccountSelect", props : {type: "TARGET"}},
		 	 4 : {component: "SetupJob"}
		 	},
		 4 : 
		 	{0 : {component: "FlickrAccountSelect", props : {type: "TARGET"}},
		 	 4 : {component: "GoogleAccountSelect", props : {type: "TARGET"}}
		 	},
		 3000 :
		 	{0 : {component: "FlickrAccountSelect", props : {type: "TARGET"}},
		 	 4 : {component: "SetupJob"}	
		 	},
		 },
	5 : {0 : 
			{0 : {component: "SetupJob"},
			 4 : {component: "SetupJob"}
			},
		 1 :
			{0 : {component: "SetupJob"},
			 4 : {component: "SetupJob"}
			},
		 3 : 
		 	{0 : {component: "ExecuteJob"},
		 	 4 : {component: "ExecuteJob"}
		 	},
		 4 : 
		 	{0 : {component: "SetupJob"},
		 	 4 : {component: "SetupJob"}
		 	},
		 3000 :
		 	{0 : {component: "ExecuteJob"},
		 	 4 : {component: "ExecuteJob"}	
		 	},
		 },
	6 : {0 : 
			{0 : {component: "ExecuteJob"},
			 4 : {component: "ExecuteJob"}
			},
		 1 :
			{0 : {component: "ExecuteJob"},
			 4 : {component: "ExecuteJob"}
			},
		 4 : 
		 	{0 : {component: "ExecuteJob"},
		 	 4 : {component: "ExecuteJob"}
		 	},
		 },
		
	
		
	
	
	};







//
// This is the text titles for the list of steps 
// displayed at the top of the wizard
//
export const stepList = (source) => {
	switch(source)
	{
		case 0: //Flickr
			return(["Select Source", "Select Source Account","Select Albums","Select Target", "Select Target Account", "Create Job","Execute"])

		case 1: //Apple Shared
			return(["Select Source", "Enter Shared Album","Confirm Album","Select Target", "Select Target Account", "Create Job","Execute"])

		
		case 3: //LocalFiles
			return(["Select Source", "Select Files","Select Destination","Authorize Destination", "Create Job","Execute"]);
		
		case 4: //Google
			return(["Select Source", "Select Source Account","Select Photos","Select Target", "Select Target Account", "Create Job","Execute"])

		
		case 3000: //staged files
			return(["Select Source", "Select Staged Batch","Select Destination","Authorize Destination", "Create Job","Execute"]);
		
		default:
			return(["Select Source", "Select User","Select Albums","Create Job","Execute"]);		
	}
}

export const getComponent = (currentStep, source, target) => 
{
	let showLog=true;
	let pmcs;
	let pms;
	let pmt;
	let rv = {found: false, component: null, props: null}
	
	if(showLog==true)
		console.log("getComponent", currentStep, source, target);

	if(!(currentStep in pageMatrix))
	{
		if(showLog==true)
			console.log("getComponent fail 1", currentStep) 
		return rv;
	}
	
	pmcs=pageMatrix[currentStep];
	
	if(source in pmcs)
	{
		pms=pmcs[source];
	}
	else
	{
		if("-" in pmcs)
			pms=pmcs["-"];
		else
		{
			if(showLog==true)
				console.log("getComponent fail 2", currentStep, source) 

			return rv;
		}
	}
	
	
	if(target in pms)
	{
		pmt=pms[target];
	}
	else
	{
		if("-" in pms)
			pmt=pms["-"];
		else
		{
			if(showLog==true)
				console.log("getComponent fail 3", currentStep, source, target) 

			return rv;
		}
	}
	
	rv.found=true;
	rv.component=pmt.component;
	if("props" in pmt && pmt.props!=null)
		rv.props=pmt.props
	
	
	if(showLog==true)
		console.log("getComponent success ", rv);

	return rv;

}

/*
export const shouldShow = (pageName, currentStep, source, target) => {
	
	return true;
	
	let showLog=false;
	
	if(showLog==true)
		console.log("shouldShow", pageName, currentStep, source, target);
	
	let sourceTrue=false;
	let targetTrue=false;
	
	if(!(pageName in viewMatrix))
	{
		if(showLog==true)
			console.log("shouldShow fail 1", pageName) 
		return false;
	}
	
	if( !(currentStep in viewMatrix[pageName]) ) 
	{
		if(showLog==true)
			console.log("shouldShow fail 2", pageName) 
		return false;
	}
	
	if(viewMatrix[pageName][currentStep][0]=="-" || 
	   viewMatrix[pageName][currentStep][0]==source || 
	   viewMatrix[pageName][currentStep][0].includes("|" + source + "|")==true ||
	    (viewMatrix[pageName][currentStep][0].startsWith("!") && 
	     viewMatrix[pageName][currentStep][0]!= "!"+source) )
		sourceTrue=true;
		
	if(viewMatrix[pageName][currentStep][1]=="-" || 
	   viewMatrix[pageName][currentStep][1]==target ||
	   viewMatrix[pageName][currentStep][1].includes("|" + target + "|")==true ||
	   (viewMatrix[pageName][currentStep][1].startsWith("!") &&
	    viewMatrix[pageName][currentStep][1]!= "!"+target ) )
		targetTrue=true;
		
	if(showLog==true)
		console.log("shouldShow", pageName, sourceTrue, targetTrue);
	 
	return sourceTrue && targetTrue;
	
	
	
}
*/