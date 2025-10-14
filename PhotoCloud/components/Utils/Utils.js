import axios from 'axios';
import dayjs from 'dayjs';
import useMediaQuery from '@mui/material/useMediaQuery';

export const formatUnixDate = (seconds) => {
	
	return dayjs.unix(seconds).format("dddd, MMMM D YYYY h:mm:ss A" )
}


export const IsXSMedia = () => {
		let rv = false;
		
		rv = useMediaQuery('(min-width:600px)')
		//console.log("Is XS", rv , !rv)
		//alert(rv)
		
		return !rv;
		
	}


export const formatInteger = (x) => {
	if(!x || x==null)
		return x;
		
	let y = parseInt(x)
	
	if(isNaN(y))
		return x;
		
	if(String(x)!==String(y))
		return x;
		
	//let y=Number.parseInt(x);
	return y.toLocaleString();
}


export const hash = (str, seed = 0) => {
  let h1 = 0xdeadbeef ^ seed, h2 = 0x41c6ce57 ^ seed;
  for(let i = 0, ch; i < str.length; i++) {
    ch = str.charCodeAt(i);
    h1 = Math.imul(h1 ^ ch, 2654435761);
    h2 = Math.imul(h2 ^ ch, 1597334677);
  }
  h1  = Math.imul(h1 ^ (h1 >>> 16), 2246822507);
  h1 ^= Math.imul(h2 ^ (h2 >>> 13), 3266489909);
  h2  = Math.imul(h2 ^ (h2 >>> 16), 2246822507);
  h2 ^= Math.imul(h1 ^ (h1 >>> 13), 3266489909);
  // For a single 53-bit numeric return value we could return
  // 4294967296 * (2097151 & h2) + (h1 >>> 0);
  // but we instead return the full 64-bit value:
  //return [h2>>>0, h1>>>0];
  return String(h2>>>0) + String(h1>>>0);
};


export const loadData = (url, params, setStateFunction, setErrorFunction, setIsLoadingFunction, successCallback) => {
		
		console.log(window.location.origin)
		
			//let baseUrl="http://localhost:8080/";
			let baseUrl = window.location.origin + "/";
			let finalUrl = baseUrl + url
			let first=true;

			if(setIsLoadingFunction)
				setIsLoadingFunction(true);

			
			if(params)
			{
				let keys = Object.keys(params)
				
				for(const k of keys)
				{
					if(first==true)
					{	
						first=false;					
						finalUrl = finalUrl + "?" + k + "=" + encodeURIComponent(params[k]);
					}
					else
						finalUrl = finalUrl + "&" + k + "=" + encodeURIComponent(params[k]);
				}
				
			}
			
			console.log("LoadData",finalUrl,params)
		
			axios.get(finalUrl)
     			.then((response) => {
			 		console.log(response);
			 		console.log(response.data);
			 
					 let o = response.data;
					 
					 console.log("loadData o=",o)
					 
					 if(o.returnCode && o.returnCode==1)
					 {
						 //alert(o.message);
						 console.log("loadData rc=1")
						 if(setErrorFunction)
						 	setErrorFunction(o.message);
						 	
						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	
						 	
						 return;
					 }
					 
					 					 
					 if(o.returnCode && o.returnCode==2)
					 {
						 console.log("loadData rc=2")
						 console.log("Expired session")
						 //alert(o.message);
						 if(setErrorFunction)
						 	setErrorFunction(o.message);
						 	
						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	
						 	
						 window.location="index.html";
						 	
						 return;
					 }
			 
			 		//console.log("Load",o)
			 		console.log("loadData rc=0 fall thru")
			 		if(setErrorFunction)
			 			setErrorFunction("")

					 if(setIsLoadingFunction)
					 	setIsLoadingFunction(false);	

			 
			 		if(setStateFunction)
			 			setStateFunction(o);
			 			
			 			
			 		if (successCallback !== undefined && successCallback) 
                		successCallback(finalUrl,o);
            		
			 			
			 			
		 		})
		 		.catch((error) => {
					 console.log("loadData error " ,error)
						if(setErrorFunction)
						 	setErrorFunction(o.message);
						 
 						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	

					 });
		 
	}




export const postData = (url, params, setStateFunction, setErrorFunction, setIsLoadingFunction) => {
		console.log(window.location.origin)
			//let baseUrl="http://localhost:8080/";
			
			let baseUrl = window.location.origin + "/";
			let finalUrl = baseUrl + url
			let first=true;

			if(setIsLoadingFunction)
				setIsLoadingFunction(true);

			
			
			
			console.log("Utils::postData",finalUrl,params)
		
			axios.post(finalUrl, params)
     			.then((response) => {
			 		console.log(response);
			 		console.log(response.data);
			 
					 let o = response.data;
					 
					 if(o.returnCode && o.returnCode==1)
					 {
						 //alert(o.message);
						 if(setErrorFunction)
						 	setErrorFunction(o.message);
						 	
						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	
						 	
						 return;
					 }
			 
			 		//console.log("Load",o)
			 
			 		if(setErrorFunction)
			 			setErrorFunction("")

					 if(setIsLoadingFunction)
					 	setIsLoadingFunction(false);	

			 
			 		if(setStateFunction)
			 			setStateFunction(o);
		 		})
		 		.catch((error) => {
					 console.log(error)
						if(setErrorFunction)
						 	setErrorFunction(o.message);
						 
 						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	

					 });
		 
	}



export const postAPI = (finalUrl, headers, params, setStateFunction, setErrorFunction, setIsLoadingFunction) => {
		//console.log(window.location.origin)
			//let baseUrl="http://localhost:8080/";
			
			//let baseUrl = window.location.origin + "/";
			//let finalUrl = baseUrl + url
			let first=true;

			if(setIsLoadingFunction)
				setIsLoadingFunction(true);

			
			
			
			console.log("Utils::postAPI",finalUrl,headers, params)
		
			axios.post(finalUrl, params, headers)
     			.then((response) => {
			 		console.log("POST API RESPONSE ",response);
			 		//console.log("POSTresponse.data);
			 
					 let o = response.data;
					 
					 if(response.status && response.status!=200)
					 {
						 //alert(o.message);
						 if(setErrorFunction)
						 	setErrorFunction(response.statusText);
						 	
						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	
						 	
						 return;
					 }
			 
			 		//console.log("Load",o)
			 
			 		if(setErrorFunction)
			 			setErrorFunction("")

					 if(setIsLoadingFunction)
					 	setIsLoadingFunction(false);	

			 
			 		if(setStateFunction)
			 			setStateFunction(o);
		 		})
		 		.catch((error) => {
					 console.error("POST API ERROR ", error)
						if(setErrorFunction)
						 	setErrorFunction("An error occured while processing the API request. Please try again.");
						 
 						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	

					 });
		 
	}







export const getAPI = (finalUrl, headers, params, setStateFunction, setErrorFunction, setIsLoadingFunction, successCallback) => {
		
		
			let first=true;

			if(setIsLoadingFunction)
				setIsLoadingFunction(true);

			
			if(params)
			{
				let keys = Object.keys(params)
				
				for(const k of keys)
				{
					if(first==true)
					{	
						first=false;					
						finalUrl = finalUrl + "?" + k + "=" + encodeURIComponent(params[k]);
					}
					else
						finalUrl = finalUrl + "&" + k + "=" + encodeURIComponent(params[k]);
				}
				
			}
			
			console.log("getAPI",finalUrl,headers, params)
		
			axios.get(finalUrl,headers)
     			.then((response) => {
			 		console.log(response);
			 		
			 
					 let o = response.data;
					 
					 
					 
					 if(response.status && response.status != 200)
					 {
						 //alert(o.message);
						 
						 if(setErrorFunction)
						 	setErrorFunction(response.statusText);
						 	
						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	
						 	
						 return;
					 }
					 

			 		if(setErrorFunction)
			 			setErrorFunction("")

					 if(setIsLoadingFunction)
					 	setIsLoadingFunction(false);	

			 		if(setStateFunction)
			 			setStateFunction(o);
			 			
			 		if (successCallback !== undefined && successCallback) 
                		successCallback(finalUrl,o);
            		
			 			
			 			
		 		})
		 		.catch((error) => {
					 console.log("getAPI error " ,error)
						if(setErrorFunction)
						 	setErrorFunction("An error occured while accssing the API. PLease try again. " + error.code + " - " + error.message);
						 
 						 if(setIsLoadingFunction)
						 	setIsLoadingFunction(false);	

					 });
		 
	}












/*

export function loadData2(url, ajaxData, stateKeyName, successCallback, beforeRenderCallback) {
   //EX:  Utils.loadData.call(this, "/webservice/Security.asmx/GetRoles", {}, "roles")
   
    var rv = [];
    let s = {};
    let _errorKeyName = "errorMessage";

    s["isLoading"] = true;

   
    //s[_errorKeyName] = "";
    //s[stateKeyName] = {};

    this.setState(s);

    //this.SelectedRows.clear();

    $.ajax({
        type: "POST",
        url: url,
        data: JSON.stringify(ajaxData),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: (response) => {
            let s = {};
            //console.log("MaintenanceUtils::loadData - " + stateKeyName)
            //console.log(response)
            var o = JSON.parse(response.d);
            if (o.returnCode && o.returnCode == 1) {
                console.log("MaintenanceUtils::loadData RC1", response, o)

                s["isLoading"] = false;
                s[_errorKeyName] = this.state[_errorKeyName] + " " + o.message;

                this.setState(s)
                return;
            } //end if

            if (o.returnCode && o.returnCode == 2) {
                //Session exipration
                window.location.href = "/expire.aspx";
                return;
            } //end if

            //console.log("locadData",o,response)

            s["isLoading"] = false;
            //s[_errorKeyName] = "";

            if (Array.isArray(o) && o.length == 1)
                s[stateKeyName] = o[0];
            else
                s[stateKeyName] = o;

            //console.log(s[stateKeyName])

            if (beforeRenderCallback !== undefined && beforeRenderCallback) {

                beforeRenderCallback(url, s[stateKeyName]);
            }


            this.setState(s);

            if (successCallback !== undefined && successCallback) {

                successCallback(url,s[stateKeyName]);
            }



        },
        failure: (response) => {
            console.log(response);


            var x = "THe network connection to the server failed.";

            if (response && response.responseJSON) {
                x = x.concat(" ")
                x = x.concat(response.responseJSON.Message)
            }

            //this.setState({ isLoading: false, errorMessage: x })
            s["isLoading"] = false;
            s[_errorKeyName] = this.state[_errorKeyName]  + " " + x;
            this.setState(s);

        },
        error: (response) => {
            //Server exceptions come here
            console.log(response);


            var x = "Your request could not be processed. Please contact your system administrator. ";

            if (response && response.responseJSON) {
                x = x.concat(" ")
                x = x.concat(response.responseJSON.Message)
            }

            s["isLoading"] = false;
            s[_errorKeyName] = this.state[_errorKeyName] + " "  + x;
            this.setState(s);

        } //end error

    }); //end ajax


    return;



}

*/

