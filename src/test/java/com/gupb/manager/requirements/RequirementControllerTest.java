package com.gupb.manager.requirements;
import com.gupb.manager.controllers.RequirementController;

import java.io.File;
import java.util.Map;
import static org.junit.Assert.*;

import com.gupb.manager.controllers.TournamentController;
import com.gupb.manager.model.AccessMode;
import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.RequirementStatus;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.repositories.RequirementRepository;
import com.gupb.manager.repositories.TournamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
class RequirementControllerTest {
    @Autowired
    private RequirementController reqC;
    @Autowired
    private TournamentController tC;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private RequirementRepository requirementRepository;

    @Test
    public void setRequirementsTest() {
        String path = "C:\\AGH\\IO\\requirements\\requirements2.txt";
        File reqFile = new File(path);
        long changed = reqFile.lastModified();
        System.out.println(changed);
        Tournament tournament = new Tournament("T1", AccessMode.OPEN);
        Requirement requirement = new Requirement("tqdm<=4.59.0",RequirementStatus.APPROVED,tournament);
        tournamentRepository.save(tournament);
        requirementRepository.save(requirement);
        try {
            reqC.setRequirements(tournament, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(changed<reqFile.lastModified());
    }

}

