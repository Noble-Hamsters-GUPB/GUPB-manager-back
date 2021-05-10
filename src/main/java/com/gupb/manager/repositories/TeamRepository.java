package com.gupb.manager.repositories;

import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Integer> {

    @Override
    @Query("select t from Team t join fetch t.tournament tr")
    List<Team> findAll();

    List<Team> findByTournament(Tournament tournament);
}
