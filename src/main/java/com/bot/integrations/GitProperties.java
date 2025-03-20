package com.bot.integrations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( "ai" )
public record GitProperties(String provider,
                            String token,
                            String url,
                            String organization,
                            String project,
                            String version) {
}
