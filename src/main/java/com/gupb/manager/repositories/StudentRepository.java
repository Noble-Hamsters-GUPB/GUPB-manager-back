package com.gupb.manager.repositories;

import com.gupb.manager.model.Student;
import com.gupb.manager.model.Team;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Integer> {
}
