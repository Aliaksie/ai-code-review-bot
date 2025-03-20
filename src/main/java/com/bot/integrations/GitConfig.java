package com.bot.integrations;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bot.client.APIClient;

@EnableConfigurationProperties( GitProperties.class )
@Configuration
public class GitConfig {
   // todo: !!!
   @Bean
   public GitIntegration gitHubIntegration( GitProperties props, APIClient apiClient ) throws IOException {
      return switch ( props.provider() ) {
         case "gitlab" -> new GitLabIntegration( props );
         case "azure" -> new AzureReposIntegration( props, apiClient );
         default -> new GitHubIntegration( props );
      };
   }
}
