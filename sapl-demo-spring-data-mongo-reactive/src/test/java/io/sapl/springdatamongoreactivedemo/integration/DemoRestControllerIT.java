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
package io.sapl.springdatamongoreactivedemo.integration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.springdatamongoreactivedemo.controller.DemoRestController;
import io.sapl.springdatamongoreactivedemo.repository.Role;
import io.sapl.springdatamongoreactivedemo.repository.User;
import reactor.test.StepVerifier;

@SpringBootTest
@ActiveProfiles("test")
class DemoRestControllerIT extends TestContainerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoRestControllerIT.class);

	@Autowired
	DemoRestController reactiveMongoUserController;

	@Test
	void when_findAll_then_getDataWithManipulationByPDP() {
		// GIVEN
        var inputStream = new ByteArrayInputStream(usersAsJsonString.getBytes(StandardCharsets.UTF_8));
        var users = new ArrayList<User>();

        var objectMapper = new ObjectMapper();
        try {
            users.addAll(objectMapper.readValue(inputStream, new TypeReference<List<User>>() {}));
        } catch (IOException e) {
        	LOGGER.error("Error: ", e);
        }
	
		// WHEN
		var result = reactiveMongoUserController.findAll().collectList();

		// THEN
		StepVerifier.create(result).expectNext(users).verifyComplete();
	}

	@Test
	void when_findAllByAgeAfter_then_getDataWithManipulationByPDP() {
		// GIVEN
		var expectedPersons = List.of(
				new User(new ObjectId("64de3bd9fbf82799677ed338"), "Terrel", null, null, null, null),
				new User(new ObjectId("64de3bd9fbf82799677ed33a"), "Konstantine", null, null, null, null));

		// WHEN
		var result = reactiveMongoUserController.findAllByAgeAfter(90).collectList();

		// THEN
		StepVerifier.create(result).expectNext(expectedPersons).verifyComplete();
	}

	@Test
	void when_fetchingByQueryMethod_then_getDataWithManipulationByPDP() {
		// GIVEN
		var expectedPersons = List
				.of(new User(new ObjectId("64de3bd9fbf82799677ed344"), null, "Morfell", 35, Role.USER, Boolean.TRUE));

		// WHEN
		var result = reactiveMongoUserController.fetchingByQueryMethod("ll").collectList();

		// THEN
		StepVerifier.create(result).expectNext(expectedPersons).verifyComplete();
	}

	private final String usersAsJsonString = """
[
  {
    "id": "64de3bd9fbf82799677ed336",
    "firstname": null,
    "lastname": "Rowat",
    "age": 82,
    "role": "USER",
    "active": false
  },
  {
    "id": "64de3bd9fbf82799677ed338",
    "firstname": null,
    "lastname": "Woodings",
    "age": 96,
    "role": "USER",
    "active": true
  },
  {
    "id": "64de3bd9fbf82799677ed339",
    "firstname": null,
    "lastname": "Bartolijn",
    "age": 33,
    "role": "USER",
    "active": false
  },
  {
    "id": "64de3bd9fbf82799677ed33a",
    "firstname": null,
    "lastname": "Hampton",
    "age": 96,
    "role": "USER",
    "active": true
  },
  {
    "id": "64de3bd9fbf82799677ed33c",
    "firstname": null,
    "lastname": "Streeton",
    "age": 46,
    "role": "USER",
    "active": true
  },
  {
    "id": "64de3bd9fbf82799677ed33d",
    "firstname": null,
    "lastname": "Tomaskov",
    "age": 64,
    "role": "USER",
    "active": true
  },
  {
    "id": "64de3bd9fbf82799677ed342",
    "firstname": null,
    "lastname": "Albinson",
    "age": 54,
    "role": "USER",
    "active": false
  },
  {
    "id": "64de3bd9fbf82799677ed344",
    "firstname": null,
    "lastname": "Morfell",
    "age": 35,
    "role": "USER",
    "active": true
  },
  {
    "id": "64de3bd9fbf82799677ed345",
    "firstname": null,
    "lastname": "Bickerstasse",
    "age": 66,
    "role": "USER",
    "active": true
  },
  {
    "id": "64de3bd9fbf82799677ed346",
    "firstname": null,
    "lastname": "Angell",
    "age": 94,
    "role": "USER",
    "active": false
  }
]
""";
	
}