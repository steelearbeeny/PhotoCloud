package com.sga.photocloud.flickr;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photosets.Photosets;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuth1Token;
import com.google.gson.JsonObject;
import com.sga.common.dataaccess.DataUtil;
import com.sga.common.flickr.Albums;
import com.sga.common.flickr.FlickrConnection;
import com.sga.common.flickr.Users;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Regex;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;
import com.sga.common.util.WebMethodResult;
import com.sga.photocloud.auth.Security;
/**
 * Servlet implementation class FlickrServlet
 */
@WebServlet("/FlickrServlet")
public class FlickrServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FlickrServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		
		WebMethodResult rc = new WebMethodResult();
		String mn="FlickrServlet::doGet";
		
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
			isPrivate=Utils.GetMapValueA(p, "isprivate");
			Log.Info(mn, "IsPrivate: " + isPrivate);
			
			switch(query)
			{
			
				case "finduser":
					
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
		
		
		
	} //end get method



	
	
	private void FindPublicUser(HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		String flickrUserName;
		User flickrUser=null;
		String errorMessage="";
		ReturnValue<User> rv=null;
		WebMethodResult rc=new WebMethodResult();
		
		
		flickrUserName=Utils.GetMapValueA(p, "flickrusername");
		
		if(flickrUserName==null)
		{
			rc.Set(3, "The user could not be retrieved. The Flickr user name was not valid.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		FlickrConnection f = new FlickrConnection();
		Users.Initialize(f);
		Albums.Initialize(f);
		
		rv=Users.FindUser(flickrUserName);
		
		flickrUser=rv.data;
		
		
		if(flickrUser==null)
		{
			rc.Set(3, "The requested user could not be found. " + rv.message,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		int numAlbums=Albums.GetAlbumCount(flickrUser.getId());
		
		String userJson=Utils.AddProperty(flickrUser, "numalbums", numAlbums);
		
		response.getWriter().write(userJson);
		return;
	}
	
	
	
	private void FindPrivateUser(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		
		User flickrUser=null;
		String errorMessage="";
		ReturnValue<User> rv=null;
		WebMethodResult rc=new WebMethodResult();
		int userId=0;
		String mn="FlickrServlet::FindPrivateUser";
		
		Log.Info(mn, "Method Entered");		

		
		
		
		
		
		/*
		Users.Initialize(f);
		Albums.Initialize(f);
		
		rv=Users.FindUser(flickrUserName);
		
		flickrUser=rv.data;
		
		
		if(flickrUser==null)
		{
			rc.Set(3, "The requested user could not be found. " + rv.message,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		int numAlbums=Albums.GetAlbumCount(flickrUser.getId());
		
		String userJson=Utils.AddProperty(flickrUser, "numalbums", numAlbums);
		
		response.getWriter().write(userJson);
		*/
		
		FlickrConnection f=null;
		
		try
		{
			f=IsFlickrAuthorized(request,response,p);
			Log.Info(mn, "Flickr Authorized");
			Log.Info(mn,Utils.ToJSON(f.flickr.getAuth().getUser()   ));
			response.getWriter().write(Utils.ToJSON(f.flickr.getAuth().getUser()   ));
			
			
			
			return;
		}
		catch(Exception ex)
		{
			Log.Info(mn, "Flickr not authorized");
		}
		
		
		//Flickr.debugStream = false;
		userId=Security.GetUserId(request);
		
		 f = new FlickrConnection();
		
		
        AuthInterface authInterface = f.flickr.getAuthInterface();

        //Scanner scanner = new Scanner(System.in);
        
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
        /*
        String SQLStr="INSERT INTO public.oauthrequest "
        		+ "(userid, serviceid, key, token, secret, senttime) "
        		+ "VALUES (?, 0, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        DataUtil.getInstance().ExecuteUpdate(SQLStr,userId,key,requestToken.getToken(),requestToken.getTokenSecret());
        */
        
        OauthUtil.InsertTokenRequest(userId,0,key,requestToken.getToken(),requestToken.getTokenSecret());        
        
		response.getWriter().write(json.toString());
		return;
        
        
        /*
        String tokenKey = scanner.nextLine();
        scanner.close();

        OAuth1Token accessToken = authInterface.getAccessToken(requestToken, tokenKey);
        System.out.println("Authentication success");

        Auth auth = authInterface.checkToken(accessToken);
		
		
		
		
		Log.Info(getServletName(), "Token: " + auth.getToken());
		Log.Info(getServletName(), "Token Secret: " + auth.getTokenSecret());
		
		
		Log.Info(getServletName(), "nsid: " + auth.getUser().getId());
		Log.Info(getServletName(), "Realname: " + auth.getUser().getRealName());
		Log.Info(getServletName(), "Username: " + auth.getUser().getUsername());
		Log.Info(getServletName(), "Permission: " + auth.getPermission().getType());
		*/
		
		
	}
	
	private void LoadAuthorizedUser(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		String SQLStr;
		WebMethodResult rc = new WebMethodResult();
		String mn="FlickrServlet::LoadAuthorizedUser";
		int userId=0;
		
		Log.Info(mn,"In Method");
		
		String key=Utils.ToString(Utils.GetMapValueA(p, "key"));
		String token="";
		String secret="";
		String tokenResponse="";
		
		if(key.length() > 100 || key.length() < 10 || !Regex.IsMatch("^[a-zA-z0-9-].*$", key))
		{
			Log.Info(mn,"Invalid input key");
			rc.Set(2, "Invalid Request ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		token=Utils.ToString(Utils.GetMapValueA(p, "token"));
		
		if(token.length() > 100 || token.length() < 10 || !Regex.IsMatch("^[a-zA-z0-9-].*$", token))
		{
			Log.Info(mn,"Invalid input token");
			rc.Set(2, "Invalid Request ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		userId=Security.GetUserId(request);
		
		SQLStr="SELECT * FROM OAUTHREQUEST WHERE USERID=?::INT4 AND KEY=? AND TOKEN=?";
		
		ResultSet rs = DataUtil.getInstance().GetRowSet(SQLStr,Utils.ToString(userId),key,token);
		Timestamp sentTime;
		Timestamp responseTime;
		
		
		try
		{
		
			if(rs.next())
			{
				
				sentTime=rs.getTimestamp("senttime");
				responseTime=rs.getTimestamp("responsetime");
				tokenResponse=Utils.ToString(rs.getString("tokenresponse"));	 //verifier
				secret=Utils.ToString(rs.getString("secret"));
			}
			else
			{
				Log.Info(mn, "Not Found");
				rc.Set(1, "Not found","");
				response.getWriter().write(rc.ToJSON());
				return;
			}
		
		}
		catch(Exception ex)
		{
			Log.Info(mn,"Exception: " + ex.toString());
			rc.Set(2, ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
		}
		finally
		{
			DataUtil.getInstance().Close(rs);
		}
		
		//
		//We got the token response and now use it to get the real token
		//
		
		FlickrConnection f = new FlickrConnection();

        AuthInterface authInterface = f.flickr.getAuthInterface();
		
        //recreate the token string as a object - read from DB
        OAuth1RequestToken oauthRequestToken = new OAuth1RequestToken(token, secret);
        
        //add in the verifier (ie tokenresponse) - also from DB
        OAuth1Token accessToken = authInterface.getAccessToken(oauthRequestToken, tokenResponse);
        
        Log.Info(mn,"Authentication success");

        Auth auth = authInterface.checkToken(accessToken);

        // This token can be used until the user revokes it.
        Log.Info(mn,"Token: " + accessToken.getToken());
        Log.Info(mn,"Secret: " + accessToken.getTokenSecret());
        Log.Info(mn,"nsid: " + auth.getUser().getId());
        Log.Info(mn,"Realname: " + auth.getUser().getRealName());
        Log.Info(mn,"Username: " + auth.getUser().getUsername());
        Log.Info(mn,"Permission: " + auth.getPermission().getType());
		
        RequestContext.getRequestContext().setAuth(auth);
		
	
        PhotosetsInterface pi = f.flickr.getPhotosetsInterface();
        
	
	 
	 
	
	 
        int numAlbums=pi.getPhotosetCount(auth.getUser().getId());
	 
        
        Log.Info(mn,"Number of Albums: " + numAlbums);
      
		
		String userJson=Utils.AddProperty(auth.getUser(), "numalbums", numAlbums);
		
		response.getWriter().write(userJson);
		return;
	}
	
	
	private FlickrConnection IsFlickrAuthorized(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		int userId=Security.GetUserId(request);
		String SQLStr;
		String mn="FlickrServlet::IsFlickrAuthorized";
		WebMethodResult rc = new WebMethodResult();
		
		Log.Info(mn, "In method");
		
		OauthToken flickrToken=OauthUtil.GetTokenForUserAndService(userId, 0); //flickr
		
		/*
		SQLStr="SELECT * FROM OAUTHREQUEST "
				+ "WHERE USERID=?::INT4 AND "
				+ "SERVICEID=0 AND "
				+ "TOKEN IS NOT NULL AND "
				+ "SECRET IS NOT NULL AND "
				+ "TOKENRESPONSE IS NOT NULL AND "
				+ "RESPONSETIME="
				+ 	"(SELECT MAX(RESPONSETIME) "
				+ 	"FROM OAUTHREQUEST "
				+ 	"WHERE USERID=?::INT4 AND "
				+   "SERVICEID=0 AND "
				+ 	"TOKEN IS NOT NULL AND "
				+ 	"SECRET IS NOT NULL AND "
				+ 	"TOKENRESPONSE IS NOT NULL)";
		
		ResultSet rs=null; 
		String tokenResponse="";
		String secret="";
		String token="";
*/		
		try
		{
		
	/*
			rs = DataUtil.getInstance().GetRowSet(SQLStr,Utils.ToString(userId),Utils.ToString(userId));
		
		
			if(rs.next())
			{
				
				token=Utils.ToString(rs.getString("token"));
				tokenResponse=Utils.ToString(rs.getString("tokenresponse"));	 //verifier
				secret=Utils.ToString(rs.getString("secret"));
				Log.Info(mn, "Found token " + token);
			}
			else
			{
				Log.Info(mn, "Not Found");
				throw new Exception("No Flickr authorization found.");
				
			}
		*/
			
			if(flickrToken==null)
			{
				Log.Info(mn, "Not Found");
				throw new Exception("No Flickr authorization found.");
				
			}
			
			FlickrConnection f = new FlickrConnection();

	        AuthInterface authInterface = f.flickr.getAuthInterface();
			
	        //recreate the token string as a object - read from DB
	        OAuth1RequestToken oauthRequestToken = new OAuth1RequestToken(
	        		flickrToken.token, 
	        		flickrToken.secret);
	        
	        
	        
	        //add in the verifier (ie tokenresponse) - also from DB
	        OAuth1Token accessToken = authInterface.getAccessToken(
	        		oauthRequestToken, 
	        		flickrToken.tokenResponse);
	        
	        Log.Info(mn,"Authentication success");

	        
	        Auth auth = authInterface.checkToken(accessToken);
	        RequestContext.getRequestContext().setAuth(auth);
	        f.flickr.setAuth(auth);
	        

	        // This token can be used until the user revokes it.
	        Log.Info(mn,"Token: " + accessToken.getToken());
	        Log.Info(mn,"Secret: " + accessToken.getTokenSecret());
	        Log.Info(mn,"nsid: " + auth.getUser().getId());
	        Log.Info(mn,"Realname: " + auth.getUser().getRealName());
	        Log.Info(mn,"Username: " + auth.getUser().getUsername());
	        Log.Info(mn,"Permission: " + auth.getPermission().getType());
			
			return f;
		
		
		}
		catch(Exception ex)
		{
			Log.Info(mn,"Exception: " + ex.toString());
			rc.Set(2, ex.toString(),"");
			throw new Exception("Error getting Flickr authorization. " + ex.toString());
		}
		
		
		
		
		
	}
	
	
	private void ListAlbums(HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		String flickrUserId;
		User flickrUser=null;
		String errorMessage="";
		ReturnValue<Photosets> rv=null;
		WebMethodResult rc=new WebMethodResult();
		Photosets photosets=null;
		int page=0;
		int perPage=20;
		
		flickrUserId=Utils.GetMapValueA(p, "flickruserid");
		page=Utils.ToInt(Utils.GetMapValueA(p, "page"));
		perPage=Utils.ToInt(Utils.GetMapValueA(p, "perPage"));
		
		if(page < 1) page=1;
		if(perPage< 1) perPage=20;
		
		
		if(flickrUserId==null)
		{
			rc.Set(3, "The photosets could not be retrieved. The Flickr user Id was not valid.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		FlickrConnection f = new FlickrConnection();
		
		Albums.Initialize(f);
		
		
		rv=Albums.ListAlbums(flickrUserId,perPage,page);
		
		photosets=rv.data;
		
		if(photosets==null)
		{
			rc.Set(3, "The photosets could not be retrieved. " + rv.message,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		response.getWriter().write(Utils.ToJSON(photosets));
		return;
	}
	
	
	
	
	

	private void ListPrivateAlbums(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		String flickrUserId;
		User flickrUser=null;
		String errorMessage="";
		ReturnValue<Photosets> rv=null;
		WebMethodResult rc=new WebMethodResult();
		Photosets photosets=null;
		String mn="FlickrServlet::ListPrivateAlbums";
		int page=0;
		int perPage=20;
		
		
		page=Utils.ToInt(Utils.GetMapValueA(p, "page"));
		perPage=Utils.ToInt(Utils.GetMapValueA(p, "perPage"));
		
		if(page < 1) page=1;
		if(perPage< 1) perPage=20;
		
		
		
		FlickrConnection f=null;
		
		try
		{
			f=IsFlickrAuthorized(request,response,p);
		}
		catch(Exception ex)
		{
			rc.Set(2, "Flickr was not authorized properly. " + ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		Albums.Initialize(f);
		
		
		
		flickrUserId=f.flickr.getAuth().getUser().getId();
		
		Log.Info(mn, "Getting albums for "  + flickrUserId);
		
		rv=Albums.ListAlbums(flickrUserId,perPage,page);

		
		photosets=rv.data;
		
		if(photosets==null)
		{
			rc.Set(3, "The photosets could not be retrieved. " + rv.message,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		response.getWriter().write(Utils.ToJSON(photosets));
		return;
	}
	
	
	
	
	
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
