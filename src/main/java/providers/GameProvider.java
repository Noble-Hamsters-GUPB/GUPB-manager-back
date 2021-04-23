package providers;

import com.gupb.manager.git.GitUtilities;
import com.gupb.manager.model.Team;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;

public class GameProvider {

    private static final String source = "https://github.com/Prpht/GUPB";

    private static final String configFileName = "default_config.py";

    private static final String controllerDirectoryName = "controller" + File.separator;

    @Autowired
    private GitUtilities gitUtilities;

    public void provideGame() {

    }

    public void provideTestGameWithBot(String path, Team team) throws GitAPIException, IOException {

        gitUtilities.cloneRepository(source, path, "main");
        gitUtilities.cloneRepository(team.getGithubLink(), path + controllerDirectoryName + team.getPackageName(), "main");

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(new File(path + configFileName)));
        String line;
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line).append("\n");
            if (line.contains("from gupb.controller import random")) {
                stringBuilder.append("from gupb.controller import ").append(team.getPackageName()).append("\n");
            }
            else if (line.contains("random.RandomController(\"Darius\"),")) {
                stringBuilder.append(team.getPackageName()).append(".")
                        .append(team.getControllerClassName()).append("(").append(team.getBotName()).append(")\n");
            }
        }
        br.close();
        System.out.println(stringBuilder.toString());
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path + configFileName), false));
        bw.write(stringBuilder.toString());
        bw.close();
    }
}
