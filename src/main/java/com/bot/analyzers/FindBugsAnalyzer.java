package com.bot.analyzers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bot.models.CodeRecommendation;

@Component
public class FindBugsAnalyzer implements StaticAnalyzer {
   @Override
   public List<CodeRecommendation> analyze( final String filePath, final String fileContent ) {
      return List.of(); // todo:
   }

   @Override
   public String type() {
      return "FindBugsAnalyzer";
   }
}
