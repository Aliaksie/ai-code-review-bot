package com.bot.analyzers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bot.models.AIClientException;
import com.bot.models.GitFile;

// SonarQube: Deep analysis & multi-file issues

@Component
public class SonarQubeAnalyzer implements StaticAnalyzer {
   private final String sonarHostUrl;
   private final String sonarToken;

   public SonarQubeAnalyzer( @Value( "${sonar.host.url}" ) String sonarHostUrl, @Value( "${sonar.login}" ) String sonarToken ) {
      this.sonarHostUrl = sonarHostUrl;
      this.sonarToken = sonarToken;
   }

   @Override
   public String analyze( String codeContent ) {
      String apiUrl = sonarHostUrl + "/api/issues/search";
      HttpRequest request = HttpRequest.newBuilder()
            .uri( URI.create( apiUrl ) )
            .header( "Authorization", "Bearer " + sonarToken )
            .GET()
            .build();
      try ( final HttpClient client = HttpClient.newHttpClient() ) {
         HttpResponse<String> response = client.send( request, HttpResponse.BodyHandlers.ofString() );
         return response.body();
      } catch ( IOException e ) {
         throw new AIClientException( "API Error: " + e.getMessage() );
      } catch ( InterruptedException e ) {
         Thread.currentThread().interrupt();
         throw new AIClientException( "API Error: " + e.getMessage() );
      }
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