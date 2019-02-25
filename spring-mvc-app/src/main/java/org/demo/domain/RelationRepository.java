package org.demo.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RelationRepository extends CrudRepository<Relation, Long> {

	List<Relation> findByPatientid(Long id);

}
