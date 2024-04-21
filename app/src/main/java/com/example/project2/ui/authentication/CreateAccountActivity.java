package com.example.project2.ui.authentication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.MainActivity;
import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.TimeRange;
import com.example.project2.databinding.ActivityCreateaccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    public static final String CLIENT_ID = "14c60e61b15243e3ad38b5dbcb57b6b2";
    public static final String REDIRECT_URI = "project2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private String mAccessToken, mAccessCode;
    private TextView tokenTextView, codeTextView;
    private FirebaseAuth mAuth;
    private ActivityCreateaccountBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateaccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        Button goToLoginButton = findViewById(R.id.go_to_login_from_create_account_button);
        goToLoginButton.setOnClickListener(v -> {
            Intent i = new Intent(CreateAccountActivity.this, LoginActivity.class);
            startActivity(i);
        });

        Button connectToSpotify = findViewById(R.id.connect_to_spotify_create_account_button);
        connectToSpotify.setOnClickListener(v -> getToken());

        Button createAccountButton = findViewById(R.id.create_account_button_new);
        createAccountButton.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {
        EditText fullName = findViewById(R.id.create_account_name_new);
        EditText email = findViewById(R.id.create_account_email_new);
        EditText password = findViewById(R.id.create_account_password_new);

        // Firebase authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (checkNameValid(fullName) && checkEmailValid(email) && checkPasswordValid(password) && checkSpotifyConnected()) {
            firebaseAuthCreateAccount(fullName, email, password, mAuth);
            SpotifyHandler.populateArtistAndTrackData(mAccessToken, TimeRange.SHORT_TERM);
        }
    }

    private void firebaseAuthCreateAccount(EditText fullName, EditText email, EditText password, FirebaseAuth mAuth) {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        // honestly, i dont know whether to store the uid or the email/pw combo ig we'll see what to use later

                        // Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("uid", uid);
                        newUser.put("name", fullName.getText().toString());
                        newUser.put("email", email.getText().toString());
                        newUser.put("password", password.getText().toString());
                        newUser.put("spotifyToken", mAccessToken);

                        db.collection("users").document(uid).set(newUser);

                        Toast.makeText(CreateAccountActivity.this, "Account created!",
                                Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(CreateAccountActivity.this, MainActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Account creation failed. Check the email is not used already",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(CreateAccountActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
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

            Button connectSpotifyLogin = findViewById(R.id.connect_to_spotify_create_account_button);
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
            Toast.makeText(CreateAccountActivity.this, "Please enter your email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean incorrectFormat = !emailEditText.getText().toString().contains("@")
                || !emailEditText.getText().toString().contains(".");
        if (incorrectFormat) {
            Toast.makeText(CreateAccountActivity.this, "Please enter a valid email",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPasswordValid(EditText passwordEditText) {
        boolean emptyField = passwordEditText.getText().toString().isEmpty();
        if (emptyField) {
            Toast.makeText(CreateAccountActivity.this, "Please enter your password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean tooShort = passwordEditText.getText().toString().length() < 6;
        if (tooShort) {
            Toast.makeText(CreateAccountActivity.this, "Please enter a password that is 6 characters or longer",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkNameValid(EditText nameEditText) {
        if (nameEditText.getText().toString().isEmpty()
                || !nameEditText.getText().toString().contains(" ")) {
            Toast.makeText(CreateAccountActivity.this, "Please enter your full name",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSpotifyConnected() {
        if (mAccessToken == null) {
            Toast.makeText(CreateAccountActivity.this, "Please connect to Spotify first!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
