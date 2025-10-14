import React, {memo, useState, useEffect} from 'react';
//import 'bootstrap/dist/css/bootstrap.min.css';

import * as Utils from "../Utils/Utils.js";
import * as ShowPage from "../Utils/ShowPage.js";
import * as RelativeTime from "../Utils/RelativeTime.js";
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import 'bootstrap/dist/css/bootstrap.min.css';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';
import ListItemButton from '@mui/material/ListItemButton';
import Alert from '@mui/material/Alert';
import OverlaySpinner from '../Spinner/OverlaySpinner.js';
import Pagination from '@mui/material/Pagination';



export default function GoogleSelectAlbum(props) {
	
	const [errorMessage, setErrorMessage] = useState("")
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	const [page, setPage] = useState(1);
	const [perPage, setPerPage] = useState(20);
	const [numPages, setNumPages] = useState(1);
	const [pickingSession, setPickingSession] = useState({})
	const [mediaItems, setMediaItems] = useState([]);


	var pickerWindowHandle=null;
	
	
	
	var isXSMedia=Utils.IsXSMedia();


	console.log("GoogleSelectAlbum", props)
/**************************************/
/**************************************/
/**************************************/
	const createSessionComplete = (ps) =>
	{
		
		let pi="";
		console.log("Pick Started ", ps)
		pi=ps.pollingConfig.pollInterval;
		pi = pi.replaceAll("[^\\d.]", "");
		let w=0, h=0;
		
		setPickingSession(ps);
		var t = new Date();
		t.setSeconds(t.getSeconds() + 1800);
		
		w=window.innerWidth - 200;
		h=window.innerHeight - 200;
		
		if(w < 100) w=100;
		if(h < 100) h=100;
		
		
		const windowFeatures = "popup=true,left=100,top=100,width=" + w + ",height=" + h +",scrollbars=yes";
		
		pickerWindowHandle = window.open(ps.pickerUri,"picker",windowFeatures);
	
		if(pickerWindowHandle==null)
		{
			setWizardErrorMessage("Could not open picker window");
			return;	  
		}
		console.log("Opened Window", pickerWindowHandle, windowFeatures)
		
		window.numRequests=0;
			setTimeout( () => {
				
				pollPicker(ps);
				
			},5000)
	
	};
/**************************************/
/**************************************/
/**************************************/
	const getMediaItemsComplete = async (m) => { 
									 
		 if(m==null || m,mediaItems==null || !Array.isArray(m.mediaItems))
		 {
			 setWizardErrorMessage("No selected media items were found.")
			 return;
		 }
		 
		 
		 let items = m.mediaItems;
		 
		 if(items.length < 1)
		 {
			 setWizardErrorMessage("No selected media items could be found. Please try again.")
			 return;
		 }
		 
		 let options={headers: {
		  		'Authorization': 'Bearer ' + props.jobParameters.sourceUser      
			}};

		 
		 let url;
		 for(const a of items)
		 {
		 
		 	if(a.mediaFile && a.mediaFile.baseUrl)
				url=a.mediaFile.baseUrl
			else
				url="images/onedrivesm.png";

			
			let res = await fetch(url, options)
			let blob = await res.blob();
			let imgBlob=URL.createObjectURL(blob)
			
			a["blob"]=imgBlob;
		
		 
		 } //end for
		 
	setMediaItems(items)
 };
									 
/**************************************/
/**************************************/
/**************************************/									 
const pollComplete = (ps) => {
						
						
	console.log("Pick Poll Returned", window.numRequests, ps)

	if(ps.mediaItemsSet==true)
	{
		console.log("Pick Poll Complete")
		if(pickerWindowHandle!=null)
		{
			console.log("Closing picker ", pickerWindowHandle)
			pickerWindowHandle.close();
			 //pickerWindowHandle.location.href="http://localhost:8080/AuthComplete.html";
			//pickerWindowHandle=window.open("http://localhost:8080/AuthComplete.html","picker")
		
		}
		
		let o = {...props.jobParameters}
		//o["sourcePickerId"]=ps.id;
		o["sourceAlbums"]=ps.id; //picker id
		props.setJobParameters(o)
		
		
	  	Utils.getAPI("https://photospicker.googleapis.com/v1/mediaItems",
			{headers: {
				'Content-Type': 'application/json',
				'Authorization': 'Bearer ' + props.jobParameters.sourceUser      
			}}, 
			{
				 sessionId: ps.id
			},
			getMediaItemsComplete,
			setWizardErrorMessage,
 			setIsLoading);
		
		
		return;
	}
	
	
	setTimeout( () => {
		
		pollPicker(ps);
		
	},5000)
	
	
};								 
									
/**************************************/
/**************************************/
/**************************************/
	useEffect(() => {
		/*
		if(!ShowPage.shouldShow("GoogleSelectAlbum",
				props.currentStep, 
				props.selectedSourceProvider, 
				props.selectedTargetProvider))
		return;
		*/
		  	Utils.postAPI("https://photospicker.googleapis.com/v1/sessions",
	    		{headers: {
      				'Content-Type': 'application/json',
      				'Authorization': 'Bearer ' + props.jobParameters.sourceUser      
   				 }}, 
   				 {},
   				 createSessionComplete,
   				 setWizardErrorMessage,
   				 setIsLoading);
  
    
  }, [props.currentStep, page, perPage]); //run this code when this changes
	

/*
	if(!ShowPage.shouldShow("GoogleSelectAlbum",
				props.currentStep, 
				props.selectedSourceProvider, 
				props.selectedTargetProvider))
		return;
*/

const closeWindow = () =>
{
	console.log("In Close Window", pickerWindowHandle)
	if(pickerWindowHandle != null)
	{
		pickerWindowHandle.close();
		pickerWindowHandle=null;
	}
}

const openWindow = (url) =>
{
	console.log("In Open Window", url)
	pickerWindowHandle=window.open(url);
	
}

const pollPicker = (obj) => {
	
		console.log("pollPicker",obj)
	
		//setNumAuthRequests(numAuthRequests+1)
		let z=window.numRequests;
		window.numRequests=z+1
		
		if(z>300)
		{
			setWizardErrorMessage("We have been waiting too long for the picker to complete. Please try again.")
			return;
		}
		
		if(obj==null || obj.id==null)
		{
			setWizardErrorMessage("The picker could not be contacted because the ID was invalid. Please try again.")

			return;
		}

		  	Utils.getAPI("https://photospicker.googleapis.com/v1/sessions/" + obj.id,
	    		{headers: {
       				'Authorization': 'Bearer ' + props.jobParameters.sourceUser      
   				 }}, 
   				 {},
   				 pollComplete,
   				 setWizardErrorMessage,
   				 setIsLoading);
  

}






const renderMediaItems = () => {
	
	
	console.log("MediaItems",mediaItems)
	//let albumList=props.albumList;
	
	if(errorMessage.length > 0)
	return <Paper elevation={0} align="center" mt={3}>
			<Typography variant="h5" align="center" fontWeight="bold">
				{errorMessage}
			</Typography></Paper>;
	
	
	if(mediaItems==null || !Array.isArray(mediaItems))
		return <Paper elevation={0} align="center" mt={3}>
			<Typography variant="h5" align="center" fontWeight="bold">
				Please wait...searching for media items
			</Typography></Paper>;
		
	let oa=[];	
	let e;
	let i=0;

		
	for(const a of mediaItems)
	{
		//console.log(a.title)
		
		
		
	
		//console.log(selectedAlbumIndex, i,selectedClass)
				          
		e = <Grid size={6} key={i}> <ListItemButton
			albumindex={i}
			alignItems="flex-start">

			<ListItemAvatar sx={{ marginRight: "10px" }}>
				<Avatar src={a.blob} variant="rounded" sx={{ width: "85px", height: "85px" }} />
			</ListItemAvatar>
			<ListItemText
				albumindex={i}
				primary={
					<Typography
						sx={{ display: 'inline' }}
						fontWeight="bold"
						component="div"
						variant="body1"
						color="text.primary">
						{a.mediaFile.filename}
					</Typography>

				}
				secondary={
					<Typography
						sx={{ display: 'inline' }}
						component="span"
						variant="body2"
						color="text.primary">
						<br />
						{a.createTime != null ? RelativeTime.RelativeTime.FormatRelativeFromString(a.createTime) : null}
						
					</Typography>
				}
			/>



			{/*<Divider variant="inset" component="li" />*/}
		</ListItemButton>
		</Grid>

					   
			
				        
				 	
	
		oa.push(e);
		i++;
	}
	
	if(i>0)
		props.setNextEnabled(true)
	
	return oa;
	
				     
	
} //end fn


	
	
	
	
	return (
		
		<>
		
		{wizardErrorMessage.length > 0 ? <Alert severity="error">{wizardErrorMessage}</Alert> : null }
		<OverlaySpinner show={isLoading} />

		
		<Grid container>
			<Grid size={12} mt="20px" mb="20px" >
			<Typography variant="h5" align="center" fontWeight="bold">
			Here is a sample of the media items that will be liberated
			</Typography> 
			
			
			</Grid>
			<Grid size={12}>
				<Paper elevation={0} style={{maxHeight: 300, overflow: 'auto'}}>
  					<Grid container>
  					
				     
						
						{renderMediaItems()}

   
  					</Grid>
				</Paper>
			</Grid>
			{/*
			<Grid size={12} mt={2} sx={{ display: "flex", alignItems: "center",  justifyContent: "center" }}>
				<Pagination align="center" count={numPages} page={page} color="primary" onChange={pageChange} />
			</Grid>
			*/}

		
		</Grid>		
		
       </>
		
		
	)
	



	
	
};