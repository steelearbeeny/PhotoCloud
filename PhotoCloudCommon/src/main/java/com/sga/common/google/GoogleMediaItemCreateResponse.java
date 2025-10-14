package com.sga.common.google;

import java.util.ArrayList;
import java.util.Date;

public class GoogleMediaItemCreateResponse {

	
	 public ArrayList<NewMediaItemResult> newMediaItemResults;
		
	 
	 public GoogleMediaItemCreateResponse()
	 {
		 newMediaItemResults=new ArrayList<NewMediaItemResult>();
	 }
	
	public class MediaItem{
	    public String id;
	    public String description;
	    public String productUrl;
	    public String mimeType;
	    public MediaMetadata mediaMetadata;
	    public String filename;
	}

	public class MediaMetadata{
	    public Date creationTime;
	    public String width;
	    public String height;
	}

	public class NewMediaItemResult{
	    public String uploadToken;
	    public Status status;
	    public MediaItem mediaItem;
	}

	
	   

	public class Status{
	    public String message;
	}

	public boolean AreAllSuccessful()
	{
		boolean rv=true;
		
		if(newMediaItemResults==null || newMediaItemResults.size() < 1)
			return false;
		
		for(NewMediaItemResult r : newMediaItemResults)
		{
			if(r.status == null)
				return false;
			
			if(!r.status.message.toUpperCase().equals("SUCCESS"))
				return false;
		}
		
		return true;
	}
	
	
}
