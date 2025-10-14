package com.sga.common.util;

import org.json.JSONObject;

public class WebMethodResult {
	public int returnCode;
	public int returnCode2;
	public String message;
	public String nextPage;

	
	public WebMethodResult()
	{
		returnCode=0;
		returnCode2=0;
		message="";
		nextPage="";
		
	}
	
	public WebMethodResult(int r, String m, String n)
    {
        returnCode = r;
        message = m;
        nextPage = n;
    }

    public void Set(int returnCode, String message, String nextPage)
    {
        this.returnCode = returnCode;
        this.message = message;
        this.nextPage = nextPage;
    }
    
    public String ToJSON()
    {
    	
    	JSONObject o = new JSONObject(this);
    	return o.toString();
    	
    }

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public int getReturnCode2() {
		return returnCode2;
	}

	public void setReturnCode2(int returnCode2) {
		this.returnCode2 = returnCode2;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNextPage() {
		return nextPage;
	}

	public void setNextPage(String nextPage) {
		this.nextPage = nextPage;
	}
}
