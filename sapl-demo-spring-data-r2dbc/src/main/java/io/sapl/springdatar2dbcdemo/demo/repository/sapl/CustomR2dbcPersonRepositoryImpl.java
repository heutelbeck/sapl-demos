package io.sapl.springdatar2dbcdemo.demo.repository.sapl;

import io.sapl.springdatar2dbcdemo.demo.repository.Person;
import io.sapl.springdatar2dbcdemo.demo.repository.Role;
import reactor.core.publisher.Flux;


public class CustomR2dbcPersonRepositoryImpl<T, ID> implements CustomR2dbcPersonRepository<T, ID> {

    @SuppressWarnings({"unchecked"}) // domaintype is Person
    public Flux<T> customRepositoryMethod() {
        return (Flux<T>) Flux.just(
                new Person(1, "Malinda", "Perrot", 53, Role.ADMIN, true),
                new Person(2, "Emerson", "Rowat", 82, Role.USER, false),
                new Person(3, "Yul", "Barukh", 79, Role.USER, true),
                new Person(4, "Terrel", "Woodings", 96, Role.USER, true),
                new Person(5, "Martino", "Bartolijn", 33, Role.USER, false),
                new Person(6, "Konstantine", "Hampton", 96, Role.USER, false),
                new Person(7, "Cathleen", "Simms", 25, Role.ADMIN, false),
                new Person(8, "Adolphe", "Streeton", 46, Role.USER, true),
                new Person(9, "Alessandro", "Tomaskov", 64, Role.USER, true),
                new Person(10, "Hobie", "Maddinon", 32, Role.USER, false),
                new Person(11, "Franni", "Mingey", 57, Role.ADMIN, false),
                new Person(12, "Giraldo", "Scade", 83, Role.USER, true),
                new Person(13, "Pooh", "Cocks", 19, Role.ADMIN, true),
                new Person(14, "Mario", "Albinson", 54, Role.USER, false),
                new Person(15, "Olav", "Hoopper", 31, Role.USER, true),
                new Person(16, "Tuckie", "Morfell", 35, Role.USER, true),
                new Person(17, "Sylas", "Bickerstasse", 66, Role.USER, true),
                new Person(18, "Kacey", "Angell", 94, Role.USER, false),
                new Person(19, "Dame", "Negri", 67, Role.USER, true));
    }

}

