import React, {useState, useEffect} from 'react';
import Main from './Main';
import Login from './Login';
import * as Utils from './Utils/Utils.js'
import 'bootstrap/dist/css/bootstrap.min.css';
import OverlaySpinner from './Spinner/OverlaySpinner.js';

function MainSwitch(props) {
	
	
			const [isLoggedIn, setIsLoggedIn] = useState(0);
			//=0 not check yet, 1=not logged in 2=logged in
			
	useEffect(() => {
    
    Utils.loadData("Authorization",{query: "isloggedin"},isLoggedInCallback)
  }, []); //run this code when this changes


	const isLoggedInCallback = (o) => {
		
		
			
		if(o!=null && o.isLoggedIn!=null && o.isLoggedIn==true)
		{
			setIsLoggedIn(2)
			return;
		}
		
		setIsLoggedIn(1)
		
	}


		
		const toggleLogin = (e) => {
			console.log("MainSwitch",e);
			
			if(e.returnCode==0)
				setIsLoggedIn(2);
			else
				setIsLoggedIn(1);
		}
		
		
		
		
		const returnComponent = () => {
		/*
		if(isLoggedIn==true)
			return <Main />
		else
			return <Login setLogin={toggleLogin} />
			*/
			
		console.log("returnComponet",isLoggedIn)
			
		if(isLoggedIn==0)
		{
			return (<div><OverlaySpinner show={true} /></div>)
		}
		
		if(isLoggedIn==2)
		{
			return <Main />
		}
		
		return <Login setLogin={toggleLogin} />
		
}


	console.log("MainSwitch", isLoggedIn)


	return (
		<>
		{returnComponent()}
		</>
	);


	
}

export default MainSwitch;
