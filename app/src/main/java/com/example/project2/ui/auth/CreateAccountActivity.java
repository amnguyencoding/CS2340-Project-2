package com.example.project2.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.MainActivity;
import com.example.project2.R;
import com.example.project2.databinding.ActivityCreateAccountBinding;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class CreateAccountActivity extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;
    public static final String CLIENT_ID = "14c60e61b15243e3ad38b5dbcb57b6b2";
    public static final String REDIRECT_URI = "project2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    private TextView tokenTextView, codeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);

        Button connectSpotify = (Button) findViewById(R.id.connect_spotify);

        connectSpotify.setOnClickListener((v) -> {
            getToken();
            //getCode();
        });
    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(CreateAccountActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(CreateAccountActivity.this, AUTH_CODE_REQUEST_CODE, request);
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" })
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();
            setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    public void onDestroy() {
        cancelCall();
        super.onDestroy();
        binding = null;
    }
}
