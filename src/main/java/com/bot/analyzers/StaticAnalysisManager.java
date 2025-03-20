package com.bot.analyzers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bot.models.GitFile;

@Component
public class StaticAnalysisManager {

   private final List<StaticAnalyzer> analyzers;

   public StaticAnalysisManager( List<StaticAnalyzer> analyzers ) {
      this.analyzers = analyzers;
   }

   // todo! line / file level suggestion ?
   public Map<String, String> analyzeCode( final String codeContent ) {
      final Map<String, String> result = new HashMap<>();

      for ( final StaticAnalyzer analyzer : analyzers ) {
         String analysisResult = analyzer.analyze( codeContent );

         if ( !analysisResult.isBlank() ) {
            result.put( analyzer.type(), analysisResult.trim() );
         }
      }

      return result;
   }

   public Map<String, String> analyzeFiles( final List<GitFile> files ) {
      Map<String, String> result = new HashMap<>();

      for ( StaticAnalyzer analyzer : analyzers ) {
         String analysisResult = analyzer.analyzeMultipleFiles( files );

         if ( !analysisResult.isBlank() ) {
            result.put( analyzer.type(), analysisResult.trim() );
         }
      }

      return result;
   }
}

