package com.bot.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.bot.openai.AIClient;

@Service
public class AIService {
   private final AIClient aiClient;

   public AIService( AIClient aiClient ) {
      this.aiClient = aiClient;
   }

   public String generateCodeSuggestion( String problematicCode, String context, String language, String issueProvider, String issueDescription ) {
      // todo: code lang .. .java and etc; use templates
      String prompt = """
                You are an expert %s code reviewer. The following code has an issue:
            
                Issue identified by: %s
            
                Issue: %s
            
                Context:
                ```
                %s
                ```
            
                Problematic Code:
                ```
                %s
                ```
            
                Suggest an improved version of this code with a short explanation.
            """.formatted( language, issueProvider, issueDescription, context, problematicCode );

      String aiResponse = aiClient.generateResponse( prompt );
      return StringUtils.hasText( aiResponse ) ? formatAISuggestion( aiResponse ) : aiResponse;
   }

   private String formatAISuggestion( String aiResponse ) {
      return "**🔍 AI Code Suggestion**\n\n" + aiResponse;
   }
}