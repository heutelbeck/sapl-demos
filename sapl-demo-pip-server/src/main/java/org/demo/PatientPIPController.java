package org.demo;

import org.demo.shared.pip.PatientPIP;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("rest/patient")
@RequiredArgsConstructor
public class PatientPIPController {

    private final PatientPIP patientPIP;

    @GetMapping(value = "related/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonNode getRelations(@PathVariable String id) {
        final JsonNode relations = patientPIP.getRelations(JsonNodeFactory.instance.textNode(id), null);
        return relations;
    }

}
