import React, {memo, useState, useEffect} from 'react';
import * as Utils from "../Utils/Utils.js";
import * as ShowPage from "../Utils/ShowPage.js";
import Paper from '@mui/material/Paper';
import Typography from '@mui/material/Typography';
import 'bootstrap/dist/css/bootstrap.min.css';
import List from '@mui/material/List';
import Divider from '@mui/material/Divider';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';
import ListItemButton from '@mui/material/ListItemButton';
import Alert from '@mui/material/Alert';
import OverlaySpinner from '../Spinner/OverlaySpinner.js';
import Swal from 'sweetalert2';
import Grid from '@mui/material/Grid';



export default function SelecttTargetStep(props) {
		
	
	const message = ["Your pictures will be migrated to Flickr from the source you provided in the previous steps.\nNext we will try to connect to your Flickr account.",
	"Your pictures will be migrated from public Shared Albums Apple iCloud to the destination you provide in the next steps.\nNext we will try to find the folders you want to liberate.",
	"Your pictures will be migrated from Microsoft OneDrive to the destination you provide in the next steps.\n>Next we will try to find the folders you want to liberate.",
	"Your pictures will be migrated from folders on your computer to the destination you provide in the next steps.\n>Next we will try to find the folders you want to liberate.",
	"Your pictures will be migrated to Google Photos from the source you provided in the previous steps.\n>Next we will try to connect to the target."

	]

	// local files after image post 3
	// staged files 3000
	// and flickr source 0
	
	/*
	if(props.currentStep!=2 || 
		(props.selectedSourceProvider!=3 && 
		props.selectedSourceProvider!=3000)) 
			return null;
	*/
		/*
	if( (props.currentStep==2 && (props.selectedSourceProvider==3 || props.selectedSourceProvier==3000)) ||
		(props.currentStep==3 && (props.selectedSourceProvider==0)) )
	{
		//no action
	}	
	else
		return;	
		*/
		/*
	if(!ShowPage.shouldShow("SelectTargetStep",
		props.currentStep, 
		props.selectedSourceProvider, 
		props.selectedTargetProvider))
	return;
	*/
	
	const handleListItemClick = (event,index) => {
    	props.setSelectedTargetProvider(index);
    	props.setNextEnabled(true)
  };
	
	return (
		

		<Grid container>
			<Grid size={12} mt="20px" mb="20px" >
			<Typography variant="h5" align="center" fontWeight="bold">
			Please tell us where to put the photos that you want to liberate.
			</Typography> 
			<Typography align="center" mb="20px">
			FotoLiberator will let you copy or move your pictures between clouds. 
			<br />
			And best of all, you dont need to manage it through your personal computer. 
			<br />
			The entire transfer will take place from cloud to cloud.
			</Typography>
			</Grid>
			<Grid size={{xs:12, sm: 6}}>
				<Paper style={{maxHeight: 300, overflow: 'auto'}}>
  					<List>
  					
  					
  					
  					
  					
				        <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedTargetProvider === 0}
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
				                Select this target if you want to load your pictures to Flickr
				              </Typography>
				          }
				        />
				      </ListItemButton>
				      <Divider variant="inset" component="li" />

				        <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedTargetProvider === 1}
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
				                Select this target if you want to load your pictures to Apple iCloud
				              </Typography>
				          }
				        />
				      </ListItemButton>
				      <Divider variant="inset" component="li" />


 				        <ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedTargetProvider === 2}
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
				                Select this target if you want to load your pictures to Microsoft OneDrive
				              </Typography>
				          }
				        />
				      </ListItemButton>

 						<ListItemButton
				          alignItems="flex-start"
				          selected={props.selectedTargetProvider === 4}
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
				                Select this target if you want to load your pictures to Google Photos
				              </Typography>
				          }
				        />
				      </ListItemButton>


   
  					</List>
				</Paper>
			</Grid>

			<Grid size={{xs:12,sm:6}}>
				<Typography variant="h6" align="center" p="20px" m="20px">
				{props.selectedTargetProvider < 0 ? "" : message[props.selectedTargetProvider]}
				
				</Typography>
			
			</Grid>

			
		
		</Grid>		
		
              				
		
		
	)
	
}