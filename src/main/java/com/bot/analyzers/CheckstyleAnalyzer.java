package com.bot.analyzers;

import org.springframework.stereotype.Component;

import com.bot.models.AIClientException;

//Checkstyle: File-wide best practices

@Component
public class CheckstyleAnalyzer implements StaticAnalyzer {

   @Override
   public String analyze( final String codeContent ) {
      try {
         //         "[FILE-WIDE]"
         return "";
      } catch ( Exception e ) {
         throw new AIClientException( "Checkstyle error: " + e.getMessage() ); // todo!
      }
   }

   @Override
   public String type() {
      return "Checkstyle";
   }
}
