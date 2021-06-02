package com.gupb.manager.repositories;

import com.gupb.manager.model.Round;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoundRepository extends CrudRepository<Round, Integer> {

    @Override
    @Query("select r from Round r join fetch r.tournament t")
    List<Round> findAll();

    List<Round> findByTournamentId(Integer tournamentId);
}
