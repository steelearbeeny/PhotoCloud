package com.sga.common.util;


import java.math.BigDecimal;
import org.json.JSONObject;

import com.sga.common.log.Log;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.Date;

import javax.sql.RowSet;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
//import org.springframework.jdbc.support.JdbcUtils;

public class JSONMapper
{
	
	public static String toJSON(RowSet rs) throws JSONException, SQLException
	{
		String rv;
		
		rv=mapResultSet(rs).toString();
		
		return rv;
	}
	
	public static String toJSON(ResultSet rs) throws JSONException, SQLException
	{
		String rv;
		
		rv=mapResultSet(rs).toString();
		
		return rv;
	}
	
	
	public static JSONArray mapResultSet(RowSet rs) throws SQLException, JSONException
	{ 
        JSONArray jArray = new JSONArray();
        JSONObject jsonObject = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        
        if (!rs.isBeforeFirst()) {
            rs.beforeFirst();
        }
        
        
        
        while(rs.next())
        {
	        jsonObject = new JSONObject();
	        for (int index = 1; index <= columnCount; index++) 
	        {
	        	
	        	
	            String column = rsmd.getColumnName(index);
	            Object value = rs.getObject(column);
	            
            
	            
	            if (value == null) 
	            {
	                jsonObject.put(column, "");
	            } else if (value instanceof Integer) {
	                jsonObject.put(column, (Integer) value);
	            } else if (value instanceof String) {
	                jsonObject.put(column, (String) value);                
	            } else if (value instanceof Boolean) {
	                jsonObject.put(column, (Boolean) value);           
	            } else if (value instanceof Date) {
	                jsonObject.put(column, ((Date) value).getTime());                
	            } else if (value instanceof Long) {
	                jsonObject.put(column, (Long) value);                
	            } else if (value instanceof Double) {
	                jsonObject.put(column, (Double) value);                
	            } else if (value instanceof Float) {
	                jsonObject.put(column, (Float) value);                
	            } else if (value instanceof BigDecimal) {
	                jsonObject.put(column, (BigDecimal) value);
	            } else if (value instanceof Byte) {
	                jsonObject.put(column, (Byte) value);
	           // } else if (value instanceof oracle.sql.TIMESTAMPTZ) {
	           //     jsonObject.put(column, ((oracle.sql.TIMESTAMPTZ) value).toZonedDateTime());
	            } else if (value instanceof byte[]) {
	                jsonObject.put(column, (byte[]) value);                
	            } else {
	                throw new IllegalArgumentException("Unmappable object type: " + value.getClass());
	            }
        	}
        	jArray.put(jsonObject);
        }
        
        return jArray;
    }
	
	
	
	public static JSONArray mapResultSet(ResultSet rs) throws SQLException, JSONException
	{ 
        JSONArray jArray = new JSONArray();
        JSONObject jsonObject = null;
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        
        if (!rs.isBeforeFirst()) {
            rs.beforeFirst();
        }
        
        
        
        while(rs.next())
        {
	        jsonObject = new JSONObject();
	        for (int index = 1; index <= columnCount; index++) 
	        {
	        	
	        	
	            String column = rsmd.getColumnName(index);
	            Object value = rs.getObject(column);
	            
	            //if(value!=null)
	            //Log.Info("JSN2", column + " " + value.getClass().getName());
	            //else
	            //	Log.Info("JSN2", column + " " + null);
	            
	            if (value == null) 
	            {
	                jsonObject.put(column, "");
	            } else if (value instanceof Integer) {
	                jsonObject.put(column, (Integer) value);
	            } else if (value instanceof String) {
	                jsonObject.put(column, (String) value);                
	            } else if (value instanceof Boolean) {
	                jsonObject.put(column, (Boolean) value);           
	            } else if (value instanceof Timestamp) { 
	            	jsonObject.put(column, ((Timestamp)value).toLocalDateTime()); //this is before date becayse instance of will also recognize subclasses
	            } else if (value instanceof Date) {
	                jsonObject.put(column, ((Date) value).getTime());                
	            } else if (value instanceof Long) {
	                jsonObject.put(column, (Long) value);                
	            } else if (value instanceof Double) {
	                jsonObject.put(column, (Double) value);                
	            } else if (value instanceof Float) {
	                jsonObject.put(column, (Float) value);                
	            } else if (value instanceof BigDecimal) {
	                jsonObject.put(column, (BigDecimal) value);
	            } else if (value instanceof Byte) {
	                jsonObject.put(column, (Byte) value);
	           // } else if (value instanceof oracle.sql.TIMESTAMPTZ) {
	           //     jsonObject.put(column, ((oracle.sql.TIMESTAMPTZ) value).toZonedDateTime());
	            } else if (value instanceof byte[]) {
	                jsonObject.put(column, (byte[]) value);                
	            } else {
	                throw new IllegalArgumentException("Unmappable object type: " + value.getClass());
	            }
        	}
        	jArray.put(jsonObject);
        }
        
        return jArray;
    }
	
}