package com.sga.common.processor;

import java.util.ArrayList;

public class ProcessingResult {
	public String caption;
	public String errorMessage;
	public Integer returnCode;
	public Integer returnCode2;
	public ArrayList<String> tags;
	
	
	
	@Override
	public String toString() {
		return "ProcessingResult [caption=" + caption + ", errorMessage=" + errorMessage + ", returnCode=" + returnCode
				+ ", returnCode2=" + returnCode2 + ", tags=" + tags + "]";
	}
	
	
	

}
