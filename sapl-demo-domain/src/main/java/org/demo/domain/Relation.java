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
public class Relation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	String username;

	Long patientid;

	public Relation(String username, Long patientid) {
		this.username = username;
		this.patientid = patientid;
	}

}
