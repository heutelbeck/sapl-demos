/*
 * Copyright (C) 2017-2026 Dominic Heutelbeck (dominic@heutelbeck.com)
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
package io.sapl.mvc.demo.pip;

import io.sapl.api.attributes.Attribute;
import io.sapl.api.attributes.PolicyInformationPoint;
import io.sapl.api.model.*;
import io.sapl.mvc.demo.domain.Relation;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * This class realizes a custom Policy Information Point (PIP) which can
 * retrieve attributes of patients from the Patient and Relation repositories.
 * <p/>
 * This PIP is registered under the name 'patient'.
 * <p/>
 * As it is registered as a Spring @Service, the embedded Spring SAPL PDP will
 * pick it up automatically during the autoconfiguration process.
 */
@Service
@RequiredArgsConstructor
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

    private final ObjectMapper mapper;

    private final PIPRelationRepository relationRepo;

    private final PIPPatientRepository patientRepo;

    /**
     * This attribute is accessed in a SAPL policy through an expression like this:
     * <p/>
     * resource.patientId.<patient.relatives>
     * <p/>
     * The value on the left-hand side of the <> expression is fed into the function
     * as the first parameter as a Val. The attribute is identified within the <>
     * and consists of the name of the PIP and the name of the attribute:
     * 'patient.relatives' Import statements in a policy can be used to provide a
     * shorthand in the policy.
     * <p/>
     * This implementation does not track changes in the repository, i.e. this is a
     * non-streaming PIP.
     * 
     * @param leftHandValue     the id of the patient. This parameter must be a number, as
     *                  defined by the @Number annotation.
     * @return the relatives of the patient as registered in the relationRepo.
     *
     */
    @Attribute(name = "relatives")
    public Flux<Value> getRelations(NumberValue leftHandValue) {
        final List<Relation> relations     = relationRepo.findByPatientId(leftHandValue.value().longValue());
        final List<TextValue>   relationNames = relations.stream().map(Relation::getUsername).map(Value::of).toList();
        final ArrayValue     jsonNode      = ArrayValue.builder().addAll(relationNames).build();
        return Flux.just(jsonNode);
    }

    /**
     * This attribute is accessed in a SAPL policy through an expression like this:
     * <p/>
     * resource.patientId.<patient.patientRecord>
     * <p/>
     * The value on the left-hand side of the <> expression is fed into the function
     * as the first parameter as a Val. The attribute is identified within the <>
     * and consists of the name of the PIP and the name of the attribute:
     * 'patient.patientRecord' Import statements in a policy can be used to provide
     * a shorthand in the policy.
     * <p/>
     * This implementation does not track changes in the repository, i.e. this is a
     * non-streaming PIP.
     * 
     * @param patientId the id of the patient. This parameter must be a number, as
     *                  defined by the @Number annotation.
     * @return the patient record or null. This is a Flux containing only one value.
     *
     */
    @Attribute(name = "patientRecord")
    public Flux<Value> getPatientRecord(NumberValue patientId) {
        try {
            val maybePatient  = patientRepo.findById(patientId.value().longValue());
            if(maybePatient.isEmpty()) {
                return Flux.just(Value.NULL);
            }
            final JsonNode jsonNode = mapper.convertValue(maybePatient.get(), JsonNode.class);
            return Flux.just(ValueJsonMarshaller.fromJsonNode(jsonNode));
        } catch (IllegalArgumentException  e) {
            return Flux.just(Value.NULL);
        }
    }

}
