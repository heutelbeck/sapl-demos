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
package io.sapl.springdatamongoreactivedemo.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.springdatamongoreactivedemo.controller.DemoRestController;
import io.sapl.springdatamongoreactivedemo.repository.User;
import io.sapl.springdatamongoreactivedemo.repository.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@Profile("demo")
@AllArgsConstructor
public class DataLoader implements ApplicationRunner {

	private final UserRepository userRepository;

	@Override
	public void run(ApplicationArguments args) {

		userRepository.deleteAll().thenMany(getTestData().flatMap(userRepository::save)).subscribe();
	}

	Flux<User> getTestData() {
		var users = new ArrayList<User>();
		
        var objectMapper = new ObjectMapper();
        try {
            InputStream inputStream = DemoRestController.class.getResourceAsStream("/users.json");
            
           var usersFromFile = objectMapper.readValue(inputStream, new TypeReference<List<User>>() {});
           users.addAll(usersFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
		return Flux.fromIterable(users);
	}
}
