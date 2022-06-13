package io.sapl.demo.axon.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MedicalRecordSummaryAPI {

	/* Queries */

	@Value
	public static class FetchMedicalRecordSummariesQuery {
		int offset;
		int limit;
	}

	@Value
	public static class FetchMedicalRecordSummaryQuery {
		String id;
	}

	@Value
	public static class FetchPulseQuery {
		String id;
	}

	@Value
	public static class FetchSinglePulseQuery {
		String id;
	}

	@Value
	public static class FetchOxygenSaturationQuery {
		String id;
	}

	public static class CountMedicalRecordSummariesQuery {
	}

	@Value
	public static class CountMedicalRecordSummariesResponse {
		int  count;
		long lastEvent;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class MedicalRecordSummary {
		String id;
		String patientName;
		double pulse;
		double oxygenSaturation;

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ReducedRecord {
		String patientName;
		double value;
	}

	public static class CountChangedUpdate {

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PulseRecord {
		double pulse;
	}

}
