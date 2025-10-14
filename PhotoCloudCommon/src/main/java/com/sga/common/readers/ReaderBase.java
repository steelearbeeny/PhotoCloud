package com.sga.common.readers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.sga.common.flickr.FlickrConnection;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.Service;
import com.sga.common.util.Utils;

public abstract class ReaderBase {
	
	
	public JobDashboardLogger logger=null;	
	public JobContext jobContext=null;
	public Map<String,Object> sessionData=null;
	public JobConfiguration jobConfiguration=null;
	public DateTimeFormatter formatter=null;
	public String defaultImageDescription;
	public int currentImage=0;
	
	
	
	abstract public boolean hasNext() throws Exception;
	abstract public GenericPhoto next() throws Exception;
	
	protected ReaderBase(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration)
	{

		this.jobContext=jobContext;
		this.sessionData=sessionData;
		this.jobConfiguration=jobConfiguration;
		
		if(jobContext!=null)
			logger=jobContext.logger();
		
		ZonedDateTime startTime=ZonedDateTime.now();
		
		if(jobConfiguration.dateFormat.length() > 0)
			formatter=DateTimeFormatter.ofPattern(jobConfiguration.dateFormat);
		else
			formatter=DateTimeFormatter.RFC_1123_DATE_TIME;

		defaultImageDescription="File %d Uploaded on " + formatter.format(startTime);
		
		String serviceDesc = Service.GetServiceDescription(jobConfiguration.sourceProvider);
		
		if(serviceDesc.length() > 1)
			defaultImageDescription+=" from " + serviceDesc;
	}
	
	
	protected void GenerateDescription(GenericPhoto inPhoto)
	{
		String photoDesc = String.format(defaultImageDescription, ++currentImage);
		
		if(inPhoto.ownerName!=null)
			photoDesc+=" From: " + inPhoto.ownerName;
		
		if(inPhoto.albumName!=null)
			photoDesc+=" Album: " + inPhoto.albumName;
		
		if(inPhoto.dateCreated!=null)
			photoDesc+=" Created: " + formatter.format(inPhoto.dateCreated);
		
		if(Utils.ToString(inPhoto.description).length() > 0)
			photoDesc+=" Description: " + inPhoto.description;
		
		if(inPhoto.geoData != null && Utils.ToString(inPhoto.geoData.displayName).length() > 0)
			photoDesc+= " Location: " + inPhoto.geoData.displayName;
		
		inPhoto.description=photoDesc;
		
	}
	
	public void Close()
	{
		
	}

}
