package com.bot.analyzers;

import org.springframework.stereotype.Component;

//Security & bug detection
@Component
public class FindBugsAnalyzer implements StaticAnalyzer {
   @Override
   public String analyze( String codeContent ) {
      return "";
   }

   @Override
   public String type() {
      return "FindBugsAnalyzer";
   }
}
