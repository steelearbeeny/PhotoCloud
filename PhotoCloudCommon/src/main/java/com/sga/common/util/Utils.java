package com.sga.common.util;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sga.common.geocode.GeoData;
import com.sga.common.geocode.GeoDataDeserializer;


public class Utils {
	
	//public static Gson gson = new Gson();
	
	public static Gson _gson=null;
	public static Date MIN_DATE=new Date(0);
	public static ZonedDateTime MIN_ZONED = ZonedDateTime.of(LocalDateTime.MIN, ZoneId.systemDefault());
	 
	
	public static String HexDump(String in)
	{
		StringBuffer sb = new StringBuffer();
		char ch[] = in.toCharArray();
	      for(int i = 0; i < ch.length; i++) {
	         String hexString = Integer.toHexString(ch[i]);
	         sb.append(hexString);
	      }
	      return sb.toString();
	}
	

	public static void QuietClose(Closeable s)
	{
		if(s==null) return;
		
		try {
			s.close();
		}
		catch(Exception ex)
		{
			//ignore
		}
		
		
	}
	
	public static LocalDateTime ToLocalDateTime(String epochSeconds)
	{
		long epochTimeMillis = 0L;
		Instant instant;
		ZoneId zoneId;
		
		try
		{
			epochTimeMillis=Long.parseLong(epochSeconds)*1000L;
			instant = Instant.ofEpochMilli(epochTimeMillis);
	
			zoneId = ZoneId.systemDefault(); // Use the system default time zone
			return instant.atZone(zoneId).toLocalDateTime();
		}
		catch(Exception ex)
		{
			return LocalDateTime.MIN;
		}
		
	} //end method
	
	public static LocalDateTime ToLocalDateTimeFromMilliseconds(long epochMilliSeconds)
	{
		
		Instant instant;
		ZoneId zoneId;
		
		try
		{
			
			instant = Instant.ofEpochMilli(epochMilliSeconds);
	
			zoneId = ZoneId.systemDefault(); // Use the system default time zone
			return instant.atZone(zoneId).toLocalDateTime();
		}
		catch(Exception ex)
		{
			return LocalDateTime.MIN;
		}
		
	} //end method

	public static ZonedDateTime TryParse(String time)
	{
		
		ZonedDateTime l;
		
		try
		{
			l=ZonedDateTime.parse(time);
		}
		catch(Exception ex)
		{
			l=MIN_ZONED;
		}
		
		return l;
		
	}
	
	public static ZonedDateTime TryParse(Date javaDate)
	{
		
		long epochTimeMillis = 0L;
		Instant instant;
		ZoneId zoneId;
		
		try
		{
			
			
			
			instant = javaDate.toInstant();
	
			zoneId = ZoneId.systemDefault(); // Use the system default time zone
			return instant.atZone(zoneId);
		}
		catch(Exception ex)
		{
			return MIN_ZONED;
		}
		
	}
	
	
	public static LocalDateTime TryParseLocal(String time)
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		
		LocalDateTime l;
		
		try
		{
			l=LocalDateTime.parse(time,formatter);
		}
		catch(Exception ex)
		{
			l=LocalDateTime.MIN;
		}
		
		return l;
		
	}
	
	
	public static Date TryParseDate(String time)
	{
		

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date l;
		
		try
		{
			l=formatter.parse(time);
		}
		catch(Exception ex)
		{
			l=Utils.MIN_DATE;
		}
		
		return l;
		
	}
	
	public static LocalDateTime ToLocalDateTime(Date javaDate)
	{
		long epochTimeMillis = 0L;
		Instant instant;
		ZoneId zoneId;
		
		try
		{
			
			
			
			instant = javaDate.toInstant();
	
			zoneId = ZoneId.systemDefault(); // Use the system default time zone
			return instant.atZone(zoneId).toLocalDateTime();
		}
		catch(Exception ex)
		{
			return LocalDateTime.MIN;
		}
		
	} //end method
	
	
	
	public static Gson GetGson()
	{
		
		if(_gson!=null) return _gson;
		
		/*
		_gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
	        @Override
	        public ZonedDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
	            return ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
	        }
	        }).create();
		*/
		_gson=new GsonBuilder()
				.registerTypeAdapter(
						ZonedDateTime.class, 
						new ZonedDateTimeConverter())
				.registerTypeAdapter(
						GeoData.class, 
						new GeoDataDeserializer())
				.registerTypeAdapter(
						LocalDateTime.class,
						new LocalDateTimeConverter())
                .create();
		
		
		return _gson;
		
	}
	
	
	public static String ToJSON(Object o)
	{
		return GetGson().toJson(o);
	}
	
	
	
	
	public static String AddProperty(Object o, String property, int value)
	{
		
        JsonElement jsonElement = GetGson().toJsonTree(o);
        jsonElement.getAsJsonObject().addProperty(property,value);
        String s=GetGson().toJson(jsonElement);
        return s;
	}
	
	public static String AddProperty(Object o, String property, String value)
	{
		
        JsonElement jsonElement = GetGson().toJsonTree(o);
        jsonElement.getAsJsonObject().addProperty(property,value);
        String s=GetGson().toJson(jsonElement);
        return s;
	}
	
	public static String AddProperty(Object o, String property, boolean value)
	{
		
        JsonElement jsonElement = GetGson().toJsonTree(o);
        jsonElement.getAsJsonObject().addProperty(property,value);
        String s=GetGson().toJson(jsonElement);
        return s;
	}
	
	
	public static Map<String,Object> ToMap(Object o)
	{
		   String json = GetGson().toJson(o);
		    Map<String, Object> map = GetGson().fromJson(json, 
		    		new TypeToken<Map<String, Object>>() {}.getType());
		    
		    return map;
	}
	
	public static String GetMapValueA(Map<String,String[]> p, String key)
	{
		if(p==null) return null;
		
		if(!p.containsKey(key))
			return null;
		
		String[] a = p.get(key);
		
		return Utils.ToString(a[0]);
		
		
	}
	
	public static String GetMapValue(Map<String,String> p, String key)
	{
		if(p==null) return null;
		
		if(!p.containsKey(key))
			return null;
		
		String a = p.get(key);
		
		return Utils.ToString(a);
		
		
	}
	
	public static int ToInt(Object o)
	{
		int rv=0;
		
		if(o==null) return 0;
		
		if(o instanceof String)
		{
			try
			{
				rv=Integer.valueOf((String)o);
				return rv;
			}
			catch(Exception e)
			{
				return 0;
			}
		}
		
		try
		{
			rv=(int)o;
			return rv;
		}
		catch(Exception e)
		{
			return 0;
		}
		
		
		
	}

	
	public static String ToString(Object o)
	{
		String rv="";
		
		if(o==null) return "";
		
		
		
		try
		{
			rv=o.toString();
			return rv;
		}
		catch(Exception e)
		{
			return "";
		}
		
		
		
	}
	
	public static String Base64Decode(String in)
	{
		 Base64.Decoder decoder = Base64.getDecoder();
		 StringBuilder sb = new StringBuilder();
	        // Decode the encoded string
	        byte[] decodedBytes = decoder.decode(in);

	        // Convert decoded bytes to a string
	        
	        for (byte b : decodedBytes) {
	            sb.append(String.format("%02x", b));
	        }
	        
	        
	        return sb.toString();
	}
	
	
}
