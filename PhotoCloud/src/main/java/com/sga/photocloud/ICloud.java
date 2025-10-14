package com.sga.photocloud;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.*;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;

import com.flickr4java.flickr.photos.Photo;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;

import software.amazon.awssdk.services.s3.model.Bucket;
*/


import com.sga.common.aws.S3Util;
import com.sga.common.dataaccess.DataUtil;
import com.sga.common.generic.GenericAlbum;
import com.sga.common.http.HttpUtils;
import com.sga.common.icloud.sharedphoto.ICloudShare;
import com.sga.common.icloud.sharedphoto.SharedPhotoManager;
import com.sga.common.log.Log;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.SHA512;
import com.sga.common.util.Utils;

public class ICloud {

	public static void main(String[] args) throws Exception {
		
		
		
		//https://www.icloud.com/sharedalbum/#B2O532ODWMIWZG
		//https://www.icloud.com/sharedalbum/#B2OGWZuqDGlTvJJ
		
		
		String url;
		
		url="https://cvws.icloud-content.com/S/AdNjidJHA2ZwcqFPqa9pi2scZQfO/IMG_00008.jpg?o=Akg2A-O6umNXByW8WzO9LwaHMKuf1vYRUTWK2uKq6ftw&v=1&z=https%3A%2F%2Fp148-content.icloud.com%3A443&x=1&a=CAogh6kb5_H1oAvnM6o-XBOhE0JWU2_rnjV0PN8RdP7EvqASZRDTqdj3xzIY08Dr_McyIgEAUgQcZQfOaiXbug3AVAV92aF4s2axiBpLkySjLIuur3FF5HPs1aFZ-Ewy_xUZciXq1ObkSuaTlZUdHlAaYxyAAvAr33vZxQlbtoH45Zw6MNufrr2J&e=1737307643&r=4126a489-ad49-4207-8d51-514c718cff2f-5&s=aWN7t821hxgo63aZtUU_LmpWchg";
		
		//File f = HttpUtils.GetImage(url);		
		
		/*
		S3Util s3 = new S3Util();
		
		List<Bucket> b = s3.ListBuckets();
		
		for(Bucket u : b)
		{
			Log.Info("main", u.name());
		}
		*/
		
		//Path p = Paths.get();
		
		/*
		Path p = Paths.get("C:\\Users\\arbeeny\\AppData\\Local\\Temp\\PC_8006519782370323398.jpg");
		
		String bucket = "testphotobucket";
		String fileName="user/startdatetime/photo1.jpg";
		
		//s3.PutFile(p, bucket,fileName);
		
		String d = Utils.Base64Decode("uatbhWRz6ZKxWPkAT38leF5DHAKa2s6+gCar+mDEylY=");
		Log.Info("main", d);
		String h=SHA512.HashFile(p);
		Log.Info("main", h);
		Log.Info("main", d.equals(h)==true ? "EQUAL" : "NOT EQUAL");
		*/
	
		
		/*
		SharedPhotoManager s = new SharedPhotoManager();
		
		Map<String,Object> m = new HashMap<String,Object>();
		Map<String,String[]>p = new HashMap<String,String[]>();
		
		
		
		m.put("USER","steelearbeeny@msn.com");
		p.put("albumUrl",new String[]{"B2OGWZuqDGlTvJJ"});
		
		s.RunAsJob(null,m,p);
*/
		
		
		int x=0;
		return;
		/*
		HikariDataSource ds = DataUtil.GetDataSource();;
		
		 JobScheduler jobScheduler = JobRunr.configure()
	                .useStorageProvider(SqlStorageProviderFactory.using(ds,"scheduler."))
	                //.useDashboard()
	                
	                .initialize()
	                .getJobScheduler();

	        //jobScheduler.enqueue(() -> System.out.println("Up & Running from a background Job in Web App"));
	 
		//return;
		
			SharedPhotoManager s = new SharedPhotoManager("B2OGWZuqDGlTvJJ");
			
			Log.Info("main", "Enqueueing Job");
			
			
			JobId jobId = jobScheduler.enqueue(() -> s.RunAsJob(JobContext.Null,"B2OGWZuqDGlTvJJ"));
		 
			Log.Info("main", "Job: " + jobId.toString());
			
			*/
		 
		 /*
		ReturnValue<GenericAlbum> rv;
			
		SharedPhotoManager s = new SharedPhotoManager("B2OGWZuqDGlTvJJ");
		
		s.GetSharedPhotoStream();
		
		s.GetSharedPhotoAssets();
		
		
		//s.ListPhotos();
		
		
		rv=s.GetAlbum();
		
		int zzz=0;
		*/
		
	}
}
