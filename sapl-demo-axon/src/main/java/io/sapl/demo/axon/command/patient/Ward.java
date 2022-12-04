package io.sapl.demo.axon.command.patient;

import lombok.Getter;

public enum Ward {
	ICCU("Intensive Cardiac Care Unit"), CCU("Critical Care Unit"), SICU("Surgical intensive care Unit"),
	GENERAL("General Ward"), NONE("Not Assigned");

	@Getter
	private String description;

	Ward(String description) {
		this.description = description;
	}
}