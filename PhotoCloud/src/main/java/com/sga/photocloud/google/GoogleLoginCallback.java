package com.sga.photocloud.google;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.GenericUrl;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Utils;
import com.sga.photocloud.auth.Security;

/**
 * Servlet implementation class GoogleLoginCallback
 */
@WebServlet("/GoogleLoginCallback")
public class GoogleLoginCallback extends AbstractAuthorizationCodeCallbackServlet {
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoogleLoginCallback() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String mn="GoogleLoginCallback::service";
    	Log.Info(mn, "Method entered");
    	
    	if(!Security.IsUserLoggedIn(request))
		{
			Log.Info(mn,"Not logged in");
			//rc.Set(2, "Not logged in ","");
			//response.getWriter().write(rc.ToJSON());
			throw new ServletException("Not logged on");
			
		}
		
    	super.service(request, response);
    }
    
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    /*
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}
     */
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
	@Override
	  protected AuthorizationCodeFlow initializeFlow() throws IOException {
		  AuthorizationCodeFlow flow=null;
		  String mn="GoogleLoginCallback::initializeFlow";
		  try
		  {
			  flow = GoogleServlet.newFlow();
		  }
		  catch(Exception ex)
		  {
			  Log.Info(mn, "Exception : " + ex.toString());
		  }
	    
		  return flow;
	  }

	  @Override
	  protected String getRedirectUri(HttpServletRequest request) {
		  
		    GenericUrl url = new GenericUrl(request.getRequestURL().toString());
		    String mn="GoogleLoginCallback::getRedirectUri";
		    Log.Info(mn, "Method entered" + url.toString());
		    
		    url.setRawPath("/GoogleLoginCallback");
		    Log.Info(mn, "Returning " + url.toString());
		    return url.build();
	  }

	  @Override
	  protected String getUserId(HttpServletRequest request) {
			String mn="GoogleLoginCallback::getUserId";
			
			int userId;
			Log.Info(mn, "Method Entered");
			
			
			userId=Security.GetUserId(request);
			 return Utils.ToString(userId);	
	  }

	  @Override
	  protected void onSuccess(HttpServletRequest request, HttpServletResponse response, Credential credential)
	      throws IOException {
		  
			String mn="GoogleLoginCallback::onSuccess";
			int userId=Security.GetUserId(request);
			
			Log.Info(mn, "Method Entered - " + 
					credential.getAccessToken() + " " + 
					credential.getRefreshToken() + " " +
					credential.getExpiresInSeconds() + " " +
					credential.getTokenServerEncodedUrl() + " " +
					credential.toString()
					);
		  
			
			
			LocalDateTime ldt = LocalDateTime.now().plusSeconds(credential.getExpiresInSeconds());
			
			
			String expTime = formatter.format(ldt);
			
			Log.Info(mn, "Expires " + expTime);
			
			HttpSession session = request.getSession();
			
			String key=(String)session.getAttribute("AUTH_KEY");
			
			if(key==null || key.length() < 1)
			{
				Log.Error(mn, "Invalid auth key");
				key="";
			}
			
			
			OauthUtil.InsertTokenResponse(userId, 4, key, credential.getAccessToken(), credential.getRefreshToken(),expTime,null);
			
			response.sendRedirect("/AuthComplete.html");
	  }

	  @Override
	  protected void onError(HttpServletRequest request, HttpServletResponse response, AuthorizationCodeResponseUrl errorResponse)
	      throws IOException {
			String mn="GoogleLoginCallback::onError";
			Log.Info(mn, "Method Entered");
			int userId=Security.GetUserId(request);
			
			HttpSession session = request.getSession();
			
			String key=(String)session.getAttribute("AUTH_KEY");
			
			if(key==null || key.length() < 1)
			{
				Log.Error(mn, "Invalid auth key");
				key="";
			}
			
			
			
			OauthUtil.InsertTokenResponse(userId, 4, key, null,null,null,mn + " - Authorization error");

	    
	  }
	
	

}
