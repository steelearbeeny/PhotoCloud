package com.sga.common.processor;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.sga.common.configuration.Configuration;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.http.HttpUtils;
import com.sga.common.log.Log;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;

public class PhotoProcessor {
	
	public JobDashboardLogger logger=null;	
	public JobContext jobContext=null;
	public Map<String,Object> sessionData=null;
	public JobConfiguration jobConfiguration=null;
	public DateTimeFormatter formatter=null;
	public Date startTime=new Date();
	
	
	
		
	public PhotoProcessor(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration)
	{
		String mn="PhotoProcessor::PhotoProcessor";
		
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
	
	public void Process(GenericPhoto p) throws Exception
	{
		
		String mn="PhotoProcessor::Process";
		
		Log.Info(logger, mn,"Method entered");
		
		if(jobConfiguration.captionSwitch==0 && jobConfiguration.facialSwitch==0)
			return;
		
		ReturnValue<String> rv=null;
		
		Log.Info(logger, mn,"Processing started " + jobConfiguration.captionSwitch + " " + jobConfiguration.facialSwitch);
		
		//
		//Synchronous Processing
		//Non-Deferred
		//
		//No need to save image
		//
		
	
		//Post image to web service
		
		ProcessingResult pResult=null;
		
		rv=HttpUtils.PostPhoto(
				Configuration.PROCESSING_URL + "/inference", 
				p,
				"userid",jobConfiguration.userId, 
				"uniqueid",jobConfiguration.uniqueId,
				"captionswitch", jobConfiguration.captionSwitch,
				"facialswitch",jobConfiguration.facialSwitch,
				"guid",p.guid,
				"name",p.name);
		
		Log.Info(logger,mn, rv.toString());
			
		if(rv.data.equals("200"))
		{
			pResult=Utils.GetGson().fromJson(rv.message,ProcessingResult.class);
			Log.Info(logger, mn,pResult.toString());
			if(pResult!=null)
			{
				if(pResult.tags != null && pResult.tags.size() > 0)
					p.tags.addAll(pResult.tags);
				
				if(pResult.caption!=null && pResult.caption.length() > 0)
				{
					if(p.description.length() > 0 )
						p.description=p.description + " " + pResult.caption;
					else
						p.description=pResult.caption;
				}
			}	
				
		
		}
		
		
		
		
		
		
		
		
		
		
		
		
	}


}
