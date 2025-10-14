package com.sga.photocloud.google;

import java.io.IOException;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow.CredentialCreatedListener;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.sga.common.log.Log;

public class TokenListener  implements CredentialRefreshListener, CredentialCreatedListener {

	@Override
	public void onTokenResponse(Credential credential, TokenResponse tokenResponse) throws IOException {
		// TODO Auto-generated method stub
		String mn="RefreshListener::onTokenResponse";
		Log.Info(mn, "Method Entered " + credential.toString() + " -- " + tokenResponse.toPrettyString());
		
	}

	@Override
	public void onTokenErrorResponse(Credential credential, TokenErrorResponse tokenErrorResponse) throws IOException {
		// TODO Auto-generated method stub
		String mn="RefreshListener::onTokenErrorResponse";
		Log.Info(mn, "Method Entered " + credential.toString() + " -- " + tokenErrorResponse.toPrettyString());
	
	}

	@Override
	public void onCredentialCreated(Credential credential, TokenResponse tokenResponse) throws IOException {
		// TODO Auto-generated method stub
		String mn="RefreshListener::onCredentialCreated";
		Log.Info(mn, "Method Entered " + credential.toString() + " -- " + tokenResponse.toPrettyString());

		
	}

}
