package com.sga.common.geocode;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sga.common.log.Log;
import com.sga.common.util.Utils;

public class Geocoder {
	
	private static String baseURL="http://localhost:8088";
	
	 // private final static Pattern DMS_PATTERN = Pattern.compile(
	 //           "(-?)([0-9]{1,2}).\\s*([0-5]?[0-9])'\\s*([0-5]?[0-9]\\.?[0-9]+)");

	  private final static Pattern DMS_PATTERN = Pattern.compile(
	            "(-?)([0-9]{1,2})°\\s*([0-5]?[0-9])'\\s*([0-5]?[0-9]\\.?[0-9]+)\"\\s*");

	  
	  public static Double dms2dd(String dms)
	  {
			Matcher matcher=null;
			String mn="Geocoder::dms2dd";
			Double lat=0D;
			
			
			Log.Info(mn,"In Method " + dms);
			matcher = DMS_PATTERN.matcher(dms);
			
			
			lat=0D;
			
			if(matcher.matches())
			{
				//Log.Info(mn,"Matches");
				//Log.Info(mn, "Groups: " + matcher.groupCount());
				
				//for(int i=1; i<= matcher.groupCount(); i ++)
				//	Log.Info(mn,matcher.group(i));
				
				lat=Double.parseDouble(matcher.group(2));
				lat += Double.parseDouble(matcher.group(3)) / 60.0D;
				lat += Double.parseDouble(matcher.group(4)) / 3600.0D;
				
				
				if(Utils.ToString(matcher.group(1)).trim().equals("-"))
					lat*=-1.0D;
					
				//Log.Info(mn, "DD " + lat);
				
			}
			else
			{
				//Log.Info(mn, "NO MATCH");
				return null;
			}
			
			return lat;
			
	  }
	  
	public static GeoData ReverseLookup(String dmslat, String dmslon)
	{
		
		
		Double lat;
		Double lon;
		Matcher matcher=null;
		String mn="Geocoder::ReverseLookup";
		
		
		
		Log.Info(mn,"In Method " + dmslat);
		lat=dms2dd(dmslat);
		lon=dms2dd(dmslon);
		
		if(lat==null || lon==null)
		{
			Log.Info(mn,"Invalid DMS coordinates");
			return null;
		}
		
		return ReverseLookup(lat,lon);
	}
	  
	  
	public static GeoData ReverseLookup(Double lat, Double lon)
	{
		String mn="Geocoder::ReverseLoopkup";
		URL url=null;
		String request;
		InputStream is=null;
		byte[] bytes=null;
		String jsonString=null;
		GeoData geoData=null;
		
		Log.Info(mn, "Method Entered - " + lat + " " + lon);
		
		request=String.format("%s/reverse?lat=%f&lon=%f&format=geojson&namedetails=1&extratags=1", 
				baseURL,lat,lon);
		
		Log.Info(mn, request);
		
		try
		{
		
			url=new URL(request);
			
			is=url.openStream();
		    bytes = is.readAllBytes();
		    jsonString=new String(bytes, StandardCharsets.UTF_8);
			
			Log.Info(mn, jsonString);
			
			geoData=Utils.GetGson().fromJson(jsonString,GeoData.class);
			
			Log.Info(mn,geoData.toString());
			return geoData;
		}
		catch(Exception ex)
		{
			Log.Error(mn,ex);
			return null;
		}
		finally
		{
			Utils.QuietClose(is);
		}
		
	}

}
