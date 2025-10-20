package com.sga.common.readers;

import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardLogger;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.ImageUtil;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.log.Log;
import com.sga.common.util.Utils;

public class LocalFileReader extends ReaderBase {

	//public JobContext jobContext=null;
	//public Map<String,Object> sessionData=null;
	//public JobConfiguration jobConfiguration=null;
	public List<GenericPhoto> items;
	public int currentIndex=-1;
	//public JobDashboardLogger logger=null;
	
	
	public LocalFileReader(JobContext jobContext, Map<String,Object> sessionData, JobConfiguration jobConfiguration) throws Exception
	{
		super(jobContext,sessionData,jobConfiguration);
		
		String mn="LocalFileReader::LocalFileReader";
		//Exif date format
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
		
		items=new ArrayList<GenericPhoto>();
		
		String SQLStr;
		ResultSet rs=null;
		GenericPhoto photo=null;
		ZonedDateTime z=null;
		URLConnection conn;
		URL turl;
		long millis;
		
		
		
		SQLStr="SELECT I.*, M.METADATAVALUE "
				+ "FROM USERJOBITEM I "
				+ "LEFT JOIN USERJOBITEMMETADATA M ON "
				+ "M.USERID=I.USERID AND "
				+ "M.JOBID=I.JOBID AND "
				+ "M.ITEMID=I.ITEMID AND "
				+ "M.METADATAID='36867' AND "
				+ "M.METADATADIRECTORY='Exif IFD0' "
				+ "WHERE I.JOBID=? AND "
				+ "I.USERID=?::INT "
				+ "ORDER BY I.MODTIME";
		

		
		try
		{
			
			rs=DataUtil.getInstance().GetRowSet(SQLStr,
					jobConfiguration.uniqueId,
					Utils.ToString(jobConfiguration.userId));
			
			while(rs.next())
			{
				
				z=null;
				
				try
				{
					z=ZonedDateTime.parse(
							Utils.ToString(rs.getString("metadatavalue")),
							formatter);
				}
				catch(Exception e)
				{
					z=null;
				}
				
				
				
				photo=new GenericPhoto(Utils.ToString(rs.getString("itemid")),
						Utils.ToString(rs.getString("originalfilename")),
						Utils.ToString(rs.getString("url"))						
						);
				
				
				if(z!=null)
					photo.dateCreated=z;
				else
				{
					try
					{
						turl = new URL(photo.url);
						conn=turl.openConnection();
						millis=conn.getLastModified();
						
						
						z=ZonedDateTime.ofInstant(
								Instant.ofEpochMilli(millis), 
								ZoneId.systemDefault());
	
						if(z!=null)
							photo.dateCreated=z;
					
					}
					catch(Exception ee)
					{
						
					}
				}
				
				
				photo.metadata=ImageUtil.GetMetadata(jobConfiguration.userId,
						jobConfiguration.uniqueId,
						photo.guid);
						
				
				photo.Geocode();
				photo.LoadByteArray();
				
				items.add(photo);
				
			} //end while
			
			Log.Info(logger, mn,"Items Read: " + items.size());
			
		}
		catch(Exception ex)
		{
			Log.Error(logger, mn,ex.toString());
			throw ex;
		}
		finally
		{
			DataUtil.getInstance().Close(rs);
		}
		
		
	}
	
	
	
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		String mn="LocalFileReader::hasNext";
		
		Log.Info(logger,mn, "Size: " + items.size() + "  " + currentIndex + "  " + (items.size() > (currentIndex+1)));
		
		return items.size() > (currentIndex+1);
	}

	@Override
	public GenericPhoto next() {
		// TODO Auto-generated method stub
		
		GenericPhoto p=null;
		String mn="LocalFileReader::next";
		
		Log.Info(logger,mn, "Start Size: " + items.size() + "  " + currentIndex );

		
		//if(currentIndex < 0)
		//	currentIndex=0;
		
		if(items.size()==0)
		{
			throw new NoSuchElementException("No elements in collection");
		}
		
		if((currentIndex+1) < items.size())
			currentIndex++;
		else
			throw new NoSuchElementException("No more elements remaining");
		
		p=items.get(currentIndex);
	
		super.GenerateDescription(p);
		
		Log.Info(logger,mn, "End Size: " + items.size() + "  " + currentIndex );

		
		return p;
	}

}
