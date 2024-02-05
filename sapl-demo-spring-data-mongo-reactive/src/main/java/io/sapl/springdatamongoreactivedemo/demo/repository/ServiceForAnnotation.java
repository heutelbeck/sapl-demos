package io.sapl.springdatamongoreactivedemo.demo.repository;

import org.springframework.stereotype.Service;


@Service
public class ServiceForAnnotation {

    public String setSubject(Role role) {

        return role.name();
    }
}
