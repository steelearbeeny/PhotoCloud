package com.sga.common.readers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jobrunr.jobs.context.JobContext;

import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.Size;
import com.sga.common.flickr.FlickrConnection;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.ImageUtil;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.Response;
import com.sga.common.google.GoogleMediaItemList;
import com.sga.common.google.GoogleMediaItemList.GoogleMediaFile;
import com.sga.common.google.GoogleMediaItemList.GoogleMediaFileMetadata;
import com.sga.common.google.GoogleMediaItemList.GoogleMediaItem;
import com.sga.common.google.GoogleMediaItemList.GooglePhotoMetadata;
import com.sga.common.http.HttpUtils;
import com.sga.common.icloud.sharedphoto.WebAsset;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Utils;

import software.amazon.awssdk.utils.IoUtils;

public class GooglePhotoReader extends ReaderBase {
	
	String pickerId;
	int currentAlbum=0;
	HttpClient client=null;
	HttpRequest albumRequest=null;
	String baseUrl="https://photospicker.googleapis.com/v1/mediaItems";
	String deleteUrl = "https://photospicker.googleapis.com/v1";
	int httpResponseCode=0;
	HttpResponse<String> response=null;
	GoogleMediaItemList mediaItems=null;
	public Iterator<GoogleMediaItem> currentIterator=null;

	public GooglePhotoReader(JobContext jobContext, Map<String, Object> sessionData,
			JobConfiguration jobConfiguration) throws Exception {

		super(jobContext,sessionData,jobConfiguration);
		
		String mn="GooglePhotoReader::GooglePhotoReader";
		OauthToken sourceToken=null;
		URI uri=null;
		String json;
		
		Log.Info(logger, mn,"Method entered. JobConfiguration: " + jobConfiguration.toString());
		
		sourceToken=jobConfiguration.sourceToken;
	
	        
		
		 if(jobConfiguration.sourceAlbums.size() < 1)
		 {
			 Log.Info(logger,mn,"No source albums were specified");
		 }
		 else
		 {
			 pickerId=jobConfiguration.sourceAlbums.get(currentAlbum);
			
		 }
		 
		 try
		 {
			 client = HttpClient.newBuilder()
				      .version(Version.HTTP_2)
				      .followRedirects(Redirect.NORMAL)
				      //.proxy(ProxySelector.of(new InetSocketAddress("www-proxy.com", 8080)))
				      //.authenticator(Authenticator.getDefault())
				      .build();
			
			 // get the list of mediaItems
			 
			 //Log.Info(mn, "Http Client created");
			 
			 
				uri = URI.create(baseUrl + "?sessionId="+ pickerId);
				
				Log.Info(mn, "URI " + uri.toString());
				
				albumRequest = HttpRequest.newBuilder()
				      .uri(uri)
				      .timeout(Duration.ofMinutes(1))
				      .header("Content-Type", "application/json")
				      .header("Authorization", "Bearer " + jobConfiguration.sourceToken.tokenResponse)
				      .GET()
				      .build();
				
				
				//Log.Info(mn, "Request created");
				
			
				
				
				response =
				      client.send(albumRequest, BodyHandlers.ofString());
					
				httpResponseCode=response.statusCode();
				
				Log.Info(mn,"Http Response: " + Utils.ToString(httpResponseCode));
				
				
				//Log.Info(mn,response.headers());
				
				//Log.Info(mn, "======  BODY  =======");
				json= response.body();
				
				Log.Info(mn, json);
				
				mediaItems = Utils.GetGson().fromJson(json, GoogleMediaItemList.class);
				
				if(mediaItems==null || mediaItems.mediaItems.size() < 1)
				{
					Log.Info(mn, "The media item list could not be parsed");
				}
				else
				{
					Log.Info(mn, "MediaItem count: " + mediaItems.mediaItems.size());
					currentIterator=mediaItems.mediaItems.iterator();
				}
				
				
				
				//webAsset = gson.fromJson(response.body(),WebAsset.class);
			}
			catch(Exception ex)
			{
				Log.Error(mn, "Exception: " + ex.toString());
				throw ex;
			}
		
		
	}

	@Override
	public boolean hasNext() throws Exception {
	String mn="GooglePhotoReader::hasNext";

		if(currentIterator==null) return false;
		
		return currentIterator.hasNext();
		
	}

	@Override
	public GenericPhoto next() throws Exception {
		
		GenericPhoto gPhoto=null;
		String mn="GooglePhotoReader::next";
		String url=null;
		Response res=null;
        
        String filename=null;
			
		if(currentIterator == null || currentIterator.hasNext()==false)
		{
			throw new NoSuchElementException("No elements in collection");
		}
		
	
		GoogleMediaItem p =  currentIterator.next();
		GoogleMediaFile f = p.mediaFile;
		GoogleMediaFileMetadata md = f.mediaFileMetadata;
		GooglePhotoMetadata pm = md.photoMetadata;
		
		
		if(f!=null)
		{
			url=f.baseUrl;
			filename=f.filename;
		}
		
		if(url==null || url.length() < 1 || filename==null || filename.length() < 1)
		{
			Log.Error(mn, "Invalid file from iterator");
			return null;
		}
       
        
        try
        {
        	
        	
        	
        
        	Log.Info(logger, mn,"FileName: " + filename + " URL:" + url);
  		
	        gPhoto=new GenericPhoto(p.id);
	        
	    	
			gPhoto.width=md.width;
			gPhoto.height=md.height;
			gPhoto.name=filename;
			gPhoto.url=url;
			gPhoto.dateCreated=Utils.TryParse(p.createTime);
			gPhoto.token=jobConfiguration.sourceToken;	        
	        gPhoto.description="Picked using Google Photo Picker";
	        			
			try
			{
				//gPhoto.OpenStream();
				gPhoto.LoadByteArray();
				res = ImageUtil.IsValidImage(gPhoto.inputStream);
			}
			catch(Exception ex)
			{
				Log.Error(mn,"Could not open stream...Skipping");
				gPhoto.CloseStream();
				return null;
			}
			finally
			{
				
				
			}
			
            
            if(!res.success )
            {
            	gPhoto.CloseStream();
            	
            	Log.Info(logger,mn, "The Google photo was not valid...Skipping - "  + res.message);
            	throw new Exception("The file was not a valid Google photo image");
            	
            }
            
	        /*
				Directory
				Exif IFD0
				id 271 = camera maker name
				272 - camera model
				
				Exif SubIFD
				33434 exposure time
				33437 F number
				34855 ISO
				37386 Focal Length
	         */
            
            res.AddToMetadata("Exif IFD0",271,"Make",md.cameraMake);
            res.AddToMetadata("Exif IFD0",272,"Model",md.cameraModel);
            
            if(pm!=null)
            {
	            res.AddToMetadata("Exif SubIFD",33434,"Exposure Time",pm.exposureTime);
	            res.AddToMetadata("Exif SubIFD",33437,"F-Number",Utils.ToString(pm.apertureFNumber));
	            res.AddToMetadata("Exif SubIFD",34855,"ISO Speed Ratings",Utils.ToString(pm.isoEquivalent));
	            res.AddToMetadata("Exif SubIFD",37386,"Focal Length",Utils.ToString(pm.focalLength));
            }
            
            //gpout.metadata=res.metadata;
            gPhoto.SetMetadata(res);

            
            gPhoto.Geocode();
            
			ImageUtil.WriteImageAndMetadataToDB(
					jobConfiguration.userId,
					jobConfiguration.uniqueId,
					filename,
					//currentItem,
					url, 
					res,
					logger);
            
			//the writer expects the stream to be reset
            //gPhoto.inputStream.reset();
            
            super.GenerateDescription(gPhoto);
            
	      
	        
        }
        catch(Exception ex)
        {
        	Log.Error(logger, mn,ex.toString());
        	
        	if(gPhoto!=null)
        		gPhoto.CloseStream();
        	
        	throw new Exception(ex.toString());
        }
		
		
		return gPhoto;
		
		
	}
	
	@Override
	public void Close()
	{
		super.Close();
		
		String url =deleteUrl + "/sessions/"+ pickerId;
		
		HttpUtils.DeleteRequest(jobConfiguration.sourceToken,url);

	}
	

}
