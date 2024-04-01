package com.example.project2.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project2.GPTHandler;
import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String mAccessToken;

    private String gptResponseText = "temp; delete when using api";
    private ArrayList<String> topArtists;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
//                        SpotifyHandler mainActivityHandler = new SpotifyHandler(call);
//                        mainActivityHandler.getUserProfileData(MainActivity.this, SpotifyHandler.TOP_ARTISTS_URL,
//                                mAccessToken);//maybe make this method return an arraylist idk
                        SpotifyHandler homeHandlerTest = new SpotifyHandler();
                        topArtists = homeHandlerTest.getUserProfileData(SpotifyHandler.TOP_ARTISTS_URL,
                                mAccessToken);
                        Log.i("topArtists", topArtists.toString());
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

        TextView gptResponse = view.findViewById(R.id.gpt_response);

        if (gptResponseText.isEmpty()) {
            GPTHandler handler = new GPTHandler();
            if (topArtists != null){ // remove this later when we figure out the issue
                try {
                    gptResponseText = handler.makeRequest(topArtists.toString()).get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else Log.i("oops!", "its null i guess");
            Log.i("GPTHandler", gptResponseText);

            gptResponse.setText(gptResponseText);
        } else {
            gptResponse.setText(gptResponseText);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
