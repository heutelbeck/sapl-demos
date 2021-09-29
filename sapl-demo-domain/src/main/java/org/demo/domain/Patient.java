package org.demo.domain;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String medicalRecordNumber;

    String name;

    String icd11Code;

    String diagnosisText;

	String attendingDoctor;

	String attendingNurse;

    String phoneNumber;

    String roomNumber;

    public static Patient clone(Patient template) {
        return new Patient(template.id, template.medicalRecordNumber, template.name, template.icd11Code,
				template.diagnosisText, template.attendingDoctor, template.attendingNurse, template.phoneNumber,
				template.roomNumber);
    }

}
