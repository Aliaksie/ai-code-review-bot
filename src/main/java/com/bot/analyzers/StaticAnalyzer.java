package com.bot.analyzers;

import java.util.List;

import com.bot.models.GitFile;

public interface StaticAnalyzer {
   String analyze( String codeContent );

   default String analyzeMultipleFiles( List<GitFile> files ) {
      return "";
   }

   String type();
}
