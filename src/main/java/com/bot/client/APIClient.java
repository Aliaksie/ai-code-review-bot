package com.bot.client;

import org.json.JSONObject;

public interface APIClient {
   String sendPostRequest( String url, String apiKey, JSONObject body );
}
