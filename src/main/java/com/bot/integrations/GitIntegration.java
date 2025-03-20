package com.bot.integrations;

import java.io.IOException;
import java.util.List;

import com.bot.models.GitFile;

public interface GitIntegration {
   List<GitFile> getChangedFiles( String repoId, String prId ) throws IOException;

   void addPrComment( String repoId, String prId, String comment ) throws IOException;

   void addInlineComment( String repoId, String prId, String filePath, int line, String comment ) throws IOException;
}