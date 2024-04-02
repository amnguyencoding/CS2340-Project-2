package com.example.project2;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyHandler {
    public static final String CLIENT_ID = "14c60e61b15243e3ad38b5dbcb57b6b2";
    public static final String REDIRECT_URI = "project2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
    /*private static String accessToken;
    private String accessCode;*///fragments should pull from firebase for this info
    private Call call;
    private static ArrayList<String> topData = new ArrayList<>();
    public static final String TOP_ARTISTS_URL = "https://api.spotify.com/v1/me/top/artists";
    public static final String TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks";

    private void fetchToken(Activity contextActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(contextActivity, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private void fetchCode(Activity contextActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(contextActivity, AUTH_CODE_REQUEST_CODE, request);
    }

    public ArrayList<String> getUserProfileData(String url, String accessToken) {
        //ArrayList<String> topData = new ArrayList<>();
        if (accessToken == null) {
            return topData;
        }

        //User profile request -- change URL to get different data
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        cancelCall();
        call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    final JSONArray jsonArray = jsonObject.getJSONArray("items");
                    topData.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        topData.add(jsonArray.getJSONObject(i).getString("name"));
                    }
                    Log.i("Inner Test", topData.toString());
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });
        while (topData.isEmpty()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i("Outer Test",topData.toString());
        return topData;
    }

    private static Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private static AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" })
                .setCampaign("your-campaign-token")
                .build();
    }

    private void cancelCall() {
        if (call != null) {
            call.cancel();
        }
    }

    public void setCall(Call call) {
        this.call = call;
    }

}


