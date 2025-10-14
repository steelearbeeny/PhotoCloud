package com.sga.common.http;

import java.awt.image.BufferedImage;
//import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.sga.common.generic.MetadataItem;
import com.sga.common.generic.Response;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.util.HttpStatus;
import com.sga.common.util.ReturnValue;
import com.sga.common.util.SHA512;
import com.sga.common.util.Utils;





public class HttpUtils {
	
	
	public static ReturnValue<String> DeleteRequest(OauthToken token, String url)
	{
		URL turl; 
		HttpURLConnection conn=null;
		InputStream inputStream=null;
		byte[] bytes=null;
		String result="";
		String mn="HttpUtils::DeleteRequest";
		ReturnValue<String> rv = null;
		String httpStatus="";
		
		try
		{
			Log.Info(mn, "Method Entered");
			
			turl= new URL(url);
			
			conn=(HttpURLConnection) turl.openConnection();
			conn.setRequestMethod("DELETE");
			
			if(token!=null)
				conn.setRequestProperty("Authorization", "Bearer " + token.tokenResponse);
	
			inputStream=conn.getInputStream();
			
	        bytes = inputStream.readAllBytes();
	        
	        httpStatus=Utils.ToString(conn.getResponseCode());
	        
	        result=new String(bytes, StandardCharsets.UTF_8); // Specify character encoding
		}
		catch(Exception ex)
		{
			Log.Error(mn, "Exception: " + ex.toString());
		}
		finally
		{
			Utils.QuietClose(inputStream);
		}
			
		return new ReturnValue<String>(httpStatus,result);
		
	}
	
	
	public static ReturnValue<String> GetRequest(OauthToken token, String url, Object... parms)
	{
		URL turl; 
		HttpURLConnection conn=null;
		InputStream inputStream=null;
		byte[] bytes=null;
		String result="";
		String mn="HttpUtils::GetRequest";
		
		String httpStatus="";
		int i=0;
		
		try
		{
			Log.Info(mn, "Method Entered");
			
			if(parms != null && parms.length > 0)
			{
				url += "?";
				
				for(Object p : parms)
				{
					url += Utils.ToString(p);

					if(i%2==0)
						url+="=";
					else
						if(i+1 > parms.length)
							url+="&";
					
					i++;
					
				}
				
				
			} //end if
			
			Log.Info(mn, "URL: " + url);
			
			turl= new URL(url);
			
			conn=(HttpURLConnection) turl.openConnection();
			conn.setRequestMethod("GET");
			
			if(token!=null)
				conn.setRequestProperty("Authorization", "Bearer " + token.tokenResponse);
	
			inputStream=conn.getInputStream();
			
			
	        bytes = inputStream.readAllBytes();
	        
	        httpStatus=Utils.ToString(conn.getResponseCode());
	        
	        result=new String(bytes, StandardCharsets.UTF_8); // Specify character encoding
		}
		
		catch(Exception ex)
		{
			try
			{
				httpStatus=Utils.ToString(conn.getResponseCode());
				result=httpStatus + " - " + HttpStatus.GetDescription(httpStatus);
			}
			catch(Exception ex2)
			{
				httpStatus="-1";
				result="";
				Log.Error(mn,ex2.toString());
			}
			result+=" - " + ex.toString();
			Log.Error(mn, "Exception: " +result);
		}
		finally
		{
			Utils.QuietClose(inputStream);
		}
			
		return new ReturnValue<String>(httpStatus,result);
		
	}
	
	
	
	public static ReturnValue<String> PostRaw(OauthToken token, String url, byte[] data, Object... headers)
	{
		URL turl; 
		HttpURLConnection conn=null;
		InputStream inputStream=null;
		byte[] bytes=null;
		String result="";
		String mn="HttpUtils::PostRaw";
		OutputStream os=null;
		
		String httpStatus="";
		int i=0;
		String headerName;
		String headerValue;
		
		try
		{
			Log.Info(mn, "Method Entered");
			
			Log.Info(mn, "URL: " + url);
			
			turl= new URL(url);
			
			conn=(HttpURLConnection) turl.openConnection();
			conn.setRequestMethod("POST");
			
			conn.setRequestProperty("Content-Type", "application/octet-stream");
		    conn.setRequestProperty("Content-Length", Utils.ToString(data.length));
			
			if(token!=null)
				conn.setRequestProperty("Authorization", "Bearer " + token.tokenResponse);
			
			conn.setDoOutput(true);
			
			if(headers != null && headers.length > 0)
			{
				
				i=0;
				headerName="";
				headerValue="";
				for(Object p : headers)
				{
					
					if(i%2==0)
						headerName=Utils.ToString(p);
					else
					{
						headerValue=Utils.ToString(p);
						conn.setRequestProperty(headerName, headerValue);
						Log.Info(mn,"Setting header:" + headerName + " " + headerValue);
						headerName=headerValue="";
					}
					
					i++;
					
				}
				
				
			} //end if
			
			
	
			os=conn.getOutputStream();
		    os.write(data);
		    os.flush();
			
			inputStream=conn.getInputStream();
	
			bytes = inputStream.readAllBytes();
	        
	        httpStatus=Utils.ToString(conn.getResponseCode());
	        
	        result=new String(bytes, StandardCharsets.UTF_8); // Specify character encoding
		}
		
		catch(Exception ex)
		{
			try
			{
				httpStatus=Utils.ToString(conn.getResponseCode());
				result=httpStatus + " - " + HttpStatus.GetDescription(httpStatus);
			}
			catch(Exception ex2)
			{
				httpStatus="-1";
				result="";
				Log.Error(mn,ex2.toString());
			}
			result+=" - " + ex.toString();
			Log.Error(mn, "Exception: " +result);
		}
		finally
		{
			Utils.QuietClose(os);
			Utils.QuietClose(inputStream);
		}
			
		return new ReturnValue<String>(httpStatus,result);
		
	}
	
	
	

	public static ReturnValue<String> PostRequest(OauthToken token, String url, Object data)
	{
		URL turl; 
		HttpURLConnection conn=null;
		String result="";
		String mn="HttpUtils::PostRequest";
		OutputStream os=null;
		String jsonString;
		byte[] postData;
		InputStream inputStream=null;
		
		String httpStatus="";
		int i=0;

		
		try
		{
			Log.Info(mn, "Method Entered");
			
			Log.Info(mn, "URL: " + url);
			
			turl= new URL(url);
			
			conn=(HttpURLConnection) turl.openConnection();
			conn.setRequestMethod("POST");
			
			conn.setRequestProperty("Content-Type", "application/json");
			
			if(token!=null)
				conn.setRequestProperty("Authorization", "Bearer " + token.tokenResponse);
			
			conn.setDoOutput(true);
			
			if(data != null)
			{
				jsonString=Utils.GetGson().toJson(data);
				postData=jsonString.getBytes("utf-8");
				
				Log.Info(mn,"Posting: " + jsonString);
	
				os=conn.getOutputStream();
			    os.write(postData,0,postData.length);
			    os.flush();
			}
		    
		    
			
			inputStream=conn.getInputStream();
	
			postData = inputStream.readAllBytes();
	        
	        httpStatus=Utils.ToString(conn.getResponseCode());
	        
	        result=new String(postData, StandardCharsets.UTF_8); // Specify character encoding
	        Log.Info(mn, "Post return " + httpStatus + " " + result);
	        
	 
		
		}
		
		catch(Exception ex)
		{
			try
			{
				httpStatus=Utils.ToString(conn.getResponseCode());
				result=httpStatus + " - " + HttpStatus.GetDescription(httpStatus);
			}
			catch(Exception ex2)
			{
				httpStatus="-1";
				result="";
				Log.Error(mn,ex2.toString());
			}
			result+=" - " + ex.toString();
			Log.Error(mn, "Exception: " +result);
		}
		finally
		{
			Utils.QuietClose(os);
			Utils.QuietClose(inputStream);
		}
			
		return new ReturnValue<String>(httpStatus,result);
		
	}
	

	
	
	
	
	
	/*
	public static Response GetImage(String url) throws Exception
	{
		URL imageUrl=null;
		BufferedImage image=null;
		String mn="HttpUtils::GetImage";
		//File file=null;
		Path path=null;
		InputStream is = null;
		byte[] imageData;
		FileOutputStream fos=null;
		ByteArrayInputStream bis=null;
		Iterator<ImageReader> readers=null;
		ImageInputStream iis=null;
		String formatName;
		Metadata metadata = null;
		String fileName=null;
		Response response=new Response();
		
		//List<MetadataItem> genericMetadata = new ArrayList<MetadataItem>();
		MultiKeyMap<String, MetadataItem> genericMetadata=new MultiKeyMap<String,MetadataItem>(); //2 keys - directory & itemid

		
		try
		{
			imageUrl=new URL(url);
			
			Log.Info(mn,imageUrl.getPath() + " " + imageUrl.getFile() + " " + imageUrl.getQuery());
			
			fileName=imageUrl.getPath().replaceAll("\\/.*\\/", "");
			
			Log.Info(mn,"FileName: " + fileName);
			
			
			is=imageUrl.openStream();
			
			//cache the data in a local array
			imageData=is.readAllBytes();
			is.close();
			is=null;
			
			Log.Info(mn,"Read Bytes: " + imageData.length);
			
			//create streams to wrap the data reading
			//ImageIO requires a image input stream so need 2 streams here
			bis = new ByteArrayInputStream(imageData);
			iis = new MemoryCacheImageInputStream(bis);
			bis.mark(100);
			
			//Get the valid image readers for this data
			//to determine if its really an image and to know the extension
			readers = ImageIO.getImageReaders(iis);

	        if (readers.hasNext()) {
			
	        	 // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);
                
                //get the image format = ex: JPEG
                formatName=reader.getFormatName();
                formatName=formatName.toLowerCase();
                
                Log.Info(mn, "Format: "  + formatName);
                
                image=reader.read(0);
                
                
                // read metadata of first image
             
                
                bis.reset();
                metadata= ImageMetadataReader.readMetadata(bis);
                
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) 
                    {
                    	
                        
                        
                       MetadataItem mi = new MetadataItem(
                   			Utils.ToString(tag.getTagType()),
                   			tag.getDirectoryName(),
                   			tag.getTagName(),
                   			tag.getDescription()); 
                       
                       Log.Info(mn, mi.toString());
                        
                       
                       if(!genericMetadata.containsKey(mi.directory,mi.id))
                    	   genericMetadata.put(mi.directory,mi.id, mi);
                        
                    }
                }
                
               
                //Clean up streams
                iis.close();
                iis=null;
                bis.close();
                bis=null;
                
                
                //save image as a file
               
                path=Files.createTempFile("PC_", "." + formatName);
                fos=new FileOutputStream(path.toString());
               
                
    			fos.write(imageData);
    			fos.close();
    			fos=null;
    			
                response.success=true;
                response.path=path;
                response.returnCode=0;
                response.metadata=genericMetadata;
                response.message=Log.Info(mn,"File Saved At: " + path.toString());
                response.hash=SHA512.HashFile(path);
                response.size=imageData.length;
    			
    			
    			
    			return response;

			
	        } //end if
	        else
	        {
	        	response.success=false;
	        	response.message=Log.Error(mn,"No image reader could be found for the specified image.");
	        	response.returnCode=-1;
	        	response.path=null;
	        	response.metadata=null;
	        	response.hash="";
	        	response.size=0;
	        	
	        	return response;
	        }
			
			
	

			
			
		}
		catch(Exception ex)
		{
			Log.Error(mn,ex);
			throw ex;
			
		}
		finally
		{
			
			if(fos!=null)
			{
				try {
					fos.close();
				}
				catch(Exception ex)
				{
					
				}
			}
			
			
			if(bis!=null)
			{
				try {
					bis.close();
				}
				catch(Exception ex)
				{
					
				}
			}
			
			if(iis!=null)
			{
				try {
					iis.close();
				}
				catch(Exception ex)
				{
					
				}
			}
			
			if(is!=null)
			{
				try {
					is.close();
				}
				catch(Exception ex)
				{
					
				}
			}
			
			
			
		}
		
	}
	*/
	
	/*
	public static File GetImageX(String url) throws Exception
	{
		URL imageUrl=null;
		BufferedImage image=null;
		String mn="HttpUtils::GetBytes";
		File file=null;
		ImageInputStream iis=null;
		Iterator<ImageReader> readers=null;
		
		try
		{
			imageUrl=new URL(url);
			
			iis = ImageIO.createImageInputStream(url);
	        readers = ImageIO.getImageReaders(iis);

	        if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();

                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                    Log.Info(mn, "Format name: " + names[ i ] );
                    IIOMetadataNode node = (IIOMetadataNode)metadata.getAsTree(names[i]);
                    printChildren(node,"");
                }
                
                
                image = reader.read(0);
                
                
                
            } 
	         
	         
			//image = ImageIO.read(imageUrl);
			
			
			
			file=File.createTempFile("PC_", ".jpg");
			ImageIO.write(image, "jpeg", file);
			Log.Info(mn,"File Saved At: " + file.toString() + " " + file.getAbsolutePath());
			return file;
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			throw ex;
		}
		
		
	}
	*/
	/*
	private static void printChildren(IIOMetadataNode node, String indent) {
		  String nodeName = node.getNodeName();
		  indent += '/' + nodeName;
		  final NodeList childNodes = node.getChildNodes();
		  int childCount = childNodes.getLength();
		  for (int n = 0; n < childCount; ++n) {
		    IIOMetadataNode child = (IIOMetadataNode) childNodes.item(n);
		    String childName = child.getNodeName();
		    // child.getNodeValue() and child.getPrefix() always return null
		    NamedNodeMap attributes = child.getAttributes();
		    int aLength = attributes.getLength();
		    for (int a = 0; a < aLength; ++a) {
		      Node item = attributes.item(a);
		      final String itemName = item.getNodeName();
		      System.out.printf("  [%s/%s] %s = %s%n",
		          indent, childName, itemName, child.getAttribute(itemName));
		    }
		    printChildren(child, indent);
		  }
	}
	*/

}
