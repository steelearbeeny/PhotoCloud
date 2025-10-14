package com.sga.common.geocode;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;                       
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sga.common.log.Log;
import com.sga.common.util.Utils;

import java.lang.reflect.Type;                       

public class GeoDataDeserializer implements JsonDeserializer<GeoData> 
{
	
	                                           
	    
	    public GeoData deserialize(JsonElement jsonElement, 
	    		Type type, 
	    		JsonDeserializationContext jsonDeserializationContext) 
	        throws JsonParseException 
	    { 
	        
	    	GeoData geoData = new GeoData();
	    	JsonObject jsonObject =null;
	    	JsonObject o=null;
	    	JsonObject p=null;
	    	JsonObject a=null;
	    	JsonElement e=null;
	    	String mn="GetDataDeserializer::deserialize";
	    	
	    	
	    	jsonObject=jsonElement.getAsJsonObject();
	    	
	    	if(jsonObject==null || jsonObject.isJsonNull())
	    	{
	    		geoData.errorMessage="NULL JsonElement returned";
	    		Log.Info(mn, geoData.errorMessage);
	    		return null;
	    	}
	    	
	    	e=jsonObject.get("error");
	    	if(e!=null && !e.isJsonNull())
	    	{
	    		geoData.errorMessage=Utils.ToString(e.getAsString());
	    		Log.Info(mn, geoData.errorMessage);
	    		//return null;
	    	}
	    			
	    			
	    	JsonArray features = jsonObject.getAsJsonArray("features");
	    	
	    	if(features != null && features.size() > 0)
	    	{
	    		o=features.get(0).getAsJsonObject();
	    		if(o!=null& !o.isJsonNull())
	    		{
	    			p=o.getAsJsonObject("properties");
	    			if(p!=null && !p.isJsonNull())
	    			{
	    				geoData.category=GetString(p,"category");
	    				geoData.type=GetString(p,"type"); //category type
	    				geoData.addresstype=GetString(p,"addresstype");
	    				geoData.name=GetString(p,"name");
	    				geoData.displayName=GetString(p,"display_name");
	    				
	    				a=p.getAsJsonObject("address");
	    				if(a!=null && !a.isJsonNull())
	    				{
	    					geoData.road=GetString(a,"road");
	    					geoData.hamlet=GetString(a,"hamlet");
	    					geoData.village=GetString(a,"village");
	    					geoData.county=GetString(a,"county");
	    					
	    					geoData.state=GetString(a,"state");
	    					geoData.iso=GetString(a,"iso");
	    					geoData.postcode=GetString(a,"postcode");
	    					geoData.country=GetString(a,"country");
	    					geoData.countryCode=GetString(a,"country_code");
	    				
	    				}
	    				
	    				
	    				
	    				e= p.get("namedetails");
   				
	    				if(e!=null && !e.isJsonNull())
	    				{
	    					
		    				a=p.getAsJsonObject("namedetails");

		    				if(a!=null && !a.isJsonNull())
		    				{
		    					geoData.name_en=GetString(a,"name:en");
		    				}
	    				}
	    				
	    				
	    			}
	    		}
	    	}
	    	else
	    		geoData.errorMessage+= " No features found";
	    	
	    	
	    	return geoData;
	    }
	

	    private String GetString(JsonObject o, String key)
	    {
	    	String rv="";
	    	JsonElement z;
	    	
	    	if(o==null || key==null) return "";
	    	
	    	z=o.get(key);
	    	
	    	if(z==null) return "";
	    	
	    	return Utils.ToString(z.getAsString());
	
	    }
	    
}
