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
package io.sapl.springdatar2dbcdemo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest(classes = SaplSpringDataR2dbcDemoApplication.class)
class SaplSpringDataR2dbcDemoApplicationTest {

	@Test
	void when_applicationStarts_then_runApplication() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {

			mocked.when(() -> SpringApplication.run(SaplSpringDataR2dbcDemoApplication.class
					))
					.thenReturn(mock(ConfigurableApplicationContext.class));

			SaplSpringDataR2dbcDemoApplication.main(new String[] {});

			mocked.verify(() -> SpringApplication.run(SaplSpringDataR2dbcDemoApplication.class), times(1));
		}
	}
}