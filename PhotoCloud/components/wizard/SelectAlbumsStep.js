import React, {memo, useState, useEffect} from 'react';
//import 'bootstrap/dist/css/bootstrap.min.css';

import * as Utils from "../Utils/Utils.js";
import * as ShowPage from "../Utils/ShowPage.js";
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



export default function SelectAlbumStep(props) {
	
	
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	const [selectedAlbumIndex, setSelectedAlbumIndex] = useState([]);
	const [page, setPage] = useState(1);
	const [perPage, setPerPage] = useState(20);
	const [numPages, setNumPages] = useState(1);
	const [selectedAlbumIds, setSelectedAlbumIds] = useState([]);
	const [albumList, setAlbumList] = useState({})

	useEffect(() => {
    //if(albumList!=null && Object.keys(albumList).length < 1 && prevStepRef.current==1)
	//{
		console.log("Loading albums");
		
		
			/*
		if(!ShowPage.shouldShow("SelectAlbumStep",
			props.currentStep, 
			props.selectedSourceProvider, 
			props.selectedTargetProvider))
				return;
		*/
		//prevStepRef.current=2;
		Utils.loadData("FlickrServlet",{
											query : "listalbums", 
											flickruserid: props.jobParameters.sourceUser.id, 
											isprivate : props.jobParameters.sourceIsPrivate,
											page: page,
											perPage: perPage
										}, 
										setAlbumList, 
										setWizardErrorMessage, 
										setIsLoading,
										successCallback);
	//}
    
  }, [props.currentStep, page, perPage]); //run this code when this changes
	
/*
if( ( props.currentStep==2) &&
		props.selectedSourceProvider==0) 
	{
		//no action
	}	
	else
	{
		return;
	}	
	
*/

/*
	if(!ShowPage.shouldShow("SelectAlbumStep",
				props.currentStep, 
				props.selectedSourceProvider, 
				props.selectedTargetProvider))
		return;

*/

  const handleAlbumListItemClick = (event) => {
	  
	  let x;
	  let index;
	  let newArray;
	  let albumArray;
	  let intx=0;
	  let newId;
	  //console.log("Album Click",index,event,event.target,event.target.attributes,event.target.attributes.albumtextindex);
	  //console.log("Album Click",event.currentTarget.getAttribute("albumindex"), selectedAlbumIds, props.albumList);
//x=event.target.attributes.albumtextindex;
//console.log("X",x);
	//console.log("attr",event.target.getAttribute("albumindex"));
	//console.log("attr",index)
	x=event.currentTarget.getAttribute("albumindex");
	event.currentTarget.classList.toggle("Mui-selected");
	
	newArray=[...selectedAlbumIndex];
	albumArray=[...selectedAlbumIds];
	
	//console.log("X",x);
	
	
	if(x==null)
		return;
	
			
	intx=parseInt(x);
	newId=albumList.photosets[x].id
	
	if(newArray.includes(intx))
		{
			
			index = newArray.indexOf(intx);
			//console.log("del",newArray,intx,index);

			newArray.splice(index, 1);
			setSelectedAlbumIndex(newArray)
			
			index = albumArray.indexOf(intx);
			albumArray.splice(index, 1);
			setSelectedAlbumIds(albumArray)
	
			//console.log("after del",newArray);
		}
		else	
		{			
			//console.log("add",newArray);
			newArray.push(intx);
			setSelectedAlbumIndex(newArray);
			//console.log("after add",newArray);
			
			albumArray.push(newId);
			setSelectedAlbumIds(albumArray)
		}
	
	
	let jp={...props.jobParameters}
	jp["sourceAlbums"]=albumArray.join("|");
	props.setJobParameters(jp);
	
	console.log("Selected Albums",albumArray, jp);
	
	if(newArray.length > 0)
		props.setNextEnabled(true);
	else
		props.setNextEnabled(false);
	
    //setSelectedAlbumIndex(x);
    //event.preventDefault();
  };




const successCallback = (url, state) => {
	console.log("SuccessCallback",url, state)
	
	if(!state || Object.keys(state).length < 1) return;
	
	if("page" in state)
		setPage(state.page)
		
	if("pages" in state)
		setNumPages(state.pages);
		
	
	
	
	
} ///end successCallbackk




const renderAlbumList = () => {
	
	
	//console.log("RAL",props.albumList.photosets)
	//let albumList=props.albumList;
	
	if(albumList==null || albumList.photosets==null || !Array.isArray(albumList.photosets))
		return <Paper elevation={0} align="center" mt={3}>
			<Typography variant="h5" align="center" fontWeight="bold">
				Please wait...searching for albums
			</Typography></Paper>;
		
	let oa=[];	
	let e;
	let i=0;
	let url;	
	let t;
	let selectedClass;
		
	for(const a of albumList.photosets)
	{
		//console.log(a.title)
		
		
		
		
		if(a.primaryPhoto && a.primaryPhoto.server && a.primaryPhoto.id && a.primaryPhoto.secret)
		{
			url="https://live.staticflickr.com/" + a.primaryPhoto.server + "/" + a.primaryPhoto.id + "_" + a.primaryPhoto.secret + "_s.jpg";
		}
		else
			url="images/onedrivesm.png";
		
		
		
		
		if(selectedAlbumIndex.includes(i))  
			selectedClass="Mui-selected";
		else
			selectedClass="";
				          
				          
		//console.log(selectedAlbumIndex, i,selectedClass)
				          
		e = <Grid size={6} key={i}> <ListItemButton
			albumindex={i}
			className={selectedClass}
			alignItems="flex-start"
			onClick={handleAlbumListItemClick}>

			<ListItemAvatar sx={{ marginRight: "10px" }}>
				<Avatar src={url} variant="rounded" sx={{ width: "85px", height: "85px" }} />
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
						{a.title}
					</Typography>

				}
				secondary={
					<Typography
						sx={{ display: 'inline' }}
						component="span"
						variant="body2"
						color="text.primary">
						<br />
						{a.description != null && a.description.length != null && a.description.length > 0 ? a.description : null}
						{a.description != null && a.description.length != null && a.description.length > 0 ? <br /> : null}

						Photos: {a.photoCount} Videos: {a.videoCount} Views: {a.viewCount}
						<br />
						Created: {a.dateCreate != null ? Utils.formatUnixDate(a.dateCreate) : null}

					</Typography>
				}
			/>



			{/*<Divider variant="inset" component="li" />*/}
		</ListItemButton>
		</Grid>

					   
			
				        
				 	
	
		oa.push(e);
		i++;
	}
	
	return oa;
	
				     
	
} //end fn


 
  const pageChange = (event, value) => {
    setPage(value);
    setSelectedAlbumIndex([])
  };
	
	
	
	
	return (
		
		<>
		
		{wizardErrorMessage.length > 0 ? <Alert severity="error">{wizardErrorMessage}</Alert> : null }
		<OverlaySpinner show={isLoading} />

		
		<Grid container>
			<Grid size={12} mt="20px" mb="20px" >
			<Typography variant="h5" align="center" fontWeight="bold">
			Please select the albums that you want to liberate.
			</Typography> 
			<Typography align="center" mb="20px">
			You have {albumList!=null && albumList.total!=null ? albumList.total : ""} albums
			</Typography>
			
			</Grid>
			<Grid size={12}>
				<Paper elevation={0} style={{maxHeight: 300, overflow: 'auto'}}>
  					<Grid container>
  					
				        {renderAlbumList()}



   
  					</Grid>
				</Paper>
			</Grid>
			<Grid size={12} mt={2} sx={{ display: "flex", alignItems: "center",  justifyContent: "center" }}>
				<Pagination align="center" count={numPages} page={page} color="primary" onChange={pageChange} />
			</Grid>

		
		</Grid>		
		
       </>
		
		
	)
	



	
	
};