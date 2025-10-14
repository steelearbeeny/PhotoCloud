package com.sga.common.util;

public class HttpStatus {
	
	public static String GetDescription(String httpStatusCode)
	{
		return GetDescription(Utils.ToInt(httpStatusCode));
	}
	
	
	public static String GetDescription(Integer httpStatusCode)
	{
		
		String message=String.format("The return code was %d. The message was not found.", httpStatusCode);
		
		
		switch(httpStatusCode)
		{
			case 200:
				message="OK";
			break;
			
			case 400:
				message="Bad request";
			break;
			
			
			case 401:
				message="Not authorized";
			break;
			
			case 402:
				message="Payment required";
			break;
			
			case 403:
				message="Forbidden";
			break;

			case 404:
				message="Not found";
			break;

			case 405:
				message="Method not allowed";
			break;

			case 406:
				message="Not acceptable";
			break;
			
			case 407:
				message="Proxy authentication required";
			break;

			case 408:
				message="Request timeout";
			break;

			case 409:
				message="Conflict";
			break;
			
			case 410:
				message="Gone";
			break;

		
		} //end sw
		
		
		return message;
		
	}

}
