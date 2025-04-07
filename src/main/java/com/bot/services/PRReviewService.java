package com.bot.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bot.analyzers.StaticAnalysisManager;
import com.bot.integrations.GitIntegration;
import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;
import com.bot.models.WebhookEvent;

@Service
public class PRReviewService {
   private final GitIntegration gitIntegration;
   private final StaticAnalysisManager analysisManager;
   private final AIService aiService;

   public PRReviewService( final GitIntegration gitIntegration, final StaticAnalysisManager analysisManager, final AIService aiService ) {
      this.gitIntegration = gitIntegration;
      this.analysisManager = analysisManager;
      this.aiService = aiService;
   }

   // todo: use header to dynamecly choose integration !??!
   public void reviewPR( Map<String, String> headers, String payload ) {
      // todo: check even_type!!!
      final WebhookEvent event = gitIntegration.handleEvent( payload );
      final List<GitFile> changedFiles = gitIntegration.getChangedFiles( event.repositoryId(), event.pullRequestId() );

      Map<String, List<CodeRecommendation>> fileResults = analysisManager.analyzeCode( changedFiles );
      if ( fileResults.isEmpty() ) {
         return;
      }

      for ( var result : fileResults.entrySet() ) {
         if ( result.getValue().isEmpty() ) {
            continue;
         }
         String fileName = result.getKey();
         String fileContent = result.getValue().stream()
               .filter( it -> it.type() != CodeRecommendation.Type.SONAR_QUBE )
               .findAny()
               .map( CodeRecommendation::content ).orElse( "" );

         result.getValue().stream()
               .filter( it -> it.line() != -1 )
               .forEach( it -> {
                  // todo extract line!
                  // todo: optimize for same recomendation and prior by analyze type
                  String aiSuggestion = aiService.generateCodeSuggestion( fileContent, fileContent, it.language(), result.getKey(), it.msg() );
                  String suggestion = StringUtils.hasText( aiSuggestion ) ? aiSuggestion : "%s : %s".formatted( result.getKey(), it.msg() );

                  gitIntegration.addInlineComment( event.repositoryId(), event.pullRequestId(), fileName, it.line(), suggestion );
               } );

         String suggestion = result.getValue().stream()
               .filter( it -> it.line() == -1 )
               .map( it -> {
                  String aiSuggestion = aiService.generateCodeSuggestion( fileContent, fileContent, it.language(), result.getKey(), it.msg() );
                  return StringUtils.hasText( aiSuggestion ) ? aiSuggestion : "%s : %s".formatted( result.getKey(), it.msg() );
               } ).collect( Collectors.joining( "\n" ) );

         gitIntegration.addInlineComment( event.repositoryId(), event.pullRequestId(), fileName, 0, "### File Issues Detected: \n" + suggestion );
      }

   }
}
