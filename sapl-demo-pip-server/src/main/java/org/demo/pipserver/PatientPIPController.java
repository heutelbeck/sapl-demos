package org.demo.pipserver;

import org.demo.domain.RelationRepo;
import org.demo.shared.pip.PatientPIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@RestController
@RequestMapping("rest/patient")
public class PatientPIPController {

    @Autowired
    private RelationRepo relationRepo;

    @GetMapping(value = "related/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getRelations(@PathVariable String id) {
        final PatientPIP patientPIP = new PatientPIP(relationRepo);
        final JsonNode relations = patientPIP.getRelations(JsonNodeFactory.instance.textNode(id), null);
        return relations;
    }

}
