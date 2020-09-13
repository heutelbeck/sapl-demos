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
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    Long patientId;

    String medicationInfo;

    String status; // e.g. PLANNED, ADMINISTERED

    Boolean prescription;
}
