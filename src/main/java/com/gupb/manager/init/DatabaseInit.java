package com.gupb.manager.init;

import com.gupb.manager.model.Admin;
import com.gupb.manager.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DatabaseInit {

    @Autowired
    private AdminRepository adminRepository;

    @Value("${gupb.manager.app.adminPass}")
    private String adminPass;

    @Value("${gupb.manager.app.adminEmail}")
    private String adminEmail;

    @PostConstruct
    public void initializeAdmin() {
        if(!adminRepository.findAll().iterator().hasNext()) {
            Admin admin = new Admin("Gupb", "Manager", adminEmail, adminPass);
            adminRepository.save(admin);
        }
    }
}
