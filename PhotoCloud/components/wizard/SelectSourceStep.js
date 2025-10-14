import React, {memo, useState, useEffect} from 'react';
//import 'bootstrap/dist/css/bootstrap.min.css';
import MainMenu from "../Menu/MainMenu.js";
import Brand from "../Menu/Brand.js";
import * as Utils from "../Utils/Utils.js";
import * as ShowPage from "../Utils/ShowPage.js";
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Footer from '../Footer.js';
import CameraEnhanceOutlinedIcon from '@mui/icons-material/CameraEnhanceOutlined';
import SchoolOutlinedIcon from '@mui/icons-material/SchoolOutlined';
import Modal from 'react-bootstrap/Modal';
import 'bootstrap/dist/css/bootstrap.min.css';
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import CloseIcon from '@mui/icons-material/Close';
import FastRewindIcon from '@mui/icons-material/FastRewind';
import FastForwardIcon from '@mui/icons-material/FastForward';
import SearchIcon from '@mui/icons-material/Search';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import Divider from '@mui/material/Divider';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';
import ListItemButton from '@mui/material/ListItemButton';
import FormGroup from '@mui/material/FormGroup';
import Switch from '@mui/material/Switch';
import Alert from '@mui/material/Alert';
import OverlaySpinner from '../Spinner/OverlaySpinner.js';
import Swal from 'sweetalert2';











export default function SelectSourceStep(props) {
	
	const [showModal, setShowModal] = useState(false);
	const [currentStep, setCurrentStep] = useState(0);
    //const [selectedSourceProvider, setSelectedSourceProvider] = useState(-1);
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
	const [nextEnabled, setNextEnabled] = useState(false);
	
	
	/*
	const message = ["Your pictures will be migrated from Flickr to the destination you provide in the next steps.\nNext we will try to find the Flickr albums you want to liberate.",
	"Your pictures will be migrated from public Shared Albums Apple iCloud to the destination you provide in the next steps.\nNext we will try to find the folders you want to liberate.",
	"Your pictures will be migrated from Microsoft OneDrive to the destination you provide in the next steps.\n>Next we will try to find the folders you want to liberate.",
	"Your pictures will be migrated from folders on your computer to the destination you provide in the next steps.\nNext we will try to find the folders you want to liberate.",
	"Your pictures will be migrated from the files you previously staged using the PhotoCloud app. In the next steps, you will select the staged files and the destination."
	]
	*/
	
	const message = {"0": "Your pictures will be migrated from Flickr to the destination you provide in the next steps.\nNext we will try to find the Flickr albums you want to liberate.",
	"1" : "Your pictures will be migrated from public Shared Albums Apple iCloud to the destination you provide in the next steps.\nNext we will try to find the folders you want to liberate.",
	"2" : "Your pictures will be migrated from Microsoft OneDrive to the destination you provide in the next steps.\n>Next we will try to find the folders you want to liberate.",
	"3" : "Your pictures will be migrated from folders on your computer to the destination you provide in the next steps.\nNext we will try to find the folders you want to liberate.",
	"4" : "Your pictures will be migrated from Google Photos to the destination you provide in the next steps.\n>Next we will try to find the albums you want to liberate.",

	"3000" : "Your pictures will be migrated from the files you previously staged using the PhotoCloud app. In the next steps, you will select the staged files and the destination."
	}

	
	
	//if(props.currentStep!=0) return null;
	
	//if(!ShowPage.shouldShow("SelectSourceStep",props.currentStep, props.selectedSourceProvider, props.selectedTargetProvider)) return;
	
	console.log("SelectSourceStep", props)
	
	const handleListItemClick = (event,index) => {
		
		//
		//Generate a uniqueId
		//
		
		let hstr = navigator.userAgent + " " + (new Date()).getTime() + " " + Math.random();
		let uniqueId=Utils.hash(hstr)
		
		
		let o = {...props.jobParameters}
		o["uniqueId"]=uniqueId
		
		console.log("Setting Unique Id ", o )
		
		props.setJobParameters(o)
		
		
		
    	props.setSelectedSourceProvider(index);
    	props.setNextEnabled(true)
  };
	
	return (
		

		<Grid container>
			<Grid size={12} mt="20px" mb="20px" >
			<Typography variant="h5" align="center" fontWeight="bold">
			Please tell us where the photos are that you want to liberate.
			</Typography> 
			<Typography align="center" mb="20px">
			FotoLiberator will let you copy or move your pictures between clouds. 
			<br />
			And best of all, you dont need to manage it through your personal computer. 
			<br />
			The entire transfer will take place from cloud to cloud.
			</Typography>
			</Grid>
			<Grid size={{xs: 12, sm:6}}>
				<Paper style={{maxHeight: 300, overflow: 'auto'}}>
  					<List>
  					
  					{Array.isArray(props.stagedJobs) && props.stagedJobs.length > 0 ?
  						<ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedSourceProvider === 3000}
      					  onClick={(event) => handleListItemClick(event, 3000)}>

				        <ListItemAvatar>
				          <Avatar alt="Local" src="images/stage.png" />
				        </ListItemAvatar>
				        <ListItemText
				          primary="Staged Job"
				          secondary={
				              <Typography
				                sx={{ display: 'inline' }}
				                component="span"
				                variant="body2"
				                color="text.primary"
				              >
				                Select this source if you have staged photos from your computer using the PhotoCloud app
				              </Typography>
				          }
				        />
				      </ListItemButton>
				      : null }
  					
  					
  					
  					
  					  <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedSourceProvider === 3}
      					  onClick={(event) => handleListItemClick(event, 3)}>

				        <ListItemAvatar>
				          <Avatar alt="Local" src="images/localcomputer.png" />
				        </ListItemAvatar>
				        <ListItemText
				          primary="My Computer"
				          secondary={
				              <Typography
				                sx={{ display: 'inline' }}
				                component="span"
				                variant="body2"
				                color="text.primary"
				              >
				                Select this source if your pictures are on your local computer
				              </Typography>
				          }
				        />
				      </ListItemButton>
  					
  					
  					
				        <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedSourceProvider === 0}
      					  onClick={(event) => handleListItemClick(event, 0)}>

				        <ListItemAvatar>
				          <Avatar alt="Flickr" src="images/flickrsm.png" />
				        </ListItemAvatar>
				        <ListItemText
				          primary="Flickr"
				          secondary={
				              <Typography
				                sx={{ display: 'inline' }}
				                component="span"
				                variant="body2"
				                color="text.primary"
				              >
				                Select this source if your pictures are on Flickr
				              </Typography>
				          }
				        />
				      </ListItemButton>
				      <Divider variant="inset" component="li" />

				        <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedSourceProvider === 1}
      					  onClick={(event) => handleListItemClick(event, 1)}>
				        <ListItemAvatar>
				          <Avatar alt="iCloud" src="images/icloudsm.png" />
				        </ListItemAvatar>
				        <ListItemText
				          primary="Apple iCloud"
				          secondary={
				              <Typography
				                sx={{ display: 'inline' }}
				                component="span"
				                variant="body2"
				                color="text.primary"
				              >
				                Select this source if your pictures are on Apple iCloud
				              </Typography>
				          }
				        />
				      </ListItemButton>
				      <Divider variant="inset" component="li" />


 				        <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedSourceProvider === 4}
      					  onClick={(event) => handleListItemClick(event, 4)}>
				        <ListItemAvatar>
				          <Avatar alt="Google" src="images/googlephotos.png" />
				        </ListItemAvatar>
				        <ListItemText
				          primary="Google Photos"
				          secondary={
				              <Typography
				                sx={{ display: 'inline' }}
				                component="span"
				                variant="body2"
				                color="text.primary"
				              >
				                Select this source if your pictures are in Google Photos
				              </Typography>
				          }
				        />
				      </ListItemButton>
				      
				      			        <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedSourceProvider === 2}
      					  onClick={(event) => handleListItemClick(event, 2)}>
				        <ListItemAvatar>
				          <Avatar alt="OneDrive" src="images/onedrivesm.png" />
				        </ListItemAvatar>
				        <ListItemText
				          primary="Microsoft OneDrive"
				          secondary={
				              <Typography
				                sx={{ display: 'inline' }}
				                component="span"
				                variant="body2"
				                color="text.primary"
				              >
				                Select this source if your pictures are on Microsoft OneDrive
				              </Typography>
				          }
				        />
				      </ListItemButton>




   
  					</List>
				</Paper>
			</Grid>

			<Grid size={{xs:12, sm: 6}}>
				<Typography variant="h6" align="center" p="20px" m="20px">
				{props.selectedSourceProvider < 0 ? "" : message[props.selectedSourceProvider]}
				
				</Typography>
			
			</Grid>

			
		
		</Grid>		
		
              				
		
		
	)
	
}