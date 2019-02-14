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
	int id;

	String name;
	String diagnosis;
	String healthRecordNumber;
	String phoneNumber;
	String attendingDoctor;
	String attendingNurse;
	String roomNumber;

	public Patient(String name, String diagnosis, String healthRecordNumber, String phoneNumber, String attendingDoctor,
			String attendingNurse, String roomNumber) {
		this.name = name;
		this.diagnosis = diagnosis;
		this.healthRecordNumber = healthRecordNumber;
		this.phoneNumber = phoneNumber;
		this.attendingDoctor = attendingDoctor;
		this.attendingNurse = attendingNurse;
		this.roomNumber = roomNumber;
	}

	public static Patient clone(Patient template) {
		final Patient copy = new Patient(
				template.name,
				template.diagnosis,
				template.healthRecordNumber,
				template.phoneNumber,
				template.attendingDoctor,
				template.attendingNurse,
				template.roomNumber);
		copy.id = template.id;
		return copy;
	}
}
