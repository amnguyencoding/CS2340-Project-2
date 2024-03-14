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
import com.example.project2.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view1 -> {
            mAuth = FirebaseAuth.getInstance();

            EditText email = findViewById(R.id.login_email);
            EditText password = findViewById(R.id.login_password);

            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                // should pass this user into main activity

//                                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                db.collection("users").get()
//                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                        Log.i("HIIII IT WORKED", document.getId() + " => " + document.getData());
////                                                        Log.i("secondary", db.collection("users").toString());
//                                                        Log.i("third", "" + (String) document.get("Cduyr4FTM2QR4DyzndVf"));
////                                                        Log.i("fourth",document.getString("spotifyToken"));
//                                                    }
//                                                } else {
//                                                    Log.i("oops", "Error getting documents.", task.getException());
//                                                }
//                                            }
//                                        });

//                                String mAccessToken = user.getSpotifyToken();
                                // should pass the spotify access token to the main activity prob right

                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Username or password is invalid.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
