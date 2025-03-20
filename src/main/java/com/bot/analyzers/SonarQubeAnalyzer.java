package com.bot.analyzers;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bot.client.APIClient;
import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;

@Component
public class SonarQubeAnalyzer implements StaticAnalyzer {
   private final String sonarHostUrl;
   private final String sonarToken;
   private final APIClient apiClient;

   public SonarQubeAnalyzer( APIClient apiClient, @Value( "${sonar.host.url}" ) String sonarHostUrl, @Value( "${sonar.login}" ) String sonarToken ) {
      this.apiClient = apiClient;
      this.sonarHostUrl = sonarHostUrl;
      this.sonarToken = sonarToken;
   }

   @Override
   public List<CodeRecommendation> analyze( final String filePath, final String fileContent ) {
      String apiUrl = sonarHostUrl + "/api/issues/search";
      String response = apiClient.sendRequest( apiUrl, "GET", sonarToken, null );
      return List.of(); // todo
   }

   @Override
   public String analyzeMultipleFiles( List<GitFile> files ) {
      // Simulate sending files to SonarQube API for analysis
      return "[MULTI-FILE] Detected duplicate code across multiple files.";
   }

   @Override
   public String type() {
      return "SonarQube";
   }
}