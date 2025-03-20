package com.bot.analyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;

@Component
public class StaticAnalysisManager {

   private final List<StaticAnalyzer> analyzers;

   public StaticAnalysisManager( List<StaticAnalyzer> analyzers ) {
      this.analyzers = analyzers;
   }

   public Map<String, List<CodeRecommendation>> analyzeCode( final List<GitFile> changedFiles ) {
      final List<CodeRecommendation> results = new ArrayList<>();
      for ( final StaticAnalyzer analyzer : analyzers ) {
         List<CodeRecommendation> analysisResult = analyzer.analyzeMultipleFiles( changedFiles );

         if ( !analysisResult.isEmpty() ) {
            results.addAll( analysisResult );
         }
      }

      return results.stream().collect( Collectors.groupingBy( CodeRecommendation::filePath ) );
   }

}

