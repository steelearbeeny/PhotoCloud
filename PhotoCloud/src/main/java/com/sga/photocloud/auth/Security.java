package com.sga.photocloud.auth;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Security {
	
	public static boolean IsUserLoggedIn(HttpServletRequest request)
	{
		
		
		HttpSession session = request.getSession(false);
		
		if(session==null)
		{
			return false;
		}
		
		String sessionUser="";
		
		sessionUser=(String)session.getAttribute("USER");
		if(sessionUser==null)
		{
			return false;
		}
		
		if(sessionUser.length() < 1)
		{
			return false;
		}
		
	
		return true;
	}
	
	public static void DestroySession(HttpServletRequest request)
	{
		
		HttpSession session = request.getSession(false);
		
		if(session!=null)
			session.invalidate();
	
		return ;
	}
	
	
	
	public static int GetUserId(HttpServletRequest request)
	{
		
		HttpSession session = request.getSession(false);
		int rv=-1;
		
		if(session==null)
		{
			return -1;
		}
		
		
		
		rv=(int)session.getAttribute("USERID");
		
		if(rv < 1)
			return -1;
		
		return rv;
		
	}
	
	
	public static Map<String,Object> GetSession(HttpServletRequest request)
	{
		Map<String,Object> m = new HashMap<String,Object>();
		
		HttpSession session = request.getSession(false);
		
		if(session==null)
		{
			return null;
		}
		
		
		
		Enumeration<String> attributeNames = session.getAttributeNames();

	    while (attributeNames.hasMoreElements()) {
	        String attributeName = attributeNames.nextElement();
	        Object attributeValue = session.getAttribute(attributeName);
	        
	        m.put(attributeName, attributeValue);

	        
	    }
	    
	    return m;
		
		
	}

}
