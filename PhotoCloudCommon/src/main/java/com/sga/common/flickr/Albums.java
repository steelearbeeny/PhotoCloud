package com.sga.common.flickr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.flickr4java.flickr.*;

import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.Size;
import com.flickr4java.flickr.photosets.Photoset;
import com.flickr4java.flickr.photosets.Photosets;
import com.flickr4java.flickr.photosets.PhotosetsInterface;
import com.google.gson.Gson;
import com.sga.common.log.Log;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.Utils;
//import com.sga.photocloud.flickr.FlickrInterfaceBase;


public class Albums extends FlickrInterfaceBase {
	
	

	
	public static ReturnValue<Photosets> ListAlbums(String userId, int perPage, int pageNumber) throws Exception
	{
		
		
		String mn="Album::ListAlbums";
		Photosets ps;
		Gson gson = new Gson();
		//int numAlbums=0;
		//String userid="99631001@N08";
		Iterator<Photoset> sets=null;

		
		
		
		IsInitialized();
		
		
		
		if(flickrConnection.flickr.getAuth()==null)
			Log.Info(mn, "Flickr not authorized. Using public");
		else
		{
			
			
			RequestContext.getRequestContext().setAuth(flickrConnection.flickr.getAuth());
			Log.Info(mn,"Flickr authorized. " + flickrConnection.flickr.getAuth().getUser().getId() );
		}
		
		 PhotosetsInterface pi = flickrConnection.flickr.getPhotosetsInterface();
	        
		
		 
		 
		 /*
		 PhotosInterface photoInt = f.getPhotosInterface();
	        Map<String, Collection> allPhotos = new HashMap<String, Collection>();
*/
		 
		 //numAlbums=pi.getPhotosetCount(userId);
		 
		 
		 
		 try
		 {
		 
			 ps=pi.getList(userId,perPage,pageNumber,null);
			 
			 
			 
			 
			 Log.Info(mn, gson.toJson(ps));
			 
	        //Iterator<Photoset> sets = pi.getList(userId,20,0,null).getPhotosets().iterator();
	        sets = ps.getPhotosets().iterator();

	        
	        while (sets.hasNext()) {
	            Photoset set = (Photoset) sets.next();
	            
	            Log.Info(mn,String.format("Title: %s "
	            		+ "Desc: %s "
	            		+ "Views: %d "
	            		+ "Photos: %d "
	            		+ "Videos: %d  "
	            		+ "Created: %s "
	            		+ "Updated: %s "
	            		+ "ID: %s "
	            		+ "Owner: %s %s %s "
	            		+ "Secret: %s "
	            		+ "URL: %s "
	            		+ "Server: %s",
	            		set.getTitle(),
	            		set.getDescription(),
	            		set.getViewCount(),
	            		set.getPhotoCount(),
	            		set.getVideoCount(),
	            		Utils.ToLocalDateTime(set.getDateCreate()),
	            		Utils.ToLocalDateTime(set.getDateUpdate()),
	            		set.getId(),
	            		set.getOwner().getId(),
	            		set.getOwner().getUsername(),
	            		set.getOwner().getRealName(),
	            		set.getSecret(),
	            		set.getUrl(),
	            		set.getServer()
	            		
	            		));
	            
	            
	            //Log.Info(mn,gson.toJson(set));
	            
	            //PhotoList photos = pi.getPhotos(set.getId(), 500, 1);
	            //allPhotos.put(set.getTitle(), photos);
	            
	            //Not needed at this point
	            //ListPhotos(set);
	            
	            
	        }
	
	        
	        return new ReturnValue<Photosets>(ps);
		 }
		 catch(Exception ex)
		 {
			 Log.Info(mn, "Exception caught: " + ex.toString());
			 return new ReturnValue<Photosets>(null,"The albums could not be retrieved. " + ex.toString());
			
		 }
	        
	        
		
	} //end method
	
	
	public static int GetAlbumCount(String userId) throws Exception
	{
		
		
		String mn="Album::GetAlbumCount";
		Photosets ps;
		
		int numAlbums=0;
		//String userid="99631001@N08";
		
		
		IsInitialized();
		
		
		
		if(flickrConnection.flickr.getAuth()==null)
			Log.Info(mn, "Flickr not authorized. Using public");
		else
		{
			
			
			RequestContext.getRequestContext().setAuth(flickrConnection.flickr.getAuth());
			Log.Info(mn,"Flickr authorized. " + flickrConnection.flickr.getAuth().getUser().getId() );
			userId=flickrConnection.flickr.getAuth().getUser().getId();
		}
		
		
		
		 PhotosetsInterface pi = flickrConnection.flickr.getPhotosetsInterface();
	        
		 /*
		 PhotosInterface photoInt = f.getPhotosInterface();
	        Map<String, Collection> allPhotos = new HashMap<String, Collection>();
		 */
		
		
		 try
		 {
			 numAlbums=pi.getPhotosetCount(userId);
			 return numAlbums;
		 }
		 catch(Exception ex)
		 {
			 Log.Info(mn, "Exception caught: " + ex.toString());
			 return -1;
			
		 }
	        
	        
		
	} //end method
	
	
	public static void ListPhotos(Photoset set) throws Exception
	{
		String mn = "Albums::ListPhotos";
		IsInitialized();
		java.util.Collection<Size> sizes;
		
		PhotosetsInterface pi = flickrConnection.flickr.getPhotosetsInterface();
		
		Log.Info(mn,"Getting photos for set: %s Count %s",set.getId(),set.getPhotoCount());
		
		PhotoList<Photo> photoList = pi.getPhotos(set.getId(), set.getPhotoCount(),1);
		
		Iterator<Photo> photo = photoList.iterator();
		String photoUrl;
		int width, height;
		
		//flickrConnection.flickr.getPhotosInterface().getExif(photoUrl, photoUrl);
		
		
		while(photo.hasNext())
		{
			
			Photo p=photo.next();
			
			/*
			Photo nfo = flickrConnection.flickr.getPhotosInterface().getInfo(p.getId(), null);
			
			Log.Info(mn, "Info %s %s %s %s",
					p.getId(),
					p.getOriginalSecret(),
					nfo.getId(),
					nfo.getOriginalSecret());
			
            if (nfo.getOriginalSecret().isEmpty()) {
            	photoUrl=p.getLargeUrl();
               Log.Info(mn, "%s %s",p.getTitle(),p.getLargeUrl());
            } else {
                p.setOriginalSecret(nfo.getOriginalSecret());
                photoUrl=p.getOriginalUrl();
                Log.Info(mn, "%s %s",p.getTitle(),p.getOriginalUrl());

            }
			*/
			
			if(p.getOriginalSecret().length() < 1)
			{
				photoUrl=p.getLargeUrl();
				
				if(p.getLargeSize()!=null)
				{
					width=p.getLargeSize().getWidth();
					height=p.getLargeSize().getHeight();
				}
				else
					width=height=0;
			}
			else
			{
				p.setOriginalSecret(p.getOriginalSecret());
				photoUrl=p.getOriginalUrl();
				if(p.getOriginalSize()!=null)
				{
					width=p.getOriginalSize().getWidth();
					height=p.getOriginalSize().getHeight();
				}
				else
					width=height=0;
			}
            
            
			Log.Info(mn,"Photo: %s %s Added %s Posted %s Taken %s Desc %s Media %s Secret %s URL %s %dx%d %s",
					p.getId(),
					p.getTitle(),
					Utils.ToLocalDateTime(p.getDateAdded()),
					Utils.ToLocalDateTime(p.getDatePosted()),
					Utils.ToLocalDateTime(p.getDateTaken()),
					p.getDescription(), 
					p.getMedia(),
					p.getOriginalSecret(),
				
					photoUrl,
					width,
					height,
					p.getOriginalFormat()
					
					);
			
			
			//Log.Info(mn, "Sizes",p.getSizes());
			
		} //end while
		
	} //end methof

}
