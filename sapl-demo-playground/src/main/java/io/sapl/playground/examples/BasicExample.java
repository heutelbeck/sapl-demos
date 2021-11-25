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

public class BasicExample extends Example {

	public BasicExample() {
		
		this.mockDefinition = "[\n"
				+ "  {\n"
				+ "    \"type\"	   : \"ATTRIBUTE\",\n"
				+ "    \"importName\": \"time.now\",\n"
				+ "    \"sequence\": [1, 2, 3]\n"
				+ "  }, \n"
				+ "  {\n"
				+ "    \"type\": \"FUNCTION\",\n"
				+ "    \"importName\": \"time.dayOfWeekFrom\",\n"
				+ "    \"sequence\": [\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]\n"
				+ "  }\n"
				+ "]";
		
		this.policy = "policy \"policy 1\"\n"
				+ "permit\n"
				+ "    action == \"read\"\n"
				+ "where\n"
				+ "    subject == \"WILLI\";\n"
				+ "    time.dayOfWeekFrom(<time.now>) =~ \"MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY|SATURDAY|SUNDAY\";";
		
		
		this.authzSub = "{\n"
    			+ " \"subject\"     : \"WILLI\",\n"
    			+ " \"action\"      : \"read\",\n"
    			+ " \"resource\"    : \"something\"\n"
    			+ "}";
		
		this.displayName = "Basic";
	}
}
