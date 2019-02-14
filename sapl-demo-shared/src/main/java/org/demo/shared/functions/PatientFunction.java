package org.demo.shared.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.demo.domain.Relation;
import org.demo.domain.RelationRepo;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.api.functions.Function;
import io.sapl.api.functions.FunctionException;
import io.sapl.api.functions.FunctionLibrary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FunctionLibrary(name = "patientfunction", description = "")
public class PatientFunction {

	private final RelationRepo relationRepo;

	private final ObjectMapper om = new ObjectMapper();

	@Function(name = "related")
	public JsonNode getRelations(JsonNode value) throws FunctionException {
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
