package com.sga.common.google;

import java.util.ArrayList;

import com.sga.common.google.GoogleMediaItemList.GoogleMediaItem;

public class GoogleMediaItemCreateRequest {
	
	/*
	   public ArrayList<String> mediaItemIds=null;
	   
	   public GoogleMediaItemCreateRequest()
	   {
		   mediaItemIds=new ArrayList<String>();
	   }
	   
	   public GoogleMediaItemCreateRequest(String id)
	   {
		   mediaItemIds=new ArrayList<String>();
		   mediaItemIds.add(id);
	   }
	   
	   public void Add(String id)
	   {
		   if(mediaItemIds==null)
			   mediaItemIds=new ArrayList<String>();
		   
		   mediaItemIds.add(id);
	   }
	   
	  */
	
    public String albumId;
    public ArrayList<NewMediaItem> newMediaItems;
    
    
    public GoogleMediaItemCreateRequest()
    {
    	newMediaItems=new ArrayList<NewMediaItem>();
    	
    }
    
    public GoogleMediaItemCreateRequest(String albumId, String description, String fileName, String uploadToken)
    {
    	this.albumId=albumId;
    	NewMediaItem nmi = new NewMediaItem();
    	SimpleMediaItem smi=new SimpleMediaItem();
    	newMediaItems=new ArrayList<NewMediaItem>();
    	
    	nmi.description=description;
    	
    	smi.fileName=fileName;
    	smi.uploadToken=uploadToken;
    	nmi.simpleMediaItem=smi;
    	
    	newMediaItems.add(nmi);
    	
    }
	
	public class NewMediaItem{
	    public String description;
	    public SimpleMediaItem simpleMediaItem;
	}

	public class SimpleMediaItem{
	    public String fileName;
	    public String uploadToken;
	}

}
