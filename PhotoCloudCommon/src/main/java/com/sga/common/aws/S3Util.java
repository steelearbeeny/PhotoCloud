package com.sga.common.aws;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.sga.common.generic.Response;
import com.sga.common.log.Log;
import com.sga.common.util.Utils;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.GetUserResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

/*
//DONT NEED THIS...outside aws, auth is in credentials file see below
//in AWS auth is different TODO
//
BasicSessionCredentials awsCreds = new BasicSessionCredentials("access_key_id", "secret_key_id", "session_token");
AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                        .build();

*/


//  OTP iT0vxzO7Kj# 
//URL https://d-9067c04f32.awsapps.com/start
//user: photoclouduseriam

// aws s3 ls --profile PowerUserAccess-283873383140

// s3 connections use credentials in ~/.aws/credentials
// They need to be refreshed/generated from the aws web portal.
// 1. log in with the above start url and click the key icon
// 2. Edit the credentials file and paste in the new values (all 3)
// 3. section heading should be [default]
// 4. test with: aws s3 ls 

// echo -n b23eiIvp8qwQxGJeJvMnrDAzgcKdkTThioDR+SY84qw= | base64 -d |xxd -ps -c 100

//
// Apple iCloud Photo Album Types
//
//Shared Album - public url, but need to copy photos in from local maching. Also need iCloud client app on Pc
//Shared Library - invite people
//icloud sharing - obfucscated js - need browser automation
//direct accoutn access - need login perl script




public class S3Util {
	
	public Region region=null;
	public S3Client s3=null;
	public IamClient iam=null;
	
	public S3Util()
	{
		 region = Region.US_EAST_1;
	     s3 = S3Client.builder()
	    		 .region(region)
	    		 .build();
	     
	        
	        iam = IamClient.builder()
	                .region(region)
	                .build();

	     
	     
	} //end constructor
	
	public String GetUserName()
	{
		
		GetUserResponse response = iam.getUser();
		
		return response.user().userName();
		
	}
	
	
	
	 public  List<Bucket> ListBuckets() {
		 
		 String mn="S3Util::ListBuckets";
		 
	        try {
	            ListBucketsResponse response = s3.listBuckets();
	            List<Bucket> bucketList = response.buckets();
	            
	            return bucketList;

	        } 
	        catch (S3Exception e) {
	            Log.Error(mn,e.awsErrorDetails().errorMessage() );
	            return null;
	        }
	        catch(Exception ex)
	        {
	        	Log.Error(mn, ex);
	        	return null;
	        }

	 } //end method
	 
	 public Response PutFile(Path localPath, String bucketName, String targetFileName) throws Exception
	 {
		 
		PutObjectRequest request;
		PutObjectResponse response;
		String mn="S3Util::PutFile";
		Response genericResponse=new Response();
		
		

		  try
	        {
		        
		        request = PutObjectRequest.builder()
			        .bucket(bucketName)
			        .key(targetFileName)
			        .checksumAlgorithm(ChecksumAlgorithm.SHA256)
			        .build();
		        
		        response = s3.putObject(request, localPath);
		        
		        
		        
		        genericResponse.returnCode=response.sdkHttpResponse().statusCode();
		        genericResponse.message=response.sdkHttpResponse().statusText().toString();
		        genericResponse.success=true;
		        genericResponse.hash=Utils.Base64Decode(Utils.ToString(response.checksumSHA256()));
		        
				Log.Info(mn,"Response: " + response.sdkHttpResponse().statusCode() + " " + response.sdkHttpResponse().statusText().toString());
				Log.Info(mn, "SUMS: " +
					Utils.ToString(response.checksumSHA256()));
				
				return genericResponse;
				
	        
	        }
	        catch (S3Exception e) {
	        	
	        	genericResponse.success=false;
	        	genericResponse.message=e.awsErrorDetails().errorMessage();
	        	genericResponse.returnCode=-1;
	        	
	            Log.Error(mn,e.awsErrorDetails().errorMessage() );
	            return genericResponse;
	            
	        }
	        catch(Exception ex)
	        {
	        	genericResponse.success=false;
	        	genericResponse.message=ex.toString();
	        	genericResponse.returnCode=-1;

	        	Log.Error(mn, ex.toString());
	        	return genericResponse;
	        	
	        }
		
	 }

	public void Close()
	{
		s3.close();
		s3=null;
		
	}
	

}
