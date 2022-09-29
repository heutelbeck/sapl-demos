package io.sapl.demo.axon.query.publisher.api;

public class PublisherAPI {
	// @formatter:off
	public record StreamNumbers		() {};
	public record StreamAllPatients () {};
	public record StreamPatient     (String patientId) {};
	// @formatter:on
}
