package com.sga.common.generic;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.log.Log;
import com.sga.common.processor.PhotoProcessor;
import com.sga.common.readers.DummyReader;
import com.sga.common.readers.FlickrReader;
import com.sga.common.readers.GooglePhotoReader;
import com.sga.common.readers.ICloudSharedAlbumReader;
import com.sga.common.readers.IReader;
import com.sga.common.readers.LocalFileReader;
import com.sga.common.readers.ReaderBase;
import com.sga.common.util.Utils;
import com.sga.common.writers.FlickrWriter;
import com.sga.common.writers.GooglePhotoWriter;
import com.sga.common.writers.IWriter;
import com.sga.common.writers.WriterBase;

public class JobWrapper {
	
	
	public JobWrapper()
	{
		
	}
	
	
	public static void UpdateJobStatus(JobContext jobContext, String jobId, String jobStatus, String message)
	{
		
		String mn="JobWrapper::UpdateJobStatus";
		JobDashboardLogger logger=null;
		String SQLStr;
		
		if(jobContext!=null)
			logger=jobContext.logger();
		
		Log.Info(logger,mn, "Method entered");
		
		SQLStr="INSERT INTO USERJOBSTATUS "
				+ "(JOBID, JOBSTATUS, MESSAGE, MODTIME) "
				+ "VALUES (?,?,?,CURRENT_TIMESTAMP)";
		
		try
		{
			DataUtil.getInstance().ExecuteUpdate(SQLStr,jobId,jobStatus,message);
		}
		catch(Exception ex)
		{
			Log.Error(logger, mn,"Exception: " + ex.toString());
		}
	}
	
	public void RunAsJob(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration) throws Exception
	{
		String mn="JobWrapper::RunAsJob";
		JobDashboardLogger logger=null;
		String albumId=null;
		String SQLStr;
		
		if(jobContext!=null)
			logger=jobContext.logger();
		
		Log.Info(logger,mn, "Method entered");
		
		
		UpdateJobStatus(jobContext, 
				Utils.ToString(jobContext.getJobId()), 
				"STARTED",
				"Job Started");
		
		
		Path currentRelativePath = Paths.get("");
		String currentWorkingDir = currentRelativePath.toAbsolutePath().normalize().toString();
		Log.Info(logger,mn,"Current working directory: " + currentWorkingDir);
		Log.Info(logger, mn,"Starting Job: " + jobConfiguration.toString());
		
		//IReader reader=null;
		ReaderBase reader=null;
		WriterBase writer=null;
		PhotoProcessor photoProcessor=null;
		
		try
		{
		
			photoProcessor=new PhotoProcessor(jobContext,sessionData,jobConfiguration);
			
			switch(jobConfiguration.sourceProvider)
			{
			
				case 0: //Flickr reader
					reader=new FlickrReader(jobContext,sessionData,jobConfiguration);
				break;
	
				case 1: //Apple reader
					reader=new ICloudSharedAlbumReader(jobContext,sessionData,jobConfiguration);
					//reader=new DummyReader(jobContext,sessionData,jobConfiguration);
				break;
				
				case 3: //Local file system
				case 3000: //staged via App
					reader=new LocalFileReader(jobContext, sessionData, jobConfiguration);
					//reader=new DummyReader(jobContext,sessionData,jobConfiguration);
				break;
				
				case 4:
					reader=new GooglePhotoReader(jobContext,sessionData,jobConfiguration);
				break;
				
				default:
					throw new Exception(
							Log.Error(logger, mn,"The source provider was not valid"));
				
				//break;
			} //end switch
		}
		catch(Exception ex)
		{
			String msg;
			
			if(ex.toString().contains("token_rejected"))
			{
				msg="The application could not connect to the source of your photos. "
						+ "Perhaps the credentials have expired. Please resubmit the job and try again. " 
						+ ex.getMessage();
				
				
			}
			else
				msg=ex.toString();
			
			UpdateJobStatus(jobContext, 
					Utils.ToString(jobContext.getJobId()), 
					"EXCEPTION",
					msg);
			
			
			throw new Exception(
					Log.Error(logger, mn,"The source reader could not be created. " +ex.toString()));
		}
		
		
		try
		{
		
			switch(jobConfiguration.targetProvider)
			{
			
				case 0: //Flickr Writer
					writer=new FlickrWriter(jobContext, sessionData, jobConfiguration);
				break;
				
				case 4: //Google Writer
					writer=new GooglePhotoWriter(jobContext, sessionData, jobConfiguration);
				break;
				
				default:
					throw new Exception(
							Log.Error(logger, mn,"The target provider was not valid"));
				
				//break;
			} //end switch
		}
		catch(Exception ex)
		{
			String msg;
			
			if(ex.toString().contains("token_rejected"))
			{
				msg="The application could not connect to the place you wanted to load your photos. "
						+ "Perhaps the credentials have expired. Please resubmit the job and try again. " 
						+ ex.getMessage();
				
				
			}
			else
				msg=ex.toString();
			
			UpdateJobStatus(jobContext, 
					Utils.ToString(jobContext.getJobId()), 
					"EXCEPTION",
					msg);
			
			
			throw new Exception(
					Log.Error(logger, mn,"The target writer could not be created. " +ex.toString()));
		}
		
		//
		//Pump
		//
		
		GenericPhoto p=null;
		long numRead=0;
		long numWritten=0;
		long exceptions=0;
		
		while(reader.hasNext())
		{
			try
			{
				p=reader.next();
				numRead++;
				Log.Info(logger, mn,p.toString());
				
				photoProcessor.Process(p);
				
				writer.write(p);
				numWritten++;
			}
			catch(Exception ex)
			{
				Log.Error(logger, mn,"Exception: " + ex.toString());
				exceptions++;
				
				if(p!=null)
					p.CloseStream();
				
				UpdateJobStatus(jobContext, 
						Utils.ToString(jobContext.getJobId()), 
						"EXCEPTION",
						ex.toString());
				
				
			}
			//note: stream should be closed in writer
			
		}
		
			String msg=Log.Info(logger, 
				mn,
				"Job Complete. Read:" + numRead + 
				" Written: " + numWritten + 
				" Exceptions: " + exceptions);
			
			UpdateJobStatus(jobContext, 
					Utils.ToString(jobContext.getJobId()), 
					"COMPLETED",
					msg);
		
			
			if(exceptions==0 && numRead==numWritten && numRead > 0)
			{
				reader.Close();
			}
			
			
	}




}
