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

	String name;
	String diagnosisText;
	String medicalRecordNumber;
	String icd11Code;
	String phoneNumber;
	String attendingDoctor;
	String attendingNurse;
	String roomNumber;

	public static Patient clone(Patient template) {
		return new Patient(template.id, template.name, template.diagnosisText, template.medicalRecordNumber,
				template.icd11Code, template.phoneNumber, template.attendingDoctor, template.attendingNurse,
				template.roomNumber);
	}
}
