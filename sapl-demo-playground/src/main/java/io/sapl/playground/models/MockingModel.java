/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.sapl.playground.models;

import java.util.LinkedList;
import java.util.List;

import io.sapl.api.interpreter.Val;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.Data;

@Data
public class MockingModel {

	MockingTargetEnum type;
	public static final String KeyValue_Type = "type";
	
	/**
	 * Name how the attribute or function is referenced to in the policy
	 */
	String importName;
	public static final String KeyValue_ImportName = "importName";
	
	/**
	 * {@link Val} to be returned by the attribute once or by the function every time it is called. One of always or sequence required. 
	 */
	Val always;
	public static final String KeyValue_AlwaysReturnValue = "always";
	
	/**
	 * List of {@link Val}'s to be returned by an attribute or by a function (one per function call). One of always or sequence required. 
	 */
	List<Val> sequence;
	public static final String KeyValue_ReturnSequenceValues = "sequence";
	
	
    public static List<MockingModel> parseMockingJsonInputToModel(JsonNode mockInput) throws MockDefinitionParsingException {
    	List<MockingModel> models = new LinkedList<>();
    	
    	checkArray(mockInput);
		
		for (JsonNode mockElement : mockInput) {
			MockingModel mockModel = new MockingModel();
			
			parseTypeField(mockElement, mockModel);
			
			extractImportNameField(mockElement, mockModel);
			
			checkOnlyOneOfOptionalFieldsIsSet(mockElement, mockModel);
			
			parseMockValueField(mockElement, mockModel);
			
			parseMockValuesField(mockElement, mockModel);
			
			models.add(mockModel);
		}
		
		return models;
    }




	private static void parseMockValuesField(JsonNode mockElement, MockingModel mockModel)
			throws MockDefinitionParsingException {
		if(mockElement.has(KeyValue_ReturnSequenceValues)) {
			var values = mockElement.get(KeyValue_ReturnSequenceValues);
			if(!values.isArray()) {
				throw new MockDefinitionParsingException("Expecting an array for field \"" + KeyValue_ReturnSequenceValues + "\""
						+ " for importName \"" + mockModel.getImportName() + "\"!");
				
			}
			var valuesArray = (ArrayNode) values;
			List<Val> valList = new LinkedList<>();
			for (var specifiedValue : valuesArray) {
				valList.add(Val.of(specifiedValue));
			}
			mockModel.setSequence(valList);
		}
	}




	private static void parseMockValueField(JsonNode mockElement, MockingModel mockModel) {
		if(mockElement.has(KeyValue_AlwaysReturnValue)) {
			mockModel.setAlways(Val.of(mockElement.get(KeyValue_AlwaysReturnValue)));
		}
	}




	private static void checkOnlyOneOfOptionalFieldsIsSet(JsonNode mockElement, MockingModel mockModel)
			throws MockDefinitionParsingException {
		if(mockElement.has(KeyValue_AlwaysReturnValue) && mockElement.has(KeyValue_ReturnSequenceValues)) {
			throw new MockDefinitionParsingException("You cannot specify an always-returned \"" + KeyValue_AlwaysReturnValue + "\" "
					+ "AND an array of \"" + KeyValue_ReturnSequenceValues + "\" for importName \"" + mockModel.getImportName() + "\". Specify only one!");
		}
	}




	private static void extractImportNameField(JsonNode mockElement, MockingModel mockModel)
			throws MockDefinitionParsingException {
		if(mockElement.has(KeyValue_ImportName)) {
			
			String importNameString = mockElement.get(KeyValue_ImportName).asText();
			
			if(!importNameString.contains(".")) {
				throw new MockDefinitionParsingException("Expecting a string value with a dot like \"function.name\" for field \"" + KeyValue_ImportName + "\"!");
			} else {
				mockModel.setImportName(importNameString);
			}
			
		} else {
			throw new MockDefinitionParsingException("Expecting the field \"" + KeyValue_ImportName + "\" in every element");
		}
	}




	private static void parseTypeField(JsonNode mockElement, MockingModel mockModel)
			throws MockDefinitionParsingException {
		if(mockElement.has(KeyValue_Type)) {
			
			String typeString = mockElement.get(KeyValue_Type).asText();
			
			if(typeString.isEmpty() || 
					!(
							typeString.equals(MockingTargetEnum.ATTRIBUTE.name()) || 
							typeString.equals(MockingTargetEnum.FUNCTION.name())
					)
				) {
				throw new MockDefinitionParsingException("Expecting for field \"" + KeyValue_Type + "\" a value of \"ATTRIBUTE\" or \"FUNCTION\"");
			} else {
				mockModel.setType(MockingTargetEnum.valueOf(typeString));
			}
			
		} else {
			throw new MockDefinitionParsingException("Expecting the field \"" + KeyValue_Type + "\" in every element");
		}
	}




	private static void checkArray(JsonNode mockInput) throws MockDefinitionParsingException {
		if(!mockInput.isArray()) {
			throw new MockDefinitionParsingException("Expecting an array at top level");
		}
	}
}
