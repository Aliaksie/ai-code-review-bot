package com.bot.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.bot.models.AIClientException;

@Component
public class JavaHttpClient implements APIClient {
   private final HttpClient httpClient;

   public JavaHttpClient() {
      this.httpClient = HttpClient.newBuilder().connectTimeout( Duration.ofSeconds( 10 ) ).build();
   }

   public String sendPostRequest( String url, String apiKey, JSONObject body ) {
      HttpRequest request = HttpRequest.newBuilder()
            .uri( URI.create( url ) )
            .header( "Content-Type", "application/json" )
            .header( "Authorization", "Bearer " + apiKey ) // todo:!!!
            .POST( HttpRequest.BodyPublishers.ofString( body.toString(), StandardCharsets.UTF_8 ) ) // todo: !
            .build();

      try {
         HttpResponse<String> response = httpClient.send( request, HttpResponse.BodyHandlers.ofString() );

         // todo: error handler
         if ( response.statusCode() != 200 ) {
            throw new AIClientException( "API Error: " + response.statusCode() + " - " + response.body() );
         }
         return response.body();
      } catch ( IOException e ) {
         throw new AIClientException( "API Error: " + e.getMessage() );
      } catch ( InterruptedException e ) {
         Thread.currentThread().interrupt();
         throw new AIClientException( "API Error: " + e.getMessage() );
      }
   }
}
