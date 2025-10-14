package com.sga.common.flickr;

import com.sga.common.log.Log;

public class FlickrInterfaceBase {
	
public static FlickrConnection flickrConnection=null;
	
	
	public static void Initialize(FlickrConnection _flickrConnection)
	{
		flickrConnection=_flickrConnection;
	}
	
	
	public static void IsInitialized() throws Exception
	{
		String mn="FlickrInterfaceBase::IsInitialized";
		if(flickrConnection==null)
		{
			Log.Info(mn, "FlickrConnection not initialized");
			throw new Exception("Not initialized");
		}
	}

}
