package io.sapl.springdatar2dbcdemo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import io.sapl.springdatar2dbcdemo.repository.Person;
import io.sapl.springdatar2dbcdemo.repository.PersonRepository;
import reactor.core.publisher.Flux;

class DemoRestControllerTest {

	private PersonRepository personRepositoryMock = mock(PersonRepository.class);
	private Flux<Person> emptyPersonFlux = Flux.just();
	
	@Test
	void when_findAll_then_returnEmptyList() {
		// GIVEN
		var demoRestController = new DemoRestController(personRepositoryMock);
		
		// WHEN
		when(personRepositoryMock.findAll()).thenReturn(emptyPersonFlux);
		
		var result = demoRestController.findAll();
		
		// THEN
		assertEquals(emptyPersonFlux, result);
	}
	
	@Test
	void when_findAllByAgeAfter_then_returnEmptyList() {
		// GIVEN
		var demoRestController = new DemoRestController(personRepositoryMock);
		
		// WHEN
		when(personRepositoryMock.findAllByAgeAfter(anyInt(), any(Sort.class))).thenReturn(emptyPersonFlux);
		
		var result = demoRestController.findAllByAgeAfter(21);
		
		// THEN
		assertEquals(emptyPersonFlux, result);
	}
	
	
	@Test
	void when_fetchingByQueryMethod_then_returnEmptyList() {
		// GIVEN
		var demoRestController = new DemoRestController(personRepositoryMock);
		
		// WHEN
		when(personRepositoryMock.fetchingByQueryMethod(anyString(), any(PageRequest.class))).thenReturn(emptyPersonFlux);
		
		var result = demoRestController.fetchingByQueryMethod("ii");
		
		// THEN
		assertEquals(emptyPersonFlux, result);
	}
	
	
}
