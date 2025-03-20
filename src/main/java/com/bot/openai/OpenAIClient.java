package com.bot.openai;

import org.json.JSONObject;

import com.bot.client.APIClient;

public class OpenAIClient implements AIClient {
   private final APIClient apiClient;
   private final AIProperties props;

   public OpenAIClient( APIClient apiClient, AIProperties props ) {
      this.apiClient = apiClient;
      this.props = props;
   }

   @Override
   public String generateResponse( final String prompt ) {
      JSONObject body = new JSONObject()
            .put( "model", props.model() )
            .put( "temperature", props.temperature() )
            .put( "max_tokens", props.tokens() )
            .put( "messages", new org.json.JSONArray()
                  .put( new JSONObject().put( "role", "system" ).put( "content", "You are a helpful AI assistant." ) )
                  .put( new JSONObject().put( "role", props.role() ).put( "content", prompt ) )
            );

      //todo
      String response = apiClient.sendRequest( props.endpoint(), "POST", props.apiKey(), body );
      return extractMessage( response );
   }

}
