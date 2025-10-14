package com.sga.photocloud.auth;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
//import javax.sql.rowset.JdbcRowSet;


import org.json.JSONObject;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.util.Regex;
import com.sga.common.util.SHA512;
import com.sga.common.util.Utils;
import com.sga.common.util.WebMethodResult;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;



/**
 * Servlet implementation class Authorization
 */
@WebServlet("/Authorization")
public class Authorization extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Authorization() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HikariConfig doNotUse=null;
		
		WebMethodResult rc = new WebMethodResult();
		
		Map<String,String[]> p = request.getParameterMap();
		String query;
		//String scanId="eaac9104-a8fe-4084-b53f-c2d51abbdb5c";
		String scanId;
		
		Log.Info(getServletName(),"Method Entered");
		
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		/*
		if(!Security.IsUserLoggedIn(request))
		{
			Log.Info(getServletName(),"Not logged in");
			rc.Set(2, "Not logged in ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		*/
		//Log.Info(getServletName(),"User logged in");	
	
		
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
			Log.Info(getServletName(), k + " - " + p.get(k)[0]);
		}
	
		String SQLStr=null;
		
		try
		{
			if(query.equals("isloggedin"))
			{
				if(!Security.IsUserLoggedIn(request))
				{
					response.getWriter().write("{ \"isLoggedIn\": false }");
					return;
				}
				else
				{
					response.getWriter().write("{ \"isLoggedIn\": true }");
					return;
				}
	
			}
			
			if(query.equals("logout"))
			{
				Security.DestroySession(request);
				rc.Set(2, "","");
				response.getWriter().write(rc.ToJSON());
				return;
			}
		}
		catch(Exception ex)
		{
			rc.Set(1, "Execption: " + ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
			response.getWriter().flush();

		}
		
		throw new ServletException("An invalid request was specified");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	WebMethodResult rc = new WebMethodResult();
		
		Map<String,String[]> p = request.getParameterMap();
		String action;
		//String scanId="eaac9104-a8fe-4084-b53f-c2d51abbdb5c";
		String SQLStr;
		ResultSet rs=null;
		
		Log.Info(getServletName(),"Method Entered");
		
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		

		Stream<String> lines = request.getReader().lines();
		
		String postData = lines.collect(Collectors.joining(System.lineSeparator()));

		
		Log.Info(getServletName(), "Post Data: " + postData);
		//Log.Info(getServletName(), "Line Count: " + lines.count());
		
		if(postData==null || postData.length() < 1)
		{
			Log.Error(getServletName(), "Invalid post data");
			throw new ServletException("Invalid post data");
		}
		
		JSONObject postObj = new JSONObject(postData);
		
		
		if(postObj.has("action"))
		{
			action=postObj.getString("action");
		}
		else
		{
			throw new ServletException("An invalid request was specified");
		}
			
		
		if(action.equals("login"))
		{
			
			String user;
			String password;
			String hashedPassword;
			String storedPassword;
			int userId=0;
			
			if(!postObj.has("user"))
			{
				rc.Set(1, "Invalid user", "");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				return;
			}
			
			user=postObj.getString("user");
			
			if(!postObj.has("password"))
			{
				rc.Set(1, "Invalid password", "");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				return;
			}
			
			password=postObj.getString("password");
			try
			{
				hashedPassword=SHA512.Hash(password);
				SQLStr="SELECT * FROM USERS WHERE USERNAME=?";
				storedPassword="*";
				
				rs=DataUtil.getInstance().GetRowSet(SQLStr,user);
				
				while(rs.next())
				{
					storedPassword=rs.getString("password");
					userId=rs.getInt("userid");
					
				}
				
				
				
				if(!storedPassword.equalsIgnoreCase(hashedPassword))
				{
					rc.Set(1, "Invalid password", "");
					response.getWriter().write(rc.ToJSON());
					response.getWriter().flush();
					return;
				}
				
				SQLStr="UPDATE USERS SET LASTLOGINDATE=CURRENT_TIMESTAMP WHERE USERNAME=?";
				
				DataUtil.getInstance().ExecuteUpdate(SQLStr,user);
				
				
				HttpSession session = request.getSession();
				
				session.setAttribute("USER",user);
				session.setAttribute("USERID", userId);
				
				
				
				rc.Set(0, "","");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				
				
			
			}
			catch(PoolInitializationException pex)
			{
				Log.Error(getServletName(), pex);
				rc.Set(1, "There was a technical problem checking your login. We're working on in. Please check back again later.","");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
			}
			catch(Exception ex)
			{
				Log.Error(getServletName(), ex);
				rc.Set(1, "An unexpected error occured while trying to log in. We're working on it. Please check back again later.","");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
			}
			finally
			{
				DataUtil.getInstance().Close(rs);
			}
		} //end login
		
		
		
		if(action.equals("change"))
		{
			
			String user;
			String password;
			String newPassword;
			String hashedPassword;
			String storedPassword;
			
			if(!postObj.has("user"))
			{
				rc.Set(1, "Invalid user", "");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				return;
			}
			
			user=postObj.getString("user");
			
			if(!postObj.has("password"))
			{
				rc.Set(1, "Invalid password", "");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				return;
			}
			
			password=postObj.getString("password");
			
			if(!postObj.has("newpassword"))
			{
				rc.Set(1, "Invalid new password", "");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				return;
			}
			
			newPassword=postObj.getString("newpassword");
			
			try
			{
			
				hashedPassword=SHA512.Hash(password);
				SQLStr="SELECT * FROM USERS WHERE USERNAME=?";
				storedPassword="*";
				
				rs=DataUtil.getInstance().GetRowSet(SQLStr,user);
				
				
				if(!rs.next())
				{
					rc.Set(1, "Invalid user or password password", "");
					response.getWriter().write(rc.ToJSON());
					response.getWriter().flush();
					return;
				}
				
				
				
				storedPassword=rs.getString("password");
					
				
				
				
				
				if(!storedPassword.equalsIgnoreCase(hashedPassword))
				{
					
					rc.Set(1, "Invalid current password", "");
					response.getWriter().write(rc.ToJSON());
					response.getWriter().flush();
					return;
				}
				
				if(password.equals(newPassword))
				{
					rc.Set(1, "The new password must be different than the old password", "");
					response.getWriter().write(rc.ToJSON());
					response.getWriter().flush();
					return;
				}
				
				
				if(!Regex.IsMatch("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,50}$",newPassword))
				{
					rc.Set(1, "The new password must contain one lower case letter, one upper case letter and a number and be at least 8 characters long", "");
					response.getWriter().write(rc.ToJSON());
					response.getWriter().flush();
					return;

				}
				
				
				hashedPassword=SHA512.Hash(newPassword);
				
				if(Utils.ToString(rs.getString("lastpassword1")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword2")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword3")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword4")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword5")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword6")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword7")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword8")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword9")).equalsIgnoreCase(hashedPassword) ||
					Utils.ToString(rs.getString("lastpassword10")).equalsIgnoreCase(hashedPassword) )
				
					{
						rc.Set(1, "The new password cannot match any of your last 10 passwords", "");
						response.getWriter().write(rc.ToJSON());
						response.getWriter().flush();
						return;
	
					}
				
				
				
				SQLStr="UPDATE USERS SET "
						+ "LASTPASSWORD10=LASTPASSWORD9, "
						+ "LASTPASSWORD9=LASTPASSWORD8, "
						+ "LASTPASSWORD8=LASTPASSWORD7, "
						+ "LASTPASSWORD7=LASTPASSWORD6, "
						+ "LASTPASSWORD6=LASTPASSWORD5, "
						+ "LASTPASSWORD5=LASTPASSWORD4, "
						+ "LASTPASSWORD4=LASTPASSWORD3, "
						+ "LASTPASSWORD3=LASTPASSWORD2, "
						+ "LASTPASSWORD2=LASTPASSWORD1, "
						+ "LASTPASSWORD1=PASSWORD, "
						+ "LASTPASSWORDCHANGEDATE=CURRENT_TIMESTAMP, " +
					       "PASSWORDRESETTOKEN=NULL, " +
				            "PASSWORDRESETREQUESTDATE=NULL," +
				            "UnsuccessfulLoginAttempts=NULL," +
				            "UnlockAttempts=NULL," 
						+ "PASSWORD=?, LASTLOGINDATE=CURRENT_TIMESTAMP WHERE USERNAME=?";
				
				DataUtil.getInstance().ExecuteUpdate(SQLStr,hashedPassword,user);
				
				
				HttpSession session = request.getSession();
				
				session.setAttribute("USER",user);
				session.setAttribute("USERID",Utils.ToString(rs.getString("USERID")));
				
				
				
				rc.Set(0, "","");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				
				
			
			}
			catch(Exception ex)
			{
				rc.Set(1, "Execption: " + ex.toString(),"");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
			}
			finally
			{
				if(rs!=null)
					DataUtil.getInstance().Close(rs);
			}
		} //end change
		
		
		if(action.equals("apilogin"))
		{
			
			String user;
			String password;
			String hashedPassword;
			String storedPassword;
			int userId=0;
			
			Log.Info(getServletName(), "API Login");
			
			if(!postObj.has("user"))
			{
				rc.Set(1, "Invalid user", "");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				return;
			}
			
			user=postObj.getString("user");
			
			if(!postObj.has("password"))
			{
				rc.Set(1, "Invalid password", "");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
				return;
			}
			
			password=postObj.getString("password");
			try
			{
				//hashedPassword=SHA512.Hash(password);
				//Password should be sent SHA-512 hadhed already
				
				SQLStr="SELECT * FROM USERS WHERE USERNAME=?";
				storedPassword="*";
				
				rs=DataUtil.getInstance().GetRowSet(SQLStr,user);
				
				while(rs.next())
				{
					storedPassword=rs.getString("password");
					userId=rs.getInt("userid");
					
				}
				
				
				
				if(!storedPassword.equalsIgnoreCase(password))
				{
					rc.Set(1, "Invalid password", "");
					response.getWriter().write(rc.ToJSON());
					response.getWriter().flush();
					return;
				}
				
				SQLStr="UPDATE USERS SET LASTLOGINDATE=CURRENT_TIMESTAMP WHERE USERNAME=?";
				
				DataUtil.getInstance().ExecuteUpdate(SQLStr,user);
				
				UUID uuid = UUID.randomUUID();
				UUID key = UUID.randomUUID();
				
				OauthToken token = new OauthToken();
				
				token.userId=userId;
				token.serviceId=3000;
				token.token=uuid.toString();
				token.tokenResponse="";
				token.secret=key.toString();
				
				
				
				SQLStr="INSERT INTO OAUTHREQUEST (USERID, SERVICEID, KEY, TOKEN, SECRET, TOKENRESPONSE, SENTTIME, RESPONSETIME) "
						+ "VALUES (?,?,?,?,?,'',CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
		
				DataUtil.getInstance().ExecuteUpdate(SQLStr,
						userId,
						token.serviceId,
						token.secret,  //key
						token.token,
						token.secret);
	
				Log.Info(getServletName(), "Success " + Utils.ToJSON(token));
				
				response.getWriter().write(Utils.ToJSON(token));
				response.getWriter().flush();
				
				
			
			}
			catch(Exception ex)
			{
				rc.Set(1, "Execption: " + ex.toString(),"");
				response.getWriter().write(rc.ToJSON());
				response.getWriter().flush();
			}
			finally
			{
				DataUtil.getInstance().Close(rs);
			}
		} //end api login
		
		
	
		

		
	} //end method

} //end class
