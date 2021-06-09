package com.gupb.manager.repositories;

import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    @Override
    List<Student> findAll();

    List<Student> findByTeamsId(int teamsId);

    Optional<Student> findByEmailAddress(String emailAddress);

    Optional<Student> findByIndexNumber(String indexNumber);

    Boolean existsByEmailAddress(String email);
}
