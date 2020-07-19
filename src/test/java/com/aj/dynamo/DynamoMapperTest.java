package com.aj.dynamo;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.aj.dynamo.exceptions.DynamoMappingException;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RunWith(MockitoJUnitRunner.class)
public class DynamoMapperTest {
	
	private static final String ID = "ID";
	private static final String NAME = "NAME";
	private static final String TAGS = "TAGS";
	private static final String IS_ACTIVE = "IS_ACTIVE";

	private static final String ID_VALUE = "654321";
	private static final String NAME_VALUE = "Bob";
	private static final List<String> TAGS_VALUE = Arrays.asList("1", "2", "3");
	private static final boolean IS_ACTIVE_VALUE = false;
	
	private static final String NULL_MESSAGE = "Non-nullable value is null: ";
	private static final String DEFAULT_VALUE = "-----";


	@Test
	public void testItemToObject() throws DynamoMappingException {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put(ID, AttributeValue.builder().s(ID_VALUE).build());
		item.put(NAME, AttributeValue.builder().s(NAME_VALUE).build());
		item.put(TAGS, AttributeValue.builder().ss(TAGS_VALUE).build());
		item.put(IS_ACTIVE, AttributeValue.builder().bool(IS_ACTIVE_VALUE).build());
		
		TestItem actual = DynamoMapper.mapObjectFromItem(TestItem.class, item);
		assertThat(actual).isNotNull();
		assertThat(actual.getID()).isEqualTo(ID_VALUE);
		assertThat(actual.getName()).isEqualTo(NAME_VALUE);
		assertThat(actual.getTags()).isEqualTo(TAGS_VALUE);
		assertThat(actual.isActive()).isEqualTo(IS_ACTIVE_VALUE);;
	}	
	
	@Test
	public void testDefaultValues() throws DynamoMappingException {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put(ID, AttributeValue.builder().s(ID_VALUE).build());
		
		TestItem actual = DynamoMapper.mapObjectFromItem(TestItem.class, item);
		assertThat(actual).isNotNull();
		assertThat(actual.getID()).isEqualTo(ID_VALUE);
		assertThat(actual.getName()).isEqualTo(DEFAULT_VALUE);
		assertThat(actual.getTags()).isEmpty();
		assertThat(actual.isActive()).isTrue();
	}
	
	@Test
	public void testItemWithMissingAttributes() throws DynamoMappingException {
		Map<String, AttributeValue> item = new HashMap<>();
		DynamoMappingException expected = new DynamoMappingException(NULL_MESSAGE + ID);
		
		try {
			DynamoMapper.mapObjectFromItem(TestItem.class, item);
			Assert.fail();
		} catch (DynamoMappingException e) {
			assertThat(e).hasMessageThat().isEqualTo(expected.getMessage());
		}
	}
	
	@Test
	public void testItemWithWrongAttributes() throws DynamoMappingException {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put(ID, AttributeValue.builder().ss(ID_VALUE).build());
		
		DynamoMappingException expected = new DynamoMappingException(NULL_MESSAGE + ID);
		
		try {
			DynamoMapper.mapObjectFromItem(TestItem.class, item);
			Assert.fail();
		} catch (DynamoMappingException e) {
			assertThat(e).hasMessageThat().isEqualTo(expected.getMessage());
		}
	}
	
	@Test
	public void testObjectToItem() throws DynamoMappingException {		
		TestItem item = new TestItem();
		item.setID(ID_VALUE);
		item.setName(NAME_VALUE);
		item.setTags(TAGS_VALUE);
		item.setActive(IS_ACTIVE_VALUE);		
		
		
		Map<String, AttributeValue> actual = DynamoMapper.mapItemFromObject(item);
		assertThat(actual).isNotNull();
		assertThat(actual).containsKey(ID);
		assertThat(actual).containsKey(NAME);
		assertThat(actual).containsKey(TAGS);
		assertThat(actual).containsKey(IS_ACTIVE);
		
		assertThat(actual.get(ID).s()).isEqualTo(ID_VALUE);
		assertThat(actual.get(NAME).s()).isEqualTo(NAME_VALUE);
		assertThat(actual.get(TAGS).ss()).isEqualTo(TAGS_VALUE);
		assertThat(actual.get(IS_ACTIVE).bool()).isEqualTo(IS_ACTIVE_VALUE);
	}
	
	@Test
	public void testObjectWithNulls() throws DynamoMappingException {		
		TestItem item = new TestItem();
		item.setID(null);
		item.setName(null);
		item.setTags(null);	
		
		DynamoMappingException expected = new DynamoMappingException(NULL_MESSAGE + ID);
		
		try {
			DynamoMapper.mapItemFromObject(item);
			Assert.fail();
		} catch (DynamoMappingException e) {
			assertThat(e).hasMessageThat().isEqualTo(expected.getMessage());
		}
	}
	
	@Test
	public void testIncorrectType() throws DynamoMappingException {		
		TestIncorrectItem item = new TestIncorrectItem();
		item.setID(ID_VALUE);
				
		try {
			DynamoMapper.mapItemFromObject(item);
			Assert.fail();
		} catch (DynamoMappingException e) {
			assertThat(e).hasCauseThat().isInstanceOf(ClassCastException.class);
		}
	}
	
}
