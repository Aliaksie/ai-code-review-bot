package com.bot.client;

import org.json.JSONObject;

public interface APIClient {
   String sendRequest( String url, String method, String apiKey, JSONObject body );
}
