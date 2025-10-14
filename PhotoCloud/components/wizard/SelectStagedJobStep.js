import React, {memo, useState, useEffect, useRef} from 'react';

import * as ShowPage from "../Utils/ShowPage.js";

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
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import moment from 'moment';




export default function SelectStagedJobStep(props) {
	
	
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
	const [authValid, setAuthValid] = useState(false);
	const [selectedJob, setSelectedJob] = useState("");
	
	
	
	//const prevStepRef = useRef(0);	//Changing this doesnt force a re-render
	
	var windowHandle=null;
	


	
	useEffect(() => {
    
    //prevStepRef.current = props.currentStep;
  }, [props.currentStep]); //run this code when this changes
	
	
	//0==FLICKR
	//sourceprovider 3000 = staged files
	//if(props.currentStep<1 || props.currentStep >= 2 || props.selectedSourceProvider!=3000) return null;
	/*
	if(!ShowPage.shouldShow("SelectStagedJobStep",
				props.currentStep, 
				props.selectedSourceProvider, 
				props.selectedTargetProvider))
		return;
	*/
		
	
	console.log("SelectStagedJobmStep",props,albumUser, albumList)
	
	
	
	
	//const prevCurrentStep=usePrevious(props.currentStep)
	
	
	
	
	
		






const rowOnClick = (e,f) => {

	let jobid=e.currentTarget.dataset.jobid
	let o = {...props.jobParameters}

	o["stagedJobId"]=jobid
	props.setJobParameters(o)

	setSelectedJob(jobid);
	props.setNextEnabled(true);
}


const renderTableRows = () => {
	
	let a=[];
	let i=0;
	let d;
	let fmt;
	let style = {}
	
	for(i=0;i<props.stagedJobs.length;i++)
	{
		d=moment(props.stagedJobs[i].modtime)
		fmt=d.format("dddd, MMMM Do YYYY, h:mm:ss a")+ ' (' + d.fromNow() + ')'
		
		if(selectedJob!=null && selectedJob==props.stagedJobs[i].jobid)
			style={backgroundColor: "#e8f0fd"}
		else
			style={}
		
		//console.log("row",selectedJob,props.stagedJobs[i].jobid)
		
		a.push(
			   <TableRow
			   	  onClick={rowOnClick}
	              key={props.stagedJobs[i].jobid}
	              data-jobid={props.stagedJobs[i].jobid}
	              sx={style}
	            >
	              <TableCell>{props.stagedJobs[i].userdescription}</TableCell>
	              <TableCell>{props.stagedJobs[i].description}</TableCell>
	              <TableCell>{fmt}</TableCell>
	              <TableCell align="right">{props.stagedJobs[i].numphotos}</TableCell>
	           </TableRow>
			
		);
		
	}
	//<TableCell>{props.stagedJobs[i].jobid}</TableCell>
	return a;
	

}

	
	
	return (
		<Grid container>
		<OverlaySpinner show={isLoading} />
	
			<Grid size={12} mt="20px" mb="20px" >
	{wizardErrorMessage.length > 0 ? <><Alert severity="error">{wizardErrorMessage}</Alert><br /></>: null }

			<Typography variant="h5" align="center" fontWeight="bold">
			Lets find the staged job that you&apos;re looking for
			</Typography> 
			<Typography align="center" mb="20px">
			Here is a list of the photos you have staged in the past, but have not yet processed. 
			We&apos;er holding these photos for you. Please click on the batch of staged photos that you want to free and click Next.
			</Typography>
			</Grid>
			
			<Grid size={12}>
			
			    <TableContainer component={Paper}>
			      <Table size="small">
			        <TableHead>
			          <TableRow sx={{backgroundColor: "#1976d3"}}>
			            <TableCell sx={{color: "white", fontWeight: "bold"}}>Job Description</TableCell>
			            <TableCell sx={{color: "white", fontWeight: "bold"}}>Source</TableCell>
			            <TableCell sx={{color: "white", fontWeight: "bold"}}>Creation Date</TableCell>
			            <TableCell sx={{color: "white", fontWeight: "bold"}}>Photos</TableCell>
			            
			          </TableRow>
			        </TableHead>
			        
			        
			        <TableBody>
						{renderTableRows()}
			        </TableBody>
			        
			        
			        
			        
			        
			        </Table>
			
					</TableContainer>
			
			</Grid>
		

		

		</Grid>

	)
	
	
	
	
	
	} // end component
