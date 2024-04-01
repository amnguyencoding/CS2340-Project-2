package com.example.project2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GPTHandler {


    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final String dummyvarname = "test";
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static String GPTResponse;


    public CompletableFuture<String> makeRequest(String artists) {
        CompletableFuture<String> future = new CompletableFuture<>();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "system");
            messageObject.put("content", "You are a helpful assistant who has vast knowledge in music.");
            messagesArray.put(messageObject);

            messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", "Please describe how someone would think, dress, and act based on the fact that they listen to " + artists);
            messagesArray.put(messageObject);

            jsonObject.put("messages", messagesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + dummyvarname)
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String responseBody = response.body().string();

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        GPTResponse = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                        future.complete(GPTResponse);
                    } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing JSON", e);
                    }
                } else {
                    future.completeExceptionally(new IOException("Request not successful"));
                }
            }
        });
        return future;
    }
}
