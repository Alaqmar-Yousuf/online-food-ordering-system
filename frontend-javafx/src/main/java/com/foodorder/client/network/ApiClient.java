package com.foodorder.client.network;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8081/api/v1";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private static String jwtToken = null;

    public static void setJwtToken(String token) {
        jwtToken = token;
    }

    public static String getJwtToken() {
        return jwtToken;
    }

    private static Request.Builder getAuthorizedRequestBuilder(String endpoint) {
        Request.Builder builder = new Request.Builder().url(BASE_URL + endpoint);
        if (jwtToken != null) {
            builder.addHeader("Authorization", "Bearer " + jwtToken);
        }
        return builder;
    }

    public static Response post(String endpoint, Object bodyObject) throws IOException {
        String json = gson.toJson(bodyObject);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = getAuthorizedRequestBuilder(endpoint)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    public static Response get(String endpoint) throws IOException {
        Request request = getAuthorizedRequestBuilder(endpoint)
                .get()
                .build();
        return client.newCall(request).execute();
    }

    public static Response put(String endpoint, Object bodyObject) throws IOException {
        String json = bodyObject != null ? gson.toJson(bodyObject) : "{}";
        RequestBody body = RequestBody.create(json, JSON);
        Request request = getAuthorizedRequestBuilder(endpoint)
                .put(body)
                .build();
        return client.newCall(request).execute();
    }
    
    public static Gson getGson() {
        return gson;
    }
}
