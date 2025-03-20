package com.bot.integrations;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bot.client.APIClient;
import com.bot.models.AIException;
import com.bot.models.GitFile;
import com.bot.models.WebhookEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AzureReposIntegration implements GitIntegration {

   private final GitProperties props;
   private final APIClient apiClient;
   private final ObjectMapper objectMapper;

   public AzureReposIntegration( GitProperties props, APIClient apiClient ) {
      this.props = props;
      this.apiClient = apiClient;
      this.objectMapper = new ObjectMapper();
   }

   @Override
   public List<GitFile> getChangedFiles( String repoId, int prId ) {
      List<GitFile> gitFiles = new ArrayList<>();

      String prCommits = "%s/%s/%s/_apis/git/repositories/%s/pullRequests/%s/commits?api-version=%s".formatted( props.url(), props.organization(),
            props.project(), repoId, prId, props.version() );
      // see https://learn.microsoft.com/en-us/rest/api/azure/devops/git/pull-request-commits/get-pull-request-commits?view=azure-devops-rest-7.1#gitcommitref
      String commitsRs = apiClient.sendRequest( prCommits, "GET", props.token(), null );

      JSONObject commitsJson = new JSONObject( commitsRs );
      JSONArray commitRefs = commitsJson.getJSONArray( "value" );

      // todo !!!
      for ( int i = 0; i < commitRefs.length(); i++ ) {
         JSONObject commit = commitRefs.getJSONObject( i );
         String commitId = commit.getString( "commitId" );
         // see https://learn.microsoft.com/en-us/rest/api/azure/devops/git/commits/get-changes?view=azure-devops-rest-7.1&tabs=HTTP
         String url = "%s/%s/%s/_apis/git/repositories/%s/commits/%s/changes?api-version=%s".formatted( props.url(), props.organization(), props.project(),
               repoId, commitId, props.version() );
         String changesRs = apiClient.sendRequest( url, "GET", props.token(), null );

         JSONObject changesJson = new JSONObject( changesRs );
         JSONArray changes = changesJson.getJSONArray( "changes" );

         for ( int j = 0; j < changes.length(); j++ ) {
            // todo!
            JSONObject change = changes.getJSONObject( j );
            JSONObject item = change.getJSONObject( "item" );
            String filePath = item.getString( "path" );

            // Fetch the content of the changed file
            String fileContent = getFileContent( repoId, filePath, commitId );

            // todo: Avoid duplicates
            if ( gitFiles.stream().noneMatch( f -> f.filename().equals( filePath ) ) ) {
               gitFiles.add( new GitFile( repoId, filePath, fileContent ) );
            }
         }
      }

      return List.of();
   }

   public String getFileContent( String repoId, String filePath, String commitId ) {
      return ""; // todo!!!
   }

   @Override
   public void addPrComment( final String repoId, final int prId, final String comment ) {
      //      POST https://dev.azure.com/{organization}/{project}/_apis/git/repositories/{repositoryId}/pullRequests/{pullRequestId}/threads?api-version=7.1
      String url = String.format( "%s/%s/%s/_apis/git/repositories/%s/pullRequests/%d/threads?api-version=%s", props.url(), props.organization(),
            props.project(), repoId, prId, props.version() );

      JSONObject body = new JSONObject()
            .put( "comments", new JSONArray()
                  .put( new JSONObject()
                        .put( "parentCommentId", 0 )
                        .put( "content", comment )
                        .put( "commentType", "text" ) ) )
            .put( "status", "active" );

      apiClient.sendRequest( url, "POST", props.token(), body );
   }

   @Override
   public void addInlineComment( String repoId, int prId, String filePath, int line, String comment ) {
      // todo
      String url = String.format( "%s/%s/%s/_apis/git/repositories/%s/pullRequests/%d/threads?api-version=%s", props.url(), props.organization(),
            props.project(), repoId, prId, props.version() );

      JSONObject body = new JSONObject()
            .put( "comments", new JSONArray()
                  .put( new JSONObject()
                        .put( "content", comment )
                        .put( "commentType", "text" ) ) )
            .put( "status", "active" )
            .put( "threadContext", new JSONObject()
                  .put( "filePath", filePath )
                  .put( "rightFileStart", new JSONObject()
                        .put( "line", line + 1 )
                        .put( "offset", 1 ) ) )
            .put( "pullRequestThreadContext", new JSONObject() // todo!
                  .put( "changeTrackingId", 1 )
                  .put( "iterationContext", new JSONObject()
                        .put( "firstComparingIteration", 1 )
                        .put( "secondComparingIteration", 2 ) ) );

      apiClient.sendRequest( url, "POST", props.token(), body );
   }

   @Override
   public WebhookEvent handleEvent( String payload ) {
      try {
         JsonNode jsonNode = objectMapper.readTree( payload );
         String eventType = jsonNode.path( "eventType" ).asText();
         String repoId = jsonNode.path( "resource" ).path( "repository" ).path( "name" ).asText();
         String repoUrl = jsonNode.path( "resource" ).path( "repository" ).path( "url" ).asText();
         int prId = jsonNode.path( "resource" ).path( "pullRequestId" ).asInt();
         String sourceBranch = jsonNode.path( "resource" ).path( "sourceRefName" ).asText().replace( "refs/heads/", "" );
         String targetBranch = jsonNode.path( "resource" ).path( "targetRefName" ).asText().replace( "refs/heads/", "" );

         return new WebhookEvent( repoId, repoUrl, prId, eventType, sourceBranch, targetBranch );
      } catch ( Exception e ) {
         throw new AIException( "Error processing Azure webhook" );
      }
   }
}
