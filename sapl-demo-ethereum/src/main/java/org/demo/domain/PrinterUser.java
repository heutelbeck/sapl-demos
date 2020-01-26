package org.demo.domain;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrinterUser implements Serializable, Cloneable {

	private static final long serialVersionUID = -8397038584366614376L;

	private String firstName;

	private String lastName;

	private String ethereumAddress;

	private LocalDate birthDate;

	public PrinterUser() {

	}

	public PrinterUser(String firstName, String lastName, String ethereumAddress, LocalDate birthDate) {
		setFirstName(firstName);
		setLastName(lastName);
		setEthereumAddress(ethereumAddress);
		setBirthDate(birthDate);
	}

	@Override
	public PrinterUser clone() throws CloneNotSupportedException {
		return (PrinterUser) super.clone();
	}

}
