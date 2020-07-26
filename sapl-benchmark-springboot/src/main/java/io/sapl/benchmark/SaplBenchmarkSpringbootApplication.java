package io.sapl.benchmark;

import io.sapl.db.BenchmarkResultRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses= BenchmarkResultRepository.class)
public class SaplBenchmarkSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaplBenchmarkSpringbootApplication.class, args);
    }

}
