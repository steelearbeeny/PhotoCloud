package com.sga.scheduler;

import java.text.SimpleDateFormat;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.jobrunr.utils.mapper.gson.GsonJsonMapper;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.log.Log;
import com.sga.common.util.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SchedulerMain {

	   
	   static HikariDataSource ds=null;

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String mn="SchedulerMain::Main";
		
		Log.Info(mn,"In Main");
		
		
	   ds=DataUtil.GetDataSource();
	       
	  if(ds==null)
	  {
		  Log.Error(mn, "A connection to the scheduler database could not be created. Exiting...");
		  return;
	  }

	  Log.Info(mn,"DB Pool: " + ds.getPoolName() + " " + ds.getMaximumPoolSize());
	  GsonJsonMapper gson = new GsonJsonMapper();
	  
	  
       JobRunrConfigurationResult jobRunrConfigurationResult= JobRunr.configure()
   		    .useJsonMapper(gson) //needs to be bedore storage provider
   		    .useStorageProvider(SqlStorageProviderFactory.using(ds,"scheduler."))
            .useBackgroundJobServer()
            .useDashboard() //default port 8000
            .useJmxExtensions()
            .initialize()
            //.getJobScheduler()
            ;
      	           
       Log.Info(mn, "Job scheduler created");
       
       Log.Info(mn, "Scheduler dashboard at http://localhost:8000");
      

       //jobScheduler.enqueue(() -> System.out.println("Up & Running from a background Job"));
	
       Log.Info(mn, "Method complete. Blocking on scheduler termination.");
       //
       // Note: This wont exit but will hang after the last line and the JobRunr will stay running
       //
    		
		
	}

}
