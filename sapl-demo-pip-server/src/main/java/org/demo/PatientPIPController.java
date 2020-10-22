package org.demo;

import org.demo.pip.PatientPIP;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.sapl.api.interpreter.Val;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * REST controller providing endpoints for the policy information point
 * providing patient attributes. The endpoints can be connected using the client
 * {@code HttpPolicyInformationPoint} of the SAPL policy engine.
 */
@RestController
@RequestMapping("rest/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientPIPController {

	private final PatientPIP patientPIP;

	private final ObjectMapper objectMapper;

	/**
	 * Delegates to {@link PatientPIP#getRelations(JsonNode, Map)}.
	 * 
	 * @param id the id of the patient whose relatives should be returned.
	 * @return a flux emitting an object containing the current list of names of
	 *         users being related to the patient with the given id.
	 * @see PatientPIP#getRelations(JsonNode, Map)
	 */
	@GetMapping(value = "{id}/relatives", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<JsonNode> getRelations(@PathVariable String id) {
		final Flux<JsonNode> relations = patientPIP.getRelations(Val.of(id), null).map(Val::get);
		// PatientPIP.getRelations(id) returns a Flux emitting ArrayNodes containing the
		// names of all relatives. When ArrayNodes are serialized for a Flux
		// (mediaType=application/stream+json), Spring's Jackson2Tokenizer passes each
		// array item to the ObjectMapper to serialize and flush it
		// (see org.springframework.http.codec.json.Jackson2Tokenizer.tokenize(
		// dataBuffers, jsonFactory, tokenizeArrayElements)).
		// This results in a stream of names instead of a stream of arrays containing
		// names. Expressions like 'subject.name in pipUrl.<http.get>' in the
		// policies will not work, because 'pipUrl.<http.get>' is a string, not an
		// array.
		// As it is not possible to configure the Jackson2Tokenizer to not treat
		// ArrayNodes like this, we have to work around the problem by wrapping the
		// array
		// with an object before serializing it. The expressions in the policies must
		// then be adjusted to 'subject.name in pipUrl.<http.get>.relatives'.
		return relations.map(jsonNode -> {
			final ObjectNode objectNode = objectMapper.createObjectNode();
			final ArrayNode relatives = objectNode.putArray("relatives");
			relatives.addAll((ArrayNode) jsonNode);
			return (JsonNode) objectNode;
		}).doOnNext(jsonNode -> log.trace("PatientPIPController.getRelations() returns {}", jsonNode));
	}

	/**
	 * Delegates to {@link PatientPIP#getPatientRecord(JsonNode, Map)}.
	 * 
	 * @param id the id of the patient whose data record should be returned.
	 * @return a flux emitting the current data record of the patient identified by
	 *         the given id.
	 * @see PatientPIP#getPatientRecord(JsonNode, Map)
	 */
	@GetMapping(value = "{id}", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<JsonNode> getPatientRecord(@PathVariable String id) {
		return patientPIP.getPatientRecord(Val.of(id), null)
				.doOnNext(jsonNode -> log.trace("PatientPIPController.getPatientRecord() returns {}", jsonNode))
				.map(Val::get);
	}

}
