package com.sga.common.log;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jobrunr.jobs.context.JobDashboardLogger;

import java.net.http.HttpHeaders;
import java.time.LocalDateTime;    

public class Log {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  

    public static String Info(String method, String message)
    {
    	
        return Write(method + " - " + message);
        
    }
    
    public static String Info(JobDashboardLogger logger, String method, String message)
    {
    	if(logger!=null)
    		logger.info(method + " - " + message);
    	
    	return Write(method + " - " + message);
    	
    }
    
    public static String Info(String method, String message, Object... args)
    {
        String msg=method + " - " + String.format(message, args);
        return Write(msg);
    }

    
    public static void Error(String method, Exception ex)
    {
        Write(method + " - Exception: " + ex.toString());
    }
    
    public static String Error(String method, String message)
    {
    	
        return Write(method + " - " + message);
        
    }
    
    public static String Error(JobDashboardLogger logger, String method, String message)
    {
    	if(logger!=null)
    		logger.error(method + " - " + message);
    	
    	return Write(method + " - " + message);
    	
    }
    
    
    
    public static void Info(String method, String message, Collection coll)
    {
    	
    	if(coll==null)
    	{
    		Write(method + " - " + message + " - null collection");
    		return;
    	}
    	
    	if(coll.size() > 1)
    	{
    		Write(method + " - " + message + " - empty collection");
    		return;
    	}
    	
    	
    	Write(method + " - " + message);
    	Iterator it = coll.iterator();
    	while(it.hasNext())
    	{
    		Write(method + " - " + it.next().toString());
    	}
    	
    	
    }
    
    
    public static void Info(String method, HttpHeaders headers)
    {
    	
    	if(headers==null)
    	{
    		Write(method + " - null headers");
    		return;
    	}
    	
    	if(headers.map().size()==0)
    	{
    		Write(method + " - empty headers");
    		return;
    	}
    	
    	
    	Map<String,List<String>> h = headers.map();
		List<String> v;
		
		for(String k : h.keySet())
		{
			Write(method + " - Header: " + k);
			
			v=h.get(k);
			
			for(String hv : v)
			{
				Write(method + " - " + hv);
			}
			
		}
    	
    }
    
    
    
    
    private static String Write(String out)
    {
        
        LocalDateTime now = LocalDateTime.now();  
        String m = dtf.format(now) + " - " + out;
        System.out.println(m);
        return m;
    }
    
    
    
}
