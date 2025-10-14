package com.sga.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Regex {
	
	public static boolean IsMatch(String pattern, String stringToCheck)
	{
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(stringToCheck);
		
		return m.matches();
		
		
	}
	
	public static String ExtractFilenameFromURL(String url)
	{
		if(url==null) return null;
		String rv= Utils.ToString(url);
		
		return rv.replaceAll("^.*\\/","").replaceAll("\\?.*$", "");

	}

	
	
	
}
