package com.sga.common.oauth;

import java.util.Date;

//import java.time.LocalDateTime;

public class OauthToken {

	public int userId;
	public int serviceId;
	public String token;
	public String tokenResponse="";
	public String secret="";
	public String key="";
	public String refreshToken="";
	public Date expirationDateTime=null;
	
	
	public OauthToken()
	{
		//helps with serialization for scheduler
		userId=0;
		serviceId=0;
		token=null;
		tokenResponse=null;
		secret=null;
		key=null;
		refreshToken=null;
		expirationDateTime=null;
	}
	
	
	public OauthToken(int userId, int serviceId, String token, String tokenResponse, String secret, String key, String refreshToken, Date expirationDateTime) {
		super();
		this.userId = userId;
		this.serviceId = serviceId;
		this.token = token;
		this.tokenResponse = tokenResponse;
		this.secret = secret;
		this.key=key;
		this.refreshToken=refreshToken;
		this.expirationDateTime=expirationDateTime;
	}


	@Override
	public String toString() {
		return "OauthToken [userId=" + userId + ", serviceId=" + serviceId + ", token=" + token + ", tokenResponse="
				+ tokenResponse + ", secret=" + secret + ", key=" + (key==null ? "null" : key) + "]";
	}
	
	

	
}
