package com.sga.photocloud.google;

import java.io.IOException;
import java.util.UUID;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Utils;
import com.sga.photocloud.auth.Security;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * Servlet implementation class GoogleLogin
 */
@WebServlet("/GoogleLogin")
public class GoogleLogin extends AbstractAuthorizationCodeServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoogleLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String mn="GoogleLogin::service";
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//We really shouldnt be getting in here at all
		//
		String mn="GoogleLogin::doGet";
		
		Log.Info(mn,"Method Entered");
		
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
	
	@Override 
	protected void onAuthorization(HttpServletRequest request, HttpServletResponse response, AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException 
	{ 
		
		String mn="GoogleLogin::onAuthorization";

	    String key=UUID.randomUUID().toString();
	    int userId=Security.GetUserId(request);
	    
	    Log.Info(mn, "Method Entered " + key);
	       
		authorizationUrl.setState(key); 
		
		Log.Info(mn,"Auth URL " + authorizationUrl.toString());
		
		OauthUtil.InsertTokenRequest(userId, 4, key, null, null);
		
		HttpSession session = request.getSession();
		session.setAttribute("AUTH_KEY",key);
		
		
		super.onAuthorization(request, response, authorizationUrl); 
	}
	
	@Override
	  protected String getUserId(HttpServletRequest request) {
		String mn="GoogleLogin::getUserId";
		
		int userId;
		Log.Info(mn, "Method Entered");
		
		
		userId=Security.GetUserId(request);
		 return Utils.ToString(userId);	
	
	}
	
	
	  
	  @Override
	  protected AuthorizationCodeFlow initializeFlow()  {
		  
		  AuthorizationCodeFlow flow=null;
		  String mn="GoogleLogin::initializeFlow";
		  
		  Log.Info(mn, "Method Entered");
		  
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
	    
	    String mn="GoogleLogin::getRedirectUri";
	    Log.Info(mn, "Method entered" + url.toString());
	    
	    url.setRawPath("/GoogleLoginCallback");
	    Log.Info(mn, "Returning " + url.toString());
	    return url.build();
	  }

}
