package com.bot.openai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bot.client.APIClient;

@EnableConfigurationProperties( AIProperties.class )
@Configuration
public class AIConfig {

   // todo: // spring ai ? ? !!!
   @Bean
   public AIClient aiClient( AIProperties props, APIClient apiClient ) {
      if ( !props.enabled() ) {
         return new NoOpAIClient();
      }
      return switch ( props.provider() ) {
         case "azure" -> new AzureOpenAIClient( apiClient, props );
         case "local" -> new LocalAIClient( apiClient, props );
         case "openai" -> new OpenAIClient( apiClient, props );
         default -> new NoOpAIClient();
      };
   }
}
