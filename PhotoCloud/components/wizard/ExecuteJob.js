import React, {memo, useState, useEffect} from 'react';
//import 'bootstrap/dist/css/bootstrap.min.css';

import * as Utils from "../Utils/Utils.js";
import * as ShowPage from "../Utils/ShowPage.js";
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import 'bootstrap/dist/css/bootstrap.min.css';
import Alert from '@mui/material/Alert';
import OverlaySpinner from '../Spinner/OverlaySpinner.js';
import Swal from 'sweetalert2';



export default function ExecuteJob(props) {
	
	
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

	const [copyAlbumSwitch, setCopyAlbumSwitch] = useState(false);
	const [categorizeSwitch, setCategorizeSwitch] = useState(false);
	const [jobStatus, setJobStatus] = useState({});
		
		/*	
		if(	!(props.currentStep==4 && props.selectedTargetProvider!=0 ||
			props.currentStep==5 && props.selectedTargetProvider==0))
				return null;
		*/
	
	var isXSMedia=Utils.IsXSMedia();
	/*
		if(!ShowPage.shouldShow("ExecuteJob",
				props.currentStep, 
				props.selectedSourceProvider, 
				props.selectedTargetProvider))
		return;
	*/
	
		console.log("ExecuteJob",props)
	
	
	
	//props.setNextEnabled(false)
	

const submitJobComplete = (url, rv) => 
{
	console.log("Submit Complete",rv)
	
	let o = {...props.jobParameters}
	
	o["uuid"]=rv.uuid;
	
	props.setJobParameters(o)
	props.setNextEnabled(true)
}
	
	
/***********************************/	
const executeClick = (e) => {
	
	
	let o = {...props.jobParameters}
	o["query"]="submitjob"
	o["selectedSourceProvider"]=props.selectedSourceProvider;
	o["selectedTargetProvider"]=props.selectedTargetProvider;
	
	props.setJobParameters(o)
	
	console.log("executeClick",o)
	
	
	Utils.loadData("JobManager",o, setJobStatus, setWizardErrorMessage, setIsLoading, submitJobComplete);
		
	
};


/***********************************/	

	
	return (
		
		<>
		       	{wizardErrorMessage.length > 0 ? <Alert severity="error">{wizardErrorMessage}</Alert> : null }
				<OverlaySpinner show={isLoading} />
		<Grid container>
			<Grid size={12} mt="20px" mb="20px" >
			<Typography variant="h5" align="center" fontWeight="bold">
			Execute Job	
		</Typography> 

			
			</Grid>
	
			
			{jobStatus.uuid != null ? <>
		
			
					<Grid size={{xs:0, sm:2}} />
					<Grid size={{xs:12, sm:2}}>
						<img src="images/check.png"  style={{ margin: "10px" }} alt="Success" />
					</Grid>
					<Grid size={{xs:12, sm:7}}
						sx={{ display: "flex", alignItems: "center" }}>
						<Grid container>
						<Grid size={12}>
						<Typography sx={{ cursor: "pointer" }} variant="h5" align="center" fontWeight="bold">
							Your job has been submitted successfully
						</Typography>
						</Grid>
						<Grid size={12}>
						
						<Typography align="center" >
						You can check the job status using the menu option above.<br />The job ID is: {jobStatus.uuid}
						</Typography>
						</Grid>
						</Grid>
						
					</Grid>
					<Grid size={{xs:0, sm:1}} />
					</>
			
			: 
			
			<>
			
			
			<Grid size={12}  >
			<Typography align="center" >
			Your FotoFreedom job has been successfully created.<br />You can start running the job by clicking the execute button below.	

		</Typography> 

			
			</Grid>
			
			
			{isXSMedia==true ?
			
			<>
					

					<Grid size={12}>
						
						<Typography sx={{ cursor: "pointer"}} onClick={executeClick} variant="h5" align="center" fontWeight="bold">
							Click Here to Execute Your Job
						</Typography>
					</Grid>
					
					<Grid size={2} />
					<Grid size={8}>
						<img src="images/gear.png" onClick={executeClick} style={{ cursor: "pointer"}} alt="Click to authorize Flickr" />
					</Grid>
					<Grid size={2} />
					
				</>
			
			
			:
			<>
					<Grid size={3} />
					<Grid size={2}>
						<img src="images/gear.png" onClick={executeClick} style={{ width: "100px", cursor: "pointer", margin: "20px" }} alt="Click to authorize Flickr" />
					</Grid>
					<Grid size={6}
						sx={{ display: "flex", alignItems: "center" }}>
						<Typography sx={{ cursor: "pointer", paddingLeft: "30px" }} onClick={executeClick} variant="h5" align="center" fontWeight="bold">
							Click Here to Execute Your Job
						</Typography>
					</Grid>
					<Grid size={1} />
				</>
			}
			
			</> }
			

			
		
		</Grid>		
		
       </>
		
		
	)
	


	
	
};