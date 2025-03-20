package com.bot.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bot.analyzers.StaticAnalysisManager;
import com.bot.integrations.GitIntegration;
import com.bot.models.GitFile;

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

   public void reviewPR( String repoId, String prId ) throws IOException {
      final List<GitFile> changedFiles = gitIntegration.getChangedFiles( repoId, prId );

      for ( GitFile(String filename, String content) : changedFiles ) {
         Map<String, String> fileResults = analysisManager.analyzeCode( content );
         if ( fileResults.isEmpty() )
            continue;

         String[] lines = content.split( "\n" );
         for ( int lineNum = 0; lineNum < lines.length; lineNum++ ) {
            String lineContent = lines[lineNum];
            // Run analysis for this line
            Map<String, String> results = analysisManager.analyzeCode( lineContent );
            for ( var result : results.entrySet() ) {
               if ( !result.getValue().isBlank() ) {
                  String aiSuggestion = aiService.generateCodeSuggestion( lineContent, content, result.getKey(), result.getValue() );
                  String suggestion = StringUtils.hasText( aiSuggestion ) ? aiSuggestion : "%s : %s".formatted( result.getKey(), result.getValue() );

                  // todo ?  compare line vs file suggestion
                  String fileSuggestion = fileResults.get( result.getKey() );
                  gitIntegration.addInlineComment( repoId, prId, filename, lineNum + 1, suggestion );
               }
            }
         }

         // todo: ?  make sure no line suggestion
         for ( var result : fileResults.entrySet() ) {
            if ( !result.getValue().isBlank() ) {
               String aiSuggestion = aiService.generateCodeSuggestion( content, content, result.getKey(), result.getValue() );
               String suggestion = StringUtils.hasText( aiSuggestion ) ? aiSuggestion : "%s : %s".formatted( result.getKey(), result.getValue() );

               gitIntegration.addInlineComment( repoId, prId, filename, 0, "### File Issues Detected: \n" + suggestion );
            }
         }
      }

      // If any general issues exist, add a single PR-wide comment
      // Multi-file analysis (optional)
      final Map<String, String> results = analysisManager.analyzeFiles( changedFiles );
      final String content = changedFiles.stream().map( GitFile::content ).collect( Collectors.joining( "\n" ) );
      for ( var result : results.entrySet() ) {
         if ( !result.getValue().isBlank() ) {
            String aiSuggestion = aiService.generateCodeSuggestion( content, content, result.getKey(), result.getValue() );
            String suggestion = StringUtils.hasText( aiSuggestion ) ? aiSuggestion : "%s : %s".formatted( result.getKey(), result.getValue() );

            gitIntegration.addPrComment( repoId, prId, "### Multi-file Issues Detected: \n" + suggestion );
         }
      }

   }
}
