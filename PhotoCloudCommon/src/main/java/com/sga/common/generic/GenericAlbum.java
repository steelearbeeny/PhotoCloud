package com.sga.common.generic;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GenericAlbum {

	public String name;
	public String description;
	public String url;
	public Integer itemCount;
	public ZonedDateTime creationDate;
	public String guid;
	public String creatorName;
	
	public Map<String,GenericPhoto> thumbnails;
	public Map<String,GenericPhoto> images;
	
	public GenericAlbum()
	{
		thumbnails=new HashMap<String, GenericPhoto>();
		images=new HashMap<String,GenericPhoto>();
		guid=UUID.randomUUID().toString();
		
	}
	
	public GenericAlbum(String name, String creatorName)
	{
		thumbnails=new HashMap<String, GenericPhoto>();
		images=new HashMap<String,GenericPhoto>();
		guid=UUID.randomUUID().toString();
		this.name=name;
		this.creatorName=creatorName;
		
	}
	
	
}
