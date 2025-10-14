package com.sga.common.flickr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import com.flickr4java.flickr.*;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.Photosets;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.WebMethodResult;


//
// Flickr apps are registered in the App Garden under user settings
// The API key and secret below are for the PhotoMetadata app I registered.
// New ones with different names can also be registered later...
//
// Therefore this info is not user sign in info, but instead the app registration info
//
// Flickr oauth process: https://www.flickr.com/services/api/auth.oauth.html#request_token





public class FlickrConnection {
	
	public Flickr flickr=null;

	//the PhotoMetadata App - this is not the user login but the app regisration
	//for public albums - this is all that is needed to read
	//for private ablums - users need to authenticate through he oauth flow on the flickr page
	//
	private String _apiKey = "0cbde9e2483aa5d311cf5c11ce7ba2f5";
	private String _sharedSecret = "8f9c3f5a456af8c8";
	public OAuth1Token accessToken=null;
	public Auth auth=null;
	//private String userId="99631001@N08";
	
	
	public FlickrConnection()
	{
		flickr = new Flickr(_apiKey, _sharedSecret, new REST());
	}
	
	public FlickrConnection(String apiKey, String sharedSecret)
	{
		flickr = new Flickr(apiKey, sharedSecret, new REST());
		
	}
	
	
	public String DumpToken()
	{
		String x;
		if(accessToken==null) return "null";
		
		x="Token: " + accessToken.getToken() + 
				" Secret: " + accessToken.getTokenSecret() + 
				" Response: " + accessToken.getRawResponse();
		
		if(auth!=null)
			x+=	" Auth Token: " + auth.getToken() + 
				" Auth Secret: " + auth.getTokenSecret() + 
				" Auth NSID: " + auth.getUser().getId() + 
				" Auth UN: " + auth.getUser().getUsername() + 
				" Auth RN: " + auth.getUser().getRealName() + 
				" Auth Perm: " + auth.getPermission().getType();
		
		return x;
	}
	
	public void Reauthorize()
	{
		if(auth==null) return;
		String mn="FlickrConnection::Reauthorize";
		
	    RequestContext.getRequestContext().setAuth(auth);
        flickr.setAuth(auth);
        

        // This token can be used until the user revokes it.
        Log.Info(mn,DumpToken());
		
		
	}
	
	public FlickrConnection(int appUserId) throws Exception
	{
		
		String SQLStr;
		String mn="FlickrConnection::FlickrConnection";
		WebMethodResult rc = new WebMethodResult();
		
		Log.Info(mn, "In method");
		
		OauthToken flickrToken=OauthUtil.GetTokenForUserAndService(appUserId, 0); //flickr
		
		
		try
		{
		
	
			
			if(flickrToken==null)
			{
				Log.Info(mn, "Not Found");
				throw new Exception("No Flickr authorization found.");
				
			}
			
			flickr = new Flickr(_apiKey, _sharedSecret, new REST());


	        AuthInterface authInterface = flickr.getAuthInterface();
			
	        //recreate the token string as a object - read from DB
	        OAuth1RequestToken oauthRequestToken = new OAuth1RequestToken(
	        		flickrToken.token, 
	        		flickrToken.secret);
	        
	        
	        
	        //add in the verifier (ie tokenresponse) - also from DB
	       accessToken = authInterface.getAccessToken(
	        		oauthRequestToken, 
	        		flickrToken.tokenResponse);
	        
	        Log.Info(mn,"Authentication success");

	        
	        auth = authInterface.checkToken(accessToken);
	        RequestContext.getRequestContext().setAuth(auth);
	        flickr.setAuth(auth);
	        

	        // This token can be used until the user revokes it.
	        Log.Info(mn,DumpToken());
	       
	
			
			
		
		
		}
		catch(Exception ex)
		{
			Log.Info(mn,"Exception: " + ex.toString());
			rc.Set(2, ex.toString(),"");
			throw new Exception("Error getting Flickr authorization. " + ex.toString());
		}
		
		
		
		
		
	} //end ctor
	
	
	
	public FlickrConnection(OauthToken flickrToken) throws Exception
	{
		
		String SQLStr;
		String mn="FlickrConnection::FlickrConnection";
		WebMethodResult rc = new WebMethodResult();
		
		Log.Info(mn, "In method");
		
		//OauthToken flickrToken=OauthUtil.GetTokenForUserAndService(userId, 0); //flickr
		
		
		try
		{
		
	
			
			if(flickrToken==null)
			{
				Log.Info(mn, "Not Found");
				throw new Exception("No Flickr authorization found.");
				
			}
			
			Log.Info(mn, "Input Token: " + flickrToken.toString());

			
			flickr = new Flickr(_apiKey, _sharedSecret, new REST());


	        AuthInterface authInterface = flickr.getAuthInterface();
			
	        //recreate the token string as a object - read from DB
	        OAuth1RequestToken oauthRequestToken = new OAuth1RequestToken(
	        		flickrToken.token, 
	        		flickrToken.secret);
	        
	        
	        
	        //add in the verifier (ie tokenresponse) - also from DB
	        accessToken = authInterface.getAccessToken(
	        		oauthRequestToken, 
	        		flickrToken.tokenResponse);
	        
	        Log.Info(mn,"Authentication success ");

	        
	        Auth auth = authInterface.checkToken(accessToken);
	        RequestContext.getRequestContext().setAuth(auth);
	        flickr.setAuth(auth);
	        

	        // This token can be used until the user revokes it.
	        Log.Info(mn,"Token: " + accessToken.getToken());
	        Log.Info(mn,"Secret: " + accessToken.getTokenSecret());
	        Log.Info(mn,"Response: " + accessToken.getRawResponse());
	        
	        Log.Info(mn,"nsid: " + auth.getUser().getId());
	        Log.Info(mn,"Realname: " + auth.getUser().getRealName());
	        Log.Info(mn,"Username: " + auth.getUser().getUsername());
	        Log.Info(mn,"Permission: " + auth.getPermission().getType());
			
			
		
		
		}
		catch(Exception ex)
		{
			Log.Info(mn,"Exception: " + ex.toString());
			rc.Set(2, ex.toString(),"");
			throw new Exception("Error getting Flickr authorization. " + ex.toString());
		}
		
		
		
		
		
	} //end ctor
	
	
	public Map<String,String> GetAlbumMap() throws Exception
	{
		Map<String,String> rv= new HashMap<String,String>();
		String mn="FLickrConnection::GetAlbumMap";
		String userId=flickr.getAuth().getUser().getId();
	
		RequestContext.getRequestContext().setAuth(flickr.getAuth());
		Log.Info(mn,"Flickr authorized. " + userId );
		
		
		 PhotosetsInterface pi = flickr.getPhotosetsInterface();
	        
		 int setsPage=1;
		
		 try {
			 
			 while(true)
			 {
				 
				 Photosets photosets = pi.getList(userId, 500, setsPage, null);
				 Collection<Photoset> setsColl = photosets.getPhotosets();
				 Iterator<Photoset> setsIter = setsColl.iterator();
				 while (setsIter.hasNext()) 
				 {
                    Photoset set = setsIter.next();
                  
                    if(!rv.containsKey(set.getTitle()))
                    	rv.put(set.getTitle(),set.getId());

				 }

                if (setsColl.size() < 500) 
                    break;

                setsPage++;
				 
				 
			 } //end while
			 
		 }
		 catch(Exception ex)
		 {
			 Log.Error(mn, ex.toString());
			 
		 }
		 
		 
		 return rv;
		
	}
	
	

}
