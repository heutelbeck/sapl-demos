package io.sapl.demo.axon.query;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VitalSignsRepository extends MongoRepository<VitalSignsDocument, String> {

	@Query("{ connectedSensors : ?0}")
	Optional<VitalSignsDocument> findByMonitorId(String monitorId);
		
}
