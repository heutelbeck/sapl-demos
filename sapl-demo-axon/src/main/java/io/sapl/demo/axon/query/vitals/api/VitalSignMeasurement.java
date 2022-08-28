package io.sapl.demo.axon.query.vitals.api;

import java.time.Instant;

// @formatter:off
public record VitalSignMeasurement (String monitorDeviceId, String type,	String value, String unit, Instant timestamp) {};
// @formatter:on
