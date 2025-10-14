import React, {memo, useState, useEffect, useRef} from 'react';
import * as Utils from "../Utils/Utils.js";
import * as ShowPage from "../Utils/ShowPage.js";
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import 'bootstrap/dist/css/bootstrap.min.css';

import Alert from '@mui/material/Alert';
import OverlaySpinner from '../Spinner/OverlaySpinner.js';
import Swal from 'sweetalert2';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import LinearProgress from '@mui/material/LinearProgress';




export default function GoogleAccountSelect(props) {
	
	
 
	const [sourceUser, setSourceUser] = useState({});
	const [targetUser, setTargetUser] = useState({});
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	const [albumList, setAlbumList] = useState({});
	const [isUserLoggedIn, setIsUserLoggedIn] = useState(false)

	
	
	//const prevStepRef = useRef(0);	//Changing this doesnt force a re-render
	
	var windowHandle=null;
	
	var isXSMedia=Utils.IsXSMedia();

	
	useEffect(() => {
    
    	//prevStepRef.current = props.currentStep;
    
    	/*
	    if(ShowPage.shouldShow("GoogleAccountSelect_Source",
	    	props.currentStep, 
	    	props.selectedSourceProvider, 
	    	props.selectedTargetProvider) ||
	       ShowPage.shouldShow("GoogleAccountSelect_Target",
	    	props.currentStep, 
	    	props.selectedSourceProvider, 
	    	props.selectedTargetProvider)	    	
	    	)	
		{
			*/
	    	Utils.loadData("GoogleServlet",
	    		{query : "isuserloggedin"}, 
	    		setIsUserLoggedIn, 
	    		setWizardErrorMessage, 
	    		setIsLoading,
	    		(url,o) => {
					//success callback
					//console.log("LoginCallback",o)
					if(o!=null && o.isLoggedIn!=true)
					{
						//console.log("LoginCallback","Opening window")
						windowHandle=window.open("/GoogleLogin")
						
						//return;
					}
					
					window.numRequests=0;
	
					
	
					setTimeout( () => {
						
						shortPoll2({});
						
					},3000)
					
				});
		//}
	 
  }, [props.currentStep]); //run this code when this changes
	
	
	
	
	
	
	let sType="";
	
	/*
	if(ShowPage.shouldShow(
		"GoogleAccountSelect_Source",
		props.currentStep, 
		props.selectedSourceProvider, 
		props.selectedTargetProvider))	
			sType="SOURCE"
		
		//console.log("STYPE SRC", sType)	
		
	if(ShowPage.shouldShow(
		"GoogleAccountSelect_Target",
		props.currentStep, 
		props.selectedSourceProvider, 
		props.selectedTargetProvider))	
		{
			
				sType="TARGET";
				
				//if("id" in targetUser)
				//	props.setNextEnabled(true);
				
				
		}
	*/
	sType=props.type;

	//console.log("GOOGLE STYPE TGT", sType)	
	
	if(sType.length <= 1) return;
	
	
	
	
	console.log("GoogleAccountSelect",sType, props,sourceUser, targetUser, albumList)
	


const loadAuthorizedUserComplete = (u) => {
	console.log("LAUC",u)
	//setAlbumUser(u)
	let o={...props.jobParameters}
	
	if(sType=="SOURCE")
	{
		o["sourceUser"]=u.accessToken
		o["sourceIsPrivate"]=true
		setSourceUser(u)
	}
	else
	{
		o["targetUser"]=u.accessToken;
		o["targetIsPrivate"]=true;
		setTargetUser(u);
	}	
	
	//o["albumUser"]=u;
	//o["isPrivate"]=switchState;
	props.setJobParameters(o);
	props.setNextEnabled(true);
}



const loadCheckAuthComplete = (u,a) => {
	console.log("CAC",u,a)
	
	if(a==null || !a.hasOwnProperty("isLoggedIn"))
	{
		setWizardErrorMessage("An unknown error occured which processing the authoeization. Please try again.");
		return;
	}
		
	if(a.isLoggedIn!=true)
	{
		 		console.log("waiting",window.numRequests)
			  //setTimeout(() => {shortPoll(obj, getNumAuthRequests, setNumAuthRequests)}, 3000)
			  
			  setTimeout( () => {
		
					shortPoll2(u);
		
				},3000)
				
				return;
	}
		
	if(a.isLoggedIn==true)
	{
	  
			  //token response recieved
			  if(!windowHandle || windowHandle==null)
			  {
				  console.log("Invalid window handle")
			  		//setWizardErrorMessage("The authorization window could not be closed");
			  	}
			  else
			  {
				  console.log("Closing Window",windowHandle);
			  		windowHandle.close();
			  	}
			  	
			  //Utils.loadData("FlickrServlet",{query : "authorizeduserquery", key: u.key, token: u.token }, loadAuthorizedUserComplete, setWizardErrorMessage, setIsLoading);
			loadAuthorizedUserComplete(a);
			
			return;
	}
			  	
	
	//not found - input token not found
	setWizardErrorMessage("There was an error reading the authentication token. Please try again.");

}



const shortPoll2 = (obj) => {
	
		console.log("ShortPoll2",obj, window.numRequests)
	
	
	//setNumAuthRequests(numAuthRequests+1)
	let z=window.numRequests;
	window.numRequests=z+1
	
	if(z>50)
	{
		setWizardErrorMessage("We have been waiting too long for the authorization response. Please try again.")
		return;
	}

	

    	Utils.loadData("GoogleServlet",
	    		{query : "isuserloggedin"}, 
	    		(x) => loadCheckAuthComplete(obj,x), 
	    		setWizardErrorMessage, 
	    		setIsLoading);


} //end shortPoll2




			/*
			//Back button pressed
			if(props.currentStep==1 && prevStepRef.current==2)
			{
				prevStepRef.current=1;
				let o={...props.jobParameters}
				
				//delete o["albumUser"]
				//o["isPrivate"]=switchState
				
				
				if(sType=="SOURCE")
				{
					delete o["sourceUser"]
					o["sourceIsPrivate"]=true
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
					If you want to transfer photos from your account, you must authorize FotoFreedom to connect to your account.
					Don&apos;t worry, you don&apos;t need to provide your passwors to us.
					In the next steps, you will be forwarded to a login page where you can authorize this app to connect.
					Once you&apos;re done, you can remove the authorization for this app.									</Typography>
				</> :
				<>
				
				<Typography variant="h5" align="center" fontWeight="bold">
					Tell us the account you want to load these pictures to
				</Typography> 
				<Typography align="center" mb="20px">
					If you want to save photos to your account, you must authorize FotoFreedom to connect to your account.
					Don&apos;t worry, you don&apos;t need to provide your passwors to us.
					In the next steps, you will be forwarded to a login page where you can authorize this app to connect.
					Once you&apos;re done, you can remove the authorization for this app.
				</Typography>
				
				</>}
				
			</Grid>
		
			

			 


					{(sType=="SOURCE" && sourceUser!=null && sourceUser.hasOwnProperty("id")) ||
					 (sType=="TARGET" && targetUser!=null && targetUser.hasOwnProperty("id")) ?
					<>
					
					
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
										<TableCell sx={{ backgroundColor: "#1976d3", color: "white", fontWeight: "bold" }}>ID</TableCell>
										<TableCell>{sType=="SOURCE" ? sourceUser.id : targetUser.id}</TableCell>
									</TableRow>
									<TableRow>
										<TableCell sx={{ backgroundColor: "#1976d3", color: "white", fontWeight: "bold" }}>User Name</TableCell>
										<TableCell>{sType=="SOURCE" ? sourceUser.name : targetUser.name}</TableCell>
									</TableRow>
									<TableRow>
										<TableCell sx={{ backgroundColor: "#1976d3", color: "white", fontWeight: "bold" }}>Email</TableCell>
										<TableCell>{sType=="SOURCE" ? sourceUser.email : targetUser.email}</TableCell>
									</TableRow>
									<TableRow>
										<TableCell sx={{ backgroundColor: "#1976d3", color: "white", fontWeight: "bold" }}>Picture</TableCell>
										<TableCell><img src={sType=="SOURCE" ? sourceUser.picture : targetUser.picture} style={{"width" : "80px"}} /></TableCell>
									</TableRow>
								</TableBody>
							</Table>
						</TableContainer>
						


					</Grid>
					</> : 	<Grid size={{xs:12, sm:12}} >
			
						<Typography align="center" variant="h6">Please wait...attempting login</Typography>
						<OverlaySpinner show={true} />
						<Box sx={{ width: '100%' }}>
					      <LinearProgress />
					    </Box>
					</Grid>
	
					
					 }
					<Grid size={{xs: 0, sm: 4}} />

				</Grid>
	
	)
	
	
	
	
	
	} // end component
