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

public class SpringSecurityExample extends Example {

	public SpringSecurityExample() {
		
		this.mockDefinition = "[]";
		
		this.policy = "/*\r\n"
				+ " * All doctors and nurses have full read access on all patient records.\r\n"
				+ " */\r\n"
				+ "policy \"doctor and nurse access to patient data\"\r\n"
				+ "permit \r\n"
				+ "       action.java.name == \"findById\"\r\n"
				+ "where \r\n"
				+ "       \"ROLE_DOCTOR\" in subject..authority || \"ROLE_NURSE\" in subject..authority; ";
		
		
		this.authzSub = "{\r\n"
				+ "\"action\":{\"java\":{\"name\":\"findById\"}},\r\n"
				+ "\"resource\":\"ui:view:patients:createPatientButton\", \r\n"
				+ "\"subject\":{\"authorities\":[{\"authority\":\"ROLE_DOCTOR\"}],\"details\":{\"remoteAddress\":\"0:0:0:0:0:0:0:1\",\"sessionId\":\"102486A0FD0D716DDF4A8D4DD38940D0\"},\"authenticated\":true,\"principal\":{\"password\":null,\"username\":\"Alina\",\"authorities\":[{\"authority\":\"ROLE_DOCTOR\"}],\"accountNonExpired\":true,\"accountNonLocked\":true,\"credentialsNonExpired\":true,\"enabled\":true},\"credentials\":null,\"name\":\"Alina\"}\r\n"
				+ "}";
		
		this.displayName = "Spring Security";
	}
}
