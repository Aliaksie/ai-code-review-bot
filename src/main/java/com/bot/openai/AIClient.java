package com.bot.openai;

import org.json.JSONObject;

public interface AIClient {

   String generateResponse( String prompt );

   // todo ?
   default String extractMessage( String response ) {
      JSONObject jsonResponse = new JSONObject( response );
      return jsonResponse.getJSONArray( "choices" ).getJSONObject( 0 ).getJSONObject( "message" ).getString( "content" );
   }
}
