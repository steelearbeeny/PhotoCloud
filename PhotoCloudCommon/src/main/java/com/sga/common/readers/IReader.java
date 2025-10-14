package com.sga.common.readers;

import java.time.format.DateTimeFormatter;

import com.sga.common.generic.GenericPhoto;
import com.sga.common.generic.JobConfiguration;
import com.sga.common.generic.Response;
import com.sga.common.generic.Service;

public interface IReader {

	boolean hasNext() throws Exception;
	GenericPhoto next() throws Exception;
	
	
	
	
}
