package com.gupb.manager.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;

@Component
public class GitUtilities {

    public void cloneRepository(String source, String destination, String branch) throws GitAPIException {
        File file = new File(destination);
        if(file.exists()) {
            FileSystemUtils.deleteRecursively(file);
        }
        Git.cloneRepository().setURI(source).setDirectory(new File(destination)).setBranch(branch).call();
    }


}
