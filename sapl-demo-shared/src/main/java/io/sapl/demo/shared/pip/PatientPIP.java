package io.sapl.demo.shared.pip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.pip.Attribute;
import io.sapl.api.pip.PolicyInformationPoint;
import io.sapl.demo.domain.Relation;
import io.sapl.demo.repository.RelationRepo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PolicyInformationPoint(name = "patient", description = "retrieves information about patients")
public class PatientPIP {

	private Optional<RelationRepo> relationRepo = Optional.empty();

	private final ObjectMapper om = new ObjectMapper();

	private RelationRepo getRelationRepo() {
		if (!relationRepo.isPresent()) {
			relationRepo = Optional.of(ApplicationContextProvider.getApplicationContext().getBean(RelationRepo.class));
		}
		return relationRepo.get();
	}

	@Attribute(name = "related")
	public JsonNode getRelations(JsonNode value, Map<String, JsonNode> variables) {
		List<String> returnList = new ArrayList<>();
		try {
			int id = Integer.parseInt(value.asText());

			returnList.addAll(getRelationRepo().findByPatientid(id).stream().map(Relation::getUsername)
					.collect(Collectors.toList()));

		} catch (NumberFormatException e) {
			LOGGER.error("getRelations couldn't parse the value to Int", e);
		}
		JsonNode result = om.convertValue(returnList, JsonNode.class);
		return result;
	}
}
