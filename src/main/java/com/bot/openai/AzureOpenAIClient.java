package com.bot.openai;

import org.json.JSONObject;

import com.bot.client.APIClient;

public class AzureOpenAIClient implements AIClient {
   private final APIClient apiClient;
   private final AIProperties props;

   public AzureOpenAIClient( APIClient apiClient, AIProperties props ) {
      this.apiClient = apiClient;
      this.props = props;
   }

   @Override
   public String generateResponse( String prompt ) {
      JSONObject body = new JSONObject()
            .put( "messages", new org.json.JSONArray()
                  .put( new JSONObject().put( "role", props.role() ).put( "content", prompt ) )
            );

      // todo
      String url = "%s/openai/deployments/%s/chat/completions?api-version=2024-03-01".formatted( props.endpoint(), props.model() );
      String response = apiClient.sendPostRequest( url, props.apiKey(), body );
      return extractMessage( response );
   }

}
