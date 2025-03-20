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

import com.bot.models.AIClientException;
import com.bot.models.GitFile;

public class GitHubIntegration implements GitIntegration {
   private final GitHub github;

   public GitHubIntegration( GitProperties props ) throws IOException {
      this.github = GitHub.connectUsingOAuth( props.token() );
   }

   @Override
   public List<GitFile> getChangedFiles( final String repoId, final String prId ) throws IOException {
      List<GitFile> files = new ArrayList<>();
      GHPullRequest pr = github.getRepository( repoId ).getPullRequest( Integer.parseInt( prId ) );

      for ( final GHPullRequestFileDetail file : pr.listFiles() ) {
         files.add( new GitFile( file.getFilename(), getFileContent( repoId, file.getFilename() ) ) );
      }
      return files;
   }

   public String getFileContent( final String repoId, final String filePath ) throws IOException {
      GHRepository repo = github.getRepository( repoId );
      GHContent fileContent = repo.getFileContent( filePath );
      try ( var in = fileContent.read() ) {
         return new String( in.readAllBytes(), StandardCharsets.UTF_8 );
      } catch ( IOException e ) {
         throw new AIClientException( "Can't read file: " + e.getMessage() );
      }
   }

   @Override
   public void addPrComment( final String repoId, final String prId, final String comment ) throws IOException {
      final GHPullRequest pr = github.getRepository( repoId ).getPullRequest( Integer.parseInt( prId ) );

      // Post analysis results as a PR comment
      pr.comment( comment );
   }

   @Override
   public void addInlineComment( String repoId, String prId, String filePath, int line, String comment ) throws IOException {
      final GHPullRequest pr = github.getRepository( repoId ).getPullRequest( Integer.parseInt( prId ) );
      pr.createReviewComment( comment, pr.getHead().getSha(), filePath, line );
   }
}