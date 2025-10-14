package com.sga.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;

import com.sga.common.log.Log;  

public class SHA512 {

	
	   private static byte[] getSHA(String input) throws NoSuchAlgorithmException  
	    {  
	        /* MessageDigest instance for hashing using SHA512*/  
	        MessageDigest md = MessageDigest.getInstance("SHA-512");  
	  
	        /* digest() method called to calculate message digest of an input and return array of byte */  
	        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
	    }  
	      
	    private static String toHexString(byte[] hash)  
	    {  
	        /* Convert byte array of hash into digest */  
	        BigInteger number = new BigInteger(1, hash);  
	  
	        /* Convert the digest into hex value */  
	        StringBuilder hexString = new StringBuilder(number.toString(16));  
	  
	        /* Pad with leading zeros */  
	        while (hexString.length() < 32)  
	        {  
	            hexString.insert(0, '0');  
	        }  
	  
	        return hexString.toString();  
	    }  
	    
	    public static String Hash(String input) throws Exception
	    {
	    	
	    	byte [] hash;
	    	
	    	hash=getSHA(input);
	    	return toHexString(hash);
	    	
	    }
	    
	    public static String HashFile(Path inputFile) 
	    {
			MessageDigest md;
			String mn="SHA512::HashFile";
			
			try
			{
			md = MessageDigest.getInstance("SHA-256");
			try (FileInputStream fis = new FileInputStream(inputFile.toString())) {
			    byte[] buffer = new byte[8192];
			    int bytesRead;
			    while ((bytesRead = fis.read(buffer)) != -1) {
			        md.update(buffer, 0, bytesRead);
			    }
			}
			
			byte[] digest = md.digest();
			
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
			    sb.append(String.format("%02x", b));
			    }
			    return sb.toString();
			}
			catch(Exception ex)
			{
				Log.Error(mn, ex);
				return null;
			}

	    }
	  
	    public static String HashBytes(byte[] byteArray) 
	    {
			MessageDigest md;
			String mn="SHA512::HashBytes";
			byte[] digest;
			
			try
			{
				md = MessageDigest.getInstance("SHA-256");
				digest=md.digest(byteArray);
				
				
			
				StringBuilder sb = new StringBuilder();
				for (byte b : digest) {
				    sb.append(String.format("%02x", b));
				    }
				    return sb.toString();
			}
			catch(Exception ex)
			{
				Log.Error(mn, ex);
				return null;
			}

	    }
	  
	
}
