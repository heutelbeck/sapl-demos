package io.sapl.demo.domain;

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
public class Relation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;

	String username;
	int patientid;

	public Relation(String username, int patientid) {
		this.username = username;
		this.patientid = patientid;
	}

}
