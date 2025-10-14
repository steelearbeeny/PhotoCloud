package com.sga.photocloud;

import java.nio.file.*;

import com.sga.common.log.Log;
import com.sga.common.util.Utils;

import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;



import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.AwsSessionCredentialsIdentity;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.licensemanagerusersubscriptions.model.CredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import java.util.List;

public class s3test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String mn = "s3test::main";
		Log.Info(mn,"In Main");
		
		Path currentRelativePath = Paths.get("");
		String currentWorkingDir = currentRelativePath.toAbsolutePath().normalize().toString();
		Log.Info(mn,"Current working directory: " + currentWorkingDir);
		
		
		
		
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
		
		
		
		 Region region = Region.US_EAST_1;
	        S3Client s3 = S3Client.builder()
	            .region(region)
	            .build();

	        listBuckets(s3);
	        
	        UUID uuid =UUID.randomUUID();
	        
	        String bucketName="testphotobucket";
	        String imageName="userabc/images/" + uuid.toString();
	        String localFile = "D:\\Pictures\\DSC_0001.JPG";
	        
	        PutObjectRequest request;
	        PutObjectResponse response;
	        
	        imageName="userabc/images/testimage.jpg";
	        
	        try
	        {
	        
		        Path localFilePath=Paths.get(localFile);
		        
		        request = PutObjectRequest.builder()
			        .bucket(bucketName)
			        .key(imageName)
			        .checksumAlgorithm(ChecksumAlgorithm.SHA256)
			        .build();
			        
		        
		        
		        response = s3.putObject(request, localFilePath);
		        
		        Log.Info(mn,"Response: " + response.sdkHttpResponse().statusCode() + " " + response.sdkHttpResponse().statusText().toString());
		        Log.Info(mn, "SUMS: " + 
				        Utils.ToString(response.checksumCRC32()) + " - " + 
				        Utils.ToString(response.checksumCRC32C()) + " - " + 
		        		Utils.ToString(response.checksumSHA1()) + " - " + 
        				Utils.ToString(response.checksumSHA256()));
		        
		        int zzz=0;
	        }
	        catch(Exception ex)
	        {
	        	Log.Info(mn, "Exception: " + ex.toString());
	        }
		
	        int ii=0;
	        
	        
	        
	        
	        
		

	}
	
	 public static void listBuckets(S3Client s3) {
	        try {
	            ListBucketsResponse response = s3.listBuckets();
	            List<Bucket> bucketList = response.buckets();
	            bucketList.forEach(bucket -> {
	                System.out.println("Bucket Name: " + bucket.name());
	            });

	        } catch (S3Exception e) {
	            System.err.println(e.awsErrorDetails().errorMessage());
	            System.exit(1);
	        }
	    }

}
