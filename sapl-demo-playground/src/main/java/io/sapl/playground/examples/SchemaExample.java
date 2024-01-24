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
package io.sapl.playground.examples;

public class SchemaExample extends Example {

	public SchemaExample() {

		this.mockDefinition = "[]";

		this.policy = """
		        subject enforced schema 
		               {
                            "$id": "https://example.com/person.schema.json",
                            "$schema": "https://json-schema.org/draft/2020-12/schema",
                            "title": "Person",
                            "type": "object",
                            "properties": {
                                "firstName": {
                                    "type": "string",
                                    "description": "The person's first name."
                                },
                                "lastName": {
                                    "type": "string",
                                    "description": "The person's last name."
                                },
                                "age": {
                                    "description": "Age in years which must be equal to or greater than zero.",
                                    "type": "integer",
                                    "minimum": 0
                                }
                            }
                        }

				policy "policy 1"
				permit
				    action == "read"
				where
				    resource == "something";
		        """;
		this.authzSub = """
				{
				  "subject"  : {
                                 "firstName" : "Ophelia",
                                 "lastName" : "Olling",
                                 "age" : 34
                               },
				  "action"   : "read",
				  "resource" : "something"
				}
				""";

		this.displayName = "JSON Schema";
	}

}
