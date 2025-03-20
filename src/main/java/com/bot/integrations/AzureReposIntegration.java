package com.bot.integrations;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bot.models.GitFile;

public class AzureReposIntegration implements GitIntegration {
   @Override
   public List<GitFile> getChangedFiles( String repoId, String prId ) throws IOException {
      return List.of();
   }

   @Override
   public String getFileContent( String repoId, String filePath ) throws IOException {
      return "";
   }

   @Override
   public void addPrComment( final String repoId, final String prId, final String comment ) throws IOException {

   }

   @Override
   public void addInlineComment( String repoId, String prId, String filePath, int line, String comment ) throws IOException {

   }
}
