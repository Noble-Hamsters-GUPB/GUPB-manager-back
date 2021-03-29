package com.gupb.manager.repositories;

import com.gupb.manager.model.Participant;
import com.gupb.manager.model.Team;
import org.springframework.data.repository.CrudRepository;

public interface ParticipantRepository extends CrudRepository<Participant, Integer> {
}
