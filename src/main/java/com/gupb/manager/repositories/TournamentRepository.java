package com.gupb.manager.repositories;

import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import org.springframework.data.repository.CrudRepository;

public interface TournamentRepository extends CrudRepository<Tournament, Integer> {
}
