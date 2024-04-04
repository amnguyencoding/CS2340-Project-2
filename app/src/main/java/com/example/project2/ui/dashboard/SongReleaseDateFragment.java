package com.example.project2.ui.dashboard;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentSongReleaseDateBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class SongReleaseDateFragment extends Fragment {

    private String mAccessToken;
    private FragmentSongReleaseDateBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongReleaseDateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        Button returnButton = root.findViewById(R.id.return_to_dashboard_from_song_game_button);
        returnButton.setOnClickListener((v)-> {
            Navigation.findNavController(v).navigate(R.id.navigation_dashboard);
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    mAccessToken = document.getString("spotifyToken");

                    SpotifyHandler SongGameHandler = new SpotifyHandler();
                    ArrayList<String> topTracks = SongGameHandler.getUserProfileData(SpotifyHandler.TOP_TRACKS_URL,
                            SpotifyHandler.NAME_DATA, mAccessToken);
                    // this toptracks doesnt work get cause spotify handler hasn't adapted the new method but it will work soon once we change the getuserprofiledata method

                }
            }
        });

        return root;
    }
}