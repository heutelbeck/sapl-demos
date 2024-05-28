/*
 * Copyright (C) 2017-2024 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * SPDX-License-Identifier: Apache-2.0
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
package io.sapl.springdatar2dbcdemo.integration;

import java.io.ByteArrayInputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.BadSqlGrammarException;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.springdatar2dbcdemo.controller.DemoRestController;
import io.sapl.springdatar2dbcdemo.repository.Person;
import io.sapl.springdatar2dbcdemo.repository.Role;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class DemoRestControllerIT extends TestContainerBase {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoRestControllerIT.class);


	@Autowired
	DemoRestController r2dbcPersonController;

	@Test
	void when_findAll_then_getDataWithManipulationByPDP() {
		// GIVEN
        var inputStream = new ByteArrayInputStream(personsJsonString.getBytes(StandardCharsets.UTF_8));
        var persons = new ArrayList<Person>();

        var objectMapper = new ObjectMapper();
        try {
        	persons.addAll(objectMapper.readValue(inputStream, new TypeReference<List<Person>>() {}));
        } catch (IOException e) {
        	LOGGER.error("Error: ", e);
        }

		// WHEN
		var result = r2dbcPersonController.findAll().collectList();

		// THEN
		StepVerifier.create(result).expectNext(persons).verifyComplete();
	}

	@Test
	void when_findAllByAgeAfter_then_getDataWithManipulationByPDP() {
		// GIVEN
		var expectedPersons = List.of(new Person(0, "Petra", "Shackleford", 97, Role.ADMIN, true, 0),
				new Person(0, "Terrel", "Woodings", 96, Role.USER, true, 0),
				new Person(0, "Tabby", "Skittreal", 93, Role.ADMIN, true, 0),
				new Person(0, "Lexine", "Blakden", 91, Role.ADMIN, true, 0));

		// WHEN
		var result = r2dbcPersonController.findAllByAgeAfter(90).collectList();

		// THEN
		StepVerifier.create(result).expectNext(expectedPersons).verifyComplete();
	}

	@Test
	void when_fetchingByQueryMethod_then_getDataWithManipulationByPDP() {
		// GIVEN

		// WHEN
		var result = r2dbcPersonController.fetchingByQueryMethod("ll").collectList();

		// THEN
		StepVerifier.create(result).expectErrorMatches(error -> error instanceof BadSqlGrammarException).verify();
	}
	
	private final String personsJsonString = """
[
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 82,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 79,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 96,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 33,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 96,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 46,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 64,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 32,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 83,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 54,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 31,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 35,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 66,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 94,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 67,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 75,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 55,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 60,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 39,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 55,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 50,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 98,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 65,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 52,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 53,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 29,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 70,
    "role": "USER",
    "active": false,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 45,
    "role": "USER",
    "active": true,
    "addressId": 0
  },
  {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 23,
    "role": "USER",
    "active": false,
    "addressId": 0
   },
   {
    "personId": 0,
    "firstname": null,
    "lastname": null,
    "age": 69,
    "role": "USER",
    "active": true,
    "addressId": 0
   }
   ]
""";
	
}