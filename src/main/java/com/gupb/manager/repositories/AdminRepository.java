package com.gupb.manager.repositories;

import com.gupb.manager.model.Admin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, Integer> {

    Optional<Admin> findByEmailAddress(String emailAddress);
}
