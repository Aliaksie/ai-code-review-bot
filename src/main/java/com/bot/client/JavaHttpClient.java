package com.bot.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bot.models.AIException;

@Component
public class JavaHttpClient implements APIClient {
   private final HttpClient httpClient;

   public JavaHttpClient() {
      this.httpClient = HttpClient.newBuilder().connectTimeout( Duration.ofSeconds( 10 ) ).build();
   }

   public String sendRequest( String url, String method, String apiKey, JSONObject body ) {
      HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri( URI.create( url ) )
            .header( "Content-Type", "application/json" )
            .header( "Authorization", "Bearer " + apiKey );

      if ( "POST".equalsIgnoreCase( method ) ) {
         builder = builder.POST( HttpRequest.BodyPublishers.ofString( body.toString() ) );
      } else {
         builder = builder.GET();
      }

      HttpRequest request = builder.build();

      try {
         HttpResponse<String> response = httpClient.send( request, HttpResponse.BodyHandlers.ofString() );

         // todo: error handler
         if ( response.statusCode() != 200 ) {
            throw new AIException( "API Error: " + response.statusCode() + " - " + response.body() );
         }
         return response.body();
      } catch ( IOException e ) {
         throw new AIException( "API Error: " + e.getMessage() );
      } catch ( InterruptedException e ) {
         Thread.currentThread().interrupt();
         throw new AIException( "API Error: " + e.getMessage() );
      }
   }
}
