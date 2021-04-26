package com.gupb.manager.repositories;

import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    @Override
    @Query("select s from Student s join fetch s.team t")
    List<Student> findAll();
}
