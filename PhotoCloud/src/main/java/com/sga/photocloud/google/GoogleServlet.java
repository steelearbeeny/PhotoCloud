package com.sga.photocloud.google;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.gson.JsonElement;

//import com.google.api.services.oauth2.Oauth2;
//import com.google.api.services.oauth2.model.Userinfo;


import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Utils;
import com.sga.common.util.WebMethodResult;
import com.sga.photocloud.auth.Security;


/**
 * Servlet implementation class GoogleServlet
 */
@WebServlet("/GoogleServlet")
public class GoogleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String clientId="833263805285-nk2koc7pfq0njk4qbbcb6rop6asktj26.apps.googleusercontent.com";
	private static final String clientSecret="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";
	private static final String appName = "FotoFreedomWeb";
	//private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();  
	private static NetHttpTransport HTTP_TRANSPORT = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoogleServlet() {
        super();
        
       
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		WebMethodResult rc = new WebMethodResult();
		String mn="GoogleServlet::doGet";
		
		Map<String,String[]> p = request.getParameterMap();
		String query;
		
		
		Log.Info(mn,"Method Entered");
		
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		
		if(!Security.IsUserLoggedIn(request))
		{
			Log.Info(mn,"Not logged in");
			rc.Set(2, "Not logged in ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		String isPrivate="";
		
		Log.Info(mn,"User logged in");	
	
		
		if(p.containsKey("query"))
		{
			query=p.get("query")[0];
		}
		else
		{
			throw new ServletException("An invalid request was specified");
		}
			
		
		for(String k : p.keySet())
		{
			//response.getWriter().append(k).append(" - ").append(p.get(k)[0]).append("\n");
			Log.Info(mn, k + " - " + p.get(k)[0]);
		}
	
				
		try
		{
			//isPrivate=Utils.GetMapValueA(p, "isprivate");
			//Log.Info(mn, "IsPrivate: " + isPrivate);
			
			switch(query)
			{
			
			case "isuserloggedin":
				
				IsUserLoggedIn(request,response,p);
				break;
			
			case "getuser":
				GetUser(request,response,p);
				break;
			
				//case "finduser":
				//	FindPrivateUser(request, response,p);

					
					/*
					if(isPrivate.equals("true"))
						FindPrivateUser(request, response,p);
					else
						FindPublicUser(response,p);
					break;
					
				case "listalbums":
					if(isPrivate.equals("true"))
						ListPrivateAlbums(request, response,p);
					else
						ListAlbums(response,p);
				break;
				
				case "authorizeduserquery":
					LoadAuthorizedUser(request,response,p);
					
					break;
					*/
					
				default:
					throw new ServletException("An invalid request action was specified");
				
			
			} //end switch
			
			
		}
		catch(Exception ex)
		{
			Log.Info(mn, ex.toString());
			//throw new ServletException("An error occured",ex);
			rc.Set(1, "An error was encountered in the servlet. " + ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	

	private void GetUser(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		
		String mn="GoogleServlet::GetUser";
		
		Log.Info(mn, "Method Entered");		
			int userId=Security.GetUserId(request);
		    Credential credential = newFlow().loadCredential(Utils.ToString(userId));
		    
		   
		    Log.Info(mn, "Read credential " + credential.toString());
		    
		    Oauth2 oauth2Client =
		        new Oauth2.Builder(HTTP_TRANSPORT, 
		        		GsonFactory.getDefaultInstance(), 
		        		credential)
		            .setApplicationName(appName)
		            .build();

		    Userinfo userInfo = oauth2Client.userinfo().get().execute();
		    
		    OauthToken token=OauthUtil.GetTokenForUserAndService(userId,4);
		    String rv;
		    
		    if(token!=null)
		    	rv=Utils.AddProperty(userInfo, "key", token.key);
		    else
		    	rv=Utils.ToJSON(userInfo);
		    
		    Log.Info(mn, "UserInfo: " + rv);		    
		    response.getWriter().write(rv);
		  
	}
	
	
	
	private void IsUserLoggedIn(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		String mn="GoogleServlet::IsUserLoggedIn";
		int userId=0;
		WebMethodResult rc=new WebMethodResult();
		Credential credential=null;
		
		
		Log.Info(mn, "Method Entered");
	
		userId=Security.GetUserId(request);
		
		 try
		 {
		      	credential = newFlow().loadCredential(Utils.ToString(userId));
		      	
		      	 
		      	
		      	if(credential==null)
		      	{
		      		Log.Info(mn, "Not logged in");
					response.getWriter().write("{ \"isLoggedIn\": false }");
					return;
		      	}
		      	
		      	
		      	 Oauth2 oauth2Client =
		 		        new Oauth2.Builder(HTTP_TRANSPORT, 
		 		        		GsonFactory.getDefaultInstance(), 
		 		        		credential)
		 		            .setApplicationName(appName)
		 		            .build();

		 		 Userinfo userInfo = oauth2Client.userinfo().get().execute();
				    OauthToken token=OauthUtil.GetTokenForUserAndService(userId,4);

		      	
		      	   JsonElement jsonElement = Utils.GetGson().toJsonTree(userInfo);
		           jsonElement.getAsJsonObject().addProperty("isLoggedIn",true);
		           jsonElement.getAsJsonObject().addProperty("accessToken",credential.getAccessToken());
		           jsonElement.getAsJsonObject().addProperty("key",token.key);
		           String rv=Utils.ToJSON(jsonElement);
		           
		 		 Log.Info(mn, "Logged In " + rv);
				response.getWriter().write(rv);
				return;
			    
		 } 
		 catch(Exception e)
		 {
	      		rc.Set(1, "Not Logged In - "+ e.toString(),"");
	      		Log.Info(mn, "Exception: " + rc.message);
				//response.getWriter().write(rc.ToJSON());
				response.getWriter().write("{ \"isLoggedIn\": false }");

	      		return;
		 }
		 

		
		
	}

	
	public static GoogleAuthorizationCodeFlow newFlow() throws Exception {
	    
	
	    List<String> scopes = Arrays.asList(
	        "https://www.googleapis.com/auth/userinfo.profile",
	        "https://www.googleapis.com/auth/userinfo.email",
	        "https://www.googleapis.com/auth/photospicker.mediaitems.readonly",
	        "https://www.googleapis.com/auth/photoslibrary.appendonly",
	        "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata",
	        "https://www.googleapis.com/auth/photoslibrary.edit.appcreateddata",
	    		"profile");
	    
	    if(HTTP_TRANSPORT==null)
	    {
	    	 HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
	    }
	    
	    
	    DbDataStoreFactory factory = DbDataStoreFactory.getDefaultInstance();
	    DataStore<StoredCredential> dbDataStore = factory.createDataStore("StoredCredential",4);	    
	    TokenListener tokenListener = new TokenListener();
	    
	    return new GoogleAuthorizationCodeFlow.Builder(
	    		HTTP_TRANSPORT, 
	            GsonFactory.getDefaultInstance() ,
	            clientId, 
	            clientSecret, 
	            scopes)
	        //.setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
	        //.setDataStoreFactory(DbDataStoreFactory.getDefaultInstance())
	        .setCredentialDataStore(dbDataStore)
	        .setAccessType("offline")
	        .addRefreshListener(tokenListener)
	        //.setCredentialCreatedListener(tokenListener) )
	        .build();
	  }
	
	
	
	/*

	private void FindPrivateUser(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		
		User flickrUser=null;
		String errorMessage="";
		ReturnValue<User> rv=null;
		WebMethodResult rc=new WebMethodResult();
		int userId=0;
		String mn="GoogleServlet::FindPrivateUser";
		
		Log.Info(mn, "Method Entered");		

		
		
		
		
	
        
        String key=UUID.randomUUID().toString();
        
        String callbackUrl=request.getRequestURL().toString().replace("FlickrServlet","FlickrAuth") + "?query=auth&key=" + key;

        OAuth1RequestToken requestToken = authInterface.getRequestToken(callbackUrl);
        Log.Info(mn,"token: " + requestToken.getToken() + " Secret: " + requestToken.getTokenSecret() + " URL: " + callbackUrl);

        String url = authInterface.getAuthorizationUrl(requestToken, Permission.WRITE);
        Log.Info(mn, url);
        
        
        JsonObject json = new JsonObject();
        json.addProperty("authrequired", 1);
        json.addProperty("token", requestToken.getToken());
        json.addProperty("callback", callbackUrl);
        json.addProperty("authUrl", url);       
        json.addProperty("key", key);
        
        Log.Info(mn,"Response: "  + json.toString());
        
        String SQLStr="INSERT INTO public.oauthrequest "
        		+ "(userid, serviceid, key, token, secret, senttime) "
        		+ "VALUES (?, 0, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        DataUtil.getInstance().ExecuteUpdate(SQLStr,userId,key,requestToken.getToken(),requestToken.getTokenSecret());
        
		response.getWriter().write(json.toString());
		return;
        
        
       
		
		
	}
	
	
	*/
	
	

}
