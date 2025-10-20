package com.sga.common.readers;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.log.Log;

public class DummyReader implements IReader {
	
	int numItems=0;
	int currentItem=0;
	JobDashboardLogger logger=null;
	
	public DummyReader(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration) throws Exception
	{
		String mn="DummyReader::DummyReader";
		
				
		if(jobContext!=null)
			logger=jobContext.logger();
		
		Log.Info(logger,mn, "Method entered " + jobConfiguration.toString());
	
		
		//TODO
		numItems=1;
		
	}

	@Override
	public boolean hasNext() throws Exception {
		// TODO Auto-generated method stub
		if(currentItem < numItems)
			return true;
		
		
		
		return false;
	}

	@Override
	public GenericPhoto next() throws Exception {
		GenericPhoto p;
		String mn="DummyReader::next";
		
		//TODO
		Log.Info(logger, mn,"Method entered");
		
		Path path = Paths.get("G:\\TestPics\\export\\DSC_0088.jpg");
		
		byte[] dum = Files.readAllBytes(path);
		p=new GenericPhoto();
		p.imageData=dum;
		p.inputStream=new ByteArrayInputStream(dum);
		p.fileSize=(long)dum.length;
		p.name="DSC_0088.jpg";
		currentItem++;
		return p;
	}

}
