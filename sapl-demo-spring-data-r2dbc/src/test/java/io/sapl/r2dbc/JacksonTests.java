package io.sapl.r2dbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sapl.r2dbc.data.DemoData;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;

class JacksonTests {

    @Test
    void libUserWithDefaultMapper() throws JsonProcessingException {
        System.err.println("=== DEFAULT OBJECT MAPPER ===");
        val mapper = new ObjectMapper();
        val encoder = new BCryptPasswordEncoder();
        for (val user : DemoData.users(encoder)) {
            var json = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(user);
            System.err.println(json);
        }
    }

    @Test
    void libUserWithSecurityModules() throws JsonProcessingException {
        System.err.println("=== OBJECT MAPPER WITH SECURITY MODULES ===");
        val mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        val encoder = new BCryptPasswordEncoder();
        for (val user : DemoData.users(encoder)) {
            var json = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(user);
            System.err.println(json);
        }
    }

    @Test
    void authenticationWithDefaultMapper() throws JsonProcessingException {
        System.err.println("=== AUTHENTICATION WITH DEFAULT MAPPER ===");
        val mapper = new ObjectMapper();
        val encoder = new BCryptPasswordEncoder();
        for (val user : DemoData.users(encoder)) {
            val auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            var json = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(auth);
            System.err.println(json);
        }
    }

    @Test
    void authenticationWithSecurityModules() throws JsonProcessingException {
        System.err.println("=== AUTHENTICATION WITH SECURITY MODULES ===");
        val mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        val encoder = new BCryptPasswordEncoder();
        for (val user : DemoData.users(encoder)) {
            val auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
            var json = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(auth);
            System.err.println(json);
        }
    }
}
