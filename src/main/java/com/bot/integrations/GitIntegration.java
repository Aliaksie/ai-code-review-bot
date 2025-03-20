package com.bot.integrations;

import java.util.List;

import com.bot.models.GitFile;
import com.bot.models.WebhookEvent;

public interface GitIntegration {
   List<GitFile> getChangedFiles( String repoId, int prId );

   void addPrComment( String repoId, int prId, String comment );

   void addInlineComment( String repoId, int prId, String filePath, int line, String comment );

   WebhookEvent handleEvent( String payload );

}