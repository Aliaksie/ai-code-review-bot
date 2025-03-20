package com.bot.openai;

public class NoOpAIClient implements AIClient {
   @Override
   public String generateResponse( String prompt ) {
      return "";  // No AI suggestions
   }
}
