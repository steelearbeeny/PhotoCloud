package com.sga.common.generic;

import java.util.ArrayList;
import java.util.List;

import com.sga.common.oauth.OauthToken;

public class JobConfiguration {
	
	public int userId;
	public int sourceProvider;
	public int targetProvider;
	public String uniqueId;
	public OauthToken sourceToken;
	public OauthToken targetToken;
	public String autoCreateAlbums;
	public String albumNamePrefix;
	public String albumNameSuffix;
	public String dateFormat;
	public String albumName;
	public String exifMetadata;
	public String exifDirectory;
	public List<String> sourceAlbums;
	public String albumUrl;
	public int captionSwitch;
	public int facialSwitch;
	
	
	
		
	
	
	public JobConfiguration()
	{
		userId=-1;
		sourceProvider=-1;
		targetProvider=-1;
		uniqueId="";
		sourceToken=null;
		targetToken=null;
		autoCreateAlbums="";
		albumNamePrefix="";
		albumNameSuffix="";
		dateFormat="";
		albumName="";
		exifMetadata="";
		exifDirectory="";
		sourceAlbums=new ArrayList<String>();
		albumUrl="";
		captionSwitch=0;
		facialSwitch=0;
	}
	
	
	public JobConfiguration(int userId, int sourceProvider, int targetProvider, String uniqueId) 
	{
		super();
		this.userId = userId;
		this.sourceProvider = sourceProvider;
		this.targetProvider = targetProvider;
		this.uniqueId = uniqueId;
		sourceToken=null;
		targetToken=null;
		
		autoCreateAlbums="";
		albumNamePrefix="";
		dateFormat="";
		albumName="";
		exifMetadata="";
		exifDirectory="";
		albumNameSuffix="";
		sourceAlbums=new ArrayList<String>();
		albumUrl="";
		captionSwitch=0;
		facialSwitch=0;
		
	}


	@Override
	public String toString() {
		return "JobConfiguration [userId=" + userId + ", sourceProvider=" + sourceProvider + ", targetProvider="
				+ targetProvider + ", uniqueId=" + uniqueId + ", sourceToken=" + sourceToken + ", targetToken="
				+ targetToken + ", autoCreateAlbums=" + autoCreateAlbums + ", sourceAlbums " + sourceAlbums.toString() + 
				", albumUrl=" + albumUrl + ", captionSwitch=" + captionSwitch + ", facialSwitch=" + facialSwitch + "]";
	}
	
	

}
