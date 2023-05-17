package io.sapl.demo.axon.query.vitals;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import io.sapl.demo.axon.query.vitals.api.VitalSignsDocument;

public interface VitalSignsRepository extends MongoRepository<VitalSignsDocument, String> {

	@Query("{ connectedSensors : ?0}")
	Optional<VitalSignsDocument> findByMonitorId(String monitorId);

}
