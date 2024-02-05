package io.sapl.springdatamongoreactivedemo.demo.repository.sapl;

import io.sapl.springdatamongoreactivedemo.demo.repository.Role;
import io.sapl.springdatamongoreactivedemo.demo.repository.User;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;


public class CustomReactiveMongoUserRepositoryImpl<T, ID> implements CustomReactiveMongoUserRepository<T, ID> {

    @SuppressWarnings({"unchecked"}) // domaintype is User and it is used in ProtectedReactiveMongoUserRepository
    public Flux<T> customRepositoryMethod() {

        return (Flux<T>) Flux.just(
                new User(new ObjectId("64de2fb8375aabd24878daa4"), "Malinda", "Perrot", 53, Role.ADMIN, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed336"), "Emerson", "Rowat", 82, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed337"), "Yul", "Barukh", 79, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed338"), "Terrel", "Woodings", 96, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed339"), "Martino", "Bartolijn", 33, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed33a"), "Konstantine", "Hampton", 96, Role.USER, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed33b"), "Cathleen", "Simms", 25, Role.ADMIN, Boolean.FALSE),
                new User(new ObjectId("64de3bd9fbf82799677ed33c"), "Adolphe", "Streeton", 46, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33d"), "Alessandro", "Tomaskov", 64, Role.USER, Boolean.TRUE),
                new User(new ObjectId("64de3bd9fbf82799677ed33e"), "Hobie", "Maddinon", 32, Role.USER, Boolean.FALSE));
    }
}

