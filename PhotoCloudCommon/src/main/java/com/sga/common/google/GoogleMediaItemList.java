package com.sga.common.google;

import java.util.ArrayList;
import java.util.Date;

public class GoogleMediaItemList {

	
	    public ArrayList<GoogleMediaItem> mediaItems;
	

	    public class GoogleMediaItem{
	        public String id;
	        public Date createTime;
	        public String type;
	        public GoogleMediaFile mediaFile;
	    }
	    
	    public class GoogleMediaFile{
	        public String baseUrl;
	        public String mimeType;
	        public GoogleMediaFileMetadata mediaFileMetadata;
	        public String filename;
	    }

	    public class GoogleMediaFileMetadata{
	        public int width;
	        public int height;
	        public String cameraMake;
	        public String cameraModel;
	        public GooglePhotoMetadata photoMetadata;
	    }

	   

	    public class GooglePhotoMetadata{
	        public double focalLength;
	        public double apertureFNumber;
	        public int isoEquivalent;
	        public String exposureTime;
	    }

	    
	
} //end GoogleMediaItemList
