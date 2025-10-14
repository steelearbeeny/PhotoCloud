package com.sga.photocloud.job;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.generic.GenericAlbum;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.JobWrapper;
import com.sga.common.icloud.sharedphoto.ICloudShare;
import com.sga.common.icloud.sharedphoto.SharedPhotoManager;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.JSONMapper;
import com.sga.common.util.Regex;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;
import com.sga.common.util.WebMethodResult;
import com.sga.photocloud.auth.Security;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Servlet implementation class JobManager
 */
@WebServlet("/JobManager")
public class JobManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static HikariDataSource ds=null;
	private static JobScheduler jobScheduler=null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JobManager() {
        
    	super();
    	String mn="JobManager::JobManager";
    	
    	
        // TODO Auto-generated constructor stub
        if(ds==null)
        {
	    	ds = DataUtil.GetDataSource();;
			
	    	if(ds==null)
	    	{
	    		Log.Error(mn, "Could not instantiate jon scheduler data source.");
	    		return;
	    	}
	    	
			jobScheduler = JobRunr.configure()
		                .useStorageProvider(SqlStorageProviderFactory.using(ds,"scheduler."))
		                //.useDashboard()
		                
		                .initialize()
		                .getJobScheduler();
        
			Log.Info(mn,"Job scheduler started " + jobScheduler.toString());
			
        }
        
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
			
				case "submitjob":
					SubmitJob(request,response,p);

					break;
					
				case "listjobs":
					//ListAlbums(response,p);
				break;
				
				
				case "jobcontent":
					GetJobContent(request,response,p);
					
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

	private void SubmitJob(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		
		WebMethodResult rc=new WebMethodResult();
		String mn="JobManager::SubmitJob";
		String SQLStr;
		int userId=0;
		
		Log.Info(mn,"Method Entered");
		
		userId=Security.GetUserId(request);
		
		String uniqueId;
		String stagedJobId;
		String dum;
		int selectedSourceProvider=-1;
		int selectedTargetProvider=-1;
		JobConfiguration jobConfiguration;
		ResultSet rs=null; 

		
		uniqueId=Utils.GetMapValueA(p,"uniqueId");
		stagedJobId=Utils.GetMapValueA(p, "stagedJobId");
		
		if(stagedJobId!=null)
		{
			uniqueId=stagedJobId;
		}
		
		dum=Utils.GetMapValueA(p, "selectedSourceProvider");
		if(dum!=null && dum.length() > 0)
			selectedSourceProvider=Utils.ToInt(dum);
		
		dum=Utils.GetMapValueA(p, "selectedTargetProvider");
		if(dum!=null && dum.length() > 0)
			selectedTargetProvider=Utils.ToInt(dum);
		
		//
		//Validate the job configuration
		//
		
		if(userId<1)
		{
			rc.Set(1,Log.Error(mn, "The user could not be retrieved. The value was not valid.") ,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		if(selectedSourceProvider < 0)
		{
			rc.Set(1,Log.Error(mn, "The source for the photos could not be retrieved. The value was not valid.") ,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		else
		{
			if(uniqueId.length() < 10 || 
					uniqueId.length() > 64 || 
					!Regex.IsMatch("^[0-9]*$", uniqueId))
			{
				rc.Set(1,Log.Error(mn, "The unique id for the photos could not be retrieved. The value was not valid.") ,"");
				response.getWriter().write(rc.ToJSON());
				return;
			}

		}

		if(selectedTargetProvider < 0)
		{
			rc.Set(1,Log.Error(mn, "The target for the photos could not be retrieved. The value was not valid.") ,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}

		jobConfiguration=new JobConfiguration(
				userId,
				selectedSourceProvider,
				selectedTargetProvider,
				uniqueId);
		
		OauthToken sourceToken=null;
		OauthToken targetToken=null;
		
		if(selectedSourceProvider==0 || selectedSourceProvider==4) //flickr or Google
		{
			//Get the oauth token
			sourceToken=OauthUtil.GetTokenForUserAndService(
					userId, 
					selectedSourceProvider);
			
			jobConfiguration.sourceToken=sourceToken;
			
			if(sourceToken==null)
			{
				rc.Set(1,Log.Error(mn, "The security authorization token for the source could not be retrieved. The value was not valid.") ,"");
				response.getWriter().write(rc.ToJSON());
				return;
			}
		}
		

		if(selectedTargetProvider==0 || selectedTargetProvider==4) //flickr
		{
			//Get the oauth token
			targetToken=OauthUtil.GetTokenForUserAndService(
					userId, 
					selectedTargetProvider);
			
			jobConfiguration.targetToken=targetToken;
			
			if(targetToken==null)
			{
				rc.Set(1,Log.Error(mn, "The security authorization token for the target could not be retrieved. The value was not valid.") ,"");
				response.getWriter().write(rc.ToJSON());
				return;
			}
		}
		
		jobConfiguration.autoCreateAlbums="none";
		
		dum=Utils.GetMapValueA(p, "autocreate");
		if(dum!=null && (dum.equals("none") || dum.equals("single") || dum.equals("auto")))
			jobConfiguration.autoCreateAlbums=dum;
		else
			throw new Exception("Invalid album auto creation option");
		
		
		if(jobConfiguration.autoCreateAlbums.equals("single"))
		{
			dum=Utils.GetMapValueA(p, "albumname");
			if(dum!=null && dum.length() > 0 && dum.length() < 200)
				jobConfiguration.albumName=dum;
			else
				throw new Exception("You must specify an album name when choosing \"single\". Please enter a valid album name and try again.");
			
		}
		
		
		if(jobConfiguration.autoCreateAlbums.equals("auto"))
		{
			dum=Utils.GetMapValueA(p, "albumprefix");
			if(dum!=null && dum.length() < 200)
				jobConfiguration.albumNamePrefix=dum;
			
			dum=Utils.GetMapValueA(p, "albumsuffix");
			if(dum!=null && dum.length() > 0)
			{
				if(dum!=null && dum.length() < 200)
					jobConfiguration.albumNameSuffix=dum;
				
			}
			
			
			
			
			dum=Utils.GetMapValueA(p, "dateformat");
			if(dum!=null && dum.length() > 0)
			{
				if(Regex.IsMatch("^[yMm\\- ]*$", dum))
					jobConfiguration.dateFormat=dum;
				else
					throw new Exception("Invalid date format");
			}
			
			dum=Utils.GetMapValueA(p, "exifmetadata");
			if(dum!=null && dum.length() > 0)
			{
				jobConfiguration.exifMetadata=Utils.ToString(Utils.ToInt(dum));
			
				dum=Utils.GetMapValueA(p, "exifdirectory");
				if(dum!=null && dum.length() > 0 && dum.length() < 10)
					jobConfiguration.exifDirectory=dum;
				else
					throw new Exception("Invalid EXIF directory");			
			}
			
			//
			// Validate autocreate combinriona here
			//
			
			if(jobConfiguration.albumNamePrefix.length() < 1 &&
					jobConfiguration.dateFormat.length() < 1 &&
					jobConfiguration.exifMetadata.length() < 1)
				throw new Exception("You must supply either an album name prefix, a date format or a metadata tag to autocreate albums. Please select at least one and try again.");
				
			
		} //end of autocreate alnum validation
		
		
		dum=Utils.GetMapValueA(p, "albumUrl");
		if(dum!=null && dum.length() > 0)
			jobConfiguration.albumUrl=Utils.ToString(dum);
		
		
		
		//set the source albums if any
		dum=Utils.ToString(Utils.GetMapValueA(p, "sourceAlbums"));
		
		
		try
		{
		
			jobConfiguration.sourceAlbums=Arrays.asList(dum.split("\\|"));
			
			Log.Info(mn, "Source Albums: " + dum + " - " + jobConfiguration.sourceAlbums );
	
			Log.Info(mn, "Ready to Submit Job "  + jobConfiguration.toString());
		
		
		

			JobWrapper w = new JobWrapper();
			Log.Info(mn, "Enqueueing Job " + jobScheduler.toString());
			
			Map<String,Object> sessionData = Security.GetSession(request);
			
			//This map of job parameters gets loaded but never used.
			/*
			Map<String,String> parms = new HashMap<String,String>();
			String v;
			
			for(String k : p.keySet())
			{
				v=p.get(k)[0];
				parms.put(k,v);
				Log.Info(mn, "Putting map " + k + " " + v);
				
				
			}
			*/
			
			JobId jobId = jobScheduler.enqueue(() -> w.RunAsJob(JobContext.Null,
															sessionData, 
															jobConfiguration));

			//JobId jobId = jobScheduler.enqueue(() -> w.RunAsJob(JobContext.Null,
			//		sessionData));

			SQLStr = "SELECT COUNT(*) FROM USERJOB WHERE JOBID=? AND USERID=?::INT";
			
			
			long recs = DataUtil.getInstance().GetSingleLong(SQLStr, uniqueId, Utils.ToString(userId));
			
			if(recs > 0)
			{
				//
				//The local file stager will have added the userjob record 
				//when the first file is staged
				//
				//1=S3 no authentication
				SQLStr="UPDATE USERJOB SET SCHEDULERJOBID=? WHERE JOBID=? AND USERID=?";
			
				DataUtil.getInstance().ExecuteUpdate(SQLStr,
					jobId.asUUID().toString(),
					uniqueId,
					userId);
			}
			else
			{
				SQLStr="INSERT INTO USERJOB (JOBID,"
						+ "JOBTYPE,"
						+ "USERID,"
						+ "SOURCESERVICEID,"
						+ "TARGETSERVICEID,"
						+ "UNIQUEID,"
						+ "SCHEDULERJOBID,"
						+ "SOURCEOAUTHKEY, "
						+ "TARGETOAUTHKEY,"
						+ "MODTIME) VALUES(?,'SCHEDULER',?::INT,?::INT,?::INT,?,?,?,?,CURRENT_TIMESTAMP)";
				
				DataUtil.getInstance().ExecuteUpdate(SQLStr,
						jobId.asUUID().toString(),
						userId,
						jobConfiguration.sourceProvider,
						jobConfiguration.targetProvider,
						jobConfiguration.uniqueId,
						jobId.asUUID().toString(),
						jobConfiguration.sourceToken==null ? null : jobConfiguration.sourceToken.key,
						jobConfiguration.targetToken==null ? null : jobConfiguration.targetToken.key
						
						);
		
			}
			
			Log.Info(mn,SQLStr);
			response.getWriter().write(Utils.ToJSON(jobId));
			
			Log.Info(mn, Utils.ToJSON(jobId));
			
			
			return;

		}
		catch(Exception ex)
		{
			Log.Error(mn, ex.toString());
			rc.Set(3, "The PhotoCloud job could not be submitted. " + ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		
		
		
		
		
		
		
	}
	
	
	/*
	
	private void SubmitICloudJob(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		//String albumId;
		WebMethodResult rc=new WebMethodResult();
		ICloudShare share=null;
		ReturnValue<GenericAlbum> rv=null;
		String inAlbumId;
		String mn="JobManager::SubmitJob";
		String SQLStr;
		int userId=0;
		
		inAlbumId=Utils.GetMapValueA(p, "albumUrl");
		
		if(inAlbumId==null)
		{
			rc.Set(1, "The iCloud shared album could not be retrieved. The album id was not valid.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		
		Log.Info(mn,"AlbumID:" + inAlbumId);
		//albumId="B2OGWZuqDGlTvJJ";
		
		userId=Security.GetUserId(request);
		
		
		try
		{
			SharedPhotoManager s = new SharedPhotoManager();
			Log.Info(mn, "Enqueueing Job " + jobScheduler.toString());
			
			Map<String,Object> sessionData = Security.GetSession(request);
			Map<String,String> parms = new HashMap<String,String>();
			String v;
			
			for(String k : p.keySet())
			{
				v=p.get(k)[0];
				parms.put(k,v);
				Log.Info(mn, "Putting map " + k + " " + v);
				
				
			}
			
			
			JobId jobId = jobScheduler.enqueue(() -> s.RunAsJob(JobContext.Null,
															sessionData, 
															parms));
			
			//1=S3 no authentication
			SQLStr="INSERT INTO USERJOB (JOBID,JOBTYPE,USERID,SERVICEID,USERSERVICEAUTHENTICATIONID) VALUES(?,'SCHEDULER',?,'1',NULL)";
			
			DataUtil.getInstance().ExecuteUpdate(SQLStr,jobId.asUUID().toString(),userId);
			
			response.getWriter().write(Utils.ToJSON(jobId));
			
			Log.Info(mn, Utils.ToJSON(jobId));
			
			
			return;

		}
		catch(Exception ex)
		{
			rc.Set(3, "The iCloud shared album could not be retrieved. The album id url was not in the expected format.","");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		*/
		
 
		
		
		
		/*
		s.GetSharedPhotoStream();
		s.GetSharedPhotoAssets();
		rv=s.GetAlbum();
		
		
		if(rv.data==null)
		{
			rc.Set(3, rv.message,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		
		rc.Set(0, "", "");
		//response.getWriter().write(Utils.ToJSON(rv.data));
		return;
		*/
		
	//}
	
	
	

	private void GetJobContent(HttpServletRequest request, HttpServletResponse response, Map<String,String[]> p) throws Exception
	{
		
		WebMethodResult rc=new WebMethodResult();
		String mn="JobManager::GetJobContent";
		String SQLStr;
		int userId=0;
		
		Log.Info(mn,"Method Entered");
		
		userId=Security.GetUserId(request);
		
		String uniqueId;
		String dum;
		int selectedSourceProvider=-1;
		int selectedTargetProvider=-1;
		JobConfiguration jobConfiguration;
		ResultSet rs=null; 
		String stagedJobId;

		
		uniqueId=Utils.GetMapValueA(p,"uniqueId");
		stagedJobId=Utils.GetMapValueA(p, "stagedJobId");
		
		if(stagedJobId!=null)
		{
			uniqueId=stagedJobId;
		}
		
		if(userId<1)
		{
			rc.Set(1,Log.Error(mn, "The user could not be retrieved. The value was not valid.") ,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		

		if(uniqueId.length() < 10 || 
				uniqueId.length() > 64 || 
				!Regex.IsMatch("^[0-9]*$", uniqueId))
		{
			rc.Set(1,Log.Error(mn, "The unique id for the photos could not be retrieved. The value was not valid.") ,"");
			response.getWriter().write(rc.ToJSON());
			return;
		}

		

		int fileCount=0;
		
		
		
		
		try
		{
			SQLStr="SELECT COUNT(*) FROM USERJOBITEM "
					+ "WHERE USERID=?::INT AND "
					+ "JOBID=?";
			
			fileCount=(int)DataUtil.getInstance().GetSingleLong(SQLStr, 
					Utils.ToString(userId), 
					uniqueId);
			
			
			//response.getWriter().write(Utils.ToJSON(jobId));
			
			//Log.Info(mn, Utils.ToJSON(jobId));
			
			
			SQLStr="select m.metadataid, m.metadatadirectory, m.metadatakey, z.samplevalue, count(*) "
					+ "from userjobitemmetadata i "
					+ "inner join metadatamaster m on "
					+ "i.metadataid=m.metadataid and "
					+ "i.metadatadirectory=m.metadatadirectory "
					+ "left join ( "
					+ "select metadataid,metadatadirectory, max(metadatavalue) as samplevalue "
					+ "from userjobitemmetadata "
					+ "where jobid=? and "
					+ "metadataid in ('271','272','305','315','42034','42035','42036','41729') and "
					+ "metadatavalue <> '' and "
					+ "metadatadirectory in ('Exif IFD0','Exif SubIFD') "
					+ "group by metadataid, metadatadirectory "
					+ ") z on z.metadataid=m.metadataid "
					+ "where userid=?::int and jobid=? and  "
					+ "m.metadataid in ('271','272','305','315','42034','42035','42036','41729') and "
					+ "i.metadatavalue <> '' and "
					+ "m.metadatadirectory in ('Exif IFD0','Exif SubIFD') "
					+ "group by m.metadataid, m.metadatadirectory, m.metadatakey, z.samplevalue";
			
				rs=DataUtil.getInstance().GetRowSet(SQLStr,
					uniqueId,
					Utils.ToString(userId),
					uniqueId);
			
				//Map<String,Object> map = Utils.ToMap(rs);
				//String rv=Utils.ToJSON(rs);				
				
				JSONObject obj = new JSONObject();
				JSONArray a = JSONMapper.mapResultSet(rs);
				
				obj.put("metadata", a);
				obj.put("filecount", fileCount);				
				
				
				SQLStr="select m.metadataid, m.metadatakey, count(*) "
						+ "from userjobitemmetadata i "
						+ "inner join metadatamaster m on "
						+ "i.metadataid=m.metadataid "
						+ "where userid=?::int and jobid=? and  "
						+ "m.metadataid in ('306','36867') and "
						+ "i.metadatavalue <> '' and "
						+ "m.metadatadirectory in ('Exif IFD0','Exif SubIFD') "
						+ "group by m.metadataid, m.metadatadirectory, m.metadatakey";				
				
				
				rs=DataUtil.getInstance().GetRowSet(SQLStr,
						Utils.ToString(userId),
						uniqueId);
					
				a = JSONMapper.mapResultSet(rs);
				obj.put("datedata", a);
				
				String rv=obj.toString();
				Log.Info(mn, rv);
				
				response.getWriter().write(rv);
			
			return;

		}
		catch(Exception ex)
		{
			Log.Error(mn, ex.toString());
			rc.Set(3, "The PhotoCloud job content could not be read. " + ex.toString(),"");
			response.getWriter().write(rc.ToJSON());
			return;
		}
		
		
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
