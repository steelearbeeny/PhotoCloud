package com.sga.common.writers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.collections.Collection;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.sga.common.flickr.Albums;
import com.sga.common.flickr.FlickrConnection;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.MetadataItem;
import com.sga.common.generic.Service;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.util.Utils;

public class FlickrWriter extends WriterBase {

	
	public FlickrConnection flickrConnection=null;
	//public Map<String,String> albumMap=null;
	//public String defaultImageName="";
	//public String defaultImageDescription="";
	public int currentImage=0;
	
	
	public FlickrWriter(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration) throws Exception
	{
		super(jobContext,sessionData,jobConfiguration);
		String mn="FlickrWriter::FlickrWriter";
		//ZonedDateTime startTime=ZonedDateTime.now();
		
		OauthToken targetToken=null;
		
		Log.Info(logger, mn,"Method entered");
		
		targetToken=jobConfiguration.targetToken;
		
		
		flickrConnection = new FlickrConnection(targetToken);
		//Log.Info(logger,mn,"Returned Token: " + flickrConnection.DumpToken());
		albumMap=flickrConnection.GetAlbumMap();
		
		for(String key : albumMap.keySet())
		{
		
			Log.Info(logger, mn,"Album " + key + " " + albumMap.get(key));
		
		}	
		
		//Log.Info(logger,mn,"Returned Token: " + flickrConnection.DumpToken());
			
		
		/*
		defaultImageDescription="File %d Uploaded on " + formatter.format(startTime);
		
		String serviceDesc = Service.GetServiceDescription(jobConfiguration.sourceProvider);
		
		if(serviceDesc.length() > 1)
			defaultImageDescription+=" from " + serviceDesc;
		*/
		//flickrConnection=null;
	}
	
	
	
	
	@Override
	public void write(GenericPhoto inPhoto) throws Exception {
		// TODO Auto-generated method stub
		String mn="FlickrWriter::write";
		String id="";
		String albumName;
		String albumId;
		Photoset pset;
		int numTries=0;
		boolean uploadSuccess=false;
		
		Log.Info(logger, mn,"Method entered");
		
		Log.Info(logger, mn,"Writing " + inPhoto.toString());
		Log.Info(logger,mn,"Returned Token: " + flickrConnection.DumpToken());
		
		Uploader u;
		
		UploadMetaData metadata = new UploadMetaData();
		/*
		String photoDesc = String.format(defaultImageDescription, ++currentImage);
		
		if(inPhoto.ownerName!=null)
			photoDesc+=" From: " + inPhoto.ownerName;
		
		if(inPhoto.albumName!=null)
			photoDesc+=" Album: " + inPhoto.albumName;
		*/
		
		metadata.setDescription(Utils.ToString(inPhoto.description));
		metadata.setFamilyFlag(false);
		metadata.setFilename(inPhoto.name); //this cant be null. Will throw FlickrException 105/401 Unauthorized
		metadata.setFriendFlag(false);
		metadata.setPublicFlag(false);
		metadata.setTitle(inPhoto.name);
		
		List<String> tags=inPhoto.GetTags();
		if(tags!=null && tags.size() > 0)
			metadata.setTags(tags);
				
		/*
		byte[] photoBytes=null;
		
		try
		{
			Log.Info(logger, mn,"About to read stream to array");
			inPhoto.OpenStream();
			photoBytes = inPhoto.inputStream.readAllBytes();
			Log.Info(logger, mn,"Read complete " + photoBytes.length);
		}
		catch(Exception ex)
		{
			Log.Info(logger,mn,"Exception: reading stream " + ex.toString());
		}
		
		
		if(flickrConnection==null)
		{
			Log.Info(mn, "Authenticating");
			flickrConnection = new FlickrConnection(jobConfiguration.targetToken);
			albumMap=flickrConnection.GetAlbumMap();
			Log.Info(mn, "Authentication Done");
			
		}
		*/
		
		PhotosetsInterface psi = flickrConnection.flickr.getPhotosetsInterface();
		
		
	

		try
		{
			
			//inPhoto.OpenStream();
			inPhoto.LoadByteArray();
		
			u=flickrConnection.flickr.getUploader();
			
			uploadSuccess=false;
			for(numTries=0;numTries < MAX_RETRIES && uploadSuccess==false; numTries++)
			{
				Log.Info(logger, mn,"About to upload. Try Number: " + numTries);
				
				try
				{
					uploadSuccess=false;
					id=u.upload(inPhoto.inputStream, metadata);
					uploadSuccess=true;
				}
				catch(FlickrException fex)
				{
					uploadSuccess=false;
					Log.Error(logger, mn,"Flickr Exception. " + fex.toString());
					Log.Error(logger, mn,"Flickr Code. " + fex.getErrorCode());
					Log.Error(logger, mn,"Flickr Error Message. " + fex.getErrorMessage());
					Log.Error(logger, mn,"API Key " + flickrConnection.flickr.getApiKey());
					Log.Error(logger, mn,"UserId " + flickrConnection.flickr.getAuth().getUser().getId());
					Log.Error(logger, mn,"Permission " + flickrConnection.flickr.getAuth().getPermission().getType());
					Log.Info(logger,mn,"Returned Token: " + flickrConnection.DumpToken());
					
					flickrConnection.Reauthorize();
					
					//fex.printStackTrace();
					// Error code 105 flickr not available
					
					try {
						inPhoto.inputStream.reset();
					} catch (Exception e) {
						// TODO: handle exception
						Log.Error(logger,mn,"Couldnt reset stream. " + e.toString());
						
						//inPhoto.CloseStream();
						//inPhoto.inputStream=null;
						//inPhoto.OpenStream();
						
						
					}
					
					
					
					Thread.sleep(1000);
				}
				catch(Exception ex)
				{
					uploadSuccess=false;
					Log.Error(logger, mn,"Upload Exception. " + ex.toString());
					
					try {
						inPhoto.inputStream.reset();
					} catch (Exception e) {
						// TODO: handle exception
						Log.Error(logger,mn,"Couldnt reset stream. " + e.toString());
						
						//inPhoto.CloseStream();
						//inPhoto.inputStream=null;
						//inPhoto.OpenStream();
					}
					
					
					
					Thread.sleep(1000);
				}
			} //end for
			
			if(uploadSuccess==false)
			{
				//inPhoto.CloseStream();
				Log.Error(logger, mn,"Could not upload after retries: " + MAX_RETRIES);
				throw new Exception("Could not upload");
			}
			
			//
			// Upload successful
			// Now add it to the album
			//			
			
			Log.Info(mn, "Uploaded " + id);
	
			albumName=GetAlbumName(inPhoto);
			
			if(albumName.length() < 1)
				return;
			
			
			if(albumMap.containsKey(albumName))
			{
				albumId=albumMap.get(albumName);
				psi.addPhoto(albumId, id);
				Log.Info(logger,mn, "Added to existing album "  + albumId + "  " + albumName);
			}
			else
			{
				pset=psi.create(albumName,"Uploaded Album", id);
				albumId=pset.getId();
				albumMap.put(albumName, albumId);
				Log.Info(logger,mn, "Added to New album "  + albumId + "  " + albumName);
				
			}
			
			
			
			
			
		}
		catch(Exception ex)
		{
			Log.Error(logger,mn, "Exception: " + ex.toString());
		}
		finally
		{
			inPhoto.CloseStream();
		}
		
		
		//return false;
	}

}
