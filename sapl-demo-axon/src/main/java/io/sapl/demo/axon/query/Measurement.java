package io.sapl.demo.axon.query;

import java.time.Instant;

// @formatter:off
public record Measurement (String monitorDeviceId, String type,	String value, String unit, Instant timestamp) {};
// @formatter:on
