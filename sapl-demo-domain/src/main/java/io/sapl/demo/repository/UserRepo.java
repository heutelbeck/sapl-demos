package io.sapl.demo.repository;

import org.springframework.data.repository.CrudRepository;

import io.sapl.demo.domain.User;

public interface UserRepo extends CrudRepository<User, String> {
}
