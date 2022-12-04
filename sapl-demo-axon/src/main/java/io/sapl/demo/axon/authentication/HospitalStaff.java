package io.sapl.demo.axon.authentication;

import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.sapl.demo.axon.command.patient.Ward;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class HospitalStaff extends User implements UserDetails {

	@Getter
	private Ward     assignedWard;
	@Getter
	private Position position;

	public HospitalStaff(String username, Ward assignedWard, Position position, String password) {
		super(username, password, true, true, true, true, List.of());
		this.assignedWard = assignedWard;
		this.position     = position;
	}

}