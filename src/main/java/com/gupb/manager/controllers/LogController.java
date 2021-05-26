package com.gupb.manager.controllers;

import com.gupb.manager.model.Round;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class LogController {

 //   @PostMapping("/logs")
    public String getLogs(Round round) {
        StringBuilder logs = new StringBuilder();
        try {
            File logFile = new File(round.getPathToLogs());
            Scanner logReader = new Scanner(logFile);
            while (logReader.hasNextLine()){
                logs.append(logReader.nextLine()+'\n');
            }
            logReader.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return logs.toString();
    }
}
