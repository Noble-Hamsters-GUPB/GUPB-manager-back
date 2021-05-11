package com.gupb.manager.repositories;

import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.Team;
import org.springframework.data.jpa.repository.Query;
import com.gupb.manager.model.Round;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequirementRepository extends CrudRepository<Requirement, Integer> {
    @Override
    @Query("select requirement from Requirement requirement join fetch requirement.tournament tournament join fetch requirement.requestedBy")
    List<Requirement> findAll();
}
