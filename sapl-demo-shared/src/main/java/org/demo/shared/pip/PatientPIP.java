package org.demo.shared.pip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.demo.domain.Relation;
import org.demo.domain.RelationRepository;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.api.validation.Number;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

	private final ObjectMapper mapper;
	private final RelationRepository relationRepo;

	@Attribute(name = "relatives")
	public JsonNode getRelations(@Number JsonNode value, Map<String, JsonNode> variables) {
		List<String> returnList = new ArrayList<>();
		int id = value.asInt();
		returnList.addAll(
				relationRepo.findByPatientid(id).stream().map(Relation::getUsername).collect(Collectors.toList()));
		return mapper.convertValue(returnList, JsonNode.class);
	}
}
