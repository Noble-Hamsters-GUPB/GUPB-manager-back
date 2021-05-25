package com.gupb.manager.security;

import com.gupb.manager.model.Admin;
import com.gupb.manager.model.ResourceNotFound;
import com.gupb.manager.model.Student;
import com.gupb.manager.model.UserDetailsImpl;
import com.gupb.manager.repositories.AdminRepository;
import com.gupb.manager.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DetailsService implements UserDetailsService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Student> studentOptional = studentRepository.findByEmailAddress(email);
        Optional<Admin> adminOptional = adminRepository.findByEmailAddress(email);

        if(studentOptional.isPresent()) {
            return UserDetailsImpl.build(studentOptional.get());
        }

        else if(adminOptional.isPresent()) {
            return UserDetailsImpl.build(adminOptional.get());
        }

        else {
            throw  new ResourceNotFound("User not found");
        }

//        return adminOptional
//                .map(admin -> {
//                    System.out.println(admin.getEmailAddress());
//                    return UserDetailsImpl.build(admin);
//                })
//                .orElse(studentOptional
//                        .map(student -> {
//                            System.out.println("student");
//                            return UserDetailsImpl.build(student);
//                        })
//                .orElseThrow(() -> new ResourceNotFound("User not found")));
    }
}
