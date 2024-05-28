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
package io.sapl.springdatamongoreactivedemo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.sapl.springdatamongoreactivedemo.repository.User;
import io.sapl.springdatamongoreactivedemo.repository.UserRepository;
import reactor.core.publisher.Flux;

class DemoRestControllerTest {

	private UserRepository userRepositoryMock = mock(UserRepository.class);
	private Flux<User> emptyUserFlux = Flux.just();
	
	@Test
	void when_findAll_then_returnEmptyList() {
		// GIVEN
		var demoRestController = new DemoRestController(userRepositoryMock);
		
		// WHEN
		when(userRepositoryMock.findAll()).thenReturn(emptyUserFlux);
		
		var result = demoRestController.findAll();
		
		// THEN
		assertEquals(emptyUserFlux, result);
	}
	
	@Test
	void when_findAllByAgeAfter_then_returnEmptyList() {
		// GIVEN
		var demoRestController = new DemoRestController(userRepositoryMock);
		
		// WHEN
		when(userRepositoryMock.findAllByAgeAfter(anyInt(), any(Pageable.class))).thenReturn(emptyUserFlux);
		
		var result = demoRestController.findAllByAgeAfter(21);
		
		// THEN
		assertEquals(emptyUserFlux, result);
	}
	
	
	@Test
	void when_fetchingByQueryMethod_then_returnEmptyList() {
		// GIVEN
		var demoRestController = new DemoRestController(userRepositoryMock);
		
		// WHEN
		when(userRepositoryMock.fetchingByQueryMethod(anyString(), any(PageRequest.class))).thenReturn(emptyUserFlux);
		
		var result = demoRestController.fetchingByQueryMethod("ii");
		
		// THEN
		assertEquals(emptyUserFlux, result);
	}
	
	
}
