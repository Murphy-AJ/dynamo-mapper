package com.aj.dynamo.exceptions;

public class DynamoMappingException extends Exception {
	private static final long serialVersionUID = 1254319061942282335L;
	
	public DynamoMappingException(Exception e) {
		super(e);
	}
	
	public DynamoMappingException(String s) {
		super(s);
	}
	
}
