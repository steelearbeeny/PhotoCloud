import React, {memo, useState, useEffect, useRef} from 'react';
import * as Utils from "../Utils/Utils.js";
import * as ShowPage from "../Utils/ShowPage.js";
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import 'bootstrap/dist/css/bootstrap.min.css';
import SearchIcon from '@mui/icons-material/Search';
import Switch from '@mui/material/Switch';
import Alert from '@mui/material/Alert';
import OverlaySpinner from '../Spinner/OverlaySpinner.js';
import Swal from 'sweetalert2';
import SelectAlbumsStep from '../wizard/SelectAlbumsStep.js';
import KeyIcon from '@mui/icons-material/Key';
import { FilePond, registerPlugin } from 'react-filepond';

// Import FilePond styles
import 'filepond/dist/filepond.min.css';
import FilePondPluginImageExifOrientation from 'filepond-plugin-image-exif-orientation';
import FilePondPluginImagePreview from 'filepond-plugin-image-preview';
import 'filepond-plugin-image-preview/dist/filepond-plugin-image-preview.css';

// Register the plugins
//registerPlugin(FilePondPluginImageExifOrientation, FilePondPluginImagePreview);




export default function LocalFolderSelectionPage(props) {
	
	
    	const [userName, setUserName] = useState("");
	const [password, setPassword] = useState("");
	const [userNameMessage, setUserNameMessage] = useState("");
	const [passwordMessage, setPasswordMessage] = useState("");
	const [errorMessage, setErrorMessage] = useState("");
	const [albumUser, setAlbumUser] = useState({});
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	const [albumList, setAlbumList] = useState({});
	const [selectedAlbumIndex, setSelectedAlbumIndex] = useState([]);
	const [authValid, setAuthValid] = useState(false);
	const [files, setFiles]  = useState([]);
	
	
	
	//const prevStepRef = useRef(0);	
	
	var windowHandle=null;
	


	
	useEffect(() => {
    
    //prevStepRef.current = props.currentStep;
  }, [props.currentStep]); //run this code when this changes
	
	
	//3==Local
	//if(props.currentStep!=1 || props.selectedSourceProvider!=3) return null;
	
	//if(!ShowPage.shouldShow("LocalFolderSelectionPage",props.currentStep, props.selectedSourceProvider, props.selectedTargetProvider))
	//	return;
	
	
	console.log("LocalFolder",props,albumUser, albumList)
	
	//props.setNextEnabled(false)
	
	
	//const prevCurrentStep=usePrevious(props.currentStep)
	
	/*
	 const inputChange = (e) => {
	  
		if (!e || !e.target || e.target.value==null)
			return;

	
	
		if(e.target.id=="user-name")
			setUserName(e.target.value)
		
		if(e.target.id=="password")
			setPassword(e.target.value)
	
	
		


	
	
  };


	const keyUpEvent = (e) => {
		
		if(e.key=="Enter")
			searchClick(null);
	}


 
const switchChange = () => {
	
	setSwitchState(!switchState)
};


async function selectDirectory() {
  try {
    const dirHandle = await window.showDirectoryPicker();
    for await (const entry of dirHandle.values()) {
      console.log(entry.name, entry.kind);
    }
  } catch (error) {
    if (error.name !== 'AbortError') {
      console.error(error);
    }
  }
}



const searchClick = (e) => {
	setAlbumUser({});
	
	console.log("Search Click", this)
	//selectDirectory();
	
	 const fileInput = document.getElementById("fileInput");
  const selectedFiles = fileInput.files;
  // Check if any files are selected
  if (selectedFiles.length === 0) {
    alert("Please select at least one file to upload.");
    return;
  }
  
  console.log("selected files",selectedFiles)

};



const loadUserComplete = (u) =>
{
	console.log("LU",u)
	
	if(u!=null && u.hasOwnProperty("returnCode"))
	{
		if(u.returnCode==3)
		{
			props.setNextEnabled(false);
			setAlbumUser(u);
			return;
		}
	}
	
	props.setNextEnabled(true)
	setAlbumUser(u)
};


const loadPrivateUserComplete = (u) =>
{
	console.log("LPU",u, this)
	
	if(u!=null && u.hasOwnProperty("returnCode"))
	{
		if(u.returnCode==3)
		{
			props.setNextEnabled(false);
			setErrorMessage(u.message)
			//setAlbumUser(u);
			return;
		}
	}
	
	if(u!=null && !u.hasOwnProperty("authrequired"))
	{
		props.setNextEnabled(true)
		setAlbumUser(u);
		return;
	}
	
	props.setNextEnabled(true)
	
	
	window.numRequests=0;
	
	//shortPoll({token: u.token, key: u.key}, getNumAuthRequests, setNumAuthRequests);
	
	setTimeout( () => {
		
		shortPoll2({token: u.token, key: u.key});
		
	},1000)
	
	
	
	windowHandle=window.open(u.authUrl)

}


const loadAuthorizedUserComplete = (u) => {
	console.log("LAUC",u)
	setAlbumUser(u)
}



const loadCheckAuthComplete = (u,a) => {
	console.log("CAC",u,a)
	
	    switch(a.returnCode2)
      {
		 	case 0:
			  //token response recieved
			  if(!windowHandle || windowHandle==null)
			  {
				  console.log("Invalid window handle")
			  	setWizardErrorMessage("The Flickr window could not be closed");
			  	}
			  else
			  {
				  console.log("Closing Window",windowHandle);
			  	windowHandle.close();
			  	}
			  	
			  	
			  	
			  Utils.loadData("FlickrServlet",{query : "authorizeduserquery", key: u.key, token: u.token }, loadAuthorizedUserComplete, setWizardErrorMessage, setIsLoading);

			  	
			  //setAlbumUser({id: "testuser", username: "joe tester", numalbums: 100})
		  	break;
		  
		  
		  	case 1:
			  //input token ok - response not recieved yet
			  console.log("waiting",window.numRequests)
			  //setTimeout(() => {shortPoll(obj, getNumAuthRequests, setNumAuthRequests)}, 3000)
			  
			  setTimeout( () => {
		
					shortPoll2(u);
		
				},3000)
			  
			break;
			
			
			case 2:
				//not found - input token not found
				setWizardErrorMessage("There was an error reading the Flickr authentication token. Please try again.");
			break;
			
			
			case 3:
				//Exception thrown
				setWizardErrorMessage("An error occured which processing the Flickr authoeization. " + u.message);
			break;
			
			default:
				setWizardErrorMessage("An unknown error occured which processing the Flickr authoeization.");

			break;	
		  
	  }
	
	
	
}



const shortPoll2 = (obj) => {
	
		console.log("ShortPoll2",obj, window.numRequests)
	
	
	//setNumAuthRequests(numAuthRequests+1)
	let z=window.numRequests;
	window.numRequests=z+1
	
	if(z>5)
	{
		setWizardErrorMessage("We have been waiting too long for the Flickr authorization response. Please try again.")
		return;
	}

	Utils.loadData("FlickrAuth",{query : "checkauth", token: obj.token, key: obj.key }, (x) => loadCheckAuthComplete(obj,x), setWizardErrorMessage, setIsLoading);

}




	
const successCallback = (url, state) => {
	console.log("SuccessCallback",url, state)
	
	if(url.indexOf("listalbums") > 0 && Object.keys(state).length > 0)
		props.setNextEnabled(true);
}
			
			if(props.currentStep==1 && prevStepRef.current==2)
			{
				prevStepRef.current=1;
				setAlbumUser({})
				setAlbumList({});
			}
	
			
			
			if(props.currentStep==2)
			{
					
					
					if(albumUser==null || albumUser.id==null)
					{
						Swal.fire({
						  title: 'Error!',
						  text: 'The Flickr user was not valid.',
						  icon: 'error',
						  confirmButtonText: 'Ok'
						});

						return;
					}
					
					if(albumList!=null && Object.keys(albumList).length < 1 && prevStepRef.current==1)
					{
						console.log("Loading albums");
						prevStepRef.current=2;
						Utils.loadData("FlickrServlet",{query : "listalbums", flickruserid: albumUser.id, isprivate : switchState}, setAlbumList, setWizardErrorMessage, setIsLoading, successCallback);
					}
			}
	
	
	if(Object.keys(albumList).length > 0)
	{
		return <SelectAlbumsStep albumList={albumList} nextEnabledCallback={props.setNextEnabled} />
	}
	*/
	
	const OnProcessFiles = () => 
	{
		//called when all files are done in the queie
		console.log("OnProcessFiles2")
		props.setNextEnabled(true)
	}
	
	const OnWarning = (error, file, status) => 
	{
		
		console.log("OnWarning", error, file, status)
	}
	
	const OnError = (error, file, status) => 
	{
		
		console.log("OnError", error, file, status)
	}
	
	const OnInitFile = (file) => {
		console.log("OnInitFile", file)
	}
	
	const OnAddFileStart = (file) => {
		console.log("OnAddFileStart", file)
	}	
	const OnAddFileProgress = (file, progress)	=> {
		console.log("OnAddFileProgress", file, progress)
	}
	const OnAddFile = (error, file)	=> {
		console.log("OnAddFile", file)
	}
	const OnProcessFileStart = (file)	=> {
		//called for each file
		console.log("OnProcessFileStart", file)
		props.setNextEnabled(false)
	}
	const OnProcessFileProgress = (file, progress)	=> {
		//console.log("OnProcessFileProgress", file, progress)
	}
	
	
	return (
		<Grid container>
		<OverlaySpinner show={isLoading} />
	
			<Grid size={12} mt="20px" mb="20px" >
	{wizardErrorMessage.length > 0 ? <><Alert severity="error">{wizardErrorMessage}</Alert><br /></>: null }

			<Typography variant="h5" align="center" fontWeight="bold">
			Lets find the folders that you&apos;re looking for
			</Typography> 
			<Typography align="center" mb="20px">
			Click the Choose Folders button below to display the filesystem browser and pick the folders you want to liberate			<br />
			</Typography>
			</Grid>
			

			<Grid size={12}>
		
		  <FilePond
                files={files}
                onupdatefiles={setFiles}
                allowMultiple={true}
                maxFiles={3}
                onprocessfiles={OnProcessFiles}
                onwarning={OnWarning}
                onerror={OnError}
                oninitfile={OnInitFile}
                onaddfilestart={OnAddFileStart}
				onaddfileprogress={OnAddFileProgress}
				onaddfile={OnAddFile}
				onprocessfilestart={OnProcessFileStart}
				onprocessfileprogress={OnProcessFileProgress}
                
                server={{
				        url: './',
				        process: {
				            url: 'LocalFile',
				            method: 'POST',
				            withCredentials: false,
				            headers: {},
				            timeout: 30000,
				            onload: null,
				            onerror: null,
				            ondata: (formData) => {
								
								
								
								console.log("FormData", props.jobParameters.uniqueId, formData)
				                formData.append('JOBID', props.jobParameters.uniqueId);
				                props.setNextEnabled(true)
				                //let o = {...props.jobParameters}
				                //o["uniqueId"]=props.uniqueId;
				                //props.setJobParameters(o);

				                return formData;
				            },
				        }
				    }}
                
                
                name="files"
                labelIdle='Drag & Drop your files or <span class="filepond--label-action">Browse</span>'
            />
		
			</Grid>

		</Grid>

	)
	
	
	
	
	
	} // end component
