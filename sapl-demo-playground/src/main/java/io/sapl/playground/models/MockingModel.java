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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.sapl.api.interpreter.Val;
import lombok.Data;

@Data
public class MockingModel {

    MockingTargetEnum type;

    public static final String KEY_VALUE_TYPE = "type";

    /**
     * Name how the attribute or function is referenced to in the policy
     */
    String importName;

    public static final String KEY_VALUE_IMPORT_NAME = "importName";

    /**
     * {@link Val} to be returned by the attribute once or by the function every
     * time it is called. One of always or sequence required.
     */
    Val always;

    public static final String KEY_VALUE_ALWAYS_RETURN_VALUE = "always";

    /**
     * List of {@link Val}'s to be returned by an attribute or by a function (one
     * per function call). One of always or sequence required.
     */
    List<Val> sequence;

    public static final String KEY_VALUE_RETURN_SEQUENCE_VALUES = "sequence";

    public static List<MockingModel> parseMockingJsonInputToModel(JsonNode mockInput)
            throws MockDefinitionParsingException {
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
        if (mockElement.has(KEY_VALUE_RETURN_SEQUENCE_VALUES)) {
            final var values = mockElement.get(KEY_VALUE_RETURN_SEQUENCE_VALUES);
            if (!values.isArray()) {
                throw new MockDefinitionParsingException(
                        "Expecting an array for field \"" + KEY_VALUE_RETURN_SEQUENCE_VALUES + "\""
                                + " for importName \"" + mockModel.getImportName() + "\"!");

            }
            final var       valuesArray = (ArrayNode) values;
            List<Val> valList     = new LinkedList<>();
            for (var specifiedValue : valuesArray) {
                valList.add(Val.of(specifiedValue));
            }
            mockModel.setSequence(valList);
        }
    }

    private static void parseMockValueField(JsonNode mockElement, MockingModel mockModel) {
        if (mockElement.has(KEY_VALUE_ALWAYS_RETURN_VALUE)) {
            mockModel.setAlways(Val.of(mockElement.get(KEY_VALUE_ALWAYS_RETURN_VALUE)));
        }
    }

    private static void checkOnlyOneOfOptionalFieldsIsSet(JsonNode mockElement, MockingModel mockModel)
            throws MockDefinitionParsingException {
        if (mockElement.has(KEY_VALUE_ALWAYS_RETURN_VALUE) && mockElement.has(KEY_VALUE_RETURN_SEQUENCE_VALUES)) {
            throw new MockDefinitionParsingException("You cannot specify an always-returned \""
                    + KEY_VALUE_ALWAYS_RETURN_VALUE + "\" " + "AND an array of \"" + KEY_VALUE_RETURN_SEQUENCE_VALUES
                    + "\" for importName \"" + mockModel.getImportName() + "\". Specify only one!");
        }
    }

    private static void extractImportNameField(JsonNode mockElement, MockingModel mockModel)
            throws MockDefinitionParsingException {
        if (mockElement.has(KEY_VALUE_IMPORT_NAME)) {

            String importNameString = mockElement.get(KEY_VALUE_IMPORT_NAME).asText();

            if (!importNameString.contains(".")) {
                throw new MockDefinitionParsingException(
                        "Expecting a string value with a dot like \"function.name\" for field \""
                                + KEY_VALUE_IMPORT_NAME + "\"!");
            } else {
                mockModel.setImportName(importNameString);
            }

        } else {
            throw new MockDefinitionParsingException(
                    "Expecting the field \"" + KEY_VALUE_IMPORT_NAME + "\" in every element");
        }
    }

    private static void parseTypeField(JsonNode mockElement, MockingModel mockModel)
            throws MockDefinitionParsingException {
        if (mockElement.has(KEY_VALUE_TYPE)) {

            String typeString = mockElement.get(KEY_VALUE_TYPE).asText();

            if (typeString.isEmpty() || !(typeString.equals(MockingTargetEnum.ATTRIBUTE.name())
                    || typeString.equals(MockingTargetEnum.FUNCTION.name()))) {
                throw new MockDefinitionParsingException(
                        "Expecting for field \"" + KEY_VALUE_TYPE + "\" a value of \"ATTRIBUTE\" or \"FUNCTION\"");
            } else {
                mockModel.setType(MockingTargetEnum.valueOf(typeString));
            }

        } else {
            throw new MockDefinitionParsingException("Expecting the field \"" + KEY_VALUE_TYPE + "\" in every element");
        }
    }

    private static void checkArray(JsonNode mockInput) throws MockDefinitionParsingException {
        if (!mockInput.isArray()) {
            throw new MockDefinitionParsingException("Expecting an array at top level");
        }
    }

}
