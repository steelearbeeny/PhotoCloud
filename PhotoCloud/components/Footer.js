import * as React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import Typography from '@mui/material/Typography';
import Grid from '@mui/material/Grid';



function FooterLink(props) {
	return (
		
		<div style={{ marginTop: "20px"}} >
		<a className="footer-link">{props.title}</a>
		</div>
		
			)
}

export default function Footer() {
  return (

     	
      	    <Grid container sx={{
            	backgroundColor: "black" }}>
     		<CssBaseline />

			<Grid mt="100px" size={12} />
			
			<Grid size={1} />
			
						<Grid size={{xs: 12, sm: 4}}>
						<img src="./images/Logo.png" style={{height: "30px", width: "200px" }} />
						<Typography variant="h6" sx={{color : "grey", fontWeight : "100" }}>
              				Copyright &copy; 2024 FotoFreedom.com
           				 </Typography>
						</Grid>
						<Grid size={{xs:12, sm: 3}}>
						<Typography sx={{fontSize: "1.2em", color : "white" , fontWeight : "bold"}}>
              				ABOUT
           				 </Typography>
           				 
           				 
           				 
           				
           				 
           				 <FooterLink title="About FotoFreedom" />
           				 <FooterLink title="Privacy Policy" />
           				 <FooterLink title="Terms of Service" />
           				 
           				 <FooterLink title="Cookie Policy" />
						</Grid>
						<Grid size={{ xs:12, sm: 4}}>
						<Typography variant="h7" sx={{color : "white", fontWeight : "bold" }}>
              				SOCIAL
           				 </Typography>
						</Grid>
			
			
			
			     		
			<Grid mt="100px" size={12} />

     		</Grid>
        
      
  
  );
}