package io.sapl.demo.axon.query.vitals.api;

import java.time.Instant;

import io.sapl.demo.axon.command.MonitorType;

// @formatter:off
public record VitalSignMeasurement (String monitorDeviceId, MonitorType type, String value, String unit, Instant timestamp) {};
// @formatter:on
