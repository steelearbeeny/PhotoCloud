package com.sga.common.generic;

import java.sql.ResultSet;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.log.Log;
import com.sga.common.util.Utils;

public class Service {
	
	
	private Service()
	{
		
	}
	
	public static String GetServiceDescription(int service)
	{
		String SQLStr;
		ResultSet rs=null;
		String mn="Service::GetServiceDescription";
		String rv="";
		
		
		SQLStr="SELECT DESCRIPTION FROM SERVICE WHERE SERVICEID=?::INT";
		
		try
		{
			rs=DataUtil.getInstance().GetRowSet(SQLStr,Utils.ToString(service));
		
			if(rs.next())
			{
				rv=Utils.ToString(rs.getString("DESCRIPTION"));
			}
		
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			rv="";
			
		}
		finally
		{
			DataUtil.getInstance().Close(rs);
		}
		
		return rv;
		
		
	}

}
