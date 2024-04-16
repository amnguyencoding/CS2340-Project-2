package com.example.project2.ui.authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

//import com.example.project2.LoginActivity;
import com.example.project2.MainActivity;
import com.example.project2.R;
import com.example.project2.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class LoginActivity extends AppCompatActivity {
    public static final String CLIENT_ID = "14c60e61b15243e3ad38b5dbcb57b6b2";
    public static final String REDIRECT_URI = "project2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private String mAccessToken, mAccessCode;
    private TextView tokenTextView, codeTextView;
    private FirebaseAuth mAuth;
    private ActivityLoginBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        Button goToCreateAccountButton = findViewById(R.id.go_to_create_account_from_login_button);
        goToCreateAccountButton.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(i);
        });

        Button connectToSpotify = findViewById(R.id.connect_to_spotify_login_button);
        connectToSpotify.setOnClickListener(v -> {
            getToken();
        });

        Button loginButton = findViewById(R.id.login_button_new);
        loginButton.setOnClickListener(v -> login());

    }
    private void login() {
        EditText email = findViewById(R.id.login_email_new);
        EditText password = findViewById(R.id.login_password_new);

        if (checkEmailValid(email) && checkPasswordValid(password) && checkSpotifyConnected()){
            firebaseAuthLogin(email, password);
        }

        //Need to implement the reconnect to Spotify for log in
        //SpotifyHandler.populateArtistAndTrackData(mAccessToken);
    }

    private void firebaseAuthLogin(EditText email, EditText password) {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users").document(uid).update("spotifyToken", mAccessToken);

                        Toast.makeText(LoginActivity.this, "Logged in!",
                                Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, "Username or password is invalid",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(LoginActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email", "user-top-read"})
                .setCampaign("your-campaign-token")
                .build();
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            mAccessToken = response.getAccessToken();

            Button connectSpotifyLogin = findViewById(R.id.connect_to_spotify_login_button);
            connectSpotifyLogin.setText("Spotify Connected");
            connectSpotifyLogin.setClickable(false);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
//            setTextAsync(mAccessCode, codeTextView);
        }
    }

    private boolean checkEmailValid(EditText emailEditText) {
        boolean emptyField = emailEditText.getText().toString().isEmpty();
        if (emptyField) {
            Toast.makeText(LoginActivity.this, "Please enter your email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean incorrectFormat = !emailEditText.getText().toString().contains("@")
                || !emailEditText.getText().toString().contains(".");
        if (incorrectFormat) {
            Toast.makeText(LoginActivity.this, "Please enter a valid email",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPasswordValid(EditText passwordEditText) {
        boolean emptyField = passwordEditText.getText().toString().isEmpty();
        if (emptyField) {
            Toast.makeText(LoginActivity.this, "Please enter your password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean tooShort = passwordEditText.getText().toString().length() < 6;
        if (tooShort) {
            Toast.makeText(LoginActivity.this, "Please enter a password that is 6 characters or longer",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSpotifyConnected() {
        if (mAccessToken == null) {
            Toast.makeText(LoginActivity.this, "Please connect to Spotify first!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
