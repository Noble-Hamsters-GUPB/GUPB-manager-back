package com.gupb.manager.repositories;

import com.gupb.manager.model.Round;
import com.gupb.manager.model.Team;
import org.springframework.data.repository.CrudRepository;

public interface RoundRepository extends CrudRepository<Round, Integer> {
}
