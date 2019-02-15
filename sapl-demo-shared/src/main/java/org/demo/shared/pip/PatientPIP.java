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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

	private final RelationRepository relationRepo;

	private final ObjectMapper om = new ObjectMapper();

	@Attribute(name = "related")
	public JsonNode getRelations(JsonNode value, Map<String, JsonNode> variables) {
		List<String> returnList = new ArrayList<>();
		try {
			int id = Integer.parseInt(value.asText());

			returnList.addAll(
					relationRepo.findByPatientid(id).stream().map(Relation::getUsername).collect(Collectors.toList()));

		} catch (NumberFormatException e) {
			LOGGER.error("getRelations couldn't parse the value to Int", e);
		}
		JsonNode result = om.convertValue(returnList, JsonNode.class);
		return result;
	}
}
