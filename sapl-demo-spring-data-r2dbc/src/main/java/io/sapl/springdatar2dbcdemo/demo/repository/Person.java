package io.sapl.springdatar2dbcdemo.demo.repository;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Getter
@Setter
@Table("person")
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    int     id;
    String  firstname;
    String  lastname;
    int     age;
    Role    role;
    boolean active;
}

