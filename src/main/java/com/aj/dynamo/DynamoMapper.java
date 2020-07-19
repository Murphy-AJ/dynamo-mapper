package com.aj.dynamo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aj.dynamo.exceptions.DynamoMappingException;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoMapper {
	
	private static final String NULL_MESSAGE = "Non-nullable value is null: ";

	public static <T> T mapObjectFromItem(Class<T> clazz, Map<String, AttributeValue> item) throws DynamoMappingException {		
		try {
			final T obj = clazz.getDeclaredConstructor().newInstance();
			List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
			List<Field> annotated = fields.stream().filter(field -> field.isAnnotationPresent(DynamoAttribute.class)).collect(Collectors.toList());
			
			for (Field field : annotated) {
				addFieldToObject(obj, field, item);
			}
				
			return obj;
		}
		catch (DynamoMappingException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DynamoMappingException(e);
		}
	}
		
	private static void addFieldToObject(Object obj, Field field, Map<String, AttributeValue> item) throws Exception {
		String name = field.getAnnotation(DynamoAttribute.class).name();
		DynamoTypes type = field.getAnnotation(DynamoType.class).type();
		boolean notNullable = field.isAnnotationPresent(NotNull.class);
		
		field.setAccessible(true);
		
		Object value = null;
		if (item.containsKey(name)) {
			value = getValue(type, item.get(name));
		}
		
		if (value != null) {
			field.set(obj, value);
		}
		else if(notNullable) {
			throw new DynamoMappingException(NULL_MESSAGE + name);
		}
	}
	
	private static Object getValue(DynamoTypes type, AttributeValue item) {
		if (type.equals(DynamoTypes.s)) {
			return item.s();
		}
		if (type.equals(DynamoTypes.ss)) {
			return item.ss();
		}
		if (type.equals(DynamoTypes.bool)) {
			return item.bool();
		}
		return null;
	}
	
	
	
	public static Map<String, AttributeValue> mapItemFromObject(Object obj) throws DynamoMappingException {
		try {
			Map<String, AttributeValue> item = new HashMap<>();
			
			List<Field> fields = Arrays.asList(obj.getClass().getDeclaredFields());
			List<Field> annotated = fields.stream().filter(field -> field.isAnnotationPresent(DynamoAttribute.class)).collect(Collectors.toList());
			
			for (Field field : annotated) {
				addFieldToItem(obj, field, item);
			}
			
			return item;
		}
		catch (DynamoMappingException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DynamoMappingException(e);
		}
	}
	
	
	private static void addFieldToItem(Object obj, Field field, Map<String, AttributeValue> item) throws Exception {
		String name = field.getAnnotation(DynamoAttribute.class).name();
		DynamoTypes type = field.getAnnotation(DynamoType.class).type();
		boolean notNullable = field.isAnnotationPresent(NotNull.class);
		
		field.setAccessible(true);
		Object fieldValue = field.get(obj);
		
		AttributeValue value = null;
		if (fieldValue != null) {
			value = createAttribute(fieldValue, type);
		}
		
		if (value != null) {
			item.put(name, value);
		}
		else if(notNullable) {
			throw new DynamoMappingException(NULL_MESSAGE + name);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static AttributeValue createAttribute(Object obj, DynamoTypes type) {
		if (type.equals(DynamoTypes.s)) {
			return AttributeValue.builder().s((String) obj).build();
		}
		if (type.equals(DynamoTypes.ss)) {
			return AttributeValue.builder().ss((List<String>) obj).build();
		}
		if (type.equals(DynamoTypes.bool)) {
			return AttributeValue.builder().bool((boolean) obj).build();
		}
		return null;
	}
	
		
}
