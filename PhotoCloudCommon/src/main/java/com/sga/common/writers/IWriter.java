package com.sga.common.writers;

import com.sga.common.generic.GenericPhoto;

public interface IWriter {

	public static final int MAX_RETRIES=10;
	
	void write(GenericPhoto inPhoto) throws Exception;
	
	
}
