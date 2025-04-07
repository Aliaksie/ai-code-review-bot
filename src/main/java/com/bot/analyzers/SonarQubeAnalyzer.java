package com.bot.analyzers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bot.client.APIClient;
import com.bot.models.AIException;
import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;
import com.bot.models.SonarIssuesResponse;
import com.bot.utility.LanguageDetector;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SonarQubeAnalyzer implements StaticAnalyzer {
   private final String sonarHost;
   private final String sonarToken;
   private final boolean sonarFetch;
   private final APIClient apiClient;
   private final ObjectMapper objectMapper;

   public SonarQubeAnalyzer( APIClient apiClient, @Value( "${sonar.host}" ) String sonarHostUrl, @Value( "${sonar.token}" ) String sonarToken,
         @Value( "${sonar.fetch}" ) boolean sonarFetch ) {
      this.apiClient = apiClient;
      this.sonarHost = sonarHostUrl;
      this.sonarToken = sonarToken;
      this.sonarFetch = sonarFetch;
      this.objectMapper = new ObjectMapper();
   }

   @Override
   public List<CodeRecommendation> analyze( GitFile file ) {
      throw new UnsupportedOperationException( "SonarQube works on multiple files at once." );
   }

   @Override
   public List<CodeRecommendation> analyzeMultipleFiles( List<GitFile> files ) {
      // todo: shall we check if repo exist in sonar and if not onboard it
      String projectKey = files.stream().findAny().map( GitFile::repo ).orElse( "" );

      return sonarFetch ? fetchAnalysisResults( projectKey ).orElseGet( () -> runAnalysis( projectKey, files ) ) : runAnalysis( projectKey, files );
   }

   private Optional<List<CodeRecommendation>> fetchAnalysisResults( String projectKey ) {
      try {
         String url = sonarHost + "/api/issues/search?componentKeys=" + projectKey;
         // todo : enchance apiClient!!!
         String response = apiClient.sendRequest( url, "GET", sonarToken, null );
         SonarIssuesResponse sonarIssues = objectMapper.readValue( response, SonarIssuesResponse.class );

         if ( sonarIssues.issues().isEmpty() ) {
            return Optional.empty();
         }

         List<CodeRecommendation> recommendations = sonarIssues.issues().stream()
               .map( issue -> new CodeRecommendation(
                     issue.component(), // File name
                     LanguageDetector.detect( issue.component() ),
                     issue.line() == null ? -1 : issue.line(),
                     issue.message(),
                     "",
                     CodeRecommendation.Type.SONAR_QUBE
               ) )
               .toList();

         return Optional.of( recommendations );

      } catch ( Exception e ) {
         // todo : logger!!!
         return Optional.empty();
      }

   }

   private List<CodeRecommendation> runAnalysis( String projectKey, List<GitFile> files ) {
      try {
         Path tempDir = Files.createTempDirectory( "sonar-analysis" );

         for ( GitFile file : files ) {
            if ( file.language().equals( "unknown" ) )
               continue;
            Path filePath = tempDir.resolve( file.filename() );
            Files.writeString( filePath, file.content() );
         }

         ProcessBuilder processBuilder = new ProcessBuilder(
               "sonar-scanner",
               "-Dsonar.projectKey=" + projectKey + "-code-review-bot", // todo: or just projectKey  ?
               "-Dsonar.sources=" + tempDir.toAbsolutePath(),
               "-Dsonar.host.url=" + sonarHost,
               "-Dsonar.login=" + sonarToken
         );

         processBuilder.inheritIO(); // Shows SonarQube logs in console
         Process process = processBuilder.start();
         process.waitFor();
         // todo: can we drop it now ?
         deleteDirectory( tempDir );

         // todo : wait for scan is ready !?
         return fetchAnalysisResults( projectKey + "-code-review-bot" ).orElse( List.of() );
      } catch ( Exception e ) {
         throw new AIException( "Error running SonarQube analysis: " + e.getMessage() );
      }
   }

   private void deleteDirectory( Path directory ) throws IOException {
      Files.walk( directory )
            .sorted( Comparator.reverseOrder() )
            .map( Path::toFile )
            .forEach( File::delete );
   }

}