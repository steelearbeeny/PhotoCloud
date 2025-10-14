package com.sga.photocloud.flickr;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Regex;
import com.sga.common.util.Utils;
import com.sga.common.util.WebMethodResult;
import com.sga.photocloud.auth.Security;

/**
 * Servlet implementation class FlickrAuth
 */
@WebServlet("/FlickrAuth")
public class FlickrAuth extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FlickrAuth() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		WebMethodResult rc = new WebMethodResult();
		String mn="FlickrAuth::doGet";
		
		Map<String,String[]> p = request.getParameterMap();
		String query;
		
		
		Log.Info(mn,"Method Entered");
		
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		
	
		
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
	
			
			switch(query)
			{
			
				
				
				case "auth":
					Auth(response,p);
					break;
					
					
				case "checkauth":
					CheckAuth(request, response,p);
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
		
		
		
		
	}

	
	private void CheckAuth(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		WebMethodResult rc = new WebMethodResult();
		String oauthToken;
		String oauthResponse;
		String SQLStr;
		String mn="FlickrAuth::CheckAuth";
		
		
		if(!Security.IsUserLoggedIn(request))
		{
			Log.Info(mn,"Not logged in");
			rc.Set(2, "Not logged in ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		Log.Info(mn, "In Check Auth");
		
		
		int userId=0;
		userId=Security.GetUserId(request);
		String key=Utils.ToString(Utils.GetMapValueA(p, "key"));
		oauthToken=Utils.ToString(Utils.GetMapValueA(p, "token"));

		
		if(key.length() > 100 || key.length() < 10 || !Regex.IsMatch("^[a-zA-z0-9-].*$", key))
		{
			Log.Info(mn,"Invalid input key");
			rc.Set(2, "Invalid Request ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		if(oauthToken.length() > 100 || oauthToken.length() < 10 || !Regex.IsMatch("^[a-zA-z0-9-].*$", oauthToken))
		{
			Log.Info(mn,"Invalid input token");
			rc.Set(2, "Invalid Request ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		Timestamp responseTime;
		String tokenResponse;
		
		SQLStr="SELECT * FROM OAUTHREQUEST WHERE KEY=? AND TOKEN=?";
		
		ResultSet rs =null;
		try
		{
			rs= DataUtil.getInstance().GetRowSet(SQLStr,key,oauthToken);
			
			if(rs.next())
			{
				tokenResponse=Utils.ToString(rs.getString("tokenresponse"));
				
				if(tokenResponse.length() < 10)
				{
					Log.Info(mn, "Not Found - blank tokenresponse");
					rc.Set(0, "Not found - blank tokenresponse","");
					rc.setReturnCode2(1);
					response.getWriter().write(rc.ToJSON());
					return;
				}
				responseTime=rs.getTimestamp("responsetime");
				
				Log.Info(mn, "Token found ! ");
			 
				rc.Set(0, "Token Found!","");
				rc.setReturnCode2(0);
				response.getWriter().write(rc.ToJSON());
				return;
				
			}
			else
			{
				//TODO: This should redirect to an error page at least
				Log.Info(mn, "Not Found - no recs");
				rc.Set(0, "Not Found","");
				rc.setReturnCode2(2);
				response.getWriter().write(rc.ToJSON());
				return;
			}
		
		}
		catch(Exception ex)
		{
			Log.Info(mn,"Exception: " + ex.toString());
			rc.Set(1, ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
		}
		finally
		{
			DataUtil.getInstance().Close(rs);
		}
		
	}
	
	// This is the callback from the Flickr oauth service
	private void Auth(HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		String SQLStr;
		WebMethodResult rc = new WebMethodResult();
		String mn="FlickrAuth::Auth";
		
		Log.Info(mn,"In Auth Method");
		
		String key=Utils.GetMapValueA(p, "key");
		String oauthToken;
		String oauthResponse;
		
		if(key.length() > 100 || key.length() < 10 || !Regex.IsMatch("^[a-zA-z0-9-].*$", key))
		{
			Log.Info(mn,"Invalid input key");
			rc.Set(2, "Invalid Request ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		oauthToken=Utils.GetMapValueA(p, "oauth_token");
		oauthResponse=Utils.GetMapValueA(p, "oauth_verifier");
		
		if(oauthToken.length() > 100 || oauthToken.length() < 10 || !Regex.IsMatch("^[a-zA-z0-9-].*$", oauthToken))
		{
			Log.Info(mn,"Invalid input token");
			rc.Set(2, "Invalid Request ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		if(oauthResponse.length() > 100 || oauthResponse.length() < 10 || !Regex.IsMatch("^[a-zA-z0-9-].*$", oauthResponse))
		{
			Log.Info(mn,"Invalid oauth response");
			rc.Set(2, "Invalid Request ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
	
		
		SQLStr="SELECT * FROM OAUTHREQUEST WHERE KEY=? AND TOKEN=?";
		
		ResultSet rs = DataUtil.getInstance().GetRowSet(SQLStr,key,oauthToken);
		Timestamp sentTime;
		
		try
		{
		
			if(rs.next())
			{
				
				sentTime=rs.getTimestamp("senttime");
			
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
		
		//SQLStr="UPDATE OAUTHREQUEST SET TOKENRESPONSE=?, RESPONSETIME=CURRENT_TIMESTAMP WHERE KEY=? AND TOKEN=?";
		//DataUtil.getInstance().ExecuteUpdate(SQLStr,oauthResponse,key,oauthToken);

		OauthUtil.InsertTokenResponse(0, key, oauthResponse, null,null,null);
		
		
		response.sendRedirect("/AuthComplete.html");
		return;
	} //end method
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
