package com.sga.common.flickr;

import com.flickr4java.flickr.people.*;
import com.sga.common.log.Log;
import com.sga.common.util.ReturnValue;

public class Users extends FlickrInterfaceBase {
	
	
	
	public static User GetUserFromUserName(String userName) throws Exception
	{
		String mn="Users::GetUserFromUserName";
		
		IsInitialized();
		
		PeopleInterface pi=null;
		User u = null;
		
		try
		{
		
			pi=flickrConnection.flickr.getPeopleInterface();
			u = pi.findByUsername(userName);
		}
		catch(Exception ex)
		{
			Log.Error(mn,ex);
			return null;
		}
		
		return u;
		
	}
	
	
	public static User GetUserFromEmail(String userEmail) throws Exception
	{
		String mn="Users::GetUserFromEmail";
		
		IsInitialized();
		
		PeopleInterface pi=null;
		User u = null;
		
		try
		{
		
			pi=flickrConnection.flickr.getPeopleInterface();
			u = pi.findByEmail(userEmail);
		}
		catch(Exception ex)
		{
			Log.Error(mn,ex);
			return null;
		}
		
		return u;
		
	}
	
	
	public static ReturnValue<User> FindUser(String userNameOrEmail) throws Exception
	{
		String mn="Users::FindUser";
		String errorMessage="";
		
		IsInitialized();
		
		
		PeopleInterface pi=null;
		User u = null;
		
		try
		{
		
			pi=flickrConnection.flickr.getPeopleInterface();
			u = pi.findByEmail(userNameOrEmail);
		}
		catch(Exception ex)
		{
			Log.Error(mn,ex);
			errorMessage=ex.getMessage();
			u=null;
		}
		
		if(u!=null)
			return new ReturnValue<User>(u,errorMessage);
		
	
				
		
		try
		{
		
			
			u = pi.findByUsername(userNameOrEmail);
		}
		catch(Exception ex)
		{
			Log.Error(mn,ex);
			errorMessage=ex.getMessage();
			u=null;
		}
		
		
		return new ReturnValue<User>(u,errorMessage);
		
		
	}
	
	
	
}
