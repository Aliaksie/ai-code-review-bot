package com.bot.analyzers;

import java.util.List;
import java.util.stream.Collectors;

import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;

public interface StaticAnalyzer {
   default List<CodeRecommendation> analyze( GitFile file ) {
      return List.of();
   }

   default List<CodeRecommendation> analyzeMultipleFiles( List<GitFile> files ) {
      return files.stream().filter( it -> !"unknown".equals( it.language() ) )
            .flatMap( file -> analyze( file ).stream() )
            .toList();
   }

}
