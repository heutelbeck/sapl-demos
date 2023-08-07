package io.sapl.pdp.multitenenat.demo.controller;

import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.pdp.multitenenat.demo.domain.Patient;
import io.sapl.pdp.multitenenat.demo.domain.PatientRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class PatientsController {

	public static final JsonNodeFactory JSON = JsonNodeFactory.instance;

	private final PatientRepository service;

	@GetMapping(value = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Optional<Patient> patient(@PathVariable long id) {
		return service.findById(id);
	}
}
