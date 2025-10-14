package com.sga.common.google;



public class GoogleAlbumRequest {

	
	public GoogleAlbumRequestItem album=null;
	
	public GoogleAlbumRequest()
	{
		album= new GoogleAlbumRequestItem();
		album.id="";
		album.title="";
	}
	
	
	public class GoogleAlbumRequestItem {
		public String id;
		public String title;
	}
	
	
}
