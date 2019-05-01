package org.demo.pip;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.demo.domain.Patient;
import org.demo.domain.PatientRepository;
import org.demo.domain.Relation;
import org.demo.domain.RelationRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.AttributeException;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Number;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

	private final ObjectMapper mapper;
	private final RelationRepository relationRepo;
	private final PatientRepository patientRepo;

	@Attribute(name = "relatives")
	public Flux<JsonNode> getRelations(@Number JsonNode value, Map<String, JsonNode> variables) {
		final List<Relation> relations = relationRepo.findByPatientid(value.asLong());
		final List<String> relationNames = relations.stream().map(Relation::getUsername).collect(Collectors.toList());
		final JsonNode jsonNode = mapper.convertValue(relationNames, JsonNode.class);
		return Flux.just(jsonNode);
	}

	@Attribute(name = "patientRecord")
	public Flux<JsonNode> getPatientRecord(@Number JsonNode patientId, Map<String, JsonNode> variables) {
		try {
			final Patient patient = patientRepo.findById(patientId.asLong()).orElseThrow(AttributeException::new);
			final JsonNode jsonNode = mapper.convertValue(patient, JsonNode.class);
			return Flux.just(jsonNode);
		} catch (IllegalArgumentException | AttributeException e) {
			return Flux.just(JsonNodeFactory.instance.nullNode());
		}
	}
}
