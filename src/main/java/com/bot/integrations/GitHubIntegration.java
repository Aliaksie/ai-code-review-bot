package com.bot.integrations;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestFileDetail;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import com.bot.models.AIException;
import com.bot.models.GitFile;
import com.bot.models.WebhookEvent;
import com.bot.utility.LanguageDetector;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GitHubIntegration implements GitIntegration {
   private final GitHub github;
   private final ObjectMapper objectMapper;

   public GitHubIntegration( GitProperties props ) throws IOException {
      // todo: bootstrap ex ?
      this.github = GitHub.connectUsingOAuth( props.token() );
      this.objectMapper = new ObjectMapper();
   }

   @Override
   public List<GitFile> getChangedFiles( final String repoId, final int prId ) {
      try {
         List<GitFile> files = new ArrayList<>();
         GHPullRequest pr = github.getRepository( repoId ).getPullRequest( prId );

         for ( final GHPullRequestFileDetail file : pr.listFiles() ) {
            files.add( new GitFile( repoId, file.getFilename(), getFileContent( repoId, file.getFilename() ), LanguageDetector.detect( file.getFilename() ) ) );
         }
         return files;
      } catch ( IOException e ) {
         throw new AIException( e.getMessage() ); // todo
      }
   }

   public String getFileContent( final String repoId, final String filePath ) {
      try {
         GHRepository repo = github.getRepository( repoId );
         GHContent fileContent = repo.getFileContent( filePath );
         return getFileAsString( fileContent );
      } catch ( IOException e ) {
         throw new AIException( e.getMessage() ); // todo
      }

   }

   @Override
   public void addPrComment( final String repoId, final int prId, final String comment ) {
      try {
         final GHPullRequest pr = github.getRepository( repoId ).getPullRequest( prId );

         // Post analysis results as a PR comment
         pr.comment( comment );
      } catch ( IOException e ) {
         throw new AIException( e.getMessage() ); // todo
      }
   }

   @Override
   public void addInlineComment( String repoId, int prId, String filePath, int line, String comment ) {
      try {
         final GHPullRequest pr = github.getRepository( repoId ).getPullRequest( prId );
         pr.createReviewComment( comment, pr.getHead().getSha(), filePath, line );
      } catch ( IOException e ) {
         throw new AIException( e.getMessage() ); // todo
      }
   }

   @Override
   public WebhookEvent handleEvent( String payload ) {
      try {
         JsonNode jsonNode = objectMapper.readTree( payload );
         String action = jsonNode.path( "action" ).asText();
         String repoId = jsonNode.path( "repository" ).path( "full_name" ).asText();
         String repoUrl = jsonNode.path( "repository" ).path( "html_url" ).asText();
         int prId = jsonNode.path( "pull_request" ).path( "number" ).asInt();
         String sourceBranch = jsonNode.path( "pull_request" ).path( "head" ).path( "ref" ).asText();
         String targetBranch = jsonNode.path( "pull_request" ).path( "base" ).path( "ref" ).asText();

         return new WebhookEvent( repoId, repoUrl, prId, action, sourceBranch, targetBranch );
      } catch ( Exception e ) {
         throw new AIException( "Error processing GitHub webhook" );
      }
   }

   private String getFileAsString( GHContent fileContent ) {
      try ( var in = fileContent.read() ) {
         return new String( in.readAllBytes(), StandardCharsets.UTF_8 );
      } catch ( IOException e ) {
         throw new AIException( "Can't read file: " + e.getMessage() );
      }
   }

}