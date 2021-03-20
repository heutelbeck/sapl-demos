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

import io.sapl.api.interpreter.PolicyEvaluationException;
import io.sapl.api.interpreter.Val;
import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Number;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * This class realizes a custom Policy Information Point (PIP) which can
 * retrieve attributes of patients from the Patient and Relation repositories.
 * 
 * This PIP is registered under the name 'patient'.
 * 
 * As it is registered as a Spring @Service, the embedded Spring SAPL PDP will
 * pick it up automatically during the auto-configuration process.
 */
@Service
@RequiredArgsConstructor
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

	private final ObjectMapper mapper;

	private final RelationRepository relationRepo;

	private final PatientRepository patientRepo;

	/**
	 * This attribute is accessed in a SAPL policy through an expression like this:
	 * 
	 * resource.patientId.<patient.relatives>
	 * 
	 * The value on the left-hand side of the <> expression is fed into the function
	 * as the first parameter as a Val. The attribute is identified within the <>
	 * and consists of the name of the PIP and the name of the attribute:
	 * 'patient.relatives' Import statements in a policy can be used to provide a
	 * shorthand in the policy.
	 * 
	 * This implementation does not track changes in the repository, i.e. this is a
	 * non-streaming PIP.
	 * 
	 * @param patientId the id of the patient. This parameter must be a number, as
	 *                  defined by the @Number annotation.
	 * @param variables the variables in the current evaluation context
	 * @return the relatives of the patient as registered in the relationRepo.
	 * 
	 */
	@Attribute(name = "relatives")
	public Flux<Val> getRelations(@Number Val value, Map<String, JsonNode> variables) {
		final List<Relation> relations = relationRepo.findByPatientid(value.get().asLong());
		final List<String> relationNames = relations.stream().map(Relation::getUsername).collect(Collectors.toList());
		final JsonNode jsonNode = mapper.convertValue(relationNames, JsonNode.class);
		return Flux.just(Val.of(jsonNode));
	}

	/**
	 * This attribute is accessed in a SAPL policy through an expression like this:
	 * 
	 * resource.patientId.<patient.patientRecord>
	 * 
	 * The value on the left-hand side of the <> expression is fed into the function
	 * as the first parameter as a Val. The attribute is identified within the <>
	 * and consists of the name of the PIP and the name of the attribute:
	 * 'patient.patientRecord' Import statements in a policy can be used to provide
	 * a shorthand in the policy.
	 * 
	 * This implementation does not track changes in the repository, i.e. this is a
	 * non-streaming PIP.
	 * 
	 * @param patientId the id of the patient. This parameter must be a number, as
	 *                  defined by the @Number annotation.
	 * @param variables the variables in the current evaluation context
	 * @return the patient record or null. This is a Flux containing only one value.
	 * 
	 */
	@Attribute(name = "patientRecord")
	public Flux<Val> getPatientRecord(@Number Val patientId, Map<String, JsonNode> variables) {
		try {
			final Patient patient = patientRepo.findById(patientId.get().asLong())
					.orElseThrow(PolicyEvaluationException::new);
			final JsonNode jsonNode = mapper.convertValue(patient, JsonNode.class);
			return Flux.just(Val.of(jsonNode));
		} catch (IllegalArgumentException | PolicyEvaluationException e) {
			return Flux.just(Val.NULL);
		}
	}

}
