package com.bot.integrations;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitConfig {
   // todo: !!!
   @Bean
   public GitIntegration gitHubIntegration( @Value( "${github.provider}" ) String provider, @Value( "${github.token}" ) String token ) throws IOException {
      return switch ( provider ) {
         case "gitlab" -> new GitLabIntegration();
         case "azure" -> new AzureReposIntegration();
         default -> new GitHubIntegration( token );
      };
   }
}
