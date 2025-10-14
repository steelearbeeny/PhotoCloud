package com.sga.common.writers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;


import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.Response;
import com.sga.common.google.GoogleAlbum;
import com.sga.common.google.GoogleAlbumItem;
import com.sga.common.google.GoogleAlbumItemList;
import com.sga.common.google.GoogleAlbumRequest;
import com.sga.common.google.GoogleMediaItemCreateRequest;
import com.sga.common.google.GoogleMediaItemCreateResponse;
import com.sga.common.http.HttpUtils;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;

public class GooglePhotoWriter extends WriterBase {

	
	
	public int currentImage=0;
	public ReturnValue<String> albumRv=null;
	OauthToken targetToken=null;
	
	//same url for create (post)
	public String listAlbumsURL = "https://photoslibrary.googleapis.com/v1/albums";
	public String uploadURL="https://photoslibrary.googleapis.com/v1/uploads";
	public String batchCreateURL = "https://photoslibrary.googleapis.com/v1/mediaItems:batchCreate";
	
	public GooglePhotoWriter(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration) throws Exception
	{

		super(jobContext,sessionData,jobConfiguration);
		String mn="GooglePhotoWriter::GooglePhotoWriter";
		
		
		Log.Info(logger, mn,"Method entered");
		
		targetToken=jobConfiguration.targetToken;
		
	
		
		albumRv=HttpUtils.GetRequest(
				targetToken, 
				listAlbumsURL, 
				"pageSize",50);
		
		
		Log.Info(mn, "Returned Albums " + albumRv.toString());
		
		if(!albumRv.data.equals("200"))
		{
			Log.Error(mn, "Could not read albums " + albumRv.data);
			String msg="The albums in your Google Photos account could not be read. The error message was: " + albumRv.message;
			throw new Exception(msg);
		}
	
		GoogleAlbumItemList als = Utils.GetGson().fromJson(albumRv.message, GoogleAlbumItemList.class);
		
		albumMap=new HashMap<String,String>();
		
		for(GoogleAlbumItem a : als.albums)
		{
			AddToAlbumMap(a.title,a.id);
		}
		
		
		PrintAlbumMap();
		
	}
	
	
	@Override
	public void write(GenericPhoto inPhoto) throws Exception {
		// TODO Auto-generated method stub
		
		String id="";
		String albumName;
		String albumId;
		int numTries=0;
		String mn="GooglePhotoWriter::write";
		boolean uploadSuccess=false;
		ReturnValue<String> rv=null;
		String uploadToken=null;
		GoogleAlbumRequest albumRequest=null;
		GoogleAlbumItem googleAlbumItem=null;
		GoogleMediaItemCreateResponse gmiResponse=null;
		
		
		Log.Info(logger, mn,"Method entered");
		
		Log.Info(logger, mn,"Writing " + inPhoto.toString());
		
		byte[] data=null;
		
		
		//
		//Check the album name. Dont bother uploading if its not there
		//
		
		albumName=GetAlbumName(inPhoto);
		
		if(albumName.length() < 1)
		{
			throw new Exception(
					Log.Error(logger, mn,"Could not get a valid album name. Cannot upload."));
		}
		
		
		
		
		uploadSuccess=false;
		for(numTries=0;numTries < MAX_RETRIES && uploadSuccess==false; numTries++)
		{
			Log.Info(logger, mn,"About to upload. Try Number: " + numTries);
			inPhoto.OpenStream();

			try
			{
				
				
				data=inPhoto.inputStream.readAllBytes();
				
				Log.Info(logger, mn,"Read bytes: " + Utils.ToString(data.length));
				
				
				rv=HttpUtils.PostRaw(
						jobConfiguration.targetToken,
						uploadURL,
						data,
						//"X-Goog-Upload-Content-Type:","",
						"X-Goog-Upload-Protocol","raw");
				
				Log.Info(logger, mn,"RV: " + rv.toString());
				
				if(!rv.data.equals("200"))
				{
					throw new Exception(
							Log.Error(logger, mn,"Could not post media file."));
					
				}
				
				uploadToken=rv.message;
				if(uploadToken.length() < 1)
				{
					throw new Exception(
							Log.Error(logger,mn,"Invalid upload token"));
					
				}
				
				uploadSuccess=true;
			}
			catch(Exception ex)
			{
				uploadSuccess=false;
				Log.Error(logger, mn,"Upload Exception. " + ex.toString());
					
				
				try {
					inPhoto.inputStream.reset();
				} catch (Exception e) {
					// TODO: handle exception
					Log.Error(logger,mn,"Couldnt reset stream. Closing/Opening Instead " + e.toString());
					
					inPhoto.CloseStream();
					inPhoto.inputStream=null;
					inPhoto.OpenStream();
					
					
				}
				
				
				
				Thread.sleep(1000);
			}
			
			
		} // end for retries
		
		if(uploadSuccess==false)
		{
			throw new Exception(
					Log.Error(logger, mn,"Could not upload after retries: " + MAX_RETRIES));
			
		}
			
			
			
			
		//
		// The file has been uploaded
		// Now add it to the album
		//
		uploadSuccess=false;
		if(albumMap.containsKey(albumName))
		{
			
			albumId=albumMap.get(albumName);
			Log.Info(logger, mn,"Album in Map " + albumName + " " + albumId);
		}
		else
		{
			//Create Album
			Log.Info(logger, mn,"Album NOT in Map " + albumName);
			
			albumRequest=new GoogleAlbumRequest();
			albumRequest.album.title=albumName;
			
			rv=HttpUtils.PostRequest(targetToken, listAlbumsURL, albumRequest);
			
			Log.Info(logger, mn,"Create complete " + rv.toString());
			
			if(Utils.ToInt(rv.data)==200)
			{
					googleAlbumItem=Utils.GetGson().fromJson(rv.message, GoogleAlbumItem.class);
					albumName=googleAlbumItem.title;
					albumId=googleAlbumItem.id;
					
					Log.Info(logger, mn,"Created " + albumName + " " + albumId);
				
					AddToAlbumMap(albumName,albumId);
			}
			else
			{
				throw new Exception("Could not create album ");
			}
			
		}
		
		

		rv=HttpUtils.PostRequest(
				targetToken, 
				//listAlbumsURL + "/" + albumId + ":batchAddMediaItems" , 
				batchCreateURL,
				new GoogleMediaItemCreateRequest(
						albumId, 
						inPhoto.description, 
						inPhoto.name, 
						uploadToken));
		
		
		
		if(Utils.ToInt(rv.data)==200)
		{
			gmiResponse=Utils.GetGson().fromJson(rv.message, GoogleMediaItemCreateResponse.class);
			
			Log.Info(logger, mn,"Media item create result: " + gmiResponse.toString());
			
			if(gmiResponse.AreAllSuccessful())
				uploadSuccess=true;
			else
				uploadSuccess=false;
			
		}
		else
			uploadSuccess=false;
		
		
		if(uploadSuccess==false)
		{
			throw new Exception(
					Log.Error(logger,mn,"Could not add to album " + rv.toString()));	
		}
		else
		{
			Log.Info(logger, mn,"Media item added to album successfully");
		}
		
	
		
	} //end method

} //end class
