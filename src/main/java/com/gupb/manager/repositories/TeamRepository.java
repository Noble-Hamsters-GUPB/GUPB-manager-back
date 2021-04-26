package com.gupb.manager.repositories;

import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Integer> {

    List<Team> findByTournament(Tournament tournament);
}
