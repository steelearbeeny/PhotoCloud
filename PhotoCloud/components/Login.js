import React, {useState, useEffect} from 'react';
import Avatar from '@mui/material/Avatar';
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
import Footer from './Footer.js';
import Alert from 'react-bootstrap/Alert';
import * as Utils from './Utils/Utils.js'
import Swal from 'sweetalert2'
import OverlaySpinner from './Spinner/OverlaySpinner.js';






export default function Login(props) {
	
	
	const [userName, setUserName] = useState("");
	const [password, setPassword] = useState("");
	const [userNameMessage, setUserNameMessage] = useState("");
	const [passwordMessage, setPasswordMessage] = useState("");
	const [errorMessage, setErrorMessage] = useState('');
	const [isLoading, setIsLoading] = useState(false);
	const [newPassword, setNewPassword] = useState("");
	const [confirmPassword, setConfirmPassword] = useState("");
	const [showChangePassword, setShowChangePassword] = useState(false);
	const [isNewPWValid, setIsNewPWValid] = useState(false);





console.log(screen.width + " " + screen.height)


	const changePassword = (e) => {
	
		console.log("scp",showChangePassword)
	
		setShowChangePassword(!showChangePassword);
	
	};
	
const DoChangePassword = (e) => {
	
	
	if(newPassword.length < 8 || newPassword!=confirmPassword)
	{
		Swal.fire({
			title: 'Error!',
  			text: 'The new password was invalid',
  			icon: 'error',
  			confirmButtonText: 'Ok'
			
		})
		
		return;
	}
	
	
	
	Utils.postData("Authorization",{action: 'change', user: email, password: password, newpassword: newPassword }, changePasswordCallback, setErrorMessage, setIsLoading);

	
};	
	
	
	
	const loginCallback = (o) => {
		
		console.log("loginCall",o,props)

				
		if(props.setLogin)
			props.setLogin(o);
			
			
			
	}
	
  const handleSubmit = (e) => {
    
    e.preventDefault();
    let errorFlag=false;
    
       
    if(userName.length < 5)
    {
		setUserNameMessage("Invalid email address");
		errorFlag=true;
	}
	else
		setUserNameMessage("")
	
	if(password.length < 5)
	{
		setPasswordMessage("Invalid password");
		errorFlag=true;
	}
	else
		setPasswordMessage("")
    
        
    if(errorFlag==true)
    	return;
    	
    	
    setUserNameMessage("");
    setPasswordMessage("");
    
    
   Utils.postData("Authorization",{action: 'login', user: userName, password: password }, loginCallback, setErrorMessage, setIsLoading);

    
    
    
  };
  
  
  const inputChange = (e) => {
	  
	     console.log(e)
    if(!e || !e.target)
		return;
		
	if(!e.target.value)
	{
		
		return;
	}	
	
	
	if(e.target.id=="user-name")
	{
		setUserName(e.target.value)
		
	}
	
	if(e.target.id=="password")
		setPassword(e.target.value)
	
	
	
  };

  return (
	  <>
	  <OverlaySpinner show={isLoading} />
     <Grid container sx={{
            backgroundImage: 'url(./images/bg.jpg)',
            backgroundRepeat: 'no-repeat',
           
            backgroundSize: 'cover',
            backgroundPosition: 'center',
           
          }}>
     <CssBaseline />
     
     <Grid size={{xs: 12, sm: 2}}>
     	
     	<div style={{ "marginLeft" : "30px", "marginTop" : "20px", "color" : "white", "fontSize" : "35px", "fontWeight" : "normal"}} >
	     	
	     		<img src="./images/Logo.png" />
	     	
     	</div>

     </Grid>
     
     <Grid size={{ xs: 0, sm: 10}}></Grid>
     
     
     <Grid
          size={{xs: 12, sm: 7}}
          
        />
     
     
     <Grid size={{ xs: 12, sm: 4}} component={Paper} elevation={6} square>
          <Box
            sx={{
              my: 8,
              mx: 4,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
            }}
          >
          
     
          
            <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
              <LockOutlinedIcon />
            </Avatar>
            <Typography component="h1" variant="h5">
              Sign in
            </Typography>
            <Box component="form" noValidate sx={{ mt: 1 }}>
                     {errorMessage!=null && errorMessage.length > 0 ? <Alert variant="danger">{errorMessage}</Alert> : null}
	
              <TextField
                margin="normal"
                required
                fullWidth
                id="user-name"
                label="Email Address"
                name="user-name"
                autoComplete="email"
                autoFocus
                onChange={inputChange}
                error={userNameMessage.length > 0 ? true : false}
                helperText={userNameMessage}
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                autoComplete="current-password"
                onChange={inputChange}
                error={passwordMessage.length > 0 ? true : false}
                helperText={passwordMessage}
              />
              <FormControlLabel
                control={<Checkbox value="remember" color="primary" />}
                label="Remember me"
              />
              <Button
                onClick={handleSubmit}
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
              >
                Sign In
              </Button>
              <Grid container>
              <Grid size={1} />
                <Grid size={5}>
                  <Link href="#" variant="body2">
                    Forgot password?
                  </Link>
                </Grid>
                <Grid size={5}>
                  <Link href="#" variant="body2">
                    Don&apos;t have an account?
                  </Link>
                </Grid>
                <Grid size={1} />
              </Grid>
              
            </Box>
          </Box>
        </Grid>
        
        <Grid size={{xs:0 , sm: 1}}  />
        
        <Grid size={12}  sx={{mt: "100px"}} />
         
     
     </Grid>
     
    	<Footer />
    	</>
  );
}