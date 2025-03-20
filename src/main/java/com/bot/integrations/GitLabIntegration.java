package com.bot.integrations;

import java.io.IOException;
import java.util.List;

import com.bot.models.GitFile;

public class GitLabIntegration  implements GitIntegration{
   @Override
   public List<GitFile> getChangedFiles( String repoId, String prId ) throws IOException {
      return List.of();
   }

   @Override
   public String getFileContent( String repoId, String filePath ) throws IOException {
      return "";
   }

   @Override
   public void addPrComment( String repoId, String prId, String comment ) throws IOException {

   }

   @Override
   public void addInlineComment( String repoId, String prId, String filePath, int line, String comment ) throws IOException {

   }
}
