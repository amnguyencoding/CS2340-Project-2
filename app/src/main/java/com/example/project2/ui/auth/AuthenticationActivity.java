package com.example.project2.ui.auth;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project2.MainActivity;
import com.example.project2.R;
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

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class AuthenticationActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "14c60e61b15243e3ad38b5dbcb57b6b2";
    public static final String REDIRECT_URI = "project2://auth";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken, mAccessCode;
    private Call mCall;
    private TextView tokenTextView, codeTextView;
    private ActivityAuthenticationBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);

        Button connectSpotify = (Button) findViewById(R.id.connect_spotify);
        Button createAccount = (Button) findViewById(R.id.create_account_button);

        connectSpotify.setOnClickListener((v) -> {
            getToken();
            //getCode();
        });

        createAccount.setOnClickListener((v) -> {
            //fetching spotify top data
            //getUserProfileData("https://api.spotify.com/v1/me/top/artists");
//            getUserProfileData("https://api.spotify.com/v1/me/top/tracks");

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

                                db.collection("users").document(uid).set(newUser);

                                Intent i = new Intent(AuthenticationActivity.this, MainActivity.class);
                                startActivity(i);
                            } else { // user already exists, probably
                                // If sign in fails, display a message to the user.
                                Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });



        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view1 -> {
            EditText email = findViewById(R.id.login_email);
            EditText password = findViewById(R.id.login_password);

            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent i = new Intent(AuthenticationActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(AuthenticationActivity.this, "Username or password is invalid.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

//        Button createAccountButton = findViewById(R.id.create_account_fragment_button);
//        Button loginButton = findViewById(R.id.login_fragment_button);

//        appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_create_account, R.id.navigation_login).build();

//        createAccountButton.setOnClickListener(view1 -> {
//            Intent i = new Intent(this, CreateAccountActivity.class);
//            startActivity(i);
//        });
//
//        loginButton.setOnClickListener(view1 -> {
//            Intent i = new Intent(this, LoginActivity.class);
//            startActivity(i);
//        });



//        final TextView textView = binding.textAuthentication;
//        authenticationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(AuthenticationActivity.this, AUTH_TOKEN_REQUEST_CODE, request);
        // wait wait wait what is the line above doing?
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" })
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

//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Button createAccountButton = view.findViewById(R.id.create_account_fragment_button);
//        Button loginButton = view.findViewById(R.id.login_fragment_button);
//
//        createAccountButton.setOnClickListener(view1 -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_authFragment_to_createAccountFragment);
//        });
//
//        loginButton.setOnClickListener(view1 -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_authFragment_to_loginFragment);
//        });
//
//    }


//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

    // do i even need this in activities?
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        binding = null;
//    }

}
