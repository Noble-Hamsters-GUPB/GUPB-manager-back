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
    List<Team> findAll();

    @Query("select team from Team team left join fetch team.tournament tournament where tournament = :tournament")
    List<Team> findByTournament(Tournament tournament);

    List<Team> findByTournamentId(Integer tournamentId);

    Optional<Team> findByName(String name);

    Optional<Team> findByPlayerName(String playerName);
}
