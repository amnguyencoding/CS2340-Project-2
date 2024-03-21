package com.example.project2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.databinding.ActivityAuthenticationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "14c60e61b15243e3ad38b5dbcb57b6b2";
    public static final String REDIRECT_URI = "project2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private String mAccessToken, mAccessCode;
    private TextView tokenTextView, codeTextView;
    private ActivityAuthenticationBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        tokenTextView = findViewById(R.id.token_text_view);
        codeTextView = findViewById(R.id.code_text_view);

        Button connectSpotify = findViewById(R.id.connect_spotify);

        Button createAccountText = findViewById(R.id.create_account_text);
        Button loginText = findViewById(R.id.login_text);
        LinearLayout createAccountLayout = findViewById(R.id.create_account_layout);
        LinearLayout loginLayout = findViewById(R.id.login_layout);
        loginLayout.setVisibility(View.GONE);

        createAccountText.setOnClickListener((v) -> {
            showCreateAccount(createAccountLayout, loginLayout);
        });

        loginText.setOnClickListener((v) -> {
            showLogin(createAccountLayout, loginLayout);
        });

        connectSpotify.setOnClickListener((v) -> {
            getToken();
            //getCode(); // doubt we need the code for anything so we dont need to store it prob
        });

        Button createAccountButton = findViewById(R.id.create_account_button);
        createAccountButton.setOnClickListener((v) -> {
            createAccount();
        });

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view1 -> {
            login();
        });
    }

    private void createAccount() {
        //fetching spotify top data EDIT: do in spotifyHandler
        //getUserProfileData("https://api.spotify.com/v1/me/top/artists");
//            getUserProfileData("https://api.spotify.com/v1/me/top/tracks");

        EditText fullName = findViewById(R.id.create_account_name);
        EditText email = findViewById(R.id.create_account_email);
        EditText password = findViewById(R.id.create_account_password);

        // Firebase authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (checkNameValid(fullName) && checkEmailValid(email) && checkPassWordValid(password)) {
         // all fields should be filled in if this condition hits
            // ^^ also need to check if the spotify account is connected to another exisiting account already
            // to do this, prob just try to read from firestore if the spotify token exists in one of the elements already, then throw error
            // actually** this is really annoying and kind of a hassle to do so maybe wait till later or ask the ta if we actually have to do this check
            // wait also what if the user puts in an email that already exists in the database? do we have to check for that too?

            firebaseAuthCreateAccount(fullName, email, password, mAuth);
        }
    }

    private void firebaseAuthCreateAccount(EditText fullName, EditText email, EditText password, FirebaseAuth mAuth) {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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

                            Toast.makeText(AuthenticationActivity.this, "Account created!",
                                    Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(AuthenticationActivity.this, MainActivity.class);
                            startActivity(i);
                        } else { // in case sign in fails
                            Toast.makeText(AuthenticationActivity.this, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void login() {
        EditText email = findViewById(R.id.login_email);
        EditText password = findViewById(R.id.login_password);

        if (checkEmailValid(email) && checkPassWordValid(password)){
            firebaseAuthLogin(email, password);
        }
    }

    private void firebaseAuthLogin(EditText email, EditText password) {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(AuthenticationActivity.this, MainActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(AuthenticationActivity.this, "Username or password is invalid",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showLogin(LinearLayout createAccountLayout, LinearLayout loginLayout) {
        createAccountLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    private void showCreateAccount(LinearLayout createAccountLayout, LinearLayout loginLayout) {
        loginLayout.setVisibility(View.GONE);
        createAccountLayout.setVisibility(View.VISIBLE);
    }

    private void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(AuthenticationActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
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
            setTextAsync(mAccessToken, tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    private boolean checkNameValid(EditText nameEditText) {
        if (nameEditText.getText().toString().isEmpty()
                || !nameEditText.getText().toString().contains(" ")) {
            Toast.makeText(AuthenticationActivity.this, "Please enter your full name",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkEmailValid(EditText emailEditText) {
        boolean emptyField = emailEditText.getText().toString().isEmpty();
        boolean incorrectFormat = !emailEditText.getText().toString().contains("@")
                || !emailEditText.getText().toString().contains(".");
        if (emptyField) {
            Toast.makeText(AuthenticationActivity.this, "Please enter your email",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (incorrectFormat) {
            Toast.makeText(AuthenticationActivity.this, "Please enter a valid email",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPassWordValid(EditText passwordEditText) {
        boolean emptyField = passwordEditText.getText().toString().isEmpty();
        boolean tooShort = passwordEditText.getText().toString().length() < 6;
        boolean notConnectedToSpotify = mAccessToken == null;
        if (emptyField) {
            Toast.makeText(AuthenticationActivity.this, "Please enter your password",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (tooShort) {
            Toast.makeText(AuthenticationActivity.this, "Please enter a password that is 6 characters or longer",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (notConnectedToSpotify) {
            Toast.makeText(AuthenticationActivity.this, "Please connect to Spotify",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
