package com.sga.common.readers;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.google.gson.Gson;
import com.sga.common.aws.S3Util;
import com.sga.common.generic.GenericAlbum;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.ImageUtil;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.MetadataItem;
import com.sga.common.generic.Response;
import com.sga.common.http.HttpUtils;
import com.sga.common.icloud.sharedphoto.Derivative;
import com.sga.common.icloud.sharedphoto.ICloudShare;
import com.sga.common.icloud.sharedphoto.Photo;
import com.sga.common.icloud.sharedphoto.SharedPhotoManager;
import com.sga.common.icloud.sharedphoto.WebAsset;
import com.sga.common.icloud.sharedphoto.WebAssetItem;
import com.sga.common.icloud.sharedphoto.WebAssetLocation;
import com.sga.common.icloud.sharedphoto.WebAssetRequest;
import com.sga.common.log.Log;
import com.sga.common.util.HttpStatus;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;




public class ICloudSharedAlbumReader extends ReaderBase {

	//HttpClient client=null;
	ICloudShare share=null;
	WebAsset webAsset=null;
	String serverName="p27-sharedstreams.icloud.com";
	String albumId;
	Gson gson = new Gson();
	GenericAlbum album=null;
	int numItems=0;
	int currentItem=0;
	Object[] photoArray=null;
	//JobDashboardLogger logger=null;
	
	SharedPhotoManager sharedPhotoManager=null;
	
	public ICloudSharedAlbumReader(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration) throws Exception
	{
		super(jobContext,sessionData,jobConfiguration);
		String mn="ICloudShareAlbumReader::ICloudSharedAlbumReader";
		
		//String albumId=null;
		String SQLStr;

		Log.Info(logger,mn, "Method entered");
		
		//Path currentRelativePath = Paths.get("");
		//String currentWorkingDir = currentRelativePath.toAbsolutePath().normalize().toString();
		//Log.Info(logger,mn,"Current working directory: " + currentWorkingDir);
	
		Log.Info(logger,mn,"Album URL " + jobConfiguration.albumUrl);
		
		
		String aid = SharedPhotoManager.IsValidURL(jobConfiguration.albumUrl);
		
		if(aid==null)
		{
			Log.Info(logger, mn,"Exception - The URL was invalid");
			throw new Exception(Log.Error(logger,mn,"The iCloud shared album URL was invalid."));
		}
		
		this.albumId=aid;
		
		sharedPhotoManager=new SharedPhotoManager(jobConfiguration.albumUrl);
		
			
		/*
		
		client = HttpClient.newBuilder()
			      .version(Version.HTTP_2)
			      .followRedirects(Redirect.NORMAL)
			      //.proxy(ProxySelector.of(new InetSocketAddress("www-proxy.com", 8080)))
			      //.authenticator(Authenticator.getDefault())
			      .build();
		
	*/	
		
		
		
		sharedPhotoManager.GetSharedPhotoStream();
		
		
		sharedPhotoManager.GetSharedPhotoAssets();
		
		
		ReturnValue<GenericAlbum> rv = sharedPhotoManager.GetAlbum();
		
		
		album = rv.data;
	
		
		numItems=album.images.size();
		currentItem=0;
		
		Log.Info(logger,mn,String.format("Read ICloudSharedAlbum total items %d",
				numItems));
		
		photoArray=album.images.values().toArray();
		
		
		Log.Info(logger, mn,"Constructor complete");
		
	}
	
	//
	//This is the same in both classes
	//
	
	/*
	private ReturnValue<ICloudShare> GetSharedPhotoStream()
	{
		
		String mn="ICloudSharedAlbumReader::GetSharedPhotoStream";

		//https://www.icloud.com/sharedalbum/#B2OGWZuqDGlTvJJ
		//String albumId="B2OGWZuqDGlTvJJ";
		
		//String streamUrl="https://" + serverName + "/" + albumId + "/sharedstreams/webstream";
		String streamUrl="https://%s/%s/sharedstreams/webstream";
		HttpRequest request=null;
		String url;
		ReturnValue<ICloudShare> rv = new ReturnValue<ICloudShare>();
		String message;
		
		
		
		Integer httpResponseCode=0;
				
		
		Log.Info(mn,"in method");
		try
		{
			
			url=String.format(streamUrl, serverName,albumId);
		
			request = HttpRequest.newBuilder()
			      .uri(URI.create(url))
			      .timeout(Duration.ofMinutes(1))
			      .header("Content-Type", "application/json")
			      .POST(BodyPublishers.ofString("{\"streamCtag\":null}"))
			      .build();
		
		
		
			HttpResponse<String> response =
			      client.send(request, BodyHandlers.ofString());
				
			httpResponseCode=response.statusCode();
			
				//Log.Info(mn,httpResponseCode.toString());
			
			
			//Log.Info(mn,response.headers());
			
			//Log.Info(mn, "=============");
			Log.Info(mn, "BODY");
			Log.Info(mn, response.body());
			
			
			if(httpResponseCode==330)
			{
				Map map = gson.fromJson(response.body(), Map.class);
				
				serverName=map.get("X-Apple-MMe-Host").toString();
				
				url=String.format(streamUrl, serverName,albumId);
				Log.Info(mn, "Redirecting to " + url);
				request = HttpRequest.newBuilder()
					      .uri(URI.create(url))
					      .timeout(Duration.ofMinutes(1))
					      .header("Content-Type", "application/json")
					      .POST(BodyPublishers.ofString("{\"streamCtag\":null}"))
					      .build();
				
				
				 response = client.send(request, BodyHandlers.ofString());
				 
				 
				 httpResponseCode=response.statusCode();
				 
				 
					
					//Log.Info(mn,httpResponseCode.toString());
				
				
				//Log.Info(mn,response.headers());
				
				//Log.Info(mn, "=============");
				Log.Info(mn, "BODY");
				Log.Info(mn, response.body());
	
			}
			
			if(httpResponseCode != 200)
			{
				message=String.format("The iCloud shared album could not be retrieved. %s (Code %d)", HttpStatus.GetDescription(httpResponseCode),httpResponseCode);
				Log.Info(mn, message);
				rv.message=message;
				return rv;
			}
			
			
			share = gson.fromJson(response.body(),ICloudShare.class);
			rv.data=share;
			return rv;
			
		}
		catch(Exception ex)
		{
			Log.Info(mn, "Exception: " + ex.toString());
			rv.message="An error occure: " + ex.toString();
			return rv;
		}
		
	} //end method
	
	
	//
	//Same in both classes
	//
	public void GetSharedPhotoAssets()
	{
		String mn="ICloudSharedPhotoReader::GetSharedPhotoAssets";
		String webStreamUrl = "https://%s/%s/sharedstreams/webasseturls";
		String url;
		Integer httpResponseCode=0;
		WebAssetRequest webAssetRequest = new WebAssetRequest();
		String jsonRequest;
		
		Log.Info(mn,"In Method");
		
		if(share==null)
		{
			Log.Info(mn, "Invalid shared album");
			return;
		}
		
		try
		{
		
			for(Photo p : share.photos)
			{
				webAssetRequest.photoGuids.add(p.photoGuid);
			}
			
			jsonRequest=gson.toJson(webAssetRequest);
			
			Log.Info(mn,jsonRequest);
			
			url=String.format(webStreamUrl, serverName,albumId);
			
			HttpRequest request = HttpRequest.newBuilder()
			      .uri(URI.create(url))
			      .timeout(Duration.ofMinutes(1))
			      .header("Content-Type", "application/json")
			      .POST(BodyPublishers.ofString(jsonRequest))
			      .build();
		
		
		
			HttpResponse<String> response =
			      client.send(request, BodyHandlers.ofString());
				
			httpResponseCode=response.statusCode();
			
				//Log.Info(mn,httpResponseCode.toString());
			
			
			//Log.Info(mn,response.headers());
			
			//Log.Info(mn, "=============");
			Log.Info(mn, "BODY");
			Log.Info(mn, response.body());
			
			webAsset = gson.fromJson(response.body(),WebAsset.class);
			
		}
		catch(Exception ex)
		{
			Log.Info(mn, "Exception" + ex.toString());
			
			
		}
		
		
	}
	
	
	public ReturnValue<GenericAlbum> GetAlbum() 
	{
		String mn="ICloudSharedAlbumReader::GetAlbum";
		ReturnValue<GenericAlbum> rv = new ReturnValue<GenericAlbum>();
		String message;
	
		
		if(share==null || webAsset==null)
		{
			message="Invalid state. Share and web assets have not been retrieved";
			rv.message=message;
			Log.Info(mn,message);
			return rv;
		}
		
		Log.Info(mn, "Method Entered");
		
		
		//StringBuilder sb = new StringBuilder();
		//int i=0;
		String url;
		WebAssetItem webAssetItem;
		GenericAlbum album= new GenericAlbum(share.streamName, share.userFirstName + " " + share.userLastName);
		album.itemCount=share.photos.size();
		String guid;
		GenericPhoto gPhoto;
		String scheme="https";
		String fileName;
		URL u;
		Response res=null;
		
		for(WebAssetLocation l : webAsset.locations.values())
		{
			scheme=l.scheme;
			break;
		}
		
		
		
		//sb.append(String.format("Album Name: %s User: %s %s\n",share.streamName,share.userFirstName,share.userLastName));
		//sb.append(String.format("Number of Photos: %d\n", share.photos.size()));
		
		for(Photo p : share.photos)
		{
			//sb.append(String.format("Photo: %d Date Created: %s Size(WxH): %dX%d\n",i++,p.dateCreated,p.width,p.height));
			//sb.append(String.format("Derivatives: %d\n",p.derivatives.size()));
			
			guid=UUID.randomUUID().toString();
			
			
			
			
			for(Derivative d : p.derivatives.values())
			{
				
				webAssetItem=null;
				webAssetItem=webAsset.items.get(d.checksum);
				
				if(webAssetItem==null)
				{
					Log.Info(mn,"Could not find asset");
					continue;
				}
				
				
				gPhoto=new GenericPhoto(guid);
				gPhoto.width=d.width;
				gPhoto.height=d.height;
				gPhoto.fileSize=d.fileSize;
				gPhoto.url=String.format("%s://%s%s", 
						scheme,
						webAssetItem.url_location,
						webAssetItem.url_path);
				
				gPhoto.expires=Utils.TryParse(webAssetItem.url_expiry);
				
				gPhoto.description=p.caption;
				gPhoto.dateCreated=Utils.TryParse(p.dateCreated)
						;
				try
				{
					fileName=Utils.ToString(webAssetItem.url_path);
					if(fileName.indexOf("?") >=0 )
						fileName = fileName.substring(0,fileName.indexOf("?") );
					
					
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
				
				}
				catch(Exception ex)
				{
					Log.Error(logger, mn,"Couldnt Parse Name " + ex.toString());
					fileName=webAssetItem.url_path;
				}
				
				gPhoto.name=fileName;
				
				try
				{
					gPhoto.OpenStream();
					res = ImageUtil.IsValidImage(gPhoto.inputStream);
				}
				catch(Exception ex)
				{
					Log.Error(logger,mn,"Could not open stream...Skipping");
					continue;
				}
				finally
				{
					gPhoto.CloseStream();
					gPhoto.inputStream=null;
				}
				
			   
	            
	            
	            if(!res.success )
	            {
	            	gPhoto.CloseStream();
	            	gPhoto.inputStream=null;
	            	Log.Info(logger,mn, "The ICloud image was not valid...Skipping - "  + res.message);
	            	//throw new Exception("The file was not a valid flickr image");
	            	continue;
	            }
				
				
				gPhoto.metadata=res.metadata;
				
				gPhoto.ownerName=(Utils.ToString(share.userFirstName) + " " + Utils.ToString(share.userLastName)).trim();
				gPhoto.albumName=Utils.ToString(share.streamName);
				
				if(p.width.equals(d.width) && p.height.equals(d.height))
				{
					
					album.images.put(guid, gPhoto);
					
				}
				else
				{
					album.thumbnails.put(guid, gPhoto);
				}
				
				
					
			}
			
		}
		
		//Log.Info(mn, Utils.ToJSON(album));
		
		rv.data=album;
		return rv;
		
	}
	*/
	
	@Override
	public boolean hasNext() throws Exception {
		// TODO Auto-generated method stub
		
		//if(photoArray!=null && currentItem+1 < numItems)
		//	return true;
		String mn="ICloudSharedPhotoReader::hasNext";
		
		Log.Info(logger,mn,"Checking next");
		
		if(photoArray!=null && currentItem < numItems)
			return true;
		
		
		return false;
	}

	@Override
	public GenericPhoto next() throws Exception {
		// TODO Auto-generated method stub
		GenericPhoto p;
		String mn="ICloudSharedPhotoReader::next";
		
		if(photoArray==null || numItems < 1)
			throw new NoSuchElementException("No elements in collection");
		
		Log.Info(logger,mn,"Getting next");
		
		
		
		p=(GenericPhoto)(photoArray[currentItem++]);
		/*
		p.OpenStream();
		
		byte[] img=p.inputStream.readAllBytes();
		ByteArrayInputStream bis = new ByteArrayInputStream(img);
		p.CloseStream();
		p.inputStream=bis;
		*/
		super.GenerateDescription(p);
		
		Log.Info(logger,mn,p.toString());
		
		return p;
		
	}

}
