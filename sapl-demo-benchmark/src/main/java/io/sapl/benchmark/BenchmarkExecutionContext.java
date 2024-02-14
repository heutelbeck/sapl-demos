package io.sapl.benchmark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sapl.api.pdp.AuthorizationSubscription;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BenchmarkExecutionContext {

    public String rsocketHost;
    public Integer rsocketPort;
    public String basic_client_key;
    public String basic_client_secret;
    public String api_key;
    public String oauth2_client_secret;
    public String oauth2_scope;
    public String oauth2_token_uri;
    public String api_key_header;
    public String http_base_url;
    public boolean useNoAuth;
    public boolean useBasicAuth;
    public boolean useAuthApiKey;
    public boolean useOauth2;
    public String oauth2_client_id;
    public boolean use_ssl;
    public AuthorizationSubscription authorizationSubscription;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static BenchmarkExecutionContext fromString(String jsonString){
        try {
            return MAPPER.readValue(jsonString, BenchmarkExecutionContext.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJsonString(){
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
