package com.gupb.manager.repositories;

import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends CrudRepository<Team, Integer> {
    @Override
    @Query("select team from Team team join fetch team.tournament tournament")
    List<Team> findAll();

    List<Team> findByTournament(Tournament tournament);

    Optional<Team> findByName(String name);

    Optional<Team> findByPlayerName(String playerName);
}
