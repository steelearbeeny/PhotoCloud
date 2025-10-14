package com.sga.common.oauth;

import java.sql.ResultSet;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sga.common.dataaccess.DataUtil;
import com.sga.common.log.Log;
import com.sga.common.util.Utils;

public class OauthUtil {

	//public static final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void InsertTokenRequest(int user, int service, String key, String token, String secret)
	{
		String SQLStr;
		String mn="OauthUtil::InsertTokenRequest";
		long count=0;
		int rc=0;
		
		
		Log.Info(mn, "Method Entered");
		
		SQLStr="SELECT COUNT(*) FROM OAUTHREQUEST WHERE USERID=?::INT4 AND SERVICEID=?::INT4";
		
		try
		{
			count=DataUtil.getInstance().GetSingleLong(SQLStr,Utils.ToString(user),Utils.ToString(service));
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			
		}
		
		try
		{
		
			if(count < 1)
			{
				
				Log.Info(mn, "Inserting Token Request " + key + " " + token);
			
			SQLStr="INSERT INTO oauthrequest "
	        		+ "(userid, serviceid, key, token, secret, senttime) "
	        		+ "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
			
			 rc=DataUtil.getInstance().ExecuteUpdate(SQLStr,
		        		user,
		        		service,
		        		key,
		        		token,
		        		secret
		        		);
			}
			else
			{
				//
				// Dont overwrite the refresh token
				//
				Log.Info(mn, "Updating Token Request " + key + " " + token);
				
				SQLStr="UPDATE OAUTHREQUEST "
						+ "SET TOKEN=?, "
						+ "SECRET=?, "
						+ "KEY=?, "
						+ "SENTTIME=CURRENT_TIMESTAMP, "
						+ "TOKENRESPONSE=NULL, "
						+ "RESPONSETIME=NULL, "
						+ "EXPIRATIONTIME=NULL, "
						+ "MESSAGE=NULL "
						+ "WHERE USERID=? AND "
						+ "SERVICEID=?";
				
				 rc=DataUtil.getInstance().ExecuteUpdate(SQLStr,
						 	token,
						 	secret,
						 	key,
			        		user,
			        		service
			        		);
			}
	        
			Log.Info(mn, "Record Count: " + rc);
	       
        
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			
		}
		
		
		
	}
	
	
	public static void InsertTokenResponse(int user, int service, String key, String tokenResponse, String refreshToken, String expirationDate,  String message)
	{
		String SQLStr;
		String mn="OauthUtil::InsertTokenResponse";
		
		try
		{
		
		if(refreshToken==null)
		{
			SQLStr="UPDATE OAUTHREQUEST SET TOKENRESPONSE=?, "
					+ "RESPONSETIME=CURRENT_TIMESTAMP, "
					+ "EXPIRATIONTIME=?::TIMESTAMP, "
					+ "MESSAGE=? "
					+ "WHERE USERID=? AND "
					+ "SERVICEID=? AND "
					+ "KEY=?";
			
			DataUtil.getInstance().ExecuteUpdate(SQLStr,tokenResponse,expirationDate,message,user,service,key);

	
		}
		else
		{
			SQLStr="UPDATE OAUTHREQUEST SET TOKENRESPONSE=?, "
					+ "RESPONSETIME=CURRENT_TIMESTAMP, "
					+ "EXPIRATIONTIME=?::TIMESTAMP, "
					+ "REFRESHTOKEN=?, "
					+ "MESSAGE=? "
					+ "WHERE USERID=? AND "
					+ "SERVICEID=? AND "
					+ "KEY=?";
			
			DataUtil.getInstance().ExecuteUpdate(SQLStr,tokenResponse,expirationDate,refreshToken,message,user,service,key);

		}
		
	
        
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			
		}
		
		
		
	}
	


	public static void InsertTokenResponse(int service, String key, String tokenResponse, String refreshToken, String expirationDate,  String message)
	{
		String SQLStr;
		String mn="OauthUtil::InsertTokenResponse";
		
		
		
		SQLStr="UPDATE OAUTHREQUEST SET TOKENRESPONSE=?, "
				+ "RESPONSETIME=CURRENT_TIMESTAMP, "
				+ "EXPIRATIONTIME=?::TIMESTAMP, "
				+ "REFRESHTOKEN=?, "
				+ "MESSAGE=? "
				+ "WHERE SERVICEID=? AND "
				+ "KEY=?";
	
        
		try
		{
			DataUtil.getInstance().ExecuteUpdate(SQLStr,
					tokenResponse,
					expirationDate,
					refreshToken,
					message,
					service,
					key);

        
		}
		catch(Exception ex)
		{
			Log.Error(mn, ex);
			
		}
		
		
		
	}
	
	public static void DeleteToken(int userId, int serviceId)
	{
		String SQLStr;
		
		
		String mn="OauthToken::DeleteToken";

		
		SQLStr="DELETE FROM OAUTHREQUEST "
				+ "WHERE USERID=?::INT4 AND "
				+ "SERVICEID=?::INT4 ";
		
		
		
		try
		{
			 DataUtil.getInstance().ExecuteUpdate(SQLStr,
					Utils.ToString(userId),
					Utils.ToString(serviceId)
					);
		
		
			
		
		}
		catch(Exception ex)
		{
			Log.Error(mn,"Exception: " + ex.toString());
			
		}
		

		
	}
	
	public static OauthToken GetTokenForUserAndService(int userId, int serviceId)
	{

		String SQLStr;
		ResultSet rs=null; 
		String tokenResponse="";
		String secret="";
		String token="";
		String mn="OauthToken::GetTokenForUserAndService";
		String key;
		String refreshToken;
		Date local=null;
		
		SQLStr="SELECT * FROM OAUTHREQUEST "
				+ "WHERE USERID=?::INT4 AND "
				+ "SERVICEID=?::INT4 AND "
				+ "TOKEN IS NOT NULL AND "
				+ "SECRET IS NOT NULL AND "
				+ "TOKENRESPONSE IS NOT NULL AND "
				+ "RESPONSETIME="
				+ 	"(SELECT MAX(RESPONSETIME) "
				+ 	"FROM OAUTHREQUEST "
				+ 	"WHERE USERID=?::INT4 AND "
				+   "SERVICEID=?::INT4 AND "
				+ 	"TOKEN IS NOT NULL AND "
				+ 	"SECRET IS NOT NULL AND "
				+ 	"TOKENRESPONSE IS NOT NULL)";
		
		SQLStr="SELECT * FROM OAUTHREQUEST "
				+ "WHERE USERID=?::INT4 AND "
				+ "SERVICEID=?::INT4";
		
		
		try
		{
			rs = DataUtil.getInstance().GetRowSet(SQLStr,
					Utils.ToString(userId),
					Utils.ToString(serviceId)
					);
		
		
			if(rs.next())
			{
				token=Utils.ToString(rs.getString("token"));
				tokenResponse=Utils.ToString(rs.getString("tokenresponse"));	 //verifier
				secret=Utils.ToString(rs.getString("secret"));
				key=Utils.ToString(rs.getString("key"));
				local=Utils.TryParseDate(rs.getString("expirationtime"));
				
				
				refreshToken=rs.getString("refreshToken"); //null if not prosent
				Log.Info(mn, "Found token " + token);

				return new OauthToken(userId,serviceId,token,tokenResponse,secret,key,refreshToken,local);
			}
			else
			{
				Log.Info(mn, "Not Found");
				return null;
			}
		
		}
		catch(Exception ex)
		{
			Log.Error(mn,"Exception: " + ex.toString());
			return null;
		}
		finally
		{
			DataUtil.getInstance().Close(rs);		
		}

		
		
	} //end method
	
	
} //end class
