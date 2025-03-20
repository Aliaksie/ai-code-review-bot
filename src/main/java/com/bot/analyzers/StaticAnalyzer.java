package com.bot.analyzers;

import java.util.List;

import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;

public interface StaticAnalyzer {
   List<CodeRecommendation> analyze( String filePath, String fileContent );

   default String analyzeMultipleFiles( List<GitFile> files ) {
      return "";
   }

   String type();
}
