package com.sga.photocloud.google;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.AbstractMemoryDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.auth.oauth2.GoogleCredentials;
import com.sga.common.log.Log;
import com.sga.common.oauth.OauthToken;
import com.sga.common.oauth.OauthUtil;
import com.sga.common.util.Utils;

public class DbDataStoreFactory extends AbstractDataStoreFactory {

	@Override
	protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
		// TODO Auto-generated method stub
		//incoming ID should be the type o the data store - ie: StoredCredential
		String mn="DbDataStoreFactory::DbDataStoreFactory";
		Log.Info(mn, "In constructor " + id + " ERROR No service Id");
		throw new IOException("Cannot create datastore without service Id");
		
		//return new DbDataStore<V>(this,id);
	}
	
	protected <V extends Serializable> DataStore<StoredCredential> createDataStore(String id, int serviceId) throws IOException {
		// TODO Auto-generated method stub
		//incoming ID should be the type o the data store - ie: StoredCredential
		String mn="DbDataStoreFactory::DbDataStoreFactory";
		Log.Info(mn, "In constructor " + id + " " + serviceId);

		return new DbDataStore<StoredCredential>(this,id,serviceId);
		
		
	}
	
	
	  /** Returns a global thread-safe instance. */
	  public static DbDataStoreFactory getDefaultInstance() {
	    return InstanceHolder.INSTANCE;
	  }

	  /** Holder for the result of {@link #getDefaultInstance()}. */
	  static class InstanceHolder {
	    static final DbDataStoreFactory INSTANCE = new DbDataStoreFactory();
	  }
	
	  static class DbDataStore<V extends Serializable> extends AbstractDataStore<V> 
	  {

		public int serviceId=-1;
		  
		protected DbDataStore(DataStoreFactory dataStoreFactory, String id) throws Exception {
			super(dataStoreFactory, id);
			
			String mn="DbDataStore::DbDataStore";
			Log.Info(mn, "In constructor " + id);
			
			throw new Exception("Cannot create DbDataStore without service Id");
			
			// TODO Auto-generated constructor stub
		}
		
		protected DbDataStore(DataStoreFactory dataStoreFactory, String id, int serviceId) {
			super(dataStoreFactory, id);
			
			this.serviceId=serviceId;
			String mn="DbDataStore::DbDataStore";
			Log.Info(mn, "In constructor " + id + " " + serviceId);
			
			// TODO Auto-generated constructor stub
		}

		@Override
		public Set<String> keySet() throws IOException {
			// TODO Auto-generated method stub
			String mn="DbDataStore::keySet";
			Log.Info(mn, "In method");
			return null;
		}

		@Override
		public Collection<V> values() throws IOException {
			// TODO Auto-generated method stub
			String mn="DbDataStore::values";
			Log.Info(mn, "In method");
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public V get(String key) throws IOException {
			// TODO Auto-generated method stub
			String mn="DbDataStore::get";
			Log.Info(mn, "In method " + key);
			
			OauthToken token = OauthUtil.GetTokenForUserAndService(Utils.ToInt(key), serviceId);
			
			if(token==null)
			{
				Log.Info(mn, "Token not found");
				return null;
			}
			
			if(token.tokenResponse==null || token.tokenResponse.length() < 1)
			{
				Log.Error(mn, "Invalid token");
				return null;
			}			
			StoredCredential sc= new StoredCredential();
			sc.setAccessToken(token.tokenResponse);
			sc.setRefreshToken(token.refreshToken);
			//TODO setExpiratonTime
			if(token.expirationDateTime==null || token.expirationDateTime.equals(Utils.MIN_DATE))
				sc.setExpirationTimeMilliseconds(null);
			else
			{
				//sc.setExpirationTimeMilliseconds(1000*token.expirationDateTime.toEpochSecond(
				//		ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())));
				sc.setExpirationTimeMilliseconds(token.expirationDateTime.getTime());
			
			}
			
			
			
			
			
			Log.Info(mn, "Returning stored credential " + sc.toString());
			
			
			return (V)sc;
		}

		@Override
		public DataStore<V> set(String key, V value) throws IOException {
			// TODO Auto-generated method stub
			String mn="DbDataStore::set";
			String tokenKey;
			
			
			Log.Info(mn, "In method " + key + " " + value);
			
			StoredCredential sc = (StoredCredential)value;
			
			OauthToken token = OauthUtil.GetTokenForUserAndService(Utils.ToInt(key),serviceId);
			
			if(token==null)
			{
				Log.Info(mn, "Token Not Found");
				return null;
			}
			
			Log.Info(mn, "Token Key " + token.key);
			
			tokenKey=token.key;
			
			Date ldt = null;
			String expTime=null;
			
			if(sc.getExpirationTimeMilliseconds()!=null)
			{
				
				//This is the time since Jan 1 1970
				//ldt=Utils.ToLocalDateTimeFromMilliseconds(sc.getExpirationTimeMilliseconds());
				ldt = new Date(sc.getExpirationTimeMilliseconds());
				
				
				expTime = OauthUtil.formatter.format(ldt);
			
				Log.Info(mn, "Expires " + expTime);
			}
			
			
			OauthUtil.InsertTokenResponse(Utils.ToInt(key), 
					serviceId, 
					tokenKey, 
					sc.getAccessToken(), 
					sc.getRefreshToken(),
					expTime,
					null);
			
			
			return null;
		}

		@Override
		public DataStore<V> clear() throws IOException {
			// TODO Auto-generated method stub
			String mn="DbDataStore::clear";
			Log.Info(mn, "In method");
			return null;
		}

		@Override
		public DataStore<V> delete(String key) throws IOException {
			// TODO Auto-generated method stub
			String mn="DbDataStore::delete";
			Log.Info(mn, "In method");
			return null;
		}
		
		
		
	  }

}
