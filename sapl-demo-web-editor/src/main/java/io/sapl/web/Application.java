package io.sapl.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.vaadin.flow.spring.annotation.EnableVaadin;

@SpringBootApplication
@EnableVaadin("io.sapl.web")
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
