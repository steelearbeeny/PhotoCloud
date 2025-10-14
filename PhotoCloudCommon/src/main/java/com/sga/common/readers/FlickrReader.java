package com.sga.common.readers;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotoSet;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.Size;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.sga.common.configuration.Configuration;
import com.sga.common.dataaccess.DataUtil;
import com.sga.common.flickr.FlickrConnection;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.ImageUtil;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.Response;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.util.Utils;

import software.amazon.awssdk.utils.IoUtils;

public class FlickrReader extends ReaderBase {

	
	
	//public JobDashboardLogger logger=null;	
	//public JobContext jobContext=null;
	//public Map<String,Object> sessionData=null;
	//public JobConfiguration jobConfiguration=null;
	public FlickrConnection flickrConnection=null;
	public Map<String,String> albumMap=null;
	//public DateTimeFormatter formatter=null;
	public int currentAlbum=0;
	public PhotoList<Photo> currentList=null;
	public Iterator<Photo> currentIterator=null;
	String albumId=null;
	PhotosetsInterface pi=null;
	PhotosInterface photoInt=null;
	
	
	public FlickrReader(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration) throws Exception
	{
		super(jobContext,sessionData,jobConfiguration);

		String mn="FlickrReader::FlickrReader";
		OauthToken sourceToken=null;
		
		Log.Info(logger, mn,"Method entered. JobConfiguration: " + jobConfiguration.toString());
		
		sourceToken=jobConfiguration.sourceToken;
		
		flickrConnection = new FlickrConnection(sourceToken);
		
		if(flickrConnection.flickr.getAuth()==null)
			Log.Info(logger, mn, "Flickr not authorized. Using public");
		else
		{
			RequestContext.getRequestContext().setAuth(flickrConnection.flickr.getAuth());
			Log.Info(mn,"Flickr authorized. " + flickrConnection.flickr.getAuth().getUser().getId() );
		}
		
		 pi = flickrConnection.flickr.getPhotosetsInterface();
		 photoInt = flickrConnection.flickr.getPhotosInterface();
	        
		
		 if(jobConfiguration.sourceAlbums.size() < 1)
		 {
			 Log.Info(logger,mn,"No source albums were specified");
		 }
		 else
		 {
			 albumId=jobConfiguration.sourceAlbums.get(currentAlbum);
			 currentList=pi.getPhotos(albumId, 100,0);
			 currentIterator=currentList.iterator();
		 }
		 
		 
	
		/*
		PhotosetsInterface pi;
		
		pi.getPhotos(mn, 0, 0)
	*/
		
	}
	

	
	
	
	@Override
	public boolean hasNext()  {
		// TODO Auto-generated method stub
		String mn="FlickrReader::hasNext";
		
		
		//Log.Info(logger,mn, "Size: " + items.size() + "  " + currentIndex + "  " + (items.size() > (currentIndex+1)));
		
		//return items.size() > (currentIndex+1);
		
		if(currentIterator==null) return false;
		
		if(currentIterator.hasNext()==false)
		{
			if(currentAlbum+1 >= jobConfiguration.sourceAlbums.size())
			{
				//were at the last photo of the last album in the list
				return false;
			}
			
			//still more albums to go
			//TODO add paging
			 currentAlbum++;
			 
			 albumId=jobConfiguration.sourceAlbums.get(currentAlbum);
			 try
			 {
				 currentList=pi.getPhotos(albumId, 100,0);
			 }
			 catch(Exception ex)
			 {
				 Log.Error(logger,mn,ex.toString());
				 return false;
			 }
			 currentIterator=currentList.iterator();
			 
			
			
		}
		
		return currentIterator.hasNext();
		
		
	}

	@Override
	public GenericPhoto next() throws Exception  {
		// TODO Auto-generated method stub
		
		GenericPhoto gpout=null;
		String mn="FlickrReader::next";
		
		
		
		
		
		//Log.Info(logger,mn, "Start Size: " + items.size() + "  " + currentIndex );

		
		//if(currentIndex < 0)
		//	currentIndex=0;
		
		if(currentIterator == null || currentIterator.hasNext()==false)
		{
			throw new NoSuchElementException("No elements in collection");
		}
		
		
		
		//p=items.get(currentIndex);
	
		//Log.Info(logger,mn, "End Size: " + items.size() + "  " + currentIndex );

		
		
		Photo p =  currentIterator.next();
        String url = p.getLargeUrl();
        URL u=null;
        String filename;
        
        try
        {
        	u = new URL(url);
        	filename = u.getFile();
        	filename = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
        
        	Log.Info(logger, mn,"Album: " + albumId + " FileName: " + filename + " URL:" + url);
        	
	        //System.out.println("Now writing " + filename + " to " + setDirectory.getCanonicalPath());
	        //BufferedInputStream inStream = new BufferedInputStream(photoInt.getImageAsStream(p, Size.ORIGINAL));
	        //File newFile = new File(setDirectory, filename);
			
	        gpout=new GenericPhoto(p.getId(),filename,url);
	        //gpout.inputStream=photoInt.getImageAsStream(p, Size.ORIGINAL);
	        InputStream is=photoInt.getImageAsStream(p, Size.ORIGINAL);
	        
	        //Log.Info(logger,mn,"Got input stream " + is.toString());
	        
	        byte[] imgArray = IoUtils.toByteArray(is);
	        
	        Log.Info(logger, mn,"Byte array: " + imgArray.length);
	        
	        Utils.QuietClose(is);
	        is = (InputStream)(new ByteArrayInputStream(imgArray));
	        gpout.inputStream=is;
	        
	        
	        
	        //
	        //
	        //
	        
	        
            Response res = ImageUtil.IsValidImage(gpout.inputStream);
            
            
            if(!res.success )
            {
            	Utils.QuietClose(gpout.inputStream);
            	gpout.inputStream=null;
            	Log.Info(logger,mn, "The flickr impage was not valid...Skipping - "  + res.message);
            	throw new Exception("The file was not a valid flickr image");
            	
            }
            
            //gpout.metadata=res.metadata;
            gpout.SetMetadata(res);
            
            gpout.Geocode();
            
			ImageUtil.WriteImageAndMetadataToDB(
					jobConfiguration.userId,
					jobConfiguration.uniqueId,
					filename,
					//currentItem,
					url, 
					res,
					logger);
            
			//the writer expects the stream to be reset
            gpout.inputStream.reset();
            
            super.GenerateDescription(gpout);
            
	      
	        
        }
        catch(Exception ex)
        {
        	Log.Error(logger, mn,ex.toString());
        	
        	if(gpout!=null)
        		gpout.CloseStream();
        	
        	throw new Exception(ex.toString());
        }
		
		
		return gpout;
	}



	
}
