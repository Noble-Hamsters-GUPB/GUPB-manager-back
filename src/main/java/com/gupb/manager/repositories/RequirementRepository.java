package com.gupb.manager.repositories;

import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.model.Round;
import org.springframework.data.jpa.repository.Query;
import com.gupb.manager.model.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RequirementRepository extends CrudRepository<Requirement, Integer> {
    @Query("select requirement from Requirement requirement join fetch requirement.requestedBy team where team.tournament = :tournament")
    List<Requirement> findByTournament(Tournament tournament);

    Optional<Requirement> findByPackageInfo(String packageInfo);

    @Override
    @Query("select requirement from Requirement requirement join fetch requirement.requestedBy")
    List<Requirement> findAll();
}
