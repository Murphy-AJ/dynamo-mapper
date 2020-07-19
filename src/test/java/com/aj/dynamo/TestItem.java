package com.aj.dynamo;

import java.util.ArrayList;
import java.util.List;

class TestItem {
	private static final String DEFAULT_VALUE = "-----";
	
	@DynamoAttribute(name = "ID")
	@DynamoType(type = DynamoTypes.s)
	@NotNull
	private String id;
	
	@DynamoAttribute(name = "NAME")
	@DynamoType(type = DynamoTypes.s)
	private String name = DEFAULT_VALUE;
	
	@DynamoAttribute(name = "TAGS")
	@DynamoType(type = DynamoTypes.ss)
	private List<String> tags = new ArrayList<>();
	
	@DynamoAttribute(name = "IS_ACTIVE")
	@DynamoType(type = DynamoTypes.bool)
	private boolean active = true;
	
	String getID() {
		return id;
	}
	void setID(String id) {
		this.id = id;
	}
	
	String getName() {
		return name;
	}
	void setName(String name) {
		this.name = name;
	}
	
	List<String> getTags() {
		return tags;
	}
	void setTags(List<String> tags) {
		this.tags = tags;
	}
	
	boolean isActive() {
		return active;
	}
	void setActive(boolean active) {
		this.active = active;
	}
}
