package com.sga.photocloud.icloud;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sga.common.generic.GenericAlbum;
import com.sga.common.icloud.sharedphoto.ICloudShare;
import com.sga.common.icloud.sharedphoto.SharedPhotoManager;
import com.sga.common.log.Log;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;
import com.sga.common.util.WebMethodResult;
import com.sga.photocloud.auth.Security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet implementation class ICloudServlet
 */
@WebServlet("/ICloudServlet")
public class ICloudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ICloudServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		WebMethodResult rc = new WebMethodResult();
		
		Map<String,String[]> p = request.getParameterMap();
		String query;
		
		
		Log.Info(getServletName(),"Method Entered");
		
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		
		if(!Security.IsUserLoggedIn(request))
		{
			Log.Info(getServletName(),"Not logged in");
			rc.Set(2, "Not logged in ","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		Log.Info(getServletName(),"User logged in");	
	
		
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
	
				
		try
		{
			
			
			switch(query)
			{
			
				case "findsharedalbum":
					FindSharedAlbum(response,p);
					break;
					
				case "listalbums":
					ListAlbums(response,p);
				break;
					
				default:
					throw new ServletException("An invalid request action was specified");
				
			
			} //end switch
			
			
		}
		catch(Exception ex)
		{
			Log.Info(getServletName(), ex.toString());
			//throw new ServletException("An error occured",ex);
			rc.Set(1, "An error was encountered in the servlet. " + ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
		}
		
	}

	
	
	
	private void FindSharedAlbum(HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		
		WebMethodResult rc=new WebMethodResult();
		ICloudShare share=null;
		ReturnValue<ICloudShare> rv=null;
		String inAlbumId;
		String mn="ICloudServlet::FindSharedAlbum";
		
		inAlbumId=Utils.GetMapValueA(p, "sharedalbumid");
		
		if(inAlbumId==null)
		{
			rc.Set(3, "The iCloud shared album could not be retrieved. The album id was not valid.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		
		
		Log.Info(mn,"AlbumID:" + inAlbumId);
		//albumId="B2OGWZuqDGlTvJJ";
		SharedPhotoManager s = null;
		
		try
		{
			s = new SharedPhotoManager(inAlbumId);
		}
		catch(Exception ex)
		{
			rc.Set(3, "The iCloud shared album could not be retrieved. The album id url was not in the expected format.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		rv=s.GetSharedPhotoStream();
		
		if(rv.data==null)
		{
			rc.Set(3, rv.message,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		response.getWriter().write(Utils.ToJSON(rv.data));
		return;
		
		
	}
	
	
	private void ListAlbums(HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		//String albumId;
		WebMethodResult rc=new WebMethodResult();
		ICloudShare share=null;
		ReturnValue<GenericAlbum> rv=null;
		String inAlbumId;
		String mn="ICloudServlet::ListAlbums";
		
		inAlbumId=Utils.GetMapValueA(p, "sharedalbumid");
		
		if(inAlbumId==null)
		{
			rc.Set(3, "The iCloud shared album could not be retrieved. The album id was not valid.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		
		Log.Info(mn,"AlbumID:" + inAlbumId);
		//albumId="B2OGWZuqDGlTvJJ";
		SharedPhotoManager s = null;
		
		try
		{
			s = new SharedPhotoManager(inAlbumId);
		}
		catch(Exception ex)
		{
			rc.Set(3, "The iCloud shared album could not be retrieved. The album id url was not in the expected format.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		s.GetSharedPhotoStream();
		s.GetSharedPhotoAssets();
		rv=s.GetAlbum();
		
		
		if(rv.data==null)
		{
			rc.Set(3, rv.message,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		response.getWriter().write(Utils.ToJSON(rv.data));
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
