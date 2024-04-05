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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    private Call call;
    private static ArrayList<String> topArtistNames = new ArrayList<>();
    private static ArrayList<String> topArtistImageURLS = new ArrayList<>();
    private static ArrayList<String> topTrackNames = new ArrayList<>();
    private static ArrayList<String> topTrackReleaseDates = new ArrayList<>();
    private static ArrayList<String> topArtistFollowers = new ArrayList<>();
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

    public void getUserProfileData(String url, String accessToken) {
        //ArrayList<String> topArtistNames = new ArrayList<>();
        if (accessToken == null) {
            return;
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
                    clearDataLists(url);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //Log.i("THERES A NEW", "ENTRY HERE!!\n\n\n");
                        JSONObject itemObject = jsonArray.getJSONObject(i);

                        // Create a Map to store the JSON data
                        Map<String, Object> jsonMap = new HashMap<>();

                        // Iterate over the keys in the JSON object
                        Iterator<String> keys = itemObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            Object value = itemObject.get(key);
                            jsonMap.put(key, value);
                        }
                        //Log.i("song title",jsonMap.get("name").toString());
                        //Log.i("song title",jsonMap.get("name").toString());
                        if (jsonMap.containsKey("album")) {
                            JSONObject album = (JSONObject) jsonMap.get("album");
//                                Log.i("ALBUM", album.toString());
//                                Log.i("name", album.getString("name"));
                            Log.i("release date", album.getString("release_date"));
                        }
                        if (url.equals(TOP_ARTISTS_URL)) {
                            topArtistNames.add(jsonMap.get("name").toString());
                            topArtistFollowers.add(((JSONObject) jsonMap.get("followers")).getString("total"));
                            topArtistImageURLS.add(((JSONArray) jsonMap.get("images")).getJSONObject(1).getString("url"));
                        } else if (url.equals(TOP_TRACKS_URL)) {
                            topTrackNames.add(((JSONObject) jsonMap.get("album")).getString("name"));
                            topTrackReleaseDates.add(((JSONObject) jsonMap.get("album")).getString("release_date"));
                        }
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });

        while (topArtistNames.isEmpty() && url.equals(TOP_ARTISTS_URL)
                || topTrackNames.isEmpty() && url.equals(TOP_TRACKS_URL)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearDataLists(String url) {
        if (url.equals(TOP_ARTISTS_URL)) {
            topArtistImageURLS.clear();
            topArtistFollowers.clear();
            topArtistNames.clear();
        } else if (url.equals(TOP_TRACKS_URL)) {
            topTrackNames.clear();
            topTrackReleaseDates.clear();
        }
    }


    public ArrayList<String> getTopArtistImageData() {
        return topArtistImageURLS;
    }
    public ArrayList<String> getTopArtistNameData() {
        return topArtistNames;
    }
    public ArrayList<String> getTopTrackNameData() {
        return topTrackNames;
    }
    public ArrayList<String> getTopTrackReleaseDateData() {
        return topTrackReleaseDates;
    }
    public ArrayList<Integer> getTopArtistFollowerData() {
        ArrayList<String> followers = topArtistFollowers;
        ArrayList<Integer> followersInt = new ArrayList<>();
        for (String s : followers) {
            followersInt.add(Integer.parseInt(s));
        }
        return followersInt;
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


