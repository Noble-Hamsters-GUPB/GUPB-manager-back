package com.gupb.manager.providers;

import com.gupb.manager.model.Requirement;
import com.gupb.manager.model.RequirementStatus;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.repositories.RequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Component
public class RequirementProvider {

    @Autowired
    private RequirementRepository requirementRepository;

    public void setRequirements(Tournament tournament, String path) {
        List<Requirement> requirements = requirementRepository.findByTournament(tournament);
        StringBuilder stringBuilder = new StringBuilder();
        for (Requirement requirement : requirements) {
            if (requirement.getStatus().equals(RequirementStatus.VALID)) {
                stringBuilder.append(requirement.getPackageInfo()).append("\n");
            }
        }
        try {
            Files.write(Paths.get(path), stringBuilder.toString().getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
        }
    }
}
