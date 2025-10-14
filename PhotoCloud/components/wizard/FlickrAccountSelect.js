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
//import SelectAlbumsStep from '../wizard/SelectAlbumsStep.js';
import KeyIcon from '@mui/icons-material/Key';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';




export default function FlickrAccountSelect(props) {
	
	
    	const [userName, setUserName] = useState("");
	const [password, setPassword] = useState("");
	const [userNameMessage, setUserNameMessage] = useState("");
	const [passwordMessage, setPasswordMessage] = useState("");
	const [switchState, setSwitchState] = useState(false);
	const [errorMessage, setErrorMessage] = useState("");
	//const [albumUser, setAlbumUser] = useState({});
	const [sourceUser, setSourceUser] = useState({});
	const [targetUser, setTargetUser] = useState({});
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	const [albumList, setAlbumList] = useState({});
	const [selectedAlbumIndex, setSelectedAlbumIndex] = useState([]);
	const [selectionType, setSelectionType] = useState("");
	
	
	//const prevStepRef = useRef(0);	//Changing this doesnt force a re-render
	
	var windowHandle=null;
	
	var isXSMedia=Utils.IsXSMedia();

	
	useEffect(() => {
    
    //prevStepRef.current = props.currentStep;
  }, [props.currentStep]); //run this code when this changes
	
	
	
	
	/*
	//0==FLICKR
	if(props.currentStep<1 || 
		props.currentStep > 2 || 
		props.selectedSourceProvider!=0) 
			return null;
	*/
	
	let sType="";
	/*
	if( (props.currentStep==1 || props.currentStep==2) &&
		props.selectedSourceProvider==0) 
		sType="SOURCE";
	*/
	/*
	if( (props.currentStep==1) &&
		props.selectedSourceProvider==0) 
		sType="SOURCE";
	
	if(props.currentStep==3 && props.selectedTargetProvider==0 )
		sType="TARGET";
	*/
		
	console.log("FlickrAccountSelect", props)
	/*
	if(ShowPage.shouldShow("FlickrAccountSelect_Source",props.currentStep, props.selectedSourceProvider, props.selectedTargetProvider))	
		sType="SOURCE"
		
		//console.log("STYPE SRC", sType)	
		
	if(ShowPage.shouldShow(
		"FlickrAccountSelect_Target",
		props.currentStep, 
		props.selectedSourceProvider, 
		props.selectedTargetProvider))	
		{
			
				sType="TARGET";
				
				if("id" in targetUser)
					props.setNextEnabled(true);
				
				
		}

	*/
	
	sType=props.type;

	//console.log("STYPE TGT", sType)	
	
	if(sType.length <= 1) return;
	
	
	//console.log("FlickrAlbum",sType, props.currentStep, prevStepRef,sourceUser, targetUser, albumList)
	
	
	
	
	//const prevCurrentStep=usePrevious(props.currentStep)
	
	
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
	

	let o={...props.jobParameters}
	
	if(sType=="SOURCE")
	{
		delete o["sourceUser"];
		o["sourceIsPrivate"]=!switchState
		setSourceUser({})
	}
	else
	{
		//Should not enter this block
		//because switch is not shown on target
		delete o["targetUser"];
		o["targetIsPrivate"]=true
		setTargetUser({});
	}	
	
	
	//delete o["albumUser"]
	//o["isPrivate"]=!switchState
	props.setJobParameters(o);
	setSwitchState(!switchState)
	//setAlbumUser({})
	
};


const searchClick = (e) => {
	//setAlbumUser({});
	
	let o={...props.jobParameters}
	
	if(sType=="SOURCE")
	{
		delete o["sourceUser"];
		o["sourceIsPrivate"]=switchState
		setSourceUser({})
	}
	else
	{
		delete o["targetUser"];
		o["targetIsPrivate"]=true;
		setTargetUser({});
	}	
	
	
	
	//delete o["albumUser"]
	//o["isPrivate"]=switchState
	props.setJobParameters(o);
	
	console.log("Search Click", this)

	Utils.loadData(
		"FlickrServlet",
		{
			query : "finduser", 
			flickrusername: userName, isprivate : (sType=="SOURCE" ? switchState : true) 
		}, 
		((sType=="SOURCE" && switchState==false) ? loadUserComplete : loadPrivateUserComplete), 
		setWizardErrorMessage, 
		setIsLoading);

};



const loadUserComplete = (u) =>
{
	console.log("LU",u)
	let o = {...props.jobParameters}
	
	
	if(u!=null && u.hasOwnProperty("returnCode"))
	{
		if(u.returnCode==3)
		{
			props.setNextEnabled(false);
			
			if(sType=="SOURCE")
			{
				o["sourceUser"]=u
				o["sourceIsPrivate"]=switchState
				setSourceUser(u)
			}
			else
			{
				o["targetUser"]=u;
				o["targetIsPrivate"]=true;
				setTargetUser(u);
			}	
			
			
			//setAlbumUser(u);
			//o["albumUser"]=u;
			//o["isPrivate"]=switchState;
			props.setJobParameters(o);
			return;
		}
	}
	
	props.setNextEnabled(true)
	
	if(sType=="SOURCE")
	{
		o["sourceUser"]=u
		o["sourceIsPrivate"]=switchState
		setSourceUser(u)
	}
	else
	{
		o["targetUser"]=u;
		o["targetIsPrivate"]=true;
		setTargetUser(u);
	}	

	props.setJobParameters(o);
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

		//setAlbumUser(u);
		let o = {...props.jobParameters}
		
		if(sType=="SOURCE")
		{
			o["sourceUser"]=u
			o["sourceIsPrivate"]=switchState
			setSourceUser(u)
		}
		else
		{
			o["targetUser"]=u;
			o["targetIsPrivate"]=true;
			setTargetUser(u);
		}	
		
		
		//o["albumUser"]=u;
		//o["isPrivate"]=switchState;
		props.setJobParameters(o);
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
	//setAlbumUser(u)
	let o={...props.jobParameters}
	
	if(sType=="SOURCE")
	{
		o["sourceUser"]=u
		o["sourceIsPrivate"]=switchState
		setSourceUser(u)
	}
	else
	{
		o["targetUser"]=u;
		o["targetIsPrivate"]=true;
		setTargetUser(u);
	}	
	
	//o["albumUser"]=u;
	//o["isPrivate"]=switchState;
	props.setJobParameters(o);
	
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
} ///end successCallbackk


			//Back pressed ?
			/*
			if(props.currentStep==1 && prevStepRef.current==2)
			{
				prevStepRef.current=1;
				let o={...props.jobParameters}
				
				//delete o["albumUser"]
				//o["isPrivate"]=switchState
				
				
				if(sType=="SOURCE")
				{
					delete o["sourceUser"]
					o["sourceIsPrivate"]=switchState
					setSourceUser({})
					setAlbumList({});
				}
				else
				{
					delete o["targetUser"]
					o["targetIsPrivate"]=true;
					setTargetUser({});
				}	
				
				
				//setAlbumUser({})
				props.setJobParameters(o);
				
			}
			*/
			
			
			if(props.currentStep==2)
			{
					
					if((sType=="SOURCE" && (sourceUser==null || sourceUser.id==null)) ||
					   (sType=="TARGET" && (targetUser==null || targetUser.id==null)))
					{
						
							Swal.fire({
							  title: 'Error!',
							  text: 'The Flickr user was not valid.',
							  icon: 'error',
							  confirmButtonText: 'Ok'
							});
	
							return;
						
					}
					
					
					/*
					if(albumList!=null && Object.keys(albumList).length < 1 && prevStepRef.current==1)
					{
						console.log("Loading albums");
						prevStepRef.current=2;
						Utils.loadData("FlickrServlet",{query : "listalbums", flickruserid: albumUser.id, isprivate : switchState}, setAlbumList, setWizardErrorMessage, setIsLoading, successCallback);
					}
					*/
			}
	
	/*
	if(Object.keys(albumList).length > 0 && sType=="SOURCE")
	{
		return <SelectAlbumsStep albumList={albumList} nextEnabledCallback={props.setNextEnabled} />
	}
	*/
	
	return (
		<Grid container>
		<OverlaySpinner show={isLoading} />
	
			<Grid size={12} mt="20px" mb="20px" >
				{wizardErrorMessage.length > 0 ? <><Alert severity="error">{wizardErrorMessage}</Alert><br /></>: null }
				
				{ (sType=="SOURCE" && ("message" in sourceUser) == true)  ? 
 						<Grid size={12}><Alert severity="warning">{sourceUser.message}</Alert></Grid> : 
 					null }
				
				{ (sType=="TARGET" && ("message" in targetUser) == true) ? 
 						<Grid size={12}><Alert severity="warning">{targetUser.message}</Alert></Grid> : 
 					null }
				
				
				{sType=="SOURCE" ?
				<>
				<Typography variant="h5" align="center" fontWeight="bold">
					Lets find the albums that you&apos;re looking for
				</Typography> 
				<Typography align="center" mb="20px">
					You dont need to provide login information for albums that are publically viewable. 
					So the easiest way to get started, is to make the ablums you want to transfer publically viewable.
					Dont worry, once youre done transferring the photos, you can make the ablum private again.
					<br />
					If you dont want to make your ablums public, you will have to provide your Flickr API Key and Shared Secret information below.
					We take your security seriously and promise not to store your login information after the transfer.
					Its also ok if you want to use a tempoary password and change it afterwards.
				</Typography>
				</> :
				<>
				
				<Typography variant="h5" align="center" fontWeight="bold">
					Tell us the Flickr account you want to load these pictures to
				</Typography> 
				<Typography align="center" mb="20px">
					If you want to save photos to your Flickr account, you must authorize FotoFreedom to connect to your account.
					Don&apos;t worry, you don&apos;t need to provide your passwors to us.
					In the next steps, you will be forwarded to a Flickr login page where you can authorize this app to connect.
					Once you&apos;re done, you can remove the authorization for this app.
				</Typography>
				
				</>}
				
			</Grid>
			
			{sType=="SOURCE" ?
			<>
			<Grid size={1} />
			<Grid size={11}>
				<FormControlLabel control={<Switch checked={switchState} onChange={switchChange} />} label="Access Your Private Flickr Account" />
			</Grid>
			</> : null}
			

			{ (sType=="SOURCE" && ("id" in sourceUser) == false) || 
			  (sType=="TARGET" && ("id" in targetUser) == false) ?

				switchState == false && sType=="SOURCE" ?
					<>
						<Grid size={12}><Typography variant="h6" align="center" >Access Public Flickr Albums</Typography></Grid>
						<Grid size={{xs: 0 , sm:1}} />
						<Grid size={{xs:12,sm:7}}>
							<TextField
								margin="normal"
								required
								fullWidth
								id="user-name"
								label="User Name or Email Address"
								name="user-name"
								autoComplete="email"
								autoFocus
								value={userName}
								onChange={inputChange}
								onKeyUp={keyUpEvent}
								error={userNameMessage.length > 0 ? true : false}
								helperText={userNameMessage}
							/>
						</Grid>

						<Grid size={{xs:12,sm :4}}>
							<Button startIcon={<SearchIcon />} onClick={searchClick} variant="contained" sx={{ m: "10px", mt: "25px" }}>Find Public User and Albums</Button>
						</Grid>

					</> :

					isXSMedia==true ? 
					
					<>
						
	
						<Grid size={12}
							sx={{ display: "flex", alignItems: "center" }}>
							<Typography sx={{ cursor: "pointer" }} onClick={searchClick} variant="h5" align="center" fontWeight="bold">
								Click Here to Connect to Your Private Flickr Albums
							</Typography>
						</Grid>
						
						<Grid size={12}>
							<img src="images/auth.png" onClick={searchClick} style={{ cursor: "pointer", margin: "10px" }} alt="Click to authorize Flickr" />
						</Grid>
						
					</>
					
					:
					<>
						<Grid size={{xs:0, sm:2}} />
						<Grid size={{xs:12, sm:2}}>
							<img src="images/auth.png" onClick={searchClick} style={{ cursor: "pointer", margin: "10px" }} alt="Click to authorize Flickr" />
						</Grid>
						<Grid size={{xs:12, sm:7}}
							sx={{ display: "flex", alignItems: "center" }}>
							<Typography sx={{ cursor: "pointer" }} onClick={searchClick} variant="h5" align="center" fontWeight="bold">
								Click Here to Connect to Your Private Flickr Albums
							</Typography>
						</Grid>
						<Grid size={{xs:0, sm:1}} />
					</>
					

				: <>



					<Grid size={{xs:12, sm:12}} >
						<Box align="center">
							<img style={ isXSMedia==true ? { width: "25%" } : {width: "10%"}} src="images/check.png" alt="Authorization Successful" />
						</Box>
						<Typography align="center" variant="h6">Authorization Successful</Typography>
					</Grid>

					<Grid size={{xs: 0, sm: 4}} />
					<Grid size={{xs:12, sm:4}} sx={{ display: "flex", alignItems: "center" }}>

						

						<TableContainer component={Paper}>
							<Table size="small">
								<TableBody>
									<TableRow>
										<TableCell sx={{ backgroundColor: "#1976d3", color: "white", fontWeight: "bold" }}>Flickr ID</TableCell>
										<TableCell>{sType=="SOURCE" ? sourceUser.id : targetUser.id}</TableCell>
									</TableRow>
									<TableRow>
										<TableCell sx={{ backgroundColor: "#1976d3", color: "white", fontWeight: "bold" }}>User Name</TableCell>
										<TableCell>{sType=="SOURCE" ? sourceUser.username : targetUser.username}</TableCell>
									</TableRow>
									<TableRow>
										<TableCell sx={{ backgroundColor: "#1976d3", color: "white", fontWeight: "bold" }}>Real Name</TableCell>
										<TableCell>{sType=="SOURCE" ? sourceUser.realName : targetUser.realName}</TableCell>
									</TableRow>
								</TableBody>
							</Table>
						</TableContainer>
						


					</Grid>
					<Grid size={{xs: 0, sm: 4}} />
				
				</>

			} {/* end if id in album user */}

 			
				
				
				</Grid>
	
	)
	
	
	
	
	
	} // end component
