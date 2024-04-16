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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static Call call;
    private static ArrayList<String> topArtistNames = new ArrayList<>();
    private static ArrayList<String> topArtistImageURLS = new ArrayList<>();
    private static ArrayList<String> topTrackNames = new ArrayList<>();
    private static ArrayList<String> topTrackAuthors = new ArrayList<>();
    private static ArrayList<String> topTrackReleaseDates = new ArrayList<>();
    private static ArrayList<String> topArtistFollowers = new ArrayList<>();
    private static ArrayList<String> topTrackImageURLs = new ArrayList<>();
    private static ArrayList<String> topGenres = new ArrayList<>();
    private static final String SHORT_TERM_TOP_ARTISTS_URL = "https://api.spotify.com/v1/me/top/artists?time_range=short_term";
    private static final String SHORT_TERM_TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks?time_range=short_term";
    private static final String MEDIUM_TERM_TOP_ARTISTS_URL = "https://api.spotify.com/v1/me/top/artists";
    private static final String MEDIUM_TERM_TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks";
    private static final String LONG_TERM_TOP_ARTISTS_URL = "https://api.spotify.com/v1/me/top/artists?time_range=long_term";
    private static final String LONG_TERM_TOP_TRACKS_URL = "https://api.spotify.com/v1/me/top/tracks?time_range=long_term";

    private static final String SHORT_TERM_TOP_TRACK_IMAGES_URL = "https://api.spotify.com/v1/me/top/tracks/images?time_range=short_term";
    private static final String MEDIUM_TERM_TOP_TRACK_IMAGES_URL = "https://api.spotify.com/v1/me/top/tracks/images?time_range=medium_term";
    private static final String LONG_TERM_TOP_TRACK_IMAGES_URL = "https://api.spotify.com/v1/me/top/tracks/images?time_range=long_term";

    private static final String SHORT_TERM_TOP_GENRES_URL = "https://api.spotify.com/v1/me/top/tracks/genres?time_range=short_term";
    private static final String MEDIUM_TERM_TOP_GENRES_URL = "https://api.spotify.com/v1/me/top/tracks/genres?time_range=medium_term";
    private static final String LONG_TERM_TOP_GENRES_URL = "https://api.spotify.com/v1/me/top/tracks/genres?time_range=long_term";


    private void fetchToken(Activity contextActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(contextActivity, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private void fetchCode(Activity contextActivity) {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(contextActivity, AUTH_CODE_REQUEST_CODE, request);
    }

    private static void getUserProfileData(String url, String accessToken) {
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
                        if (equalsArtistURL(url)) {
                            topArtistNames.add(jsonMap.get("name").toString());
                            topArtistFollowers.add(((JSONObject) jsonMap.get("followers")).getString("total"));
                            topArtistImageURLS.add(((JSONArray) jsonMap.get("images")).getJSONObject(1).getString("url"));
                            JSONArray genresArray = itemObject.getJSONArray("genres");
                            for (int j = 0; j < genresArray.length(); j++) {
                                topGenres.add(genresArray.getString(j)); //duplicate genres from here
                            }
                        } else if (equalsTrackURL(url)) {
                            topTrackNames.add(jsonMap.get("name").toString());
                            JSONArray trackAuthors = ((JSONArray) jsonMap.get("artists"));
                            topTrackAuthors.add(((JSONObject) trackAuthors.get(0)).getString("name"));
                            topTrackReleaseDates.add(((JSONObject) jsonMap.get("album")).getString("release_date"));
                            topTrackImageURLs.add(((JSONArray)(((JSONObject) jsonMap.get("album")).get("images"))).getJSONObject(1).getString("url"));
                        }
                    }
                    if (equalsArtistURL(url)) {
                        calculateTopGenres();
                    }
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                }
            }
        });

        while (topArtistNames.isEmpty() && topGenres.isEmpty() && equalsArtistURL(url)
                || topTrackNames.isEmpty() && equalsTrackURL(url)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void clearDataLists(String url) {
        if (equalsArtistURL(url)) {
            topArtistImageURLS.clear();
            topArtistFollowers.clear();
            topArtistNames.clear();
        } else if (equalsTrackURL(url)) {
            topTrackNames.clear();
            topTrackAuthors.clear();
            topTrackImageURLs.clear();
            topTrackReleaseDates.clear();
        }
    }
    private static void calculateTopGenres() {
        ArrayList<String> temp = new ArrayList<>();
        topGenres.sort(Comparator.comparing(i -> Collections.frequency(topGenres, i)).reversed());
        int i = 0;
        while (temp.size() != 5) {
            if (!temp.contains(topGenres.get(i))) {
                temp.add(topGenres.get(i));
            }
            i++;
        }
        topGenres = temp;
    }

    private static boolean equalsArtistURL(String url) {
        return url.equals(SHORT_TERM_TOP_ARTISTS_URL)
                || url.equals(MEDIUM_TERM_TOP_ARTISTS_URL)
                || url.equals(LONG_TERM_TOP_ARTISTS_URL);
    }

    private static boolean equalsTrackURL(String url) {
        return url.equals(SHORT_TERM_TOP_TRACKS_URL)
                || url.equals(MEDIUM_TERM_TOP_TRACKS_URL)
                || url.equals(LONG_TERM_TOP_TRACKS_URL);
    }


    public static void populateArtistAndTrackData(String accessToken, TimeRange timeRange) {
        // Clear existing data before fetching new data
        clearDataLists();

        switch (timeRange) {
            case SHORT_TERM:
                getUserProfileData(SHORT_TERM_TOP_ARTISTS_URL, accessToken);
                getUserProfileData(SHORT_TERM_TOP_TRACKS_URL, accessToken);
                getUserProfileData(SHORT_TERM_TOP_TRACK_IMAGES_URL, accessToken); // Add URL for fetching track images
                getUserProfileData(SHORT_TERM_TOP_GENRES_URL, accessToken); // Add URL for fetching genres
                break;
            case MEDIUM_TERM:
                getUserProfileData(MEDIUM_TERM_TOP_ARTISTS_URL, accessToken);
                getUserProfileData(MEDIUM_TERM_TOP_TRACKS_URL, accessToken);
                getUserProfileData(MEDIUM_TERM_TOP_TRACK_IMAGES_URL, accessToken); // Add URL for fetching track images
                getUserProfileData(MEDIUM_TERM_TOP_GENRES_URL, accessToken); // Add URL for fetching genres
                break;
            case LONG_TERM:
                getUserProfileData(LONG_TERM_TOP_ARTISTS_URL, accessToken);
                getUserProfileData(LONG_TERM_TOP_TRACKS_URL, accessToken);
                getUserProfileData(LONG_TERM_TOP_TRACK_IMAGES_URL, accessToken); // Add URL for fetching track images
                getUserProfileData(LONG_TERM_TOP_GENRES_URL, accessToken); // Add URL for fetching genres
                break;
        }
    }

    private static void clearDataLists() {
        topArtistImageURLS.clear();
        topArtistFollowers.clear();
        topArtistNames.clear();
        topTrackNames.clear();
        topTrackReleaseDates.clear();
        topTrackAuthors.clear();
        topTrackImageURLs.clear();
        topGenres.clear();
    }


    public static ArrayList<String> getTopArtistImageData() {
        return topArtistImageURLS;
    }
    public static ArrayList<String> getTopTrackImageData() {
        return topTrackImageURLs;
    }
    public static ArrayList<String> getTopArtistNameData() {
        return topArtistNames;
    }
    public static ArrayList<String> getTopTrackNameData() {
        return topTrackNames;
    }
    public static ArrayList<String> getTopTrackReleaseDateData() {
        return topTrackReleaseDates;
    }
    public static ArrayList<String> getTopGenres() {return new ArrayList<>(topGenres);}
    public static ArrayList<String> getTopTrackAuthors() {return new ArrayList<>(topTrackAuthors);}

    public static ArrayList<Integer> getTopArtistFollowerData() {
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

    private static void cancelCall() {
        if (call != null) {
            call.cancel();
        }
    }

    public void setCall(Call call) {
        this.call = call;
    }

}


