package com.sga.common.icloud.sharedphoto;

import java.io.File;
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
import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;
import org.postgresql.util.PSQLException;

//import com.azure.core.http.rest.Response;
import com.google.gson.Gson;
import com.sga.common.aws.S3Util;
import com.sga.common.dataaccess.DataUtil;
import com.sga.common.generic.GenericAlbum;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.ImageUtil;
import com.sga.common.generic.MetadataItem;
import com.sga.common.generic.Response;
import com.sga.common.http.HttpUtils;
import com.sga.common.log.Log;
import com.sga.common.util.HttpStatus;
import com.sga.common.util.Regex;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.SHA512;
import com.sga.common.util.Utils;

public class SharedPhotoManager {
	
	
	HttpClient client=null;
	ICloudShare share=null;
	WebAsset webAsset=null;
	String serverName="p27-sharedstreams.icloud.com";
	String albumId;
	Gson gson = new Gson();
	
	
	public SharedPhotoManager()
	{
		
	}
	
	public SharedPhotoManager(String albumId) throws Exception
	{
		
		String aid = IsValidURL(albumId);
		String mn="SharedPhotoManager::SharedPhotoManager";
		
		Log.Info(mn, "In constructor " + aid);
		
		if(aid==null)
		{
			
			throw new Exception(Log.Error(mn, "The iCloud shared album URL was invalid"));
			
		}
			
		this.albumId=aid;
		
		client = HttpClient.newBuilder()
			      .version(Version.HTTP_2)
			      .followRedirects(Redirect.NORMAL)
			      //.proxy(ProxySelector.of(new InetSocketAddress("www-proxy.com", 8080)))
			      //.authenticator(Authenticator.getDefault())
			      .build();
		
	}
	
	public static String IsValidURL(String inUrl)
	{
		Pattern p=null;
		Matcher matcher=null;
		boolean matchFound=false;
		String albumToken=null;
		
		
		if(inUrl.startsWith("https://www.icloud.com/sharedalbum/#"))
		{
			matchFound=true;
			albumToken=inUrl.replace("https://www.icloud.com/sharedalbum/#", "");
			return albumToken;
		}
		
		
		p=Pattern.compile("^[A-Za-z0-9]+$");
		matcher= p.matcher(inUrl);
		if(matcher.matches()==true)
		{
			return inUrl;
		}
		
		
		p = Pattern.compile("https://(p\\d+-sharedstreams.icloud.com)/([A-Za-z0-9]+)/sharedstreams.*");
		matcher = p.matcher(inUrl);
		
		
		if(matcher.find())
			albumToken = matcher.group(2);
		else
			return null;
	    
	    
	    return albumToken;
	
	}
	
	public void ListPhotos()
	{
		String mn="SharedPhotoManager::ListPhotos";
		
		if(share==null || webAsset==null)
		{
			Log.Info(mn,"Share and web assets have not been retrieved");
			return;
		}
		
		
		StringBuilder sb = new StringBuilder();
		int i=0;
		String url;
		WebAssetItem webAssetItem;
		
		
		sb.append(String.format("Album Name: %s User: %s %s\n",share.streamName,share.userFirstName,share.userLastName));
		sb.append(String.format("Number of Photos: %d\n", share.photos.size()));
		
		for(Photo p : share.photos)
		{
			sb.append(String.format("Photo: %d Date Created: %s Size(WxH): %dX%d\n",i++,p.dateCreated,p.width,p.height));
			sb.append(String.format("Derivatives: %d\n",p.derivatives.size()));
			
			for(Derivative d : p.derivatives.values())
			{
				
				webAssetItem=null;
				webAssetItem=webAsset.items.get(d.checksum);
				
				if(webAssetItem==null)
				{
					Log.Info(mn,"Could not find asset");
					continue;
				}
				
				if(p.width.equals(d.width) && p.height.equals(d.height))
					sb.append(String.format("PHOTO - Checksum: %s WxH: %dX%d Size: %d Expires: %s %s %s\n", 
							d.checksum,
							d.width,
							d.height,
							d.fileSize,
							webAssetItem.url_expiry,
							webAssetItem.url_location,
							webAssetItem.url_path));
				else
					sb.append(String.format("THUMB - Checksum: %s WxH: %dX%d Size: %d Expires: %s %s %s\n", 
							d.checksum,
							d.width,
							d.height,
							d.fileSize,
							webAssetItem.url_expiry,
							webAssetItem.url_location,
							webAssetItem.url_path));
				
				
				
					
			}
			
		}
		
		Log.Info(mn, sb.toString());
		
	}
	
	
	public ReturnValue<GenericAlbum> GetAlbum()
	{
		String mn="SharedPhotoManager::GetAlbum";
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
				
				//Merged Code
				try
				{
					fileName=Utils.ToString(webAssetItem.url_path);
					if(fileName.indexOf("?") >=0 )
						fileName = fileName.substring(0,fileName.indexOf("?") );
					
					
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
				
				}
				catch(Exception ex)
				{
					Log.Error(mn,"Couldnt Parse Name " + ex.toString());
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
					Log.Error(mn,"Could not open stream...Skipping");
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
	            	Log.Info(mn, "The ICloud image was not valid...Skipping - "  + res.message);
	            	//throw new Exception("The file was not a valid flickr image");
	            	continue;
	            }
				
				
	            //gpout.metadata=res.metadata;
	            gPhoto.SetMetadata(res);

				
				gPhoto.Geocode();
				
				gPhoto.ownerName=(Utils.ToString(share.userFirstName) + " " + Utils.ToString(share.userLastName)).trim();
				gPhoto.albumName=Utils.ToString(share.streamName);
				
				//End Merged Code
				
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
	
	
	
	public void GetSharedPhotoAssets()
	{
		String mn="GetSharedPhotoAssets";
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
			
				Log.Info(mn,httpResponseCode.toString());
			
			
			Log.Info(mn,response.headers());
			
			Log.Info(mn, "=============");
			Log.Info(mn, "BODY");
			Log.Info(mn, response.body());
			
			webAsset = gson.fromJson(response.body(),WebAsset.class);
			
		}
		catch(Exception ex)
		{
			Log.Info(mn, "Exception" + ex.toString());
			
			
		}
		
		
	}
	
	
	public ReturnValue<ICloudShare> GetSharedPhotoStream()
	{
		
		String mn="SharedPhotoManager::GetSharedPhotoStream";

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
			
				Log.Info(mn,httpResponseCode.toString());
			
			
			Log.Info(mn,response.headers());
			
			Log.Info(mn, "=============");
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
				 
				 
					
					Log.Info(mn,httpResponseCode.toString());
				
				
				Log.Info(mn,response.headers());
				
				Log.Info(mn, "=============");
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
	
	
	//Icloud to s3
	//This should not be run as-is anymore
	//should be broken up into reader and writer
	//
	/*
	public void RunAsJob(JobContext jobContext, Map<String,Object> sessionData, Map<String,String> params) throws Exception
	{
		String mn="SharedPhotoManager::RunAsJob";
		JobDashboardLogger logger=null;
		String albumId=null;
		String SQLStr;
		
		if(jobContext!=null)
			logger=jobContext.logger();
		
		Log.Info(logger,mn, "Method entered");
		
		Path currentRelativePath = Paths.get("");
		String currentWorkingDir = currentRelativePath.toAbsolutePath().normalize().toString();
		Log.Info(logger,mn,"Current working directory: " + currentWorkingDir);
		
		
		albumId=Utils.GetMapValue(params, "albumUrl");
		
		String aid = IsValidURL(albumId);
		
		if(aid==null)
		{
			throw new Exception(Log.Error(logger,mn,"The iCloud shared album URL was invalid. " + albumId));
		}
		
		this.albumId=aid;
		
		for(String k : sessionData.keySet())
		{
			Log.Info(logger, mn,"Session Map: " + k + " - " + Utils.ToString(params.get(k)));
		}
		
		for(String k : params.keySet())
		{
			Log.Info(logger, mn,"Parameter Map: " + k + " - " + Utils.ToString(params.get(k)));
		}
		
		
		
		client = HttpClient.newBuilder()
			      .version(Version.HTTP_2)
			      .followRedirects(Redirect.NORMAL)
			      //.proxy(ProxySelector.of(new InetSocketAddress("www-proxy.com", 8080)))
			      //.authenticator(Authenticator.getDefault())
			      .build();
		
		
		
		
		
		GetSharedPhotoStream();
		
		
		GetSharedPhotoAssets();
		
		
		ReturnValue<GenericAlbum> rv = GetAlbum();
		
		
		GenericAlbum album = rv.data;
		//Path path=null;
		//String localHash="";
		S3Util s3 = new S3Util();
		String bucket="testphotobucket";
		String baseFileName;
		String fileName;
		//String remoteHash;
		String jobId;
		Response rc;
		Response downloadResponse;
		List<MetadataItem> metadata = new ArrayList<MetadataItem>();
		int numItems=0;
		int currentItem=0;
		int userId=0;
		String url;
		//String userName=s3.GetUserName();
		
		String userName="steelearbeeny@msn.com";
		
		Log.Info(logger, mn,"AWS User Name: " + userName);
		
		if(jobContext!=null)
			jobId=jobContext.getJobId().toString();
		else
			jobId=UUID.randomUUID().toString();
		
		if(sessionData.containsKey("USER"))
		{
			baseFileName=(String)sessionData.get("USER");
			baseFileName+="/" + jobId + "/";
		}
		else
		{
			throw new Exception(Log.Error(logger,mn, "Invalid session data"));
		}
		
		if(sessionData.containsKey("USERID"))
		{
			userId=(int)sessionData.get("USERID");
			
		}
		else
		{
			throw new Exception(Log.Error(logger,mn, "Invalid user session data"));
		}
		
		numItems=album.images.size();
		currentItem=0;
		
		Log.Info(logger,mn,String.format("Putting images to s3: Bucket %s baseName: %s total items %d",
				bucket,
				baseFileName,
				numItems));
		
		for(GenericPhoto photo : album.images.values())
		{
			currentItem++;
			Log.Info(logger, mn,"Getting image " + currentItem + " of " + numItems);
			
			downloadResponse=HttpUtils.GetImage(photo.url);
			
			Log.Info(logger, mn, "Downloaded image: " + downloadResponse.message);
			
			
			fileName=Regex.ExtractFilenameFromURL(photo.url);
			
			Log.Info(logger, mn, "Uploading to S3: " + fileName);
			
			rc=s3.PutFile(downloadResponse.path,bucket,baseFileName + fileName);
			
			if(!rc.success)
			{
				throw new Exception(Log.Error(logger,mn, "Could not save image to S3. " + rc.returnCode + " " + rc.message));
				
			}
			
			if(!downloadResponse.hash.equals(rc.hash))
			{
				throw new Exception(Log.Error(logger, mn, "Hashes dont match " + downloadResponse.hash + " " + rc.hash + " " + fileName));
			}
			
			Log.Info(logger, mn, "Hashes match. Deleting local image");
			
			
			
			url=String.format("s3://%s/%s/%s",bucket,userName,baseFileName+fileName);
			Log.Info(logger, "mn","URL: " + url);
			
			
			//
			//Write the metadata to the db
			//
	
			
			ImageUtil.WriteImageAndMetadataToDB(
					userId, 
					jobId, 
					//currentItem,
					fileName,
					url, 
					downloadResponse,
					logger);
			
			Files.delete(downloadResponse.path);
			
		}
		
		s3.Close();
		
		Log.Info(logger,mn, "Method compelete...Exiting");
		
	}
	
	*/
	

}
