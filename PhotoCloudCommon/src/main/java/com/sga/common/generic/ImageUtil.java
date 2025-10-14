package com.sga.common.generic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.jobrunr.jobs.context.JobDashboardLogger;
import org.postgresql.util.PSQLException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import com.sga.common.dataaccess.DataUtil;
import com.sga.common.log.Log;
import com.sga.common.util.SHA512;
import com.sga.common.util.Utils;

public class ImageUtil {

	public static Response IsValidImage(InputStream is)
	{
		byte[] imageData;
		String mn="ImageUtil::IsValidImage";
		ByteArrayInputStream bis=null;
		Iterator<ImageReader> readers=null;
		ImageInputStream iis=null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


		String formatName;
		Metadata metadata = null;
		String fileName=null;
		Response response=new Response();
		BufferedImage image=null;
		String dum;

		
		//List<MetadataItem> genericMetadata = new ArrayList<MetadataItem>();
		MultiKeyMap<String, MetadataItem> genericMetadata=new MultiKeyMap<String,MetadataItem>(); //2 keys - directory & itemid
		
		
		//cache the data in a local array
		
		Log.Info(mn, "Method entered");
		
		try
		{
			
			
			
		imageData=is.readAllBytes();
		
		
		//is.close();
		//is=null;
		
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
       
            bis.reset();
            metadata= ImageMetadataReader.readMetadata(bis);
            Date tDate = new Date();
            
            
            
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) 
                {
                	
                   MetadataItem mi = new MetadataItem(
               			Utils.ToString(tag.getTagType()).trim(),
               			Utils.ToString(tag.getDirectoryName()).trim(),
               			Utils.ToString(tag.getTagName()).trim(),
               			Utils.ToString(tag.getDescription()).trim()); 
                   
                   //Log.Info(mn, mi.toString());
                   
                   Object obj = directory.getObject(Utils.ToInt(mi.id));
                   String objClass = obj.getClass().getName();
                   /*
                   Log.Info(mn, mi.key + " " + mi.value + " OBJ " + objClass);
                   
                   if(mi.directory.equals("GPS") && 
                		   (mi.id.equals("2") || mi.id.equals("4")))
                   {
                	   String gps[] = directory.getStringArray(Utils.ToInt(mi.id));
                	   Log.Info(mn, "GPS " + mi.value + " " + directory.getTagName(Utils.ToInt(mi.id)) + " " + gps.length);
                	   for(int i =0;i<gps.length;i++)
                	   {
                		   Log.Info(mn, "Coord " + gps[i]);
                	   }
                	   
                  	   StringValue gpsv[] = directory.getStringValueArray(Utils.ToInt(mi.id));
                	   if(gpsv!=null)
                	   {
	                  	   Log.Info(mn, "GPSV " + mi.value + " " + directory.getTagName(Utils.ToInt(mi.id)) + " " + gps.length);
	                	   
	            	   
	                	   for(int i =0;i<gpsv.length;i++)
	                	   {
	                		   Log.Info(mn, "CoordV " + gpsv[i]);
	                	   }
                	   }
                	   else
                		   Log.Info(mn, "GPSV NULL");
                	   
                	   
                	   int gpsi[] = directory.getIntArray(Utils.ToInt(mi.id));
                	   if(gpsi!=null)
                	   {
                		   for(int i=0;i<gpsi.length;i++)
                		   {
    	                	   
    	                		   Log.Info(mn, "CoordI " + gpsi[i]);
    	                	   
                		   }
                	   }
                	   else
                		   Log.Info(mn, "GPSI NULL");
                	   
                	   try
                	   {
                		   
                		   if(objClass.contains("[Lcom.drew.lang.Rational"))
                		   {
                			   Rational rr[] = directory.getRationalArray(Utils.ToInt(mi.id));
                			   
                			   for(int i=0;i<rr.length;i++)
                			   {
                					   Log.Info(mn, "RATIONAL "  + rr[i].toString());
                			   }
                		   
                		   }
                		   
                		   Log.Info(mn, "NUMS " + 
                			   directory.getLong(Utils.ToInt(mi.id)) );
                	   }
                	   catch(Exception ee)
                	   {
                		   Log.Info(mn, "EXCEPTION "  + ee.toString());
                	   }
                   }
                   
                   */
                   
                   if((mi.id.equals("36867") && mi.directory.equals("Exif SubIFD")) ||
                		   (mi.id.equals("306") && mi.directory.equals("Exif IFD0")) )
                		   {
                	   			//change the date to be standard postgres format
                	   			tDate=directory.getDate(Utils.ToInt(mi.id));
                	   			
                	   			if(tDate!=null)
                	   			{
                	   				dum=formatter.format(tDate);
                	   				mi.value=dum;
                	   			}
                	   			//Log.Info(mn, "DATE " + directory.getDate(Utils.ToInt(mi.id)));
                	   
                		   }
                  
                   
                    
                   
                   
                   if(!genericMetadata.containsKey(mi.directory,mi.id))
                	   genericMetadata.put(mi.directory,mi.id, mi);
                   
                   
                    
                }
            }
            
            Log.Info(mn,"Metadata items added: " + genericMetadata.size());
           
            //Clean up streams
            iis.close();
            iis=null;
            bis.close();
            bis=null;
            
            response.success=true;
            response.path=null;
            response.returnCode=0;
            response.metadata=genericMetadata;
            response.message="Valid image read with: " + formatName;
            response.hash=SHA512.HashBytes(imageData);
            response.size=imageData.length;
			
			Log.Info(mn,"Method exiting - image valid");
			
			return response;
        }
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
        } //end if readers.hasNext
        }
        catch(Exception ex)
        {
        	Log.Error(mn, "Caught exception " + ex.toString());
        	
        	response.success=false;
        	response.message=Log.Error(mn,"Exception: " + ex.toString());
        	response.returnCode=-1;
        	response.path=null;
        	response.metadata=null;
        	response.hash="";
        	response.size=0;
        	
        	return response;   
        }
		
		
	} //end method
	
	/*
	public static Map<String,MetadataItem> GetMetadata(int userId, String jobId, String itemId)
	{
		String mn="ImageUtil::GetMetadata";
		String SQLStr;
		ResultSet rs=null;
		MetadataItem mi;
		Map<String, MetadataItem> rv = new HashMap<String, MetadataItem>();
		
		SQLStr="SELECT "
				+ "I.METADATAID, "
				+ "I.METADATAVALUE, "
				+ "M.METADATADIRECTORY, "
				+ "M.METADATAKEY  "
				+ "FROM USERJOBITEMMETADATA I "
				+ "INNER JOIN METADATAMASTER M ON "
				+ "M.METADATAID=I.METADATAID "
				+ "WHERE I.USERID=?::INT AND "
				+ "I.JOBID=? AND "
				+ "I.ITEMID=?";
		
		try
		{
			rs=DataUtil.getInstance().GetRowSet(SQLStr, 
					Utils.ToString(userId), 
					jobId, 
					itemId);
		
			while(rs.next())
			{
			
				mi=new MetadataItem(Utils.ToString(rs.getString("METADATAID")),
						Utils.ToString(rs.getString("METADATADIRECTORY")),
						Utils.ToString(rs.getString("METADATAKEY")),
						Utils.ToString(rs.getString("METADATAVALUE"))
						);
				
				rv.put(mi.id, mi);
			}
		}
		catch(Exception ex)
		{
			Log.Error(mn, "Could not read metadata: " + ex.toString());
		}
		finally
		{
			DataUtil.getInstance().Close(rs);
		}
		
		return rv;
		
		
	} //end method GetMetadata
	*/
	
	public static MultiKeyMap<String,MetadataItem> GetMetadata(int userId, String jobId, String itemId)
	{
		String mn="ImageUtil::GetMetadata";
		String SQLStr;
		ResultSet rs=null;
		MetadataItem mi;
		MultiKeyMap<String, MetadataItem> rv = new MultiKeyMap<String, MetadataItem>();
		
		SQLStr="SELECT "
				+ "I.METADATAID, "
				+ "I.METADATAVALUE, "
				+ "M.METADATADIRECTORY, "
				+ "M.METADATAKEY  "
				+ "FROM USERJOBITEMMETADATA I "
				+ "INNER JOIN METADATAMASTER M ON "
				+ "M.METADATAID=I.METADATAID "
				+ "WHERE I.USERID=?::INT AND "
				+ "I.JOBID=? AND "
				+ "I.ITEMID=?";
		
		try
		{
			rs=DataUtil.getInstance().GetRowSet(SQLStr, 
					Utils.ToString(userId), 
					jobId, 
					itemId);
		
			while(rs.next())
			{
			
				mi=new MetadataItem(Utils.ToString(rs.getString("METADATAID")),
						Utils.ToString(rs.getString("METADATADIRECTORY")),
						Utils.ToString(rs.getString("METADATAKEY")),
						Utils.ToString(rs.getString("METADATAVALUE"))
						);
				
				rv.put(mi.directory, mi.id, mi);
			}
		}
		catch(Exception ex)
		{
			Log.Error(mn, "Could not read metadata: " + ex.toString());
		}
		finally
		{
			DataUtil.getInstance().Close(rs);
		}
		
		return rv;
		
		
	} //end method GetMetadata
	
	
	
	public static void WriteImageAndMetadataToDB(
			int userId, 
			String jobId, 
			String originalFileName,
			String url, 
			Response downloadResponse,
			JobDashboardLogger logger) throws Exception
	{
		String SQLStr;
		String tVal;
		//
		//Write the metadata to the db
		//
		
		Connection conn=null;
		long exists=0;
		String mn="ImageUtil::WriteImageAndMetadataToDB";
		UUID itemId = UUID.randomUUID();
		
		try
		{
			conn=DataUtil.getInstance().GetConnection();
			conn.setAutoCommit(false);
			
		
			SQLStr="INSERT INTO USERJOBITEM (USERID,JOBID,ITEMID,ORIGINALFILENAME,URL,MODTIME) VALUES (?,?,?,?,?,CURRENT_TIMESTAMP)";
			
			DataUtil.getInstance().ExecuteUpdate(conn,SQLStr,userId,jobId,itemId.toString(),originalFileName,url);
			
			for(MetadataItem m : downloadResponse.metadata.values())
			{
				SQLStr="SELECT COUNT(*) FROM METADATAMASTER WHERE METADATAID=? AND METADATADIRECTORY=?";
				exists=0;
				exists=DataUtil.getInstance().GetSingleLong(SQLStr,m.id,m.directory);
				
				//Log.Info(logger,mn, "Checking for key: " + m.key + " - id: " + m.id + " dir: " + m.directory + " - value: " + m.value + " - Exists:" + exists);
				//Log.Info(logger,mn,"Key " + Utils.HexDump(m.key));
				//Log.Info(logger,mn,"Id " + Utils.HexDump(m.id));
				//Log.Info(logger,mn,"Value " + Utils.HexDump(Utils.ToString(m.value).trim()));
				
				
				if(exists<1)
				{
					//add the metadata key
					SQLStr="INSERT INTO metadatamaster "
							+ "(metadataid, metadatadirectory, metadatakey,  metadatadescription, modtime) "
							+ "VALUES(?,?,?,?,CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING";
					try
					{
						DataUtil.getInstance().ExecuteUpdate(conn,
								SQLStr,
								Utils.ToString(m.id).trim(), 
								Utils.ToString(m.directory).trim(),
								Utils.ToString(m.key).trim(),
								"");
					}
					catch(PSQLException ex)
					{
						if(ex.toString().contains("duplicate key"))
						{
							//ignore because might be conmitted
							//Postgres doesnt support read uncommitted
						}
						else
						{
							throw ex;
						}
					}
					//Log.Info(logger, mn,"Inserting metadata master - " + m.key + " - " + m.id + " - " + m.value);
					
				}
				
				SQLStr="INSERT INTO USERJOBITEMMETADATA (USERID,JOBID,ITEMID,METADATAID,"
						+ "METADATADIRECTORY, METADATAVALUE,MODTIME) VALUES "
						+ "(?,?,?,?,?,?,CURRENT_TIMESTAMP) ON CONFLICT DO NOTHING";
				
				
				tVal=Utils.ToString(m.value).trim();
				if(tVal.length() > 65534)
					tVal=tVal.substring(0,65534);
				
				
				DataUtil.getInstance().ExecuteUpdate(
						conn,
						SQLStr,
						userId,
						jobId,
						itemId.toString(),
						Utils.ToString(m.id).trim(),
						Utils.ToString(m.directory).trim(),
						tVal);
		
				
			} //end foreach metadata item
			
			conn.commit();
			
		}
		catch(Exception ex)
		{
			Log.Error(logger, mn, ex.toString());
			
			conn.rollback();
			Log.Error(logger, mn,"The transaction was rolled back.");
			
			
		}
		finally
		{
			conn.setAutoCommit(true);
			DataUtil.getInstance().Close(conn);
			conn=null;
			
		}
		
		
		//Files.delete(downloadResponse.path);
	
		
		
		
	} //end writeimageandmetadata to db
	
	

}
