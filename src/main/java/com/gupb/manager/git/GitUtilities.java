package com.gupb.manager.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Component
public class GitUtilities {

    public void cloneRepository(String source, String destination, String branch) throws GitAPIException, IOException {
        File file = new File(destination);
        if(file.exists()) {
            FileUtils.deleteDirectory(file);
        }
        Git.cloneRepository().setURI(source).setDirectory(new File(destination)).setBranch(branch).call();
    }


}
