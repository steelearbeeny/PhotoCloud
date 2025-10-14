package com.sga.common.util;

public class ReturnValue<T> {

	public T data;
	public String message;
	
	
	public ReturnValue()
	{
		this.data=null;
		message="";
	}
	
	public ReturnValue(T data)
	{
		this.data=data;
		message="";
	}
	
	public ReturnValue(T data, String message)
	{
		this.data=data;
		this.message=message;
	}

	@Override
	public String toString() {
		return "ReturnValue [data=" + data + ", message=" + message + "]";
	}
	
	
}
