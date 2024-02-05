package io.sapl.springdatamongoreactivedemo.demo.repository;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    ObjectId _id;
    String   firstname;
    String   lastname;
    Integer  age;
    Role     role;
    Boolean  active;
}

