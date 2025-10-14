import React, {memo, useState, useEffect} from 'react';
//import 'bootstrap/dist/css/bootstrap.min.css';
import MainMenu from "./Menu/MainMenu.js";
import Brand from "./Menu/Brand.js";
import * as Utils from "./Utils/Utils.js";
import * as ShowPage from "./Utils/ShowPage.js";
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Footer from './Footer.js';
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
import Alert from '@mui/material/Alert';
import OverlaySpinner from './Spinner/OverlaySpinner.js';
import Swal from 'sweetalert2';
import SelectAlbumsStep from './wizard/SelectAlbumsStep.js';
import ICloudAlbumWizardPages from './wizard/ICloudAlbumWizardPages.js';
import FlickrAccountSelect from './wizard/FlickrAccountSelect.js';
import SelectSourceStep from './wizard/SelectSourceStep.js';
import SetupJob from './wizard/SetupJob.js';
import ExecuteJob from './wizard/ExecuteJob.js';
import LocalFolderSelectionPage from './wizard/LocalFolderSelectionPage.js';
import SelectTargetStep from './wizard/SelectTargetStep.js';
import SelectStagedJobStep from './wizard/SelectStagedJobStep.js';
import GoogleAccountSelect from './wizard/GoogleAccountSelect.js';
import GoogleSelectAlbum from './wizard/GoogleSelectAlbum.js';





export default function Main(props) {
	
	
	const [showModal, setShowModal] = useState(false);
	const [currentStep, setCurrentStep] = useState(0); //initially 0
    const [selectedSourceProvider, setSelectedSourceProvider] = useState(-1); //initially 1
	const [errorMessage, setErrorMessage] = useState("");
	const [wizardErrorMessage, setWizardErrorMessage] = useState("");
	const [isLoading, setIsLoading] = useState(false);
	//const [albumList, setAlbumList] = useState({});
	const [nextEnabled, setNextEnabled] = useState(false);
	const [jobParameters, setJobParameters] = useState({});
	const [steps, setSteps] = useState(ShowPage.stepList(0));
    const [selectedTargetProvider, setSelectedTargetProvider] = useState(-1); //initially 1
 	const [jobContent, setJobContent] = useState({})
 	const [stagedJobs, setStagedJobs] = useState([]);
 	
 	//const [cancelButtonText, setCancelButtonText] = useState("Cancel");
 	

	
	
/*
	const hstr = navigator.userAgent + " " + (new Date()).getTime() + " " + Math.random();
	const uniqueId=Utils.hash(hstr)
	console.log("Navigator ", navigator, hstr, uniqueId)
*/

	var isXSMedia=Utils.IsXSMedia();


	useEffect(() => {
          	
		Utils.loadData("LocalFile",{query : "getstagedjobs" }, setStagedJobs, setErrorMessage, setIsLoading)

		
		//var theme = useTheme();
  		//console.log("Theme", theme)
		//console.log("Breakpoint", theme.breakpoints.up("xs"))
		//console.log("Is XS", useMediaQuery('(min-width:0px)'))
		
		//setIsXS(!(useMediaQuery('(min-width:0px)')));
	
	//alert(useMediaQuery(theme.breakpoints.up("sm")))
 
	}, []);

	const wizardPages = {
		'SelectSourceStep' : SelectSourceStep,
		'SelectTargetStep' : SelectTargetStep,
		'SelectStagedJobStep' : SelectStagedJobStep,
		'LocalFolderSelectionPage' : LocalFolderSelectionPage,
		'FlickrAccountSelect' : FlickrAccountSelect,
		'GoogleAccountSelect' : GoogleAccountSelect,
		'SelectAlbumsStep' : SelectAlbumsStep,
		'GoogleSelectAlbum' : GoogleSelectAlbum,
		'ICloudAlbumWizardPages' : ICloudAlbumWizardPages,
		'SetupJob' : SetupJob,
		'ExecuteJob' : ExecuteJob
	};



/*
	const IsXSMedia = () => {
		let rv = false;
		
		rv = useMediaQuery('(min-width:600px)')
		//console.log("Is XS", rv , !rv)
		//alert(rv)
		
		return !rv;
		
	}
*/
	


  const handleAlbumListItemClick = (event) => {
	  
	  let x;
	  //console.log("Album Click",index,event,event.target,event.target.attributes,event.target.attributes.albumtextindex);
	  //console.log("Album Click",event.target.attributes.albumtextindex);
//x=event.target.attributes.albumtextindex;
//console.log("X",x);
	console.log("attr",event.target.getAttribute("albumtextindex"));
	//console.log("attr",index)
	x=event.target.getAttribute("albumtextindex");
    setSelectedAlbumIndex(x);
    event.preventDefault();
  };





const setSelectedSourceProviderCallback = (src) => {

	let st = ShowPage.stepList(src);
	setSteps(st);

	setSelectedSourceProvider(src)

}


	const returnStepForm = () => {
		let rv;
		let Component=null;
		//
		//Get the component from the pageMatrix
		//and retrieve it from the map
		//and return it
		//
		rv=ShowPage.getComponent(currentStep,selectedSourceProvider,selectedTargetProvider)
		
		//console.log("return step", rv)
		
		
		
		if(rv!=null && rv.found==true && rv.component in wizardPages)
		{
			Component=wizardPages[rv.component];
			return <Component 
						setSelectedSourceProvider={setSelectedSourceProviderCallback} 
						selectedSourceProvider={selectedSourceProvider} 
						setSelectedTargetProvider={setSelectedTargetProvider} 
						selectedTargetProvider={selectedTargetProvider}
						setNextEnabled={setNextEnabled}
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						currentStep={currentStep} 
						stagedJobs={stagedJobs} 
						jobContent={jobContent}
						{...rv.props}  />

		}
		
		return <div>Error: Component not found</div>
		
	}


/*
	const ZZreturnStepForm = () => {
		
		//currentStep = step number
		//selectedSourceProvider = cloud provider selected in first step
		//0=flickr 1=aapl
		
		//Note these pages need to know when to show 
		//themselves by looking at the currentStap/sourceProvider
		
					
			return <>
			
					<SelectSourceStep 
						setSelectedSourceProvider={setSelectedSourceProviderCallback} 
						selectedSourceProvider={selectedSourceProvider} 
						selectedTargetProvider={selectedTargetProvider}
						setNextEnabled={setNextEnabled}
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						currentStep={currentStep} 
						stagedJobs={stagedJobs} />
		
					<SelectTargetStep 
						setSelectedTargetProvider={setSelectedTargetProvider} 
						selectedTargetProvider={selectedTargetProvider}
						selectedSourceProvider={selectedSourceProvider} 
						setNextEnabled={setNextEnabled}
						currentStep={currentStep} />
		
		
					<SelectStagedJobStep 
						setNextEnabled={setNextEnabled} 
						selectedSourceProvider={selectedSourceProvider} 
						selectedTargetProvider={selectedTargetProvider}
						currentStep={currentStep}
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						stagedJobs={stagedJobs}
						/>
		
		
					<LocalFolderSelectionPage 
						setNextEnabled={setNextEnabled} 
						selectedSourceProvider={selectedSourceProvider} 
						selectedTargetProvider={selectedTargetProvider}
						currentStep={currentStep}
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						/>
		
		
		
					<FlickrAccountSelect 
						selectedTargetProvider={selectedTargetProvider}
						selectedSourceProvider={selectedSourceProvider} 
						setNextEnabled={setNextEnabled}
						currentStep={currentStep} 
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						/>
						
					<GoogleAccountSelect 
						selectedTargetProvider={selectedTargetProvider}
						selectedSourceProvider={selectedSourceProvider} 
						setNextEnabled={setNextEnabled}
						currentStep={currentStep} 
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						/>
						
						
					<SelectAlbumsStep 
						selectedTargetProvider={selectedTargetProvider}
						selectedSourceProvider={selectedSourceProvider} 
						setNextEnabled={setNextEnabled}
						currentStep={currentStep} 
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						/>
						
					<GoogleSelectAlbum 
						selectedTargetProvider={selectedTargetProvider}
						selectedSourceProvider={selectedSourceProvider} 
						setNextEnabled={setNextEnabled}
						currentStep={currentStep} 
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						/>
						
						
						
					<ICloudAlbumWizardPages 
						setNextEnabled={setNextEnabled} 
						selectedSourceProvider={selectedSourceProvider} 
						selectedTargetProvider={selectedTargetProvider}
						currentStep={currentStep} 
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						/>
						
					<SetupJob
						setNextEnabled={setNextEnabled} 
						selectedSourceProvider={selectedSourceProvider} 
						selectedTargetProvider={selectedTargetProvider}
						currentStep={currentStep} 
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						jobContent={jobContent}
						/>
						
					<ExecuteJob
						setNextEnabled={setNextEnabled} 
						selectedSourceProvider={selectedSourceProvider} 
						selectedTargetProvider={selectedTargetProvider}
						currentStep={currentStep} 
						jobParameters={jobParameters}
						setJobParameters={setJobParameters}
						
						/>


						
					</>
				
		
		
	}
	*/
	
	const handleNextStep = (e, action) => {
		console.log("next",currentStep,action, selectedTargetProvider)
		
		if(action=="NEXT")
		{
			if(currentStep==steps.length-1)
			{
				console.log("FINISHED")
				setShowModal(false)
				return;
			}
			
			setNextEnabled(false);
			
			if(	!(currentStep+1==3 && selectedTargetProvider!=0 ||
				currentStep+1==4 && selectedTargetProvider==0))
			{
				console.log("GetContent skipping");
				//return;
			}
			else
			{
					let o = {...jobParameters}
					o["query"]="jobcontent"
					//o["selectedSourceProvider"]=props.selectedSourceProvider;
					//o["selectedTargetProvider"]=props.selectedTargetProvider;
					console.log("Getting Jon Content", o)
			  		
					Utils.loadData("JobManager",o, setJobContent, setWizardErrorMessage, setIsLoading, submitJobComplete);
			}
			
			setCurrentStep(currentStep+1)
		}
			
		if(action=="BACK")
		{
			setNextEnabled(false);
			
			if(currentStep==0)
				return;
			
			setCurrentStep(currentStep-1)
		}
		
	}
	
	const submitJobComplete = (e) => {
		console.log("JobContentComplete",e)
	}
	
	 const handleSubmit = (e) => {
    
    	e.preventDefault();
    	setCurrentStep(0);
    	setShowModal(true)
    }
	
	



	
	const handleClose = () => {
		
		setShowModal(false);
		
	}
	
	
	let cancelButtonDisabled=false;
	let cancelButtonText="CANCEL";
	let backButtonDisabled=false;
	
	let nextButtonText="NEXT";
	
	if(currentStep>=steps.length-1)
	{
		cancelButtonDisabled=false;
		
		if("uuid" in jobParameters && jobParameters.uuid && jobParameters.uuid.length >0)
		{
			nextButtonText="FINISH"
			backButtonDisabled=true
		}
	}
	

	
	
	
	return (
		<>
		
		
				
		<Modal show={showModal} onHide={handleClose} dialogClassName={isXSMedia==true ? "modal-100w" : "modal-70w"}>
        

        <Modal.Body >
        
        
        <Grid container >
        	<Grid size={12} sx={{borderBottom : "5px" }}>
       			<Typography variant="h6" sx={{ml: "5px" , mb : "10px"}}>
       			Start Liberating My Photos
         		</Typography>
        	
        	</Grid>
        	<Grid size={12}>
        		{isXSMedia==false ? 
	        	<Stepper activeStep={currentStep} >
	        	 {
					 steps.map((label,index) => {return (<Step key={index}><StepLabel>{label}</StepLabel></Step> )})
	        	 }
	        	</Stepper>
	        	:
	        		<Stepper activeStep={currentStep}>
	        			<Step index={currentStep}><StepLabel>{steps[currentStep]}</StepLabel></Step>
	        		</Stepper>
	        	}
        	
        	        {wizardErrorMessage.length > 0 ? <Alert severity="error">{wizardErrorMessage}</Alert> : null }

        	</Grid>
        	
	        	<Grid size={12}>
	        		Current Step {currentStep} Source {selectedSourceProvider} Target {selectedTargetProvider} StepLength {steps.length}
	        	</Grid>
	        	
        	</Grid>
    
        	<Box m="10px">
        		{returnStepForm()}
        	</Box>
        	
        </Modal.Body>
      
      
        <Modal.Footer>
        	<Grid container sx={{ flexGrow: 1 , ml: '10px' , mr: '30px' }}>
				<Grid size={{xs: 4, sm: 9}}>
		 			<Button	disabled={cancelButtonDisabled} onClick={handleClose} startIcon={<CloseIcon />}variant="contained">Cancel</Button>
				</Grid>
		
				<Grid size={{xs: 0 , sm: 1}} />
				
        		<Grid size={{xs:4, sm: 1}} textAlign="right" >
        		    {currentStep>0 ? <Button disabled={backButtonDisabled} startIcon={<FastRewindIcon />}	onClick={(e) => handleNextStep(e,"BACK")} variant="contained">Back</Button> : null} 				
   				</Grid>
   				
   				<Grid size={{xs:4, sm:1}} >
        			<Button disabled={!nextEnabled} endIcon={<FastForwardIcon />} onClick={(e) => handleNextStep(e,"NEXT")} variant="contained" sx={{ ml: "10px" }}>{nextButtonText}</Button>
				</Grid>
				<Grid size={{xs:0, sm:1}} />
				
			</Grid>
 
        </Modal.Footer>
      </Modal>
		
		
		<OverlaySpinner show={isLoading} />
		       	{errorMessage.length > 0 ? <Alert severity="error">{errorMessage}</Alert> : null }
		
		    <Grid container sx={{
            	backgroundImage: 'url(./images/travelcamerabg1crop.jpg)',
            	backgroundRepeat: 'no-repeat',
           
            	backgroundSize: 'cover',
            	backgroundPosition: 'center',
           
          		}}>
     		<CssBaseline />
	     		<Grid size={12} >
					<MainMenu />
				</Grid>
	
			<Grid size={{ xs: 12, sm: 6}}  >
				<Grid container justifyContent="center" alignItems="center">
					<Grid size={{ xs: 0 , sm: 2}} />
					<Grid size={{ xs: 12, sm: 8}} sx={ { mt : "100px"}}>
						<Typography variant="h3" sx={ isXSMedia==true ? {ml: "20px", color : "MidnightBlue", fontWeight : "100" } :  {color : "MidnightBlue", fontWeight : "100" }}>
              				Welcome to <Brand />
           				 </Typography>
           				 
           				 <Typography variant="h4" sx={{"textAlign" : "center", mt: "30px", color : "white", fontWeight : "300" }}>
              				Click below to start freeing your photos, or to learn more.
           				 </Typography>
           				 
           				 
           				 <Box textAlign="center" mt="50px" >
           				 <Button
                			onClick={handleSubmit}
                			variant="contained"
                			startIcon={<CameraEnhanceOutlinedIcon />}
                			sx={{ backgroundColor : "MidnightBlue", m: 3 }}
              				>
                				Start Freeing My Photos
              				</Button>

						


              				
              				<Button sx={{ backgroundColor : "MidnightBlue", mt : "3" }}
                			onClick={handleSubmit}
                			variant="contained"
                			startIcon={<SchoolOutlinedIcon />}
              				>
                				Learn More
              				</Button>
              				</Box>
              				
           				 
					</Grid>
					<Grid size={{xs: 0, sm: 2}} />
				
				</Grid>
			
				
			</Grid>
			
			<Grid size={{ xs: 0 , sm: 6}} sx={{ mt: "700px"}} >
			
				
			</Grid>
	
	</Grid>
	<Footer />
	</>
		
	)
};