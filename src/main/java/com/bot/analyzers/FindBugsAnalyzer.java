package com.bot.analyzers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bot.models.CodeRecommendation;
import com.bot.models.GitFile;

@Component
public class FindBugsAnalyzer implements StaticAnalyzer {
   @Override
   public List<CodeRecommendation> analyze( final GitFile file ) {
      return List.of(); // todo:
   }

}
