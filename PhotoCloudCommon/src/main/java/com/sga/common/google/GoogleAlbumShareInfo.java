package com.sga.common.google;

public class GoogleAlbumShareInfo {
	
	
	    public GoogleSharedAlbumOptions sharedAlbumOptions = new GoogleSharedAlbumOptions();
	    public String shareableUrl="";
	    public String shareToken="";
	    public boolean isJoined=false;
	    public boolean isOwned=false;
	    public boolean isJoinable=false;
	
	
	public class GoogleSharedAlbumOptions{
	    public boolean isCollaborative=false;
	    public boolean isCommentable=false;
	
	}

}
