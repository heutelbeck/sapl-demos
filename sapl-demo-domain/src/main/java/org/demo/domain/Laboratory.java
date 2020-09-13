package org.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Laboratory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Long patientId;

    String labInfo;

}
