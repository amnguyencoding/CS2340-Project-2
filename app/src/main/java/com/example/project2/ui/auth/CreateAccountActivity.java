package com.example.project2.ui.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.MainActivity;
import com.example.project2.R;
import com.example.project2.databinding.ActivityCreateAccountBinding;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);

        Button connectSpotify = (Button) findViewById(R.id.connect_spotify);
        Button createAccount = (Button) findViewById(R.id.create_account_button);

        connectSpotify.setOnClickListener((v) -> {
            getToken();
            //getCode();
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        createAccount.setOnClickListener((v) -> {
            EditText fullName = findViewById(R.id.create_account_name);
            EditText email = findViewById(R.id.create_account_email);
            EditText password = findViewById(R.id.create_account_password);

            // Firebase authentication
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            // before this, also need to check if the spotify account is connected to another exisiting account already
            // to do this, prob just try to read from firestore if the spotify token exists in one of the elements already, then throw error
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

                                // need checks for all the fields to be filled and for spotify account to be connected properly (i.e. the mAccessToken is some recognized value)
                                // should probably show a toast for like "account created!"

                                db.collection("users").add(newUser);

                                Intent i = new Intent(CreateAccountActivity.this, MainActivity.class);
                                startActivity(i);
                            } else { // user already exists, probably
                                // If sign in fails, display a message to the user.
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }

    // method copied from firebase documentation lol
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null){
//            reload(); // not sure what this is supposed to be
//        }
//    }

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
