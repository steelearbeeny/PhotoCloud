import React, {useState, useEffect} from 'react';


export default function OverlaySpinner(props)
{
	if(props.show==true)
	{
		return (
				<div id="preloader">
        <div className="preloader">
            <span></span>
            <span></span>
        </div>
    </div>
			
		)
		
	}
	else{
		return null;
	}	
	
	
	
}