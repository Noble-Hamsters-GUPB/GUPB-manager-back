package com.gupb.manager.repositories;

import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequirementRepository extends CrudRepository<Requirement, Integer> {
    List<Requirement> findByTournament(Tournament tournament);
}
