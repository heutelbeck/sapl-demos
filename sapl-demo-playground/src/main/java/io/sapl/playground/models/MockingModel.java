package io.sapl.playground.models;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vaadin.flow.component.html.Paragraph;

import io.sapl.api.interpreter.Val;
import lombok.Data;

@Data
public class MockingModel {

	MockingTargetEnum type;
	
	String importName;
	
	Val always;
	
	List<Val> sequence;
	
	
    public static List<MockingModel> parseMockingJsonInputToModel(JsonNode mockInput, Paragraph mockDefinitionJsonInputError) {
    	List<MockingModel> models = new LinkedList<>();
    	
    	if(!mockInput.isArray()) {
			mockDefinitionJsonInputError.setText("Expecting an array at top level");
			mockDefinitionJsonInputError.setVisible(true);
			return null;
		}
		
		
		for (JsonNode mockElement : mockInput) {
			MockingModel mockModel = new MockingModel();
			
			//parse required "type" field
			if(mockElement.has("type")) {
				
				String typeString = mockElement.get("type").asText();
				
				if(typeString.isEmpty() || 
						!(
								typeString.equals(MockingTargetEnum.ATTRIBUTE.name()) || 
								typeString.equals(MockingTargetEnum.FUNCTION.name())
						)
					) {
					mockDefinitionJsonInputError.setText("Expecting for field \"type\" a value of \"ATTRIBUTE\" or \"FUNCTION\"");
					mockDefinitionJsonInputError.setVisible(true);
					return null;
				} else {
					mockModel.setType(MockingTargetEnum.valueOf(typeString));
				}
				
			} else {
				mockDefinitionJsonInputError.setText("Expecting the field \"type\" in every element");
				mockDefinitionJsonInputError.setVisible(true);
				return null;
			}
			
			
			
			
			//parse required "importName" field
			if(mockElement.has("importName")) {
				
				String importNameString = mockElement.get("importName").asText();
				
				if(importNameString.isEmpty() || !importNameString.contains(".")) {
					mockDefinitionJsonInputError.setText("Expecting a string value with a dot like \"function.name\" for field \"importName\"!");
					mockDefinitionJsonInputError.setVisible(true);
					return null;
				} else {
					mockModel.setImportName(importNameString);
				}
				
			} else {
				mockDefinitionJsonInputError.setText("Expecting the field \"importName\" in every element");
				mockDefinitionJsonInputError.setVisible(true);
				return null;
			}
			
			
			//check only one of "mockValue" or "mockValues" is set
			if(mockElement.has("always") && mockElement.has("sequence")) {
				mockDefinitionJsonInputError.setText("You cannot specify an always-returned \"always\" AND an array of \"sequence\" for importName \"" + mockModel.getImportName() + "\". Specify only one!");
				mockDefinitionJsonInputError.setVisible(true);
				return null;
			}
			
			//parse optional "mockValue" field
			if(mockElement.has("always")) {
				mockModel.setAlways(Val.of(mockElement.get("mockValue")));
			}
			
			
			//parse optional "mockValue" field
			if(mockElement.has("sequence")) {
				var values = mockElement.get("sequence");
				if(!values.isArray()) {
					mockDefinitionJsonInputError.setText("Expecting an array for field \"sequence\" for importName \"" + mockModel.getImportName() + "\"!");
					mockDefinitionJsonInputError.setVisible(true);
					return null;
					
				}
				var valuesArray = (ArrayNode) values;
				List<Val> vals = new LinkedList<>();
				for (var specifiedValue : valuesArray) {
					vals.add(Val.of(specifiedValue));
				}
				mockModel.setSequence(vals);
			}
			
			models.add(mockModel);
		}
		return models;
    }
}
