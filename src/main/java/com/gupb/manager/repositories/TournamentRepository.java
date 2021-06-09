package com.gupb.manager.repositories;

import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends CrudRepository<Tournament, Integer> {
    @Override
    List<Tournament> findAll();

    Optional<Tournament> findByName(String name);

    List<Tournament> findByCreatorId(Integer id);
}
