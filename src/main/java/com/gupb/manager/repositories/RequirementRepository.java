package com.gupb.manager.repositories;

import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.model.Round;
import org.springframework.data.jpa.repository.Query;
import com.gupb.manager.model.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RequirementRepository extends CrudRepository<Requirement, Integer> {
    List<Requirement> findByTournament(Tournament tournament);

    @Override
    @Query("select r from Requirement r join fetch r.tournament t")
    List<Requirement> findAll();
}
