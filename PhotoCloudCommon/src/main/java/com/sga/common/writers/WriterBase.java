package com.sga.common.writers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.MetadataItem;
import com.sga.common.log.Log;


public abstract class WriterBase {
	
	
	public JobDashboardLogger logger=null;	
	public JobContext jobContext=null;
	public Map<String,Object> sessionData=null;
	public JobConfiguration jobConfiguration=null;
	public DateTimeFormatter formatter=null;
	public Date startTime=new Date();
	public Map<String,String> albumMap=null;
	
	public static final int MAX_RETRIES=10;
	
	abstract public void write(GenericPhoto inPhoto) throws Exception;
	
	protected WriterBase(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration)
	{
		String mn="WriterBase::WriterBase";
		
		this.jobContext=jobContext;
		this.sessionData=sessionData;
		this.jobConfiguration=jobConfiguration;
		
		if(jobContext!=null)
			logger=jobContext.logger();
		
		Log.Info(logger, mn,"Method entered");
			
		if(jobConfiguration.autoCreateAlbums.equals("auto") &&
		   jobConfiguration.dateFormat.length() > 0)
				formatter=DateTimeFormatter.ofPattern(jobConfiguration.dateFormat);
		
		
		if(formatter==null)
		{
			formatter=DateTimeFormatter.RFC_1123_DATE_TIME;
		}

	}
	
	protected void AddToAlbumMap(String albumName, String albumId)
	{
		if(albumMap==null) 
		{
			albumMap=new HashMap<String,String>();
		}
		
		if(albumMap.containsKey(albumName))
			return;
		
		albumMap.put(albumName, albumId);
		
	}
	
	
	protected void PrintAlbumMap()
	{
		String mn="WriterBase::PrintAlbumMap";
		if(albumMap==null)
		{
			Log.Info(logger,mn, "NULL ALBUM MAP");
			return;
		}
		
		if(albumMap.size() < 1)
		{
			Log.Info(logger,mn, "EMPTY ALBUM MAP");
			return;
		}

		
		for(String k : albumMap.keySet())
		{
			
			Log.Info(logger,mn, k + " - " + albumMap.get(k));
			
		}
		
		
		
		
		
	}
	
	
	protected String GetAlbumName(GenericPhoto inPhoto)
	{
		
		String albumName="";
		ZonedDateTime zdt=null;
		MetadataItem mItem=null;
		String mValue;
		
		
		if(jobConfiguration.autoCreateAlbums.equals("none"))
			return albumName;

		
		if(jobConfiguration.autoCreateAlbums.equals("single"))
			albumName=jobConfiguration.albumName;
		else
		{
			albumName="";
			
			if(jobConfiguration.albumNamePrefix.length() > 0)
				albumName=jobConfiguration.albumNamePrefix;
			
			if(formatter!=null)
			{
				if(albumName.length() > 0)
					albumName+=" ";
				
				
				if(inPhoto.dateCreated != null)
				{
					zdt=inPhoto.dateCreated;
				}
				else
					zdt=ZonedDateTime.now();
				
				albumName+=formatter.format(zdt);
			}
			
			
			if(jobConfiguration.exifMetadata.length() > 0)
			{
				mItem=inPhoto.metadata.get(jobConfiguration.exifDirectory,
						jobConfiguration.exifMetadata);
			
			
				mValue=mItem.value;
				
				if(mValue.length() > 0 && mValue.length() < 200)
				{
					if(albumName.length() > 0)
						albumName+=" ";
					
					albumName+=mValue;
				}
			
			}
			
			
			if(jobConfiguration.albumNameSuffix.length() > 0)
			{
				if(albumName.length() > 0)
					albumName+=" ";
				
				albumName+=jobConfiguration.albumNameSuffix;
			}
		}
			
		return albumName;
	
	} //end method
	
	
} //end class
	