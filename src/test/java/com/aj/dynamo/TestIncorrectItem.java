package com.aj.dynamo;

class TestIncorrectItem {
	
	@DynamoAttribute(name = "ID")
	@DynamoType(type = DynamoTypes.ss)
	private String id;
	
	String getID() {
		return id;
	}
	void setID(String id) {
		this.id = id;
	}
}
