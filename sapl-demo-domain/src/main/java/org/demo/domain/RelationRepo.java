package org.demo.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RelationRepo extends CrudRepository<Relation, Integer> {

	List<Relation> findByPatientid(int id);

}
