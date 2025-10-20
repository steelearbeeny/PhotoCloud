package com.sga.common.generic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.MultiKeyMap;

import com.sga.common.geocode.GeoData;
import com.sga.common.geocode.Geocoder;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.util.Utils;

public class GenericPhoto {
	public String name;
	public String description;
	public String guid;
	public ZonedDateTime dateCreated;
	public ZonedDateTime expires;
	public Long fileSize;
	public Integer width;
	public Integer height;
	public String format;
	public String url;
	
	public ByteArrayInputStream inputStream=null;
	public MultiKeyMap<String, MetadataItem> metadata=null; //2 keys - directory & itemid
	public String ownerName;
	public String albumName;
	public GeoData geoData=null;
	public OauthToken token=null;
	public byte[] imageData=null;
	public List<String> tags=new ArrayList<String>();
	
	public GenericPhoto()
	{
		
	}
	
	public GenericPhoto(String guid)
	{
		this.guid=guid;
	}
	
	public GenericPhoto(String guid, String name, String url)
	{
		this.guid=guid;
		this.name=name;
		this.url=url;
		
	}

	public void Geocode()
	{
		
		String mn="GenericPhoto::Geocode";
		
		if(metadata==null)
		{
			Log.Info(mn, "Cannot geocode, no metadata found");
			geoData=null;
			return;
		}
			
		String dmslat, dmslon;
		MetadataItem mdi;
		
		
		mdi=metadata.get("GPS","2");
		
		if(mdi==null)
		{
			Log.Info(mn, "Cannot geocode, no LAT/LON found");
			geoData=null;
			return;
		}
		
		dmslat=Utils.ToString(mdi.value);
		
		if(dmslat.length() < 1)
		{
			Log.Info(mn, "Cannot geocode, invalid LAT/LON");
			geoData=null;
			return;
		}
		
		mdi=metadata.get("GPS","4");
		
		if(mdi==null)
		{
			Log.Info(mn, "Cannot geocode, no LAT/LON found");
			geoData=null;
			return;
		}
		
		dmslon=Utils.ToString(mdi.value);
		
		if(dmslon.length() < 1)
		{
			Log.Info(mn, "Cannot geocode, invalid LAT/LON");
			geoData=null;
			return;
		}
		
		GeoData geoData=null;
		
		geoData=Geocoder.ReverseLookup(dmslat, dmslon);
		
		this.geoData=geoData;
		
	}
	
	public List<String> GetTags()
	{
		
		
		String mn="GenericPhoto::GetTags";
		
		if(geoData==null)
		{
			Log.Info(mn, "Cannot get tags, no geo data found");
			return null;
		}
		
		
		
		if(geoData.category.length() > 0)
			tags.add(QuoteIfSpace(geoData.category));
		
		if(geoData.type.length() > 0)
			tags.add(QuoteIfSpace(geoData.type));
		
		if(geoData.addresstype.length() > 0)
			tags.add(QuoteIfSpace(geoData.addresstype));
		
		if(geoData.country.length() > 0)
			tags.add(QuoteIfSpace(geoData.country));
		
		if(geoData.countryCode.length() > 0)
			tags.add(QuoteIfSpace(geoData.countryCode));
		
		if(geoData.name.length() > 0)
			tags.add(QuoteIfSpace(geoData.name));
		
		if(geoData.name_en.length() > 0)
			tags.add(QuoteIfSpace(geoData.name_en));
		
		if(geoData.state.length() > 0)
			tags.add(QuoteIfSpace(geoData.state));
		
		if(geoData.county.length() > 0)
			tags.add(QuoteIfSpace(geoData.county));
		
		if(geoData.village.length() > 0)
			tags.add(QuoteIfSpace(geoData.village));
		
		if(geoData.hamlet.length() > 0)
			tags.add(QuoteIfSpace(geoData.hamlet));
	
		return tags;
		

	}
	
	private String QuoteIfSpace(String in)
	{
		
		String z = Utils.ToString(in);
		if(z.contains(" "))
			z="\"" + z + "\"";
		
		return z;
		
	}
	
	public void SetMetadata(Response res)
	{
		metadata=res.metadata;
		format=res.format; //mime type
	}
	
	@Override
	public String toString() {
		
		String streamDesc="null";
		String mdLength="null";
		
		if(inputStream!=null)
			streamDesc=inputStream.toString();
		
		if(metadata!=null)
			mdLength=Utils.ToString(metadata.size());
		
		return "GenericPhoto [name=" + name + ", description=" + description + ", guid=" + guid + ", dateCreated="
				+ dateCreated + ", expires=" + expires + ", fileSize=" + fileSize + ", width=" + width + ", height="
				+ height + ", format=" + format + ", url=" + url + ", stream=" + streamDesc + ", metadata count=" + mdLength + "]";
	}

	/*
	public void OpenStream() throws Exception
	{
		URL turl; 
		
		if(inputStream==null)
		{
			turl= new URL(url);
		
			inputStream=turl.openStream();
		}
		else
		{
			
		}
		
		
	}
	*/
	
	public void LoadByteArray() throws Exception
	{
		InputStream _inputStream = null;
		
		if(inputStream!=null) return;
		
		URL turl; 
		HttpURLConnection conn=null;
		String mn="GenericPhoto::LoadByteArray";
		
		Log.Info(mn, "Method Entered");
		

		turl= new URL(url);
		
		if(token==null)
		{
			_inputStream=turl.openStream();
		}
		else
		{
		
			conn=(HttpURLConnection) turl.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Bearer " + token.tokenResponse);

			_inputStream=conn.getInputStream();
		}
	
		try
		{
			imageData=_inputStream.readAllBytes();
			
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			
		}
		finally
		{
			Utils.QuietClose(_inputStream);
		}
		
		inputStream = new ByteArrayInputStream(imageData);
		
		
	}
	
	
	public void LoadByteArray(InputStream is) throws Exception
	{
		
		
		if(inputStream!=null) return;
		
	
		String mn="GenericPhoto::LoadByteArray2";
		
		Log.Info(mn, "Method Entered");
		
		try
		{
			imageData=is.readAllBytes();
			
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			
		}
		finally
		{
			Utils.QuietClose(is);
		}
		inputStream = new ByteArrayInputStream(imageData);
	}
	
	
	
	
	
	
	/*
	private void OpenStream() throws Exception
	{
		URL turl; 
		HttpURLConnection conn=null;
		
		if(inputStream==null)
		{
			turl= new URL(url);
			
			if(token==null)
			{
				inputStream=turl.openStream();
			}
			else
			{
			
				conn=(HttpURLConnection) turl.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Authorization", "Bearer " + token.tokenResponse);
	
				inputStream=conn.getInputStream();
			}
		}
		else
		{
			
		}
		
		
	}
	*/
	//CLosing a ByteArrayInputStream doesn nothing
	public void CloseStream()
	{
		if(inputStream==null) 
		{
			imageData=null;
			return;
		}
			
		
		try
		{
			inputStream.close();
			inputStream=null;
			imageData=null;
			//GC ??
			
		}
		catch(Exception ex)
		{
			
		}
		
		
		
	}
	
}
