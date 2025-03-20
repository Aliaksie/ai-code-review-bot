package com.bot.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties( "ai" )
public record AIProperties(String provider,
                           String model,
                           String role,
                           String apiKey,
                           String endpoint,
                           String temperature,
                           double tokens,
                           boolean enabled) {
}
