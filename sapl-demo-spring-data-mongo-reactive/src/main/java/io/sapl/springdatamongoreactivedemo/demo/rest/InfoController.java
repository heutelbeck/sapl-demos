package io.sapl.springdatamongoreactivedemo.demo.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class InfoController {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @GetMapping("/info")
    public JsonNode info() throws JsonProcessingException {
        var firstFuncJsonString = """
                {
                  "method name based (1)": {
                    "name": "findAllByAgeAfter(int age)",
                    "query": "{'age': {'$gt': ?0}}",
                    "manipulated query": "{'age': {'$gt': ?0}, 'role': {'$eq': 'USER'}}",
                    "url": "http://localhost:8080/admin/findAllByAgeAfter/90",
                    "url with sapl": "http://localhost:8080/user/findAllByAgeAfter/90",
                    "policy": [
                      "policy \\"permit_general_protection_reactive_user_repository\\"",
                      "permit",
                      "where",
                      "     action == \\"general_protection\\"",
                      "obligation {",
                      "             \\"type\\": \\"mongoQueryManipulation\\"",
                      "             \\"conditions\\": [",
                      "                             \\"{'role': {'$eq': 'USER'}}\\"",
                      "                          ]",
                      "           }",
                      "obligation {",
                      "             \\"type\\": \\"filterJsonContent\\"",
                      "             \\"conditions\\": [",
                      "           {",
                      "             \\"type\\": \\"blacken\\",",
                      "             \\"path\\": \\"$.lastname\\",",
                      "             \\"discloseLeft\\": 2\\"",
                      "           }",
                      "           {",
                      "             \\"type\\": \\"delete\\",",
                      "             \\"path\\": \\"$._id\\"",
                      "           }",
                      "           ]",
                      "}"
                    ]
                  },
                  "query annotation based": {
                    "name": "fetchAllUsersWithQueryMethod(String lastnameContains)",
                    "query": "{'lastname': {'$regex': ?0}}",
                    "manipulated query": "{'lastname': {'$regex': ?0}, 'role': {'$eq': 'USER'}}",
                    "url": "http://localhost:8080/admin/fetchingByQueryMethodLastnameContains/ell",
                    "url with sapl": "http://localhost:8080/user/fetchingByQueryMethodLastnameContains/ell",
                    "policy": [
                      "policy \\"permit_general_protection_reactive_user_repository\\"",
                      "permit",
                      "where",
                      "     action == \\"general_protection\\"",
                      "obligation {",
                      "             \\"type\\": \\"mongoQueryManipulation\\"",
                      "             \\"conditions\\": [",
                      "                             \\"{'role': {'$eq': 'USER'}}\\"",
                      "                          ]",
                      "           }",
                      "obligation {",
                      "             \\"type\\": \\"filterJsonContent\\"",
                      "             \\"conditions\\": [",
                      "           {",
                      "             \\"type\\": \\"blacken\\",",
                      "             \\"path\\": \\"$.lastname\\",",
                      "             \\"discloseLeft\\": 2\\"",
                      "           }",
                      "           {",
                      "             \\"type\\": \\"delete\\",",
                      "             \\"path\\": \\"$._id\\"",
                      "           }",
                      "           ]",
                      "}"
                    ]
                  },
                  "custom method based": {
                    "name": "customRepositoryMethod",
                    "query": "custom defined queries cannot be manipulated. The objects from the database are filtered using the 'jsonContentFilterPredicate' handler.",
                    "filtering": "age >= 90",
                    "url": "http://localhost:8080/admin/customRepositoryMethod",
                    "url with sapl": "http://localhost:8080/user/customRepositoryMethod",
                    "policy": [
                      "policy \\"permit_custom_repository_method_reactive_user_repository\\"",
                      "permit",
                      "where",
                      "     action == \\"custom_repository_method\\"",
                      "obligation {",
                      "             \\"type\\": \\"filterJsonContent\\"",
                      "             \\"conditions\\": [",
                      "           {",
                      "             \\"type\\": \\"blacken\\",",
                      "             \\"path\\": \\"$.lastname\\",",
                      "             \\"discloseLeft\\": 2\\"",
                      "           }",
                      "           {",
                      "             \\"type\\": \\"delete\\",",
                      "             \\"path\\": \\"$._id\\"",
                      "           }",
                      "           ]",
                      "}",
                      "obligation {",
                      "             \\"type\\": \\"jsonContentFilterPredicate\\"",
                      "             \\"conditions\\": [",
                      "           {",
                      "             \\"type\\": \\">=\\",",
                      "             \\"path\\": \\"$.age\\",",
                      "             \\"value\\": 90\\"",
                      "           }",
                      "           ]",
                      "}"
                    ]
                  },
                  "method name based (2)": {
                    "name": "findAllByAgeAfterAndRole(int age, String role)",
                    "query": "{'age': {'$gt': ?0}, 'role': {'$eq': ?1}}",
                    "manipulated query": "{'age': {'$gt': ?0}, 'role': {'$eq': 'USER'}, 'active': {'$eq': true}}",
                    "url": "http://localhost:8080/admin/findAllByAgeAfterAndRole/18/ADMIN",
                    "url with sapl": "http://localhost:8080/user/findAllByAgeAfterAndRole/18/USER",
                    "policy": [
                      "policy \\"permit_general_protection_reactive_user_repository\\"",
                      "permit",
                      "where",
                      "     action == \\"find_all_by_age\\"",
                      "obligation {",
                      "             \\"type\\": \\"mongoQueryManipulation\\"",
                      "             \\"conditions\\": [",
                      "                             \\"{'active': {'$eq': 'true}}\\"",
                      "                          ]",
                      "           }",
                      "obligation {",
                      "             \\"type\\": \\"filterJsonContent\\"",
                      "             \\"conditions\\": [",
                      "           {",
                      "             \\"type\\": \\"blacken\\",",
                      "             \\"path\\": \\"$.lastname\\",",
                      "             \\"discloseLeft\\": 2\\"",
                      "           }",
                      "           {",
                      "             \\"type\\": \\"delete\\",",
                      "             \\"path\\": \\"$._id\\"",
                      "           }",
                      "           ]",
                      "}"
                    ]
                  }
                }
                """;


        return MAPPER.readTree(firstFuncJsonString);
    }
}
