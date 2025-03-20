package com.bot.integrations;

import java.util.List;

import com.bot.models.GitFile;
import com.bot.models.WebhookEvent;

public class GitLabIntegration implements GitIntegration {

   private final GitProperties props;

   public GitLabIntegration( GitProperties props ) {
      this.props = props;
   }

   @Override
   public List<GitFile> getChangedFiles( String repoId, int prId ) {
      return List.of();
   }

   public String getFileContent( String repoId, String filePath ) {
      return "";
   }

   @Override
   public void addPrComment( String repoId, int prId, String comment ) {

   }

   @Override
   public void addInlineComment( String repoId, int prId, String filePath, int line, String comment ) {

   }

   @Override
   public WebhookEvent handleEvent( String payload ) {
      return null;
   }

}
