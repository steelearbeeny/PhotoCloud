package com.sga.common.generic;

import java.nio.file.Path;
import java.util.List;

import org.apache.commons.collections4.map.MultiKeyMap;

import com.sga.common.util.Utils;

public class Response {
	
	public Integer returnCode;
	public String message;
	public Integer size;
	public Boolean success;
	public String hash;
	public Path path;
	public String format;
	public MultiKeyMap<String, MetadataItem> metadata=null; //2 keys - directory & itemid
	
	
	public Response()
	{
		returnCode=0;
		message="";
		size=0;
		success=false;
		hash="";
		path=null;
		metadata=null;
		format=null;
	}
	
	public Response(Integer returnCode, String message, Integer size, Boolean success, String hash)
	{
		this.returnCode=returnCode;
		this.message=message;
		this.size=size;
		this.success=success;
		this.hash=hash;
		path=null;
		metadata=null;
	}
	
	public void AddToMetadata(String directory, int itemid, String key, String value)
	{
		
		if(directory==null || directory.length() < 1 ||
				itemid < 1 || key==null || key.length() < 1 ||
				value==null || value.length() < 1)
			return;
		
		
        MetadataItem mi = new MetadataItem(
       			Utils.ToString(itemid),
       			directory,
       			key,
       			value); 
        
        if(!metadata.containsKey(mi.directory,mi.id))
     	   metadata.put(mi.directory,mi.id, mi);
	}
	

	@Override
	public String toString() {
		return "Response [returnCode=" + returnCode + ", message=" + message + ", size=" + size + ", success=" + success
				+ ", path=" + path + ", format=" + format + "]";
	}
	
	

}
