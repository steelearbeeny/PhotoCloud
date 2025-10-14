package com.sga.photocloud;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import com.azure.identity.AuthorizationCodeCredential;
import com.azure.identity.AuthorizationCodeCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.microsoft.graph.core.authentication.AzureIdentityAuthenticationProvider;
import com.microsoft.graph.models.Application;
import com.microsoft.graph.models.ApplicationCollectionResponse;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.models.SharedDriveItem;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;


public class GraphTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
		
		//Under portal appregistrations
		
		String clientId;
		String tenantId;
		String clientSecret;
		String [] scopes;
		String redirectUrl;
		String authCode;
		
		//From app registrations
		//this is the steelearbeeny.msn azure
		clientId = "3882d0f9-543e-4fc9-8501-64bcb68ab6b5";
		tenantId = "7ad124c2-aa3d-4ebb-b69c-4acdbd856446";
		clientSecret="I_G8Q~Yy7bpkxNluUq.g6TFkeGyNJhbbn4iWBbm7";
		
		//Photoliberator SPO
		tenantId="f3d9e69e-96e1-44e2-bb93-9ff3b278361a";				//photoliberator tenant with SPO
		clientId="50278812-f098-446b-8395-57c235aa2cdd";
		

		
		//CLIENT CREDENTIAL PROVIDER
		//This authenticates at the app level. No user required.
		//requires SPO license to read onedrive share
		//
		// The client credentials flow requires that you request the
		// /.default scope, and pre-configure your permissions on the
		// app registration in Azure. An administrator must grant consent
		// to those permissions beforehand.
		
		/*
		scopes = new String[] { "https://graph.microsoft.com/.default" };

		final ClientSecretCredential credential = new ClientSecretCredentialBuilder()
		    .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret).build();

		*/
		
		//User and password authentication
		//needed 2 things for this to work
		//1. Create AD (entra) user to log in as and password
		//2. Set app to allow user logins App -> Authentication -> Allow public client flows
		//3. Add API permission delegated. App -> API Permissions then also add admin approval
		
		//msn settings
		String userName = "photoliberator@SteeleArbeenymsn.onmicrosoft.com";
		String password = "DIGital1camera";
		
		//Photoliberator SPO settings
		userName="steelearbeeny@photoliberator.onmicrosoft.com";
		password="DIgital1camera";
		
		scopes = new String[] {"User.Read","Application.Read.All","Files.Read.All","Sites.Read.All"};

		final UsernamePasswordCredential credential = new UsernamePasswordCredentialBuilder()
		    .clientId(clientId).tenantId(tenantId).username(userName).password(password)
		    .build();

		
		//Web interactive authentication...AKA Auth Code Flow
		//1. Surf to application URL: 
		// https://login.microsoftonline.com/7ad124c2-aa3d-4ebb-b69c-4acdbd856446/oauth2/v2.0/authorize?client_id=3882d0f9-543e-4fc9-8501-64bcb68ab6b5&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8000&scope=https%3A%2F%2Fgraph.microsoft.com%2FApplication.Read.All&state=12345&sso_reload=true
		//see https://learn.microsoft.com/en-us/entra/identity-platform/v2-oauth2-auth-code-flow
		//
		//Youll get back the auth code at the redirect URI in the original URL in 1.
		//To setup that redirect url in azure portal
			//Go to app registration
			//Authentication -? Add Platform ->Web 
			//Add URL. Must be exact match with the one in the auth URL
			//Make sure 2 other check boxes are unchecked.
		
		//The code is returned in the "URI" header redirect to your URL
		//Remove code= at beginning and all other parms at end
		//Paste it in the authCode variable below
		//Should live 10 minutes (?)
		
		
		/*
		scopes = new String[] {"User.Read","Application.Read.All","Files.ReadWrite.All","Sites.ReadWrite.All"};

		redirectUrl="http://localhost:8000";
		

		authCode="0.Ab0AwiTRej2qu062nErNvYVkRvnQgjg-VMlPhQFkvLaKtrUDAQA.AgABBAIAAAApTwJmzXqdR4BN2miheQMYAgDs_wUA9P9Ds0ryRxOvh8Z-DZlLdAMi10O1Eyw1_grcqBPmeUUtP9PkWMRoE-Hc-y9obScfVtzz2I9KZM7GIMsmbpwh2KyFGvPh3g9ydkEWPEz5aCWpaR6lV6x0SJfonziWSQlTCjiOBGJqrP0ehMDPPkBCIQrkU4b-hkhUP-UoxSWr7vGZ4Vcbo_Pz9asq1Gs2T9GbhiPU0bvXaSmOunz07NGzsLAN25KoYRechq_YMmScHuKzs6W84Oh0gEfJRLxueyRcVotaNo60iGIQipVkO-Rp_wNHjO2oPVA00gesVPU4z-_NLmarfFHlCpAQam6FlDNbx3LB9SpV3Z2MC1g5KFd1GYYAS6lkCUjrPZx8eRVlPb7Grn8a6iYqYGMKKUlj4i-PXHisORSARugvR_LBye1bWjSTkyZgSI-BOub9SsFAgDvvxzypY_drYjxO4rXUBawVM3C5SRluzsISocHEYJaJuZrlDJ2kCnhqx7umSYfpO98WK_xk5YdWrRCHx7KbE9zu5NQ8hijr3ZYEFqbRHy6EIUJlcT0qAycEfy5OZZidqoFODAtIFn73o5ZFHdUFVuVuWlqfHDVYrYr0hOGJahkzDEhlPNABaSFjFA_XqbkjddFS0CZtSbu8xX9gc66_EvrkH_f6dO_o-_o2kdUuf4_fQDCEgLb_ghn3YH_JJcDknURXyGIT1gEFgtPqDKLAbwzk9YAjPfII-RuP8Zcr_PWM6ihpLxfVvfEJc2OyNredk-qO7DqdhyNy-EPihZoGECuyN3YMTmxwvNRaRaplIma06yNXGHY27gZ8-HjFvMu-5WEG5Qo-yvTWDp_1bNmmOJH7s4Nrq2w0Vau6cBeDkJGOkdu9WgoExHFZ-DHa37gQPHuh8JurIlGWmWyUowOlpzMFRVPR3QlwzQFri-BXspGg6W9HNLJh3LwqCG03A6SwcleR_7OYU8_WzWnkxU8l0T9rAMGmRMmEnIlsimlc_A2IUcbM0iFTDYy6yIbG3wci4g9t_n2hl9d4Z9-U5-mj0bUR1j7bIEaoFAA7Bqgw3pQ07qg42WlUfQj2dV_A8hjjB-CQsDkuKGxDcyFPDCW227-hio5ZwYa8xF4QhEKeq5Pp-R-NhDhZUV6c4Up8b45sjstbXCagU7B7cXD_99K18GynOkDvzJWd970bEVwvJlEVyrHkKb8Lamn7OcOVbWrbLFiwha_wGISekxc";
		
		AuthorizationCodeCredential credential = new AuthorizationCodeCredentialBuilder()
			    .clientId(clientId).tenantId(tenantId).clientSecret(clientSecret)
			    .authorizationCode(authCode).redirectUrl(redirectUrl).build();
		
		*/
		
		
		if (null == scopes || null == credential) {
		    System.out.println("Invalid scope or credential");
		    return;
		}

		
		final GraphServiceClient graphClient = new GraphServiceClient(credential, scopes);
		
		
		//msn onedrive audrey photos
		String originalUrl = "https://1drv.ms/f/c/1ac905b2db104fa9/ElyGcsdSE3hFuBE_uOlq1mkB9XU7DtnJvz2Ou3JQ2-W6xQ";
		
		//MSNTestFolder
		//RO
		originalUrl="https://1drv.ms/f/c/1ac905b2db104fa9/Es1mSULTYClEhvd-CZTH5H0BxlGhGGTW8Zv_EOLAIAmGig?e=AAldQh";
		//RW
		//https://1drv.ms/f/c/1ac905b2db104fa9/Es1mSULTYClEhvd-CZTH5H0Bg7bG3XIvSBWZN6pxj4wB1w?e=RkvAdQ
		
		//SP
		//originalUrl = "https://photoliberator.sharepoint.com/:f:/g/EqSlPwKdIt5Dom833kvJdhMBKPomy7aLslbDOfGbZUllig?e=WfFFGh";
		
		//OD
		//originalUrl="https://photoliberator-my.sharepoint.com/:f:/g/personal/steelearbeeny_photoliberator_onmicrosoft_com/EjThzC1ayfJHqetQZSZsRo0B0cDbNCZbKUiUeL7sHzs0MA?e=ceGs2V";
		
		String baseUrl = Base64.getUrlEncoder().encodeToString(originalUrl.getBytes());
		
		String encodedUrl = "u!" + baseUrl.replaceAll("=+$","").replace('/','_').replace('+','-');
		
		ApplicationCollectionResponse result = graphClient.applications().get();
		
		List<Application> apps = result.getValue();
		
		
		for(Application a : apps)
		{
			System.out.println(a.getId());
			System.out.println(a.getDisplayName());
		}
		
		
		SharedDriveItem sdi = graphClient.shares().bySharedDriveItemId(encodedUrl).get();
		
		System.out.println("Desc " + sdi.getDescription());
		System.out.println("Owner " + sdi.getOwner());
		System.out.println("Name " + sdi.getName());
		System.out.println("WebURL " + sdi.getWebUrl());
		
		
		
		int i;
		i=0;
		
	}

}
