package io.sapl.jwt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.sapl.demo.domain.Patient;
import io.sapl.demo.repository.PatientenRepo;
import io.sapl.spring.annotation.PdpAuthorize;

@RestController
@RequestMapping("/person/")
public class RestService {

	private static final String PHONENUMBER = "phoneNumber";

	@Autowired
	private PatientenRepo patientenRepo;

	@GetMapping("{id}")
	public ResponseEntity<Patient> loadPerson(@PathVariable int id) {
		Optional<Patient> patient = patientenRepo.findById(id);
		if (patient.isPresent()) {
			return new ResponseEntity<>(patient.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("readAll/{id}")
	@PdpAuthorize
	public Map<String, Object> loadPersonAll(@PathVariable int id, HttpServletRequest request) {
		Optional<Patient> patient = patientenRepo.findById(id);
		HashMap<String, Object> map = new HashMap<>();
		if (patient.isPresent()) {
			map.put("healthRecordNumber", patient.get().getHealthRecordNumber());
			map.put("diagnosis", patient.get().getDiagnosis());
			map.put(PHONENUMBER, patient.get().getPhoneNumber());
			map.put("name", patient.get().getName());
			map.put("id", patient.get().getId());
			return map;
		}
		return map;
	}

	@GetMapping("readDiag/{id}")
	@PdpAuthorize
	public Map<String, Object> loadPersonPart(@PathVariable int id, HttpServletRequest request) {
		Optional<Patient> patient = patientenRepo.findById(id);
		HashMap<String, Object> map = new HashMap<>();
		if (patient.isPresent()) {
			map.put("diagnosis", patient.get().getDiagnosis());
			map.put(PHONENUMBER, patient.get().getPhoneNumber());
			map.put("name", patient.get().getName());
			map.put("id", patient.get().getId());
			return map;
		}
		return map;
	}

	@GetMapping("readLim/{id}") // permission to all users: VISITOR, DOCTOR,ADMIN, NURSE
	public Map<String, Object> loadPersonLim(@PathVariable int id) {
		Optional<Patient> patient = patientenRepo.findById(id);
		HashMap<String, Object> map = new HashMap<>();
		if (patient.isPresent()) {
			map.put(PHONENUMBER, patient.get().getPhoneNumber());
			map.put("name", patient.get().getName());
			map.put("id", patient.get().getId());
			return map;
		}
		return map;
	}

	@GetMapping("list") // permission to all users: VISITOR, DOCTOR, NURSE, ADMIN
	public List<Map<String, Object>> loadPersonList() {
		List<Map<String, Object>> returnList = new ArrayList<>();
		patientenRepo.findAll().forEach(p -> {
			HashMap<String, Object> map = new HashMap<>();
			map.put(PHONENUMBER, p.getPhoneNumber());
			map.put("name", p.getName());
			map.put("id", p.getId());
			returnList.add(map);
		});
		return returnList;
	}

	@PostMapping
	@PdpAuthorize
	public ResponseEntity<Void> createPerson(@RequestBody Patient person, UriComponentsBuilder uriComponentsBuilder,
			HttpServletRequest request) {
		patientenRepo.save(person);
		UriComponents uriComponents = uriComponentsBuilder.path("/person/" + person.getId()).build();
		return ResponseEntity.created(uriComponents.toUri()).build();
	}

	@PutMapping("putALL/{id}")
	@PdpAuthorize
	public Patient update(@PathVariable int id, @RequestBody Patient person, HttpServletRequest request) {
		if (!patientenRepo.existsById(id)) {
			throw new IllegalArgumentException("not found");
		}
		return patientenRepo.save(person);
	}

	@PutMapping("{id}")
	@PdpAuthorize
	public Map<String, Object> updatePart(@PathVariable int id, @RequestBody Patient person,
			HttpServletRequest request) {
		if (!patientenRepo.existsById(id)) {
			throw new IllegalArgumentException("not found");
		}
		Patient patient = patientenRepo.findById(id).orElse(null);
		if (patient == null) {
			throw new IllegalArgumentException();
		}

		HashMap<String, Object> map = new HashMap<>();

		String newPhone = person.getPhoneNumber();
		patient.setPhoneNumber(newPhone);
		map.put(PHONENUMBER, newPhone);

		String newName = person.getName();
		patient.setName(newName);
		map.put("name", newName);

		map.put("id", id);

		return map;

	}

	@DeleteMapping("{id}")
	@PdpAuthorize
	public void delete(@PathVariable int id, HttpServletRequest request) {
		patientenRepo.deleteById(id);
	}

}
