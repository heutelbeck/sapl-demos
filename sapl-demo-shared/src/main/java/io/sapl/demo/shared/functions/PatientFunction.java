package io.sapl.demo.shared.functions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sapl.api.functions.Function;
import io.sapl.api.functions.FunctionException;
import io.sapl.api.functions.FunctionLibrary;
import io.sapl.demo.domain.Relation;
import io.sapl.demo.repository.RelationRepo;
import io.sapl.demo.shared.pip.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@FunctionLibrary(name = "patientfunction", description = "")
public class PatientFunction {


    private Optional<RelationRepo> relationRepo = Optional.empty();

    private final ObjectMapper om = new ObjectMapper();

    private RelationRepo getRelationRepo(){
        LOGGER.debug("GetRelationRepo...");
        if(!relationRepo.isPresent()){
            LOGGER.debug("RelRepo not present...");
            ApplicationContext context = ApplicationContextProvider.getApplicationContext();
            LOGGER.debug("Context found: {}", context);
            relationRepo = Optional.of(ApplicationContextProvider.getApplicationContext().getBean(RelationRepo.class));
        }
        LOGGER.debug("Found required instance of RelationRepo: {}", relationRepo.isPresent());
        return relationRepo.get();
    }

    @Function(name = "related")
    public JsonNode getRelations ( JsonNode value )  throws FunctionException {
        LOGGER.debug("Entering getRelations");
        List<String> returnList = new ArrayList<>();
        try{
            int id = Integer.parseInt(value.asText());
            LOGGER.debug("Entering getRelations. ID: {}", id);

            returnList.addAll(getRelationRepo().findByPatientid(id).stream()
                    .map(Relation::getUsername)
                    .collect(Collectors.toList()));

        }catch(NumberFormatException e){
            LOGGER.debug("getRelations couldn't parse the value to Int", e);
        }
        JsonNode result = om.convertValue(returnList, JsonNode.class);
        LOGGER.debug("Result: {}", result);
        return result;
    }
}
