import React, {memo, useState, useEffect} from 'react';
//import 'bootstrap/dist/css/bootstrap.min.css';


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
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import FormControl from '@mui/material/FormControl';
import FormLabel from '@mui/material/FormLabel';
import Select from '@mui/material/Select';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';










export default function SetupJob(props) {
	
	
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
	//const [albumList, setAlbumList] = useState({});
	const [selectedAlbumIndex, setSelectedAlbumIndex] = useState([]);

	const [createAlbumSwitch, setCreateAlbumSwitch] = useState(true);
	const [categorizeSwitch, setCategorizeSwitch] = useState(false);
		
	const [albumNamePrefix, setAlbumNamePrefix] = useState("")	
		const [albumNameSuffix, setAlbumNameSuffix] = useState("")	
	const [autoCreateRadio, setAutoCreateRadio] = useState("auto")
	const [dateFormat, setDateFormat] = useState("_")
	const [albumName, setAlbumName] = useState("")		
		const [jobContent, setJobContent] = useState({});
		const [exifMetadata, setExifMetadata] = useState("_");
		const [exifDirectory, setExifDirectory] = useState("_");
		
	
	//if(	!(/*props.currentStep==3 && props.selectedTargetProvider!=0 ||*/
	//	props.currentStep==4 && props.selectedTargetProvider==0))
	//		return null;
		
	var isXSMedia=Utils.IsXSMedia();
		/*
	if(!ShowPage.shouldShow("SetupJob",
			props.currentStep, 
			props.selectedSourceProvider, 
			props.selectedTargetProvider))
	return;	
		*/
		
		props.setNextEnabled(true)
		
console.log("SetupJob",props)


	
/***********************************/	
		

	
/***********************************/	
	const inputChange = (e,z) => {

		console.log("input Cha",z,e,e.target,e.target.id,e.target.value,!e.target.value,e.target.value=="")
		
		
		
		if (!e || !e.target || e.target.value==null)
			return;

		let o = {...props.jobParameters}
		
	

		if (e.target.id == "album-name-prefix") {
			setAlbumNamePrefix(e.target.value)
			o["albumprefix"]=e.target.value
			o["autocreate"]=autoCreateRadio
			props.setJobParameters(o)
			
			return;
		}
		
		if (e.target.id == "album-name-suffix") {
			setAlbumNameSuffix(e.target.value)
			o["albumsuffix"]=e.target.value
			o["autocreate"]=autoCreateRadio
			props.setJobParameters(o)
			
			return;
		}

		if (e.target.id == "album-name") {
			setAlbumName(e.target.value)
			o["albumname"]=e.target.value
			o["autocreate"]=autoCreateRadio
			props.setJobParameters(o)
			return;
		}
		

	};
/***********************************/	
const handleRadioChange =(e) => {
	
		console.log("Radio Cha",e.target.id,e.target.value, e.target.defaultValue)
		setAutoCreateRadio(e.target.value);

		let o = {...props.jobParameters}
		
		o["autocreate"]=e.target.value;

		props.setJobParameters(o)
}


/***********************************/	
	const keyUpEvent = (e) => {
		
		//if(e.key=="Enter")
		//	searchClick(null);
	}


 /***********************************/	
const dateFormatChange = (e) =>
	{	
	
		setDateFormat(e.target.value)
		let o = {...props.jobParameters}
		
		o["dateformat"]=e.target.value
		o["autocreate"]=autoCreateRadio
		
		props.setJobParameters(o)
	}
 /***********************************/	
const exifMetadataChange = (e,ele) =>
	{	
	
	
	//let z = e.target.getAttribute('data-diretory');
		console.log("Change",e.target.value, ele.props["data-directory"])
		let dir = ele.props["data-directory"];
		
		
		setExifMetadata(e.target.value)
		setExifDirectory(dir)
		
		let o = {...props.jobParameters}
		
		o["exifmetadata"]=e.target.value
		o["autocreate"]=autoCreateRadio
		o["exifdirectory"]=dir
		
		
		props.setJobParameters(o)
		
	}
		/***********************************/	
	
	
	
	const renderMetadataSelect = () => {
		
		let a=[]
			
			
		    //<MenuItem value="yyyy-MM MMMM">Year-Month Month Name (ex: 2025-03 March)</MenuItem>

			if(props.jobContent==null || 
			props.jobContent.metadata==null || 
			props.jobContent.metadata.length < 1)
			{
				a.unshift(<MenuItem key="_" value="_">Don&apos;t use photo metadata in album names</MenuItem>)
	
				return a;
			}
			
			a=props.jobContent.metadata.map((o) =><MenuItem key={o.metadataid} data-directory={o.metadatadirectory} value={o.metadataid}>{o.metadatakey} - ({o.count} distinct values - ex: {o.samplevalue})</MenuItem> )
			
			a.unshift(<MenuItem key="_" value="_">Don&apos;t use photo metadata in album names</MenuItem>)
			
			return a;
			
			
	}
	
	const generateAlbumName = () => {
		
		let date = new Date();
		
		let monthName = date.toLocaleString('default', { month: 'long' });
		let year =  date.getYear()+1900;
		//let day = ("0" + date.getDate()).slice(-2)
		let mmm=date.getMonth()+1;
		let month = ("0" + mmm).slice(-2)
		
		//console.log(date,date.getMonth(), mmm,month)
		
		
		let a = "";
		
		
		if(albumNamePrefix.length > 0)
			a=albumNamePrefix + " ";
			
		if(dateFormat=="yyyy-MM MMMM")
			a=a+year + "-" + month + " " + monthName;
			
		if(dateFormat=="MMMM yyyy")
			a=a+monthName + " " + year;
			
		//console.log(exifMetadata,props.jobContent.metadata)	
		if(exifMetadata!="_")
		{
			console.log("in")
			let i=0
			let x;
			for(i=0;i<props.jobContent.metadata.length;i++)
			{
				
				//console.log(x)
				x=props.jobContent.metadata[i];
				
				if(x.metadataid==exifMetadata)
				{
					//console.log("found",a)
					a=a+ " " +  x.samplevalue;
					break;
				}
			}

		}
		
		return a
		
	}
	
	
		
	
	
	return (
		
		<>
		{wizardErrorMessage.length > 0 ? <Alert severity="error">{wizardErrorMessage}</Alert> : null }
				<OverlaySpinner show={isLoading} />

		<Grid container spacing={0} >
			<Grid size={12} mt="20px" mb="20px"  >
				<Typography variant="h5" align="center" fontWeight="bold">
				Setup Job - Current Step: {props.currentStep} Photo Count: {props.jobContent.filecount}
				</Typography> 
				
				
			</Grid>
			
			{autoCreateRadio=="auto" ? <Grid item container alignItems="center" justifyContent="center" lg={12}>
			<Typography variant="h6" align="center" fontWeight="bold">Sample Album Name:  {generateAlbumName()}</Typography></Grid> : null}

			
			{/*
			<Grid size={12}>
				<FormControlLabel control={<Switch id="ALBUM" checked={createAlbumSwitch} onChange={(e) => switchChange(e)} />} label="Create Albums" />
			</Grid>
			*/}
			
			<Grid size={12} container 
				alignItems="center"
				justifyContent="center" >
			
				<FormControl>
			      <FormLabel id="demo-row-radio-buttons-group-label">Auto Create Albums ?</FormLabel>
			      <RadioGroup
			        row
			        name="row-radio-buttons-group"
			        value={autoCreateRadio}
    				onChange={handleRadioChange}
			      >
			        <FormControlLabel value="none" control={<Radio />} label="Dont autocreate albums" />
			        <FormControlLabel value="single" control={<Radio />} label="Create single album" />
			        <FormControlLabel value="auto" control={<Radio />} label="Autocreate albums" />
			      
			      </RadioGroup>
			    </FormControl>
			
			</Grid>
			
			{autoCreateRadio=="single" ? <>
			
			<Grid size={{xs: 0, sm:3}} ></Grid>
			
			{isXSMedia==true ? null : 
				<Grid size={{xs:0, sm: 2}} container justifyContent="flex-end" alignItems="center">
					<FormLabel  sx={{p: 1}}>Album Name</FormLabel>
				</Grid> 
			}
			
			<Grid size={{xs:12, sm:4}} >
			
				<TextField
	                margin="dense"
	                fullWidth
	                id="album-name"
	                label="Album Name"
	                name="album-name"
	                autoFocus
	                size="small"
	                value={albumName}
	                onChange={inputChange}
	                onKeyUp={keyUpEvent}

	              />
			
			</Grid>
			<Grid size={{xs:0, sm: 3}} ></Grid>
			
			</> : null}


		{autoCreateRadio=="auto" ? <>
			
			<Grid size={{xs:0, sm:3}} ></Grid>
			
			{isXSMedia==true ? null : 
				<Grid size={2} container justifyContent="flex-end" alignItems="center">
					<FormLabel sx={{p: 1}}>Album Name Prefix</FormLabel>
				</Grid>
			}
			
			<Grid size={{ xs:12 , sm:4}}  >
			
				<TextField
	                margin="dense"
	                fullWidth
	                id="album-name-prefix"
	                label="Album Name Prefix"
	                name="album-name-prefix"
	                autoFocus
	                size="small"
	                value={albumNamePrefix}
	                onChange={inputChange}
	                onKeyUp={keyUpEvent}

	              />
			
			</Grid>
			<Grid size={{xs: 0, sm:  3}}></Grid>
			
			
			<Grid size={{xs: 0,  sm: 3}} ></Grid>
			
			{isXSMedia==true ? null : 
				<Grid size={2} container justifyContent="flex-end" alignItems="center">
					<FormLabel sx={{p: 1}}>Date Format</FormLabel>
				</Grid> }
			
			<Grid size={{xs: 12, sm: 4}} >
			    
				<TextField
					select
				    id="date-format"
				    value={dateFormat}
				    onChange={dateFormatChange}
				    label="Date Format"
				    size="small"
				    margin="dense"
				    fullWidth
				  >
				  <MenuItem value="_">Don&apos;t use photo dates in album names</MenuItem>
				    <MenuItem value="yyyy-MM MMMM">Year-Month Month Name (ex: 2025-03 March)</MenuItem>
				    <MenuItem value="MMMM yyyy">Month Name Year (ex: March 2025)</MenuItem>
				  </TextField>
			
			</Grid>
			<Grid size={{xs:0, sm:3}} ></Grid>
			
			
			
			
			<Grid size={{xs: 0, sm: 3}} ></Grid>
			
			{isXSMedia==true ? null : 
				<Grid size={2} container justifyContent="flex-end" alignItems="center">
					<FormLabel sx={{p: 1}}>EXIF Metadata</FormLabel>
				</Grid>}
			
			<Grid size={{xs:12, sm:4}} >
			    
				<TextField
					select
				    id="exif-metadata"
				    value={exifMetadata}
				    onChange={exifMetadataChange}
				    size="small"
				    margin="dense"
				    label="EXIF Metadata"
				    fullWidth
				  >
						{renderMetadataSelect()}
				  </TextField>
			
			</Grid>
			<Grid size={{xs: 0 , sm:3}} ></Grid>
			
			
			
					<Grid size={{ xs:0, sm: 3}} ></Grid>
			{isXSMedia==true ? null : 
				<Grid size={2} container justifyContent="flex-end" alignItems="center">
					<FormLabel sx={{p: 1}}>Album Name Suffix</FormLabel>
				</Grid>}
			
			<Grid size={{ xs: 12, sm: 4}}  >
			
				<TextField
	                margin="dense"
	                fullWidth
	                id="album-name-suffix"
	                label="Album Name Suffix"
	                name="album-name-suffix"
	                autoFocus
	                size="small"
	                value={albumNameSuffix}
	                onChange={inputChange}
	                onKeyUp={keyUpEvent}

	              />
			
			</Grid>
			<Grid size={{xs: 0 , sm:3}}></Grid>
			
			
			</> : null}

		
			
			
			
		
		</Grid>		
		
       </>
		
		
	)
	


	
	
};