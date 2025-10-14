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
import SelectICloudAlbumStep from '../wizard/SelectICloudAlbumStep.js';




export default function ICloudAlbumWizardPages(props) {
	
	
    	const [userName, setUserName] = useState("https://www.icloud.com/sharedalbum/#B2OGWZuqDGlTvJJ");
	const [password, setPassword] = useState("");
	const [userNameMessage, setUserNameMessage] = useState("");
	const [passwordMessage, setPasswordMessage] = useState("");
	const [switchState, setSwitchState] = useState(false);
	const [errorMessage, setErrorMessage] = useState("");
	const [albumUser, setAlbumUser] = useState({});
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	const [albumList, setAlbumList] = useState({});
	const [selectedAlbumIndex, setSelectedAlbumIndex] = useState([]);
	
	//const prevStepRef = useRef(0);	
	
	/***********************************/	
	
	useEffect(() => {
    
   // prevStepRef.current = props.currentStep;
  }, [props.currentStep]); //run this code when this hanges
	
	/***********************************/	
	//1==iCloud
	//if(props.currentStep<1 || props.currentStep > 2 || props.selectedSourceProvider!=1) return null;
	/*
	if(!ShowPage.shouldShow("ICloudAlbumWizardPages",
			props.currentStep, 
			props.selectedSourceProvider, 
			props.selectedTargetProvider))
	return;
	*/
	console.log("ICloudAlbum",props, albumUser, albumList)
	
	
	
	
	//const prevCurrentStep=usePrevious(props.currentStep)
	
	/***********************************/	
	const inputChange = (e) => {

		//console.log("input Cha",e,e.target.id,e.target.value,!e.target.value,e.target.value=="")
		
		
		
		if (!e || !e.target || e.target.value==null)
			return;

		

		if (e.target.id == "user-name") {
			setUserName(e.target.value)
		}

		if (e.target.id == "password")
			setPassword(e.target.value)
	};

/***********************************/	
	const keyUpEvent = (e) => {
		
		if(e.key=="Enter")
			searchClick(null);
	}


 /***********************************/	
const switchChange = () => {
	
	setSwitchState(!switchState)
};

/***********************************/	
const searchClick = (e) => {
	
	console.log("searchClick")
	setAlbumUser({});
	
	
	let o = {...props.jobParameters}
	o["albumUrl"]=userName
	
	props.setJobParameters(o)
	
	
	Utils.loadData("ICloudServlet",{query : "findsharedalbum", sharedalbumid: userName, isprivate : switchState }, loadAlbumComplete, setWizardErrorMessage, setIsLoading);
		
	
};


/***********************************/	
const loadAlbumComplete = (u) =>
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
}
	
	/***********************************/	
	const successCallback = (url, state) => {
		console.log("SuccessCallback",url, state)
		
		if(url.indexOf("listalbums") > 0 && Object.keys(state).length > 0)
			props.setNextEnabled(true);
	}
			
	/***********************************/		
			/*
			if(props.currentStep==1 && prevStepRef.current==2)
			{
				prevStepRef.current=1;
				setAlbumUser({})
				setAlbumList({});
			}
			*/
			
			
			if(props.currentStep==2)
			{
					/*
					
					if(albumUser==null || albumUser.id==null)
					{
						Swal.fire({
						  title: 'Error!',
						  text: 'The iCloud album was not valid.',
						  icon: 'error',
						  confirmButtonText: 'Ok'
						});

						return;
					}
					*/
					
					if(albumList!=null && Object.keys(albumList).length < 1 /*&& prevStepRef.current==1*/)
					{
						console.log("Loading albums");
						//prevStepRef.current=2;
						Utils.loadData("ICloudServlet",{query : "listalbums", sharedalbumid: userName}, setAlbumList, setWizardErrorMessage, setIsLoading);

					}
			}
	
	
	if(Object.keys(albumList).length > 0)
	{
		
		console.log("ICloudAlbumList Exists",albumList)
		if(props.setAlbumList!=null)
			props.setAlbumList(albumList)
		
		return <SelectICloudAlbumStep albumList={albumList} nextEnabledCallback={props.setNextEnabled} />
	}
	
	
	return (
		<Grid container>
		<OverlaySpinner show={isLoading} />
		{wizardErrorMessage.length > 0 ? <Alert severity="error">{wizardErrorMessage}</Alert> : null }
		

			<Grid size={12} mt="20px" mb="20px" >
			<Typography variant="h5" align="center" fontWeight="bold">
			Let&apos;s find the iCloud Shared Album that you&apos;re looking for
			</Typography> 
			<Typography align="center" mb="20px">
			You dont need to provide login information for iCloud Shared Albumns that are publically viewable. 
			So the easiest way to get started, is to make the ablums you want to transfer publically viewable.
			Dont worry, once youre done transferring the photos, you can make the ablum private again.
			<br />
			If you dont want to make your ablums public, you will have to provide your iCloud login information below.
			We take your security seriously and promise not to store your login information after the transfer.
			It&apos;s also ok if you want to use a tempoary password and change it afterwards.
			</Typography>
			</Grid>
			

			<Grid size={{xs:12, sm:8}}>
				<Grid container>
					<Grid size={12}>
						<FormControlLabel control={<Switch checked={switchState} onChange={switchChange} />} label="Access Private" />
					</Grid>


					{switchState==false ? <>

					<Grid size={{xs:12, sm:8}}>
						 <TextField
			                margin="normal"
			                required
			                fullWidth
			                id="user-name"
			                label="iCloud Shared Album URL"
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
			         
			         <Grid size={{xs:12, sm:4}}>
			                  	<Button startIcon={<SearchIcon />} onClick={searchClick} variant="contained" sx={{ m: "10px", mt: "25px" }}>Find Album</Button>
					</Grid>
					</>
					: <>
					
					<Grid size={{xs:12, sm:8}}>
						 <TextField
			                margin="normal"
			                required
			                fullWidth
			                id="user-name"
			                label="iCloud User Name or Email Address"
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
			         
			         <Grid size={{xs:0, sm:4}}></Grid>
					
						<Grid size={{xs:12, sm:8}}>
 
			 			<TextField
			                margin="normal"
			                required
			                fullWidth
			                value={password}
			                name="password"x
			                label="Password"
			                type="password"
			                id="password"
			                autoComplete="current-password"
			                onChange={inputChange}
			                error={passwordMessage.length > 0 ? true : false}
			                helperText={passwordMessage} />
			              
			          </Grid>
			          
			          <Grid size={{xs:12, sm:4}}>
			          		<Button startIcon={<SearchIcon />} onClick={searchClick} variant="contained" sx={{ m: "10px", mt: "25px" }}>Find User</Button> 

			          </Grid>
 						</>}
 

 
 			
 			</Grid>
			</Grid>

			<Grid size={{xs:12, sm:4}}>
				<Box m="20px">
				
				{"message" in albumUser ? <Alert severity="warning">{albumUser.message}</Alert> : null}
				
				
				{"streamName" in albumUser ? 
				
					<Grid container>
						<Grid size={12}>The shared album was found</Grid>
						<Grid size={4}>ID:</Grid>
						<Grid size={8}>{albumUser.streamName}</Grid>
						<Grid size={4}>User Name:</Grid>
						<Grid size={8}>{albumUser.userFirstName} {albumUser.userLastName}</Grid>
						<Grid size={4}>Number of Photos:</Grid>
						<Grid size={8}>{albumUser.photos.length}</Grid>
						
					
					</Grid>
	
					: null}
				</Box>
			</Grid>

		</Grid>

	)
	
	
	
	
	
	} // end component
