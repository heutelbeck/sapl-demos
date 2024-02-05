package io.sapl.springdatar2dbcdemo.demo.repository;

import org.springframework.stereotype.Service;


@Service
public class ServiceForAnnotation {

    public boolean setSubject(boolean active) {

        return active;
    }
}
