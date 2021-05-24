package com.gupb.manager.repositories;

import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TournamentRepository extends CrudRepository<Tournament, Integer> {

    Optional<Tournament> findByName(String name);
}
