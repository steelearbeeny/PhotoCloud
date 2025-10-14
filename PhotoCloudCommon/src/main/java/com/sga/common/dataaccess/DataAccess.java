package com.sga.common.dataaccess;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
//import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import com.sga.common.log.Log;
import com.sga.common.util.JSONMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


public class DataAccess {

    private boolean isInitialized=false;
    //String url="jdbc:oracle:thin:@//localhost:1521/orclpdb.internal.cloudapp.net";
    //String user="proddta";
    //String password="password";
    String DBVendor;
    String host;
    String database;
    String schema;
    String user;
    String password;
    String url;
    Connection conn=null;
    
    
    //
    //Normally these are static
    //But need the ability to have multiple ppols to different
    //DBs at the same time
    //Ex oracle and postgres
    //
    private HikariConfig config = new HikariConfig();
    private  HikariDataSource ds;


    public DataAccess(String DBVendor, 
    		String host, 
    		String database, 
    		String schema, 
    		String user, 
    		String password) throws Exception
    {
    	
    	switch(DBVendor.toUpperCase())
    	{
    	
    		
    		
    		
    	case "POSTGRES":
    		url = String.format("jdbc:postgresql://%s:5432/%s",host,database);
    		break;
    		
  
    		
    		default:
    			throw new Exception("Unsupported DB vendor");
    	
    	}
    	
    	this.DBVendor=DBVendor.toUpperCase();
    	
    	this.host=host;
    	this.schema=schema;
    	this.user=user;
    	this.password=password;
    	this.database=database;
    	
    	
    }
    
    
    public DataAccess(HikariDataSource dataSource)
    {
    	ds=dataSource;
    	isInitialized=true;
    }
    
    public HikariDataSource GetDataSource()
    {
    	Initialize();
    	return ds;
    }

    private void Initialize()
    {
            if(isInitialized) return;
            
            //SID Name Syntax
            //url="jdbc:oracle:thin:@192.168.127.128:1522:orcl";
            //Service name syntax            
            //url="jdbc:oracle:thin:@//192.168.127.128:1522/orcl";
            
          
            try
            {
            	switch(DBVendor)
            	{
	
	            	
	            	case "POSTGRES":
	            		Class.forName("org.postgresql.Driver");
	            	break;
	            	
	
            		
            	}
          
            
            		java.util.Date current = new java.util.Date();
            		SimpleDateFormat dt1 = new SimpleDateFormat("yyyyMMddhhmmss");
            		String dts = dt1.format(current);
            		
            
            	   config.setJdbcUrl( url );
                   config.setUsername(user);
                   config.setPassword(password);
                   
                   
                   //config.setMaximumPoolSize(20);
                   config.setConnectionTimeout(300000); //time to get from pool
                   config.setIdleTimeout(120000);
                   config.setLeakDetectionThreshold(300000);
                   
                   config.setPoolName("photocloudpool" + dts);
                   config.setRegisterMbeans(true);
                   config.setMaximumPoolSize(40);
                   
                   config.addDataSourceProperty( "cachePrepStmts" , "true" );
                   config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
                   config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
                   
                   
                   ds = new HikariDataSource( config );
                   
                   
                   
            
            
            }
            catch(Exception e)
            {
                Log.Info("DataAccess::Initialize",e.toString());
                isInitialized=false;
                
                if(ds!=null)
                {
                	try
                	{
                		ds.close();
                	}
                	catch(Exception ex)
                	{
                		
                	}
                }
                
                ds=null;
                
                return;
            }
          

            isInitialized=true;


    }

    private Connection XXGetConnection() throws Exception
    {

        Initialize();
        String mn="DataAccess::GetConnection";
        String reason;
        
        if(conn==null)
        	reason="Y ";
        else
        	reason="N ";
        
        if(conn!=null && conn.isClosed())
        	reason+="Y ";
        else
        	reason+="N ";
        
        if(conn!=null && !conn.isValid(10))
        	reason+="Y ";
        else
        	reason+="N ";
        
        
                
        
        if(reason.contains("Y"))
        {
        	Log.Info(mn,"Re/Opening connection. Reason: " + reason);
        	conn=DriverManager.getConnection(  
            url,user,password);  
        }

            return conn;
    }
    
    private Connection XXXGetConnection() throws Exception
    {

        Initialize();
        String mn="DataAccess::GetConnection";
        
       
        	conn=DriverManager.getConnection(  
            url,user,password);  
       
            return conn;
    }

    
    public Connection GetConnection() throws Exception
    {
    	return ds.getConnection();
    }
    
    
    public CachedRowSet GetCachedRowSet(String SQLStr) throws Exception
    {

    	
    	Initialize();
    	
    	String mn="DataAccess::GetCachedRowSet";
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet rowSet = factory.createCachedRowSet();
        
        try
        {

        rowSet.setUrl(url);
            rowSet.setUsername(user);
            rowSet.setPassword(password);
            rowSet.setCommand(SQLStr);
  
            // Step 5: Executing an SQL query
            rowSet.execute();
            
            

        }
        catch(Exception e)
        {
           	Log.Info(mn, SQLStr);
            Log.Info("DataAccess::GetRowSet",e.toString());
            throw e;
        	
        }
            
            
            return rowSet;

    }


    public String GetRowSetAsJSON(String SQLStr) throws Exception
    {
        
        return GetRowSetAsJSON(SQLStr,(String)null);

    }
    
    public String GetRowSetAsJSON(String SQLStr, String ...params) throws Exception
    {
    	ResultSet rs;
    	String json;
    	
    	rs=GetRowSet(SQLStr,params);
     	
    	json=JSONMapper.toJSON(rs);
    	
    	Close(rs);
    	
    	return json;
    	
    }
    
    public String GetRowSetAsJSON(String SQLStr, List<String> params) throws Exception
    {
    	ResultSet rs;
    	String json;
    	
    	rs=GetRowSet(SQLStr,params);
     	
    	json=JSONMapper.toJSON(rs);
    	
    	Close(rs);
    	
    	return json;
    	
    }
    
    
    
    
    public ResultSet GetRowSet(String SQLStr) throws Exception
    {
        
        return GetRowSet(SQLStr,(String)null);

    }


    public long GetSingleLong(String SQLStr, String ...params) throws Exception
    {
    	
    	Initialize();
    	
    	ResultSet rs=null;
    	long rv=0;
    	
    	try {
    		rs=GetRowSet(SQLStr,params);
    	
	    	
	    	
	    	while(rs.next())
	    	{
	    		
	    		rv=rs.getLong(1);
	    		
	    	}
    	}
    	catch(Exception ex)
    	{
    		throw ex;
    	}
    	finally
    	{
    		Close(rs);
    	}
    	
    	return rv;
    	
    }
    
    

    public ResultSet GetRowSet(String SQLStr, String ... params) throws Exception
    {

    	Initialize();
    	
        String mn="DataAccess::GetRowSet";
        //RowSetFactory factory = RowSetProvider.newFactory();
        //JdbcRowSet rowSet = factory.createJdbcRowSet();

	        Connection conn = GetConnection();
	        PreparedStatement stmt = null;
	        
	        //
	        //Oracle only supports a fwd only result set
	        //So this would throw a error
	        //But postgres can do it
	        //
	        if(DBVendor==null || DBVendor.equals("POSTGRES"))
	        {
	        
		        stmt=conn.prepareStatement(SQLStr,
		        		ResultSet.TYPE_SCROLL_INSENSITIVE,
		        		Statement.NO_GENERATED_KEYS);
	        }
	        else
	        {
	        
		        stmt = conn.prepareStatement(
		        		SQLStr);
	        }
	        
	        ResultSet rs = null;
        
	       

            int i=1;

            if(
            		!(params==null || 
            		(params.length ==1 && params[0]==null))
              )
            {
	            for(i=1;i<=params.length;i++)
	            {
	            	stmt.setString(i,params[i-1]);
	            }
            }


            try 
            {

            	
            	rs=stmt.executeQuery();
            }
            catch(Exception e)
            {
            	Log.Info(mn, SQLStr);
                Log.Info("DataAccess::GetRowSet",e.toString());
                throw e;
            }
            return rs;

    }

    
    
    public CachedRowSet GetCachedRowSet(String SQLStr, String ... params) throws Exception
    {

    	
    	Initialize();
    	
    	String mn="DataAccess::GetCachedRowSet";
        RowSetFactory factory = RowSetProvider.newFactory();
        CachedRowSet rowSet = factory.createCachedRowSet();
        
        try
        {

        rowSet.setUrl(url);
            rowSet.setUsername(user);
            rowSet.setPassword(password);
            rowSet.setCommand(SQLStr);
  
                        
            
            
            int i=1;

            if(
            		!(params==null || 
            		(params.length ==1 && params[0]==null))
              )
            {
	            for(i=1;i<=params.length;i++)
	            {
	                rowSet.setString(i,params[i-1]);
	            }
            }


           

            	
            	rowSet.execute();
            }
            catch(Exception e)
            {
            	Log.Info(mn, SQLStr);
                Log.Info("DataAccess::GetRowSet",e.toString());
                throw e;
            }
            
           
            return rowSet;

            
            


    }

    
    
    
    
    public ResultSet GetRowSet(String SQLStr, List<String> params) throws Exception
    {

    	Initialize();
    	
        String mn="DataAccess::GetRowSet";
        
        Connection conn = GetConnection();
        PreparedStatement stmt = conn.prepareStatement(SQLStr,
        		ResultSet.TYPE_SCROLL_INSENSITIVE,
        		Statement.NO_GENERATED_KEYS);
        		
        ResultSet rs = null;
    
        

            int i=1;

            if(
            		!(params==null)
              )
            {
	            for(i=0;i<params.size();i++)
	            {
	                stmt.setString(i+1,params.get(i));
	            }
            }


            try 
            {

            	
            	rs=stmt.executeQuery();
            }
            catch(Exception e)
            {
            	Log.Info(mn, SQLStr);
                Log.Info("DataAccess::GetRowSet",e.toString());
                throw e;
            }
            return rs;

    }


    public int ExecuteUpdate(String SQLStr, Object ... params) throws Exception
    {

    	Initialize();
        Connection conn = GetConnection();
        String mn="DataAccess::ExecuteUpdate";
        
    	int rows=0;
       PreparedStatement p = conn.prepareStatement(SQLStr);

      
       int i=1;

       
       try 
       {
    	   
    	   
       if(
       		!(params==null || 
       		(params.length ==1 && params[0]==null))
         )       
       {
           for(i=1;i<=params.length;i++)
           {
               p.setObject(i,params[i-1]);
           }
       }
       
       
  

            	
            	rows=p.executeUpdate();
            }
            catch(Exception e)
            {
            	Log.Info(mn, SQLStr);
                Log.Info(mn,e.toString());
                throw e;
            }
            finally
            {
            	Close(p);
            }
            return rows;

    }

    
    
    public int ExecuteUpdate(Connection conn, String SQLStr, Object ... params) throws Exception
    {

    	Initialize();
        
        String mn="DataAccess::ExecuteUpdate";
        
    	int rows=0;
       PreparedStatement p = conn.prepareStatement(SQLStr);

      
       int i=1;

       
       try 
       {
    	   
    	   
       if(
       		!(params==null || 
       		(params.length ==1 && params[0]==null))
         )       
       {
           for(i=1;i<=params.length;i++)
           {
               p.setObject(i,params[i-1]);
           }
       }
       
       
  

            	
            	rows=p.executeUpdate();
            }
            catch(Exception e)
            {
            	Log.Info(mn, SQLStr);
                Log.Info(mn,e.toString());
                throw e;
            }
            finally
            {
            	//Close(p);
            	
            	if(p!=null)
            	{
            		try
            		{
            			p.close();
            		}
            		catch(Exception e)
            		{
            		}
            	}
            	
            }
            return rows;

    }

    
    
    
    
    public PreparedStatement PrepareStatement(String SQLStr) throws Exception
    {

    	Initialize();
        Connection conn = GetConnection();
        String mn="DataAccess::PrepareStatement";
        
    	int rows=0;
       PreparedStatement p = conn.prepareStatement(SQLStr);

      
      return p;
    }

    
    public void AddBatch(PreparedStatement p, Object ... params) throws Exception
    {

    	
      
       int i=1;
       //p.clearParameters();
       int rows;
       String mn="DataAccess::AddBatch";
       
       try 
       {
    	   
    	   
       if(
       		!(params==null || 
       		(params.length ==1 && params[0]==null))
         )       
       {
           for(i=1;i<=params.length;i++)
           {
               p.setObject(i,params[i-1]);
           }
       }
       
       
  

            	
            	p.addBatch();
            }
            catch(Exception e)
            {
            	Log.Info(mn, p.toString());
                Log.Info(mn,e.toString());
                throw e;
            }
            finally
            {
            	//Close();
            }
            //return rows;

    }


    
    public int[] ExecuteUpdate(PreparedStatement p) throws Exception
    {

    	
      
       int i=1;
       //p.clearParameters();
       int rows[];
       String mn="DataAccess::ExecuteUpdate";
       
       try 
       {
    	   
    	   
      
       
       
  

            	
            	rows=p.executeBatch();
            }
            catch(Exception e)
            {
            	Log.Info(mn, p.toString());
                Log.Info(mn,e.toString());
                throw e;
            }
            finally
            {
            	//Close();
            }
            return rows;

    }

    
    
    
   public void Close(Connection conn)
    {
    	if(conn!=null)
    	{
    		try
    		{
    			conn.close();
    			
    		}
    		catch(Exception e)
    		{
    			
    		}
    	}
    	
    	conn=null;
    }

    public void CloseX(RowSet rs)
    {
        if(rs==null) return;

            Statement s=null;
            Connection c=null;

        try {
            s = rs.getStatement();
            
            rs.close();
            
        }
        catch(Exception e)
        {

        }

        try {
        if(s!=null)
        {
            c=s.getConnection();
            s.close();
            
        }
    }
    catch(Exception e)
    {

    }

        try {
            if(c!=null)
                c.close();
        }
        catch(Exception e)
        {

        }


        

    }
    
    
    public void Close(RowSet rs)
    {
    	
    	int x;
        if(rs==null) return;

            Statement s=null;
            Connection c=null;

            
            
            
            
            
        try {
            s = rs.getStatement();
            
            	c=s.getConnection();
            
            c.close();
            s.close();
            
            
            rs.close();
            
        }
        catch(Exception e)
        {
        		x=0;
        }

        
        /*
        try {
        if(s!=null)
        {
            c=s.getConnection();
            s.close();
            
        }
        
    }
    catch(Exception e)
    {

    }
    */
    
/*
        try {
            if(c!=null)
                c.close();
        }
        catch(Exception e)
        {

        }
*/
	
        

    }
    
    
    public void Close(ResultSet rs)
    {
    	
    	int x;
        if(rs==null) return;

            Statement s=null;
            Connection c=null;

            
            
            
            
            
        try {
            s = rs.getStatement();
            
            	c=s.getConnection();
            
            c.close();
            s.close();
            
            
            rs.close();
            
        }
        catch(Exception e)
        {
        		x=0;
        }

        
       
	
        

    }
    
    public void XXCloseRS(ResultSet rs)
    {
    	
    	int x;
        if(rs==null) return;

            Statement s=null;
            

            
            
            
            
            
        try {
            s = rs.getStatement();
            
            	
            
           
            s.close();
            
            
            rs.close();
            
        }
        catch(Exception e)
        {
        		x=0;
        }

        
       
	
        

    }
    
    
    
    public void Close(PreparedStatement s)
    {
        if(s==null) return;

          
            Connection c=null;

      

        try {
       
	            c=s.getConnection();
	            s.close();
            
        
		    }
		    catch(Exception e)
		    {
		
		    }

        try {
            if(c!=null)
                c.close();
        }
        catch(Exception e)
        {

        }


        

    }


}
