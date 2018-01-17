package io.sapl.demo.domain;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = 1;

	@Id
	String name;

	String password;
	boolean disabled;

	ArrayList<String> functions; // DOCTOR , NURSE , VISITOR, ADMIN

}
