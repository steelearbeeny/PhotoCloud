package com.sga.common.dataaccess;

import com.sga.common.log.Log;
import com.zaxxer.hikari.HikariDataSource;

public class DataUtil {
	
	private static DataAccess dataAccess = null;
	
	
	public static DataAccess getInstance()
	{
		if(dataAccess==null)
		{
			
			try
			{
				/*
				dataAccess=new DataAccess("ORACLE",
	         		"192.168.127.128:1522",
	        		"orcl",
	        		"scan",
	        		"scan",
	        		"password");
	        		*/
				dataAccess=new DataAccess("POSTGRES",
		         		//"192.168.163.128",
						"localhost",
		        		"photocloud",
		        		"public",
		        		"postgres",
		        		"password");
				
				
			}
			catch(Exception ex)
			{
				Log.Info("DataUtil::getDataAccess", ex.toString());
				return null;
			}
		}
		
		return dataAccess;
	}
	
	public static HikariDataSource GetDataSource()
	{
		return getInstance().GetDataSource();
	}

}
