package io.sapl.playground.models;

import java.time.Duration;
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
	
	/**
	 * time interval between the {@link Val}'s of the sequence to be returned by an attribute. {@link #type} has to be {@link MockingTargetEnum#ATTRIBUTE} and {@link #sequence} has to be set.
	 * Currently not configurable from the frontend.
	 */
	Duration interval = Duration.ofSeconds(2);
	
	
	
	
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
			if(mockElement.has(KeyValue_Type)) {
				
				String typeString = mockElement.get(KeyValue_Type).asText();
				
				if(typeString.isEmpty() || 
						!(
								typeString.equals(MockingTargetEnum.ATTRIBUTE.name()) || 
								typeString.equals(MockingTargetEnum.FUNCTION.name())
						)
					) {
					mockDefinitionJsonInputError.setText("Expecting for field \"" + KeyValue_Type + "\" a value of \"ATTRIBUTE\" or \"FUNCTION\"");
					mockDefinitionJsonInputError.setVisible(true);
					return null;
				} else {
					mockModel.setType(MockingTargetEnum.valueOf(typeString));
				}
				
			} else {
				mockDefinitionJsonInputError.setText("Expecting the field \"" + KeyValue_Type + "\" in every element");
				mockDefinitionJsonInputError.setVisible(true);
				return null;
			}
			
			
			
			
			//parse required "importName" field
			if(mockElement.has(KeyValue_ImportName)) {
				
				String importNameString = mockElement.get(KeyValue_ImportName).asText();
				
				if(importNameString.isEmpty() || !importNameString.contains(".")) {
					mockDefinitionJsonInputError.setText("Expecting a string value with a dot like \"function.name\" for field \"" + KeyValue_ImportName + "\"!");
					mockDefinitionJsonInputError.setVisible(true);
					return null;
				} else {
					mockModel.setImportName(importNameString);
				}
				
			} else {
				mockDefinitionJsonInputError.setText("Expecting the field \"" + KeyValue_ImportName + "\" in every element");
				mockDefinitionJsonInputError.setVisible(true);
				return null;
			}
			
			
			//check only one of "mockValue" or "mockValues" is set
			if(mockElement.has(KeyValue_AlwaysReturnValue) && mockElement.has(KeyValue_ReturnSequenceValues)) {
				mockDefinitionJsonInputError.setText("You cannot specify an always-returned \"" + KeyValue_AlwaysReturnValue + "\" "
						+ "AND an array of \"" + KeyValue_ReturnSequenceValues + "\" for importName \"" + mockModel.getImportName() + "\". Specify only one!");
				mockDefinitionJsonInputError.setVisible(true);
				return null;
			}
			
			//parse optional "mockValue" field
			if(mockElement.has(KeyValue_AlwaysReturnValue)) {
				mockModel.setAlways(Val.of(mockElement.get(KeyValue_AlwaysReturnValue)));
			}
			
			
			//parse optional "mockValue" field
			if(mockElement.has(KeyValue_ReturnSequenceValues)) {
				var values = mockElement.get(KeyValue_ReturnSequenceValues);
				if(!values.isArray()) {
					mockDefinitionJsonInputError.setText("Expecting an array for field \"" + KeyValue_ReturnSequenceValues + "\""
							+ " for importName \"" + mockModel.getImportName() + "\"!");
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
