package com.bot.openai;

import org.json.JSONObject;

import com.bot.client.APIClient;

public class LocalAIClient implements AIClient {
   private final APIClient apiClient;
   private final AIProperties props;

   public LocalAIClient( APIClient apiClient, AIProperties props ) {
      this.apiClient = apiClient;
      this.props = props;
   }

   @Override
   public String generateResponse( String prompt ) {
      JSONObject body = new JSONObject()
            .put( "model", props.model() )
            .put( "messages", new org.json.JSONArray()
                  .put( new JSONObject().put( "role", props.role() ).put( "content", prompt ) )
            );

      // todo
      String response = apiClient.sendRequest( props.endpoint() + "/v1/chat/completions", "POST", props.apiKey(), body );
      return extractMessage( response );
   }

}
