package com.sga.photocloud.local;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.javax.JavaxServletDiskFileUpload;
import org.apache.commons.fileupload2.javax.JavaxServletFileUpload;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.generic.ImageUtil;
import com.sga.common.generic.Response;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Regex;
import com.sga.common.util.Utils;
import com.sga.common.util.WebMethodResult;
import com.sga.photocloud.auth.Security;
import com.sga.common.configuration.Configuration;

/**
 * Servlet implementation class LocalFile
 */
@WebServlet("/LocalFile")
public class LocalFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//Temp upload path...files that are too big will be writeen here during upload
	//This is not where the file should be finally stored
    private static final String COMMONS_FILEUPLOAD_WORKING_DIR = "G:\\upload_working";
    private static final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 1024; // 1GB
    //private static final String STAGING_DIR = "G:\\upload_staging";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LocalFile() {
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
		String mn="LocalFile::goGet";
		
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
			
			switch(query)
			{
			
				
				case "authorizeduserquery":
					
					break;
					
				case "getstagedjobs":
					
					GetStagedJobs(request,response,p);
					
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * This method gets called for each file uploaded. There are 2 items in the post.
	 * One is a form field which is skipped below, and the other is the actual file payload.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//doGet(request, response);
		
		
		WebMethodResult rc = new WebMethodResult();
		String mn="LocalFile::doPost";
		int userId=0;
		
		Map<String,String[]> p = request.getParameterMap();
		String query;
		Path uploadedFile=null;
		String source=null;
		boolean isAppLogin=false;
		
		
		Log.Info(mn,"Method Entered");
		
		response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		
		source=Utils.GetMapValueA(p, "source");
		
		
	
		
		if(source==null || !source.equals("app"))
		{
		
		
			if(!Security.IsUserLoggedIn(request))
			{
				Log.Info(mn,"Not logged in");
				rc.Set(2, "Not logged in ","");
				response.getWriter().write(rc.ToJSON());
				return;
			}
		}
		else
		{
			
			if(source.equals("app"))
			{
				isAppLogin=true;
			}
			else
			{
				Log.Info(mn,"API client not logged in");
				rc.Set(2, "API Client Not logged in ","");
				response.getWriter().write(rc.ToJSON());
				return;
			}
			
			
		}
		
		
		
	  
		
		
		
		/*
		Log.Info(mn,"User logged in");	
	
		for(String k : p.keySet())
		{
			//response.getWriter().append(k).append(" - ").append(p.get(k)[0]).append("\n");
			Log.Info(mn, k + " - " + p.get(k)[0]);
		}
		
		
		Enumeration<String> headerNames = request.getHeaderNames();
		String header;

	    if (headerNames != null) {
	            while (headerNames.hasMoreElements()) {
	            		header=headerNames.nextElement();
	            	
	                    Log.Info(mn,"Header: " + header + " = " + request.getHeader(header));
	            }
	    }

	    */
		
	    /*
	    Collection<Part> parts = request.getParts();
	    if(parts!=null)
	    {
	    	Log.Info(mn, "Num Parts: " + parts.size());
	    	
	    	for(Part pp : parts)
	    	{
	    		Log.Info(mn, String.format("Part %s %s %s %d",
	    				pp.getContentType(),
	    				pp.getName(),
	    				pp.getSubmittedFileName(),
	    				pp.getSize()));
	    	}
	    }
	    else
	    {
	    	Log.Info(mn,"No pats found");
	    }
	    */
	
		
		boolean isMultipart = JavaxServletFileUpload.isMultipartContent(request);
	    
		if(!isMultipart)
	    {
	    	Log.Error(mn, "The request must be multipart");
	    	throw new ServletException("Invalid Request");
	    }
	
		if(!isAppLogin)
		{
			userId=Security.GetUserId(request);
		}
	    
	    //
	    //Make sure directories exist
	    //
	    
        File checkDir;
        
        checkDir = new File(COMMONS_FILEUPLOAD_WORKING_DIR);
        if (!checkDir.exists()) {
        	Log.Info(mn, "Creating temp upload path: " + checkDir.toString());
        	checkDir.mkdirs();
        }

        checkDir = new File(Configuration.STAGING_DIR);
        if (!checkDir.exists()) {
        	Log.Info(mn, "Creating staging path: " + checkDir.toString());
        	checkDir.mkdirs();
        }
	    
	    
	    
	    
	 // Create a factory for disk-based file items
	    DiskFileItemFactory factory = new DiskFileItemFactory.Builder()
	      // Set factory constraints
	      .setBufferSize(MAX_REQUEST_SIZE)
	      .setPath(COMMONS_FILEUPLOAD_WORKING_DIR)
	      .get();

	    // Create a new file upload handler
	    JavaxServletDiskFileUpload upload = new JavaxServletDiskFileUpload(factory);

	    // Set overall request size constraint
	    upload.setFileSizeMax(MAX_REQUEST_SIZE);
	    
	    List<DiskFileItem> items=null;
	    String fileName;
	    String uploadPath;
	    File uploadDir;
	    InputStream is=null;
	    int filesUploaded=0;
	    String clientJobId=null;
	    String SQLStr;
	    ResultSet rs;
	    String url;
	    OauthToken token;
	    int serviceId=3; //Localfile
	    String userDescription="";
	    
	    // Parse the request
		
        try {
        	
    	    items = upload.parseRequest(request);
    	    //Log.Info(mn, "Items in queue: " + items.size());
    	    
    	    //
    	    //Parse the form fields to get the job id
    	    //
    	    
            for (DiskFileItem item : items) {
            	
            	
            	if(item.isFormField())
            	{
	                if (Utils.ToString(item.getFieldName()).equals("JOBID") ) 
	                {
	                    
	                	clientJobId=Utils.ToString(item.getString());
	                	
	                	if(clientJobId.length() < 10 || 
	                			clientJobId.length() > 64 || 
	                			!Regex.IsMatch("^[0-9]*$", clientJobId) )
	                	{
	                		Log.Error(mn, "The uploaded job id was invalid");
	                		throw new Exception("Invalid upload job id");
	                	}
	    	    
	                	Log.Info(mn, "Client Job ID " + clientJobId);
	                	
	                }    
	                
	                if (Utils.ToString(item.getFieldName()).equals("USERDESCRIPTION") ) 
	                {
	                    
	                	userDescription=Utils.ToString(item.getString());
	                	if(userDescription.length() > 1000)
	                	{
	                		userDescription=userDescription.substring(0,999);
	                	}
	    	    
	                	Log.Info(mn, "User Description " + userDescription);
	                	
	                }    
	                
	                if (Utils.ToString(item.getFieldName()).equals("OAUTHTOKEN") ) 
	                {
	                	String dum;
	                	
	                	dum=Utils.ToString(item.getString());
	                	
	                	
	                	if(dum.length() < 10 || dum.length() > 200)
	                	{
	                		Log.Error(mn, "The uploaded oauth token was invalid");
	                		throw new Exception("Invalid upload token");

	                	}
	                	
	                	try 
	                	{
	                		token=Utils._gson.fromJson(dum, OauthToken.class);
	                	}
	                	catch(Exception ex)
	                	{
	                		Log.Error(mn, "The uploaded oauth token could not be deserialized");
	                		throw new Exception("Invalid upload token");

	                	}

	                
	                	Log.Info(mn, "Got Token: " + token.toString());
	                	
	                	//
	                	// Validate Token
	                	// need to check timestamp here too 
	                	OauthToken tempToken=OauthUtil.GetTokenForUserAndService(token.userId,token.serviceId);
	                	
	                	if(tempToken==null)
	                	{
	                  		Log.Error(mn, "The uploaded oauth token could not be found");
	                		throw new Exception("Invalid upload token");
	                	}
	                	
	                	if( token.token.length() > 10 && 
	                		tempToken.token.equals(token.token)) {
	                		//Token is valid
	                		
	                		userId=token.userId;
	                		serviceId=token.serviceId;
	                	}
	                	else
	                	{
	                		//Token is not valid
	                		Log.Error(mn, "The uploaded oauth token did not match the token in the database");
	                		throw new Exception("Invalid upload token");
	                		
	                		
	                		
	                	}
	                	
	                	
	                
	                } //ens oauthtoken

            	} //end is field
    	    
            } //end for
            
            
            for (DiskFileItem item : items) {
            	
            	
            	
                if (!item.isFormField()) {
                	uploadedFile=null;
                    fileName = new File(item.getName()).getName();
                    //uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    
                    Log.Info(mn, "Name: " + item.getName() + " Size: " + item.getSize() + " inMem: " + item.isInMemory());
                    
                    /*
                    //This block would save the uploaded file locally
                    
                    uploadPath=UPLOAD_DIR;
                    
                    uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                    	Log.Info(mn, "Creating temp upload path: " + uploadPath);                        uploadDir.mkdirs();
                    }
                    String filePath = uploadPath + File.separator + fileName;
                    Path uploadedFile = Paths.get(filePath);
                    item.write(uploadedFile);
                    */
                    
                    
                    is=item.getInputStream();
                    Response res = ImageUtil.IsValidImage(is);
                    
                    
                    if(!res.success || clientJobId==null)
                    {
                    	Utils.QuietClose(is);
                    	is=null;
                    	Log.Info(mn, "The file was not valid...Skipping - "  + res.message);
                    	throw new Exception("The file was not a valid image file");
                    	
                    }
                    
                    
                    
                    
                    
                    
                    
                    String filePath=String.format("%s%s%d_%s_%s", Configuration.STAGING_DIR,
                    		File.separator,
                    		userId,
                    		clientJobId,
                    		fileName);
                    
                    
                    url=String.format("file:///%s/%d_%s_%s", 
                    		Configuration.STAGING_DIR, 
                    		userId,
                    		clientJobId,
                    		fileName).replace("\\", "/");
                    
                    
                    
                    uploadedFile = Paths.get(filePath);
                    item.write(uploadedFile);

                    filesUploaded++;
                    
                    Utils.QuietClose(is);
                    is=null;
                    
                    Log.Info(mn,"File uploaded successfully: " + fileName + " " + filePath);
                    
                    
                    SQLStr="SELECT COUNT(*) FROM USERJOB "
                    		+ "WHERE JOBID=? AND "
                    		+ "JOBTYPE='LOCALFILE' AND "
                    		+ "USERID=?::INT AND "
                    		+ "SOURCESERVICEID=?::INT";
                    
                     
                    synchronized(this)
                    {
                    rs=DataUtil.getInstance().GetRowSet(SQLStr,
                    		clientJobId,
                    		Utils.ToString(userId),
                    		Utils.ToString(serviceId));
                    
                    if(rs.next())
                    {
                    	if(Utils.ToInt(rs.getInt(1)) < 1)
                    	{
                    		// insert the job header for a local file job
                    		//ServiceID 3 = localfile / 3000 from stager app
                  			SQLStr="INSERT INTO USERJOB (JOBID,JOBTYPE,USERID,SOURCESERVICEID,"
                  					+ "UNIQUEID,USERDESCRIPTION,MODTIME) "
                  					+ "VALUES (?,'LOCALFILE',?,?,?,?,CURRENT_TIMESTAMP)";
                			
                			DataUtil.getInstance().ExecuteUpdate(SQLStr,
                					clientJobId,
                					userId,
                					serviceId,
                					clientJobId,
                					userDescription);
                              		
                    		
                    	}
                    } //end if
                    
                    } //end sync
                    
                    DataUtil.getInstance().Close(rs);                    
                    
                    //file:///G:/upload_staging/DSC_0001.JPG
                    
                   //url=URLEncoder.encode(url,StandardCharsets.UTF_8);
                    
                    
        			ImageUtil.WriteImageAndMetadataToDB(
        					userId, 
        					clientJobId, 
        					fileName,
        					//currentItem,
        					url, 
        					res,
        					null);
                    
                    rc.Set(0, res.size + " Bytes", res.message);
                    
                }
                else
                {
                	//Log.Info(mn, "Form Field Ignored : " + item.getFieldName());
 
                }
            }
            
			
        	
        	Log.Info(mn, "Exiting Servlet - Files Uploaded: " + filesUploaded );
        	response.getWriter().write(rc.ToJSON());
			response.getWriter().flush();
            
            
        } catch (Exception e) {
            //response.getWriter().println("File upload failed: " + e.getMessage());
        	
        	//make sure that is we get an excaption above, we delete the file we saved.
        	if(uploadedFile!=null)
        	{
        		Files.deleteIfExists(uploadedFile);
        	}
            
            Utils.QuietClose(is);
            is=null;
            throw new ServletException(e.toString());
        }
        
        
        
        /*
        
		if(p.containsKey("query"))
		{
			query=p.get("query")[0];
		}
		else
		{
			throw new ServletException("An invalid request was specified");
		}
			
		*/
	
	
				
		
		
		
	}
	
	public void GetStagedJobs(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		
		WebMethodResult rc=new WebMethodResult();
		String mn="LocalFile::GetStagedJobs";
		String SQLStr;
		int userId=0;
		ResultSet rs=null;
		
		Log.Info(mn,"Method Entered");
		
		userId=Security.GetUserId(request);
		
		if(userId<1)
		{
			rc.Set(1,Log.Error(mn, "The user could not be retrieved. The value was not valid.") ,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}

		try
		{
		
		SQLStr="select j.jobid, j.userid, j.sourceserviceid, "
				+ "s.description, j.userdescription, j.modtime, count(*) as numphotos "
				+ "from userjob j "
				+ "inner join userjobitem i on "
				+ "j.userid=i.userid and "
				+ "j.jobid=i.jobid "
				+ "inner join service s on "
				+ "s.serviceid=j.sourceserviceid "
				+ "where j.userid=?::int and "
				+ "j.sourceserviceid > 1000 and "
				+ "j.targetserviceid is null and "
				+ "j.schedulerjobid is null "
				+ "group by j.jobid, j.userid, j.sourceserviceid, "
				+ "s.description, j.userdescription, j.modtime "
				+ "order by j.modtime desc";
		
		
			String jobs=DataUtil.getInstance().GetRowSetAsJSON(SQLStr,Utils.ToString(userId));
		
			Log.Info(mn, jobs);
			response.getWriter().write(jobs);
			return;
		
		
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex.toString());
			rc.Set(3, "The search for pending PhotoCloud jobs could not be completed. " + ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
			return;

		}
		finally
		{
			DataUtil.getInstance().Close(rs);
				
		}
		
		
		
		
	} //end method

}
