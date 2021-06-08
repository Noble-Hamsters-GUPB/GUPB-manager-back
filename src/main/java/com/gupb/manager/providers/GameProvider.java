package com.gupb.manager.providers;

import com.gupb.manager.git.GitUtilities;
import com.gupb.manager.model.PlayerStatus;
import com.gupb.manager.model.Round;
import com.gupb.manager.model.Team;
import com.gupb.manager.model.Tournament;
import com.gupb.manager.python.PythonPackageManagementException;
import com.gupb.manager.python.PythonPackageManager;
import com.gupb.manager.repositories.TeamRepository;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Component
public class GameProvider {

    private static final String configFile = "config.json";

    private static final String controllerDirectoryName = "controller";

    private static final String virtualenvName = "GUPB-venv";

    private static final String controllerConfigKeyName = "controllers";

    private static final String requirementsRelativePath = "requirements.txt";

    private static final String pythonImportSeparator = ".";

    private static final String runsNumberString = "runs_no";

    private static final int numberOfRunsPerTest = 5;

    private static final String randomControllerClassName = "RandomController";

    private static final String randomPackageName = "random";

    @Autowired
    private GitUtilities gitUtilities;

    @Autowired
    private RequirementProvider requirementProvider;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PythonPackageManager pythonPackageManager;


    public void provideRound(String dirName, Round round) throws GitAPIException, IOException {

        List<Team> teamsInRound = teamRepository.findByTournament(round.getTournament());

        Tournament tournament = round.getTournament();

        String moduleDirPath = dirName + File.separator + tournament.getModuleName();
        gitUtilities.cloneRepository(tournament.getGithubLink(), dirName, tournament.getBranchName());
        String controllersDestinationPath = moduleDirPath + File.separator + controllerDirectoryName;

        for (Team team : teamsInRound) {
            gitUtilities.cloneRepository(team.getGithubLink(), controllersDestinationPath + File.separator + team.getSafeName(), team.getBranchName());
        }

        String configFilePathFull = moduleDirPath + File.separator + configFile;
        File file = new File(configFilePathFull);

        String jsonString = FileUtils.readFileToString(file, Charset.defaultCharset());
        JSONObject configJson = new JSONObject(jsonString);

        configJson.put(runsNumberString, round.getNumberOfRuns());

        JSONArray playersArray = new JSONArray();

        String controllersPackage = tournament.getModuleName() + pythonImportSeparator + controllerDirectoryName;

        teamsInRound.stream()
                .filter(team -> team.getPlayerStatus() == PlayerStatus.READY)
                .forEach(team -> playersArray.put(getController(team.getMainClassName(),
                        controllersPackage + pythonImportSeparator + team.getSafeName(),
                        team.getPlayerName()
                )));

        configJson.put(controllerConfigKeyName, playersArray);

        saveJSONToFile(configFilePathFull, configJson);

        System.out.println(configJson.toString(4));

        provideRequirementsAndVirtualenv(dirName, tournament);
    }

    public void provideTestRoundWithBot(String dirName, Team team) throws GitAPIException, IOException {

        Tournament tournament = team.getTournament();
        String moduleDirPath = dirName + File.separator + tournament.getModuleName();
        gitUtilities.cloneRepository(tournament.getGithubLink(), dirName, tournament.getBranchName());
        String controllersDestinationPath = moduleDirPath + File.separator + controllerDirectoryName;
        gitUtilities.cloneRepository(team.getGithubLink(), controllersDestinationPath + File.separator + team.getSafeName(), team.getBranchName());

        String configFilePathFull = dirName + File.separator + tournament.getModuleName() + File.separator + configFile;
        File file = new File(configFilePathFull);

        String jsonString = FileUtils.readFileToString(file, Charset.defaultCharset());
        JSONObject configJson = new JSONObject(jsonString);

        configJson.put(runsNumberString, numberOfRunsPerTest);

        String moduleName = tournament.getModuleName();

        JSONArray playersArray = new JSONArray();
        String controllersPackage = moduleName + pythonImportSeparator + controllerDirectoryName;
        String teamPackagePath = controllersPackage + pythonImportSeparator + team.getSafeName();
        String randomPackagePath = controllersPackage + pythonImportSeparator + randomPackageName;

        playersArray.put(getController(team.getMainClassName(), teamPackagePath, team.getPlayerName()));

        List.of(new String[] {"Adam", "Eve", "Cain", "Abel", "Seth"}).
                forEach(name -> playersArray.put(getController(randomControllerClassName, randomPackagePath, name)));

        configJson.put(controllerConfigKeyName, playersArray);

        saveJSONToFile(configFilePathFull, configJson);

        System.out.println(configJson.toString(4));

        provideRequirementsAndVirtualenv(dirName, tournament);
    }

    private static final String AS_OBJECT = "__is-obj";
    private static final String OBJ_NAME = "obj-name";
    private static final String CALL_OR_INIT = "init/call";
    private static final String OBJ_ARGS = "args";
    private static final String OBJ_KWARGS = "kwargs";
    private static final String PACKAGE = "package";

    private static JSONObject getPythonObject(String objectName, String packageImportPath, JSONArray args, JSONObject kwargs, boolean callOrInit) {
        JSONObject pythonObject = new JSONObject();

        pythonObject.put(AS_OBJECT, true);
        pythonObject.put(CALL_OR_INIT, callOrInit);
        pythonObject.put(PACKAGE, packageImportPath);
        pythonObject.put(OBJ_NAME, objectName);

        if (args != null) {
            pythonObject.put(OBJ_ARGS, args);
        }
        if (kwargs != null) {
            pythonObject.put(OBJ_KWARGS, kwargs);
        }

        return pythonObject;
    }

    private static JSONObject getController(String mainClassName, String packageImportPath, String controllerName) {
        return getPythonObject(mainClassName, packageImportPath,
                new JSONArray(new String[]{controllerName}), null, true);
    }

    private void provideRequirementsAndVirtualenv(String dirName, Tournament tournament) {
        requirementProvider.setRequirements(tournament, dirName + File.separator + requirementsRelativePath);

        String virtualenvPath = dirName + File.separator + virtualenvName;
        try {
            pythonPackageManager.createVirtualEnvironment(dirName, virtualenvName);
            pythonPackageManager.installPackagesFromRequirements(virtualenvPath, dirName + File.separator + requirementsRelativePath);
        } catch (PythonPackageManagementException e) {
            System.out.println(e.getLogs());
            e.printStackTrace();
        }
    }

    private void saveJSONToFile(String filePath, JSONObject jsonObject) throws IOException {
        BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filePath));
        jsonObject.write(bufferedWriter);
        bufferedWriter.flush();
        bufferedWriter.close();
    }
}
