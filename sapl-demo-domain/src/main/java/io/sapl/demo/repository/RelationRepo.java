package io.sapl.demo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import io.sapl.demo.domain.Relation;

public interface RelationRepo extends CrudRepository<Relation, Integer> {

	List<Relation> findByPatientid(int id);

}
