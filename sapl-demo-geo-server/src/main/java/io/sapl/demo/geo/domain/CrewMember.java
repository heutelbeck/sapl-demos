package io.sapl.demo.geo.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CrewMember implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	String username;
	String password;
	String role;
	boolean isActive;
}
