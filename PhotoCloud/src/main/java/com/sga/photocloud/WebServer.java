package com.sga.photocloud;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServer {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		
		//https://login.microsoftonline.com/7ad124c2-aa3d-4ebb-b69c-4acdbd856446/oauth2/v2.0/authorize?
		//client_id=3882d0f9-543e-4fc9-8501-64bcb68ab6b5
		//&response_type=code
		//&redirect_uri=http%3A%2F%2Flocalhost%3A8000
		//&scope=https%3A%2F%2Fgraph.microsoft.com%2FApplication.Read.All
		//&state=12345
		//&sso_reload=true

		
		//
		// These are the things you need from azure to build the
		// auth URL
		//
		
		String baseAuthUrl = "https://login.microsoftonline.com/";
		String tenant = "7ad124c2-aa3d-4ebb-b69c-4acdbd856446";		//msn tenant
		tenant="f3d9e69e-96e1-44e2-bb93-9ff3b278361a";				//photoliberator tenant with SPO
		
		
		String clientId = "3882d0f9-543e-4fc9-8501-64bcb68ab6b5";
		clientId="50278812-f098-446b-8395-57c235aa2cdd";
		
		String redirectUri=URLEncoder.encode("http://localhost:8000","UTF-8");
		String scope = URLEncoder.encode("https://graph.microsoft.com/Application.Read.All","UTF-8");
		String state = "12345";
		
		
		//
		// Generate the URL from the above parameters
		//
		
		String authUrl=baseAuthUrl + 
				tenant + 
				"/oauth2/v2.0/authorize" + 
				"?client_id=" + clientId + 
				"&response_type=code" +
				"&redirect_uri=" + redirectUri + 
				"&scope=" + scope +
				"&state=" + state +
				"&sso_reload=true";
				
		System.out.println("Your Auth URL:");
		System.out.println(authUrl);
		
		
		 // Create an HttpServer instance
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
 
        // Create a context for a specific path and set the handler
        server.createContext("/", new MyHandler());
 
        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();
 
        System.out.println("Server is running on port 8000");
	}
	
	 static class MyHandler implements HttpHandler {
		 Pattern pattern;
		 Matcher matcher;
		 
		 	public MyHandler()
		 	{
		 		 pattern = Pattern.compile("^\\/\\?code=|&.*$", Pattern.CASE_INSENSITIVE);
		 	     
		 		
		 		
		 	}
		 	
		 	private String Strip(String inURI)
		 	{
		 		matcher=pattern.matcher(inURI);
		 		return matcher.replaceAll("");
		 	}
		 
	        @Override
	        public void handle(HttpExchange exchange) throws IOException 
	        {
	            // handle the request
	        	boolean verbose=false;
	        	Headers h = exchange.getRequestHeaders();
	        	if(verbose)
	        		System.out.println("Method: " + exchange.getRequestMethod());
	        	
	        	String URI=exchange.getRequestURI().toString();
	        	String authCode=Strip(URI);
	        	
	        	
	        	System.out.println("URI: " + URI);
	        	System.out.println(authCode);
	        	//System.out.println(exchange.toString());

	        	
	        	if(verbose)
	        	{
		        	for(String k : h.keySet())
		        	{
		        		System.out.println(k + " -> " + h.getFirst(k));
		        	}
		        	
		        	
		        	InputStream is = exchange.getRequestBody();
		        	System.out.println("BODY" + is.available());
		        	System.out.println(is.readAllBytes());
	        	}
	            
	        	//
	        	//return the authCode to the browser
	        	//
	        	String response = "Return from Auth URL\n" + URI + "\n\n" + authCode;
	            exchange.sendResponseHeaders(200, response.length());
	            OutputStream os = exchange.getResponseBody();
	            os.write(response.getBytes());
	            os.close();
	            
	            
	            
	        }
	    }

}
