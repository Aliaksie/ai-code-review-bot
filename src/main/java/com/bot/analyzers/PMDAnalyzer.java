package com.bot.analyzers;

import org.springframework.stereotype.Component;

//   PMD: Line-by-line issues
@Component
public class PMDAnalyzer implements StaticAnalyzer {
   @Override
   public String analyze( final String codeContent ) {
      // Implement PMD analysis logic here
      return "";
   }

   @Override
   public String type() {
      return "PMD";
   }
}