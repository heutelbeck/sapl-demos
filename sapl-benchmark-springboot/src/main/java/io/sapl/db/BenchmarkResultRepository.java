package io.sapl.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenchmarkResultRepository extends MongoRepository<BenchmarkResult, String> {
}
