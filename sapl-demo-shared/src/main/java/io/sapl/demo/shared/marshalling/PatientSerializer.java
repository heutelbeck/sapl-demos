package io.sapl.demo.shared.marshalling;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.sapl.demo.domain.Patient;

import java.io.IOException;

public class PatientSerializer extends StdSerializer<Patient> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PatientSerializer() {
		this(null);
	}

	public PatientSerializer(Class<Patient> t) {
		super(t);
	}

	@Override
	public void serialize(Patient patient, JsonGenerator jsonGenerator, SerializerProvider serializer)
			throws IOException {

		jsonGenerator.writeStartObject();
		jsonGenerator.writeFieldName("id");
		jsonGenerator.writeNumber(patient.getId());
		jsonGenerator.writeStringField("name", patient.getName());
		jsonGenerator.writeStringField("attendingDoctor", patient.getAttendingDoctor());
		jsonGenerator.writeStringField("attendingNurse", patient.getAttendingNurse());
		jsonGenerator.writeEndObject();

	}

}
