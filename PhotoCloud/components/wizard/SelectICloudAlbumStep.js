import React, {useState, useEffect, Fragment} from 'react';
//import 'bootstrap/dist/css/bootstrap.min.css';

//import * as Utils from "../Utils/Utils.js";
//import * as ShowPage from "../Utils/ShowPage.js";
//import Button from '@mui/material/Button';
//import CssBaseline from '@mui/material/CssBaseline';
//import TextField from '@mui/material/TextField';
//import FormControlLabel from '@mui/material/FormControlLabel';
//import Checkbox from '@mui/material/Checkbox';
//import Link from '@mui/material/Link';
//import Paper from '@mui/material/Paper';
//import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
//import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
//import Footer from '../Footer.js';
//import CameraEnhanceOutlinedIcon from '@mui/icons-material/CameraEnhanceOutlined';
//import SchoolOutlinedIcon from '@mui/icons-material/SchoolOutlined';
//import Modal from 'react-bootstrap/Modal';
import 'bootstrap/dist/css/bootstrap.min.css';
/*
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import CloseIcon from '@mui/icons-material/Close';
import FastRewindIcon from '@mui/icons-material/FastRewind';
import FastForwardIcon from '@mui/icons-material/FastForward';
import SearchIcon from '@mui/icons-material/Search';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
*/
import Divider from '@mui/material/Divider';
import ListItemText from '@mui/material/ListItemText';
import ListItemAvatar from '@mui/material/ListItemAvatar';
import Avatar from '@mui/material/Avatar';
import ListItemButton from '@mui/material/ListItemButton';
/*
import FormGroup from '@mui/material/FormGroup';
import Switch from '@mui/material/Switch';
import Alert from '@mui/material/Alert';
import OverlaySpinner from '../Spinner/OverlaySpinner.js';
*/
import Swal from 'sweetalert2';
import moment from 'moment';
import ExifReader from 'exifreader';











export default function SelectdICLoudAlbumStep(props) {
	
	/*
	const [showModal, setShowModal] = useState(false);
	const [currentStep, setCurrentStep] = useState(0);
    const [selectedIndex, setSelectedIndex] = useState(-1);
    	const [userName, setUserName] = useState("");
	const [password, setPassword] = useState("");
	const [userNameMessage, setUserNameMessage] = useState("");
	const [passwordMessage, setPasswordMessage] = useState("");
	const [errorMessage, setErrorMessage] = useState("");
	const [albumUser, setAlbumUser] = useState({});
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	*/
	//const [albumList, setAlbumList] = useState({});
	const [selectedAlbumIndex, setSelectedAlbumIndex] = useState([]);
	const [exifData, setExifData] = useState({})
	
	
	var maxPhotos=10
	//
	// This is not called as its own compoennt directly from Main,
	// It is instead a sub component of ICloudAlbumWizard Pages
	// So it doesnt use the view state
	//

	useEffect(() => {
    
    		let albumList=props.albumList;
	
	if(albumList==null || albumList.images==null || Object.keys(albumList.images).length <1)
		return;
		

	let imKey;
		
	let imageList;
	
	
	props.nextEnabledCallback(true);
	

		
	if(albumList.thumbnails != null && Object.keys(albumList.thumbnails).length > 0)
		imageList=albumList.thumbnails;
	else
		imageList=albumList.images;
	
	
	if(imageList==null)
	{
		return;
	}
	
	//console.log("Found List",imageList)
	
	let i=0
	let metadata={}
	let currentMetadataCount=0;
	
	if(Object.keys(imageList).length < maxPhotos)
		maxPhotos=Object.keys(imageList).length
	
	
	for(imKey of Object.keys(imageList))
	{
		i++
		if(i>maxPhotos) break;
		
    	ExifReader.load(imageList[imKey].url, {async: true}).then( (function(tags) {
            // The MakerNote tag can be really large. Remove it to lower
            // memory usage if you're parsing a lot of files and saving the
            // tags.
            let o={}
            delete tags['MakerNote'];

            // If you want to extract the thumbnail you can use it like
            // this:
            /*
            if (tags['Thumbnail'] && tags['Thumbnail'].image) {
                var image = document.getElementById('thumbnail');
                image.classList.remove('hidden');
                image.src = 'data:image/jpg;base64,' + tags['Thumbnail'].base64;
            }
			*/
			
			
			
			metadata[this]=tags
			currentMetadataCount++;
			
			console.log("IMG", this, tags, metadata, currentMetadataCount)
			
			if(currentMetadataCount==maxPhotos)
			{
				console.log("Setting Metadata",currentMetadataCount,metadata)
				setExifData(metadata)
			}
				
            // Use the tags now present in `tags`.
        }).bind(i)).catch(function (error) {
            // Handle error.metadata
            console.log("ERR",error)
        });
        
        }
        
        
    
  }, [props.albumList]); //run this code when this hanges
	
	
		
	
	

  const handleAlbumListItemClick = (event) => {
	  
	  let x;
	  let index;
	  let newArray;
	  let intx=0;
	  //console.log("Album Click",index,event,event.target,event.target.attributes,event.target.attributes.albumtextindex);
	  //console.log("Album Click",event.target.attributes.albumtextindex);
//x=event.target.attributes.albumtextindex;
//console.log("X",x);
	//console.log("attr",event.target.getAttribute("albumindex"));
	//console.log("attr",index)
	x=event.target.getAttribute("albumindex");
	event.target.classList.toggle("Mui-selected");
	
	newArray=[...selectedAlbumIndex];
	
	if(x==null)
		return;
		
	intx=parseInt(x);
	
	if(newArray.includes(intx))
		{
			
			index = newArray.indexOf(intx);
			//console.log("del",newArray,intx,index);

			newArray.splice(index, 1);
			setSelectedAlbumIndex(newArray)
			//console.log("after del",newArray);
		}
		else	
		{			
			//console.log("add",newArray);
			newArray.push(intx);
			setSelectedAlbumIndex(newArray);
			//console.log("after add",newArray);
		}
	
	
	if(newArray.length > 0)
		props.nextEnabledCallback(true);
	else
		props.nextEnabledCallback(false);
	
    //setSelectedAlbumIndex(x);
    //event.preventDefault();
  };


const GetExifDescription = (exif, index, key) =>
{
	let rv="";
	if(exif==null || exif[index]==null || exif[index][key]==null || exif[index][key].description==null)
		return "";
		
	return exif[index][key].description
}



const renderImages = () => {
	
	

	
	//console.log("RIM",props.albumList.images)
	let albumList=props.albumList;
	
	if(albumList==null || albumList.images==null || Object.keys(albumList.images).length <1)
		return <div>Please wait...searching for album</div>;
		
	let oa=[];	
	let e;
	let i=0;
	let url;	
	let t;
	let selectedClass;
	
	let thKey;
	let th=null;
	let imKey;
		
	let imageList;
	
	let createdDate;
	let relativeCreatedDate;
	let size;

		
	if(albumList.thumbnails != null && Object.keys(albumList.thumbnails).length > 0)
		imageList=albumList.thumbnails;
	else
		imageList=albumList.images;
	
	
	if(imageList==null)
	{
		return <div>Could not load album</div>
	}
	
	//console.log("Found List",exifData)
	
	i=0
	
	if(Object.keys(imageList).length < maxPhotos)
		maxPhotos=Object.keys(imageList).length
	
	
	for(imKey of Object.keys(imageList))
	{
		i++
		if(i>maxPhotos) break;
		
		relativeCreatedDate=imageList[imKey].dateCreated;
		createdDate = new moment(relativeCreatedDate);
		size=Math.round(imageList[imKey].fileSize / 1024)
		
		if(createdDate.isValid())
		{
			relativeCreatedDate=createdDate.format("dddd, MMMM Do YYYY") + " (" + createdDate.fromNow() + ")";
		}
		
		//console.log("ImageKey",imKey, imageList[imKey])
		/*
		var http = new XMLHttpRequest();
                http.onload = function() {
                    if (this.status == 200 || this.status === 0) {
                        console.log("loaded",this,http)
                    } else {
                        console.log( "Could not load image");
                    }
                    http = null;
                };
                //http.responseType = "arraybuffer";
                http.open("GET", imageList[imKey].url, false);
                
                http.send(null);
		*/
		
		//console.log("Exif[i]",i,exifData[i])
	
		
		e=<Fragment key={i}>
			<Grid size={{xs:4,sm:1}}>
				<Avatar src={imageList[imKey].url} variant="rounded" sx={{width:"75px", height: "75px"}} />
			</Grid>
			<Grid size={{xs:8,sm:5}}>
					<Typography variant="body2">
						{relativeCreatedDate}
						<br />
						{GetExifDescription(exifData,i,"FileType")} {size} KB
						<br />
						{GetExifDescription(exifData,i,"Make")} {GetExifDescription(exifData,i,"Model")}
						<br />
						{GetExifDescription(exifData,i,"LensModel")}

					</Typography>
			</Grid>
			
		</Fragment>
		
		oa.push(e)
		
			
		
		
	}
	
	return oa
}






/*
const renderAlbumList = () => {
	
	
	//console.log("RAL",props.albumList.images)
	let albumList=props.albumList;
	
	if(albumList==null || albumList.images==null || Object.keys(albumList.images).length <1)
		return <div>Please wait...searching for album</div>;
		
	let oa=[];	
	let e;
	let i=0;
	let url;	
	let t;
	let selectedClass;
	
	let thKey;
	let th=null;
	let imKey;
		
		
		
		
	//heading
	//oa.push(<div>Album Name: {albumList.name} </div>)
	//oa.push(<div>Creator: {albumList.creatorName} </div>)
	//oa.push(<div>Item count: {albumList.itemCount} </div>)
		
	if(albumList.thumbnails != null && Object.keys(albumList.thumbnails).length > 0)
	{
		thKey=Object.keys(albumList.thumbnails)[0];
		th=albumList.thumbnails[thKey];
		
		//oa.push(<div>{thUrl}</div>)
		
		//oa.push(<img src={th.url} width={th.width} height={th.height}/>)
		
		
	} //end if thumb
	
	
	imKey=Object.keys(albumList.images)[0]
	
	if(th.url==null)
	{
		th.url="images/icloudsm.png";
	}
	
	i++;
				          
		e=<ListItemButton
						key={i}
						albumindex={i}
						className={selectedClass}
				          alignItems="flex-start"
				          onClick={handleAlbumListItemClick}
				          
      					  >
				        <ListItemAvatar sx={{marginRight : "10px"}}>
				          <Avatar src={th.url} variant="rounded" sx={{width:"85px", height: "85px"}} />
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
				                	{albumList.name}
				                </Typography>
							  
						  }
				          secondary={
				              <Typography
				                sx={{ display: 'inline' }}
				                component="span"
				                variant="body2"
				                color="text.primary">
				              	<br />		                
				                {albumList.creatorName !=null && albumList.creatorName.length != null && albumList.creatorName.length > 0 ? <>{albumList.creatorName} <br /></> : null}
				                
				                Item Count: {albumList.itemCount} 
				                
				              </Typography>
				          }
				        />
				        
					   
					   
				        <Divider variant="inset" component="li" />
				        </ListItemButton>
		
	
		oa.push(e);
	
	
	
	
	
	
	
	
	
	return oa;
	
				     
	
} //end fn

*/
	
	//console.log("SelectIcouldAblumSteps",props.albumList);
	
	
	return (
		

		<Grid container spacing={2}>
			<Grid size={12} mt="20px" mb="20px" >
			<Typography variant="h6" align="center" fontWeight="bold">
			Here is a subset of the items in that album.<br />Please review them and confirm this is the album you want to liberate.
			</Typography> 
			{props.albumList !=null && props.albumList.images != null ? <>
			<Typography variant="h6" align="center" fontWeight="bold">
			{props.albumList.name}
			</Typography> 
			<Typography variant="body1" align="center">
			Creator: {props.albumList.creatorName}
			<br />
			Items: {props.albumList.itemCount}
			</Typography> 
			</> : null}
			
			</Grid>
			 {renderImages()}

			

			
		
		</Grid>		
		
              				
		
		
	)
	


	
	
};