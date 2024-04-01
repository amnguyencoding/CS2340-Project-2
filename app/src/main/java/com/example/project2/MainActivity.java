package com.example.project2;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project2.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import okhttp3.Call;

public class MainActivity extends AppCompatActivity {
    // have to change this to build the authentication fragment not the other 3 fragments? then i have to move the building 3 main fragments/navbar after successful login

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private String mAccessToken;
    private ArrayList<String> topDataTest;
    private Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mAccessToken = document.getString("spotifyToken");

                        //fetch data and store to arraylist -- figure out better way to store data later
                        SpotifyHandler mainActivityHandler = new SpotifyHandler();
                        topDataTest = mainActivityHandler.getUserProfileData(SpotifyHandler.TOP_ARTISTS_URL,
                                mAccessToken);

                    } else {
                        // document does not exist (but we should never reach this point since we guaranteed the login
                        Log.i("lots of errors", "No such document");
                    }
                } else {
                    // could not get the document (some other error)
                    Log.i("lots of ", "get failed with ", task.getException());
                }
            }
        });
    }
}