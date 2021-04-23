package com.gupb.manager.repositories;

import com.gupb.manager.model.Requirement;
import org.springframework.data.repository.CrudRepository;

public interface RequirementRepository extends CrudRepository<Requirement, Integer> {
}
