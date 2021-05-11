package com.gupb.manager.controllers;

import com.gupb.manager.providers.GameProvider;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class RequirementController {

    //  @PostMapping("/requirements")
    public Map<String,String> getRequirements() throws IOException, GitAPIException{
        String path = "C:\\AGH\\IO";
        String dirName = "requirements";

        File requirementsFile = new File(path + File.separator + dirName + File.separator + "requirements.txt");
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(requirementsFile));
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line).append(";");
        }
        br.close();
        String requirementsString = stringBuilder.toString();
        System.out.println(requirementsString);
        String[] lines = requirementsString.split(";");
        Map<String,String> requirements = new HashMap<>(lines.length);
        for (String l : lines){
            String requirementLine[] = l.split("==");
            requirements.put(requirementLine[0],requirementLine[1]);
        }
        return requirements;
    }
}
