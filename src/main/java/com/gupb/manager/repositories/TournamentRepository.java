package com.gupb.manager.repositories;

import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends CrudRepository<Tournament, Integer> {
    @Override
    @Query("select tournament from Tournament tournament left join fetch tournament.creator")
    List<Tournament> findAll();

    @Query("select tournament from Tournament tournament left join fetch tournament.creator")
    Optional<Tournament> findByName(String name);

    List<Tournament> findByCreatorId(Integer id);
}
