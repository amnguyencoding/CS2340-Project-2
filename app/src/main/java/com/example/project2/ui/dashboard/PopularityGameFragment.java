package com.example.project2.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentPopularitygameBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PopularityGameFragment extends Fragment {
    private String mAccessToken;

    private FragmentPopularitygameBinding binding;
    private static Pair<String, String>[] artistOne;
    private static Pair<String, String>[] artistTwo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPopularitygameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        Button returnButton = root.findViewById(R.id.return_to_dashboard_from_popularity_button);
        returnButton.setOnClickListener((v)-> {
            Navigation.findNavController(v).navigate(R.id.navigation_dashboard);
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetching image URLS and other data
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    mAccessToken = document.getString("spotifyToken");

                    SpotifyHandler.populateArtistAndTrackData(mAccessToken);
                    ArrayList<String> topArtists = SpotifyHandler.getTopArtistNameData();
                    ArrayList<String> topArtistsImages = SpotifyHandler.getTopArtistImageData();
                    ArrayList<String> topTrackImages = SpotifyHandler.getTopTrackImageData();
                    ArrayList<Integer> topArtistsFollowers = SpotifyHandler.getTopArtistFollowerData();
                    ArrayList<String> topTracks = SpotifyHandler.getTopTrackNameData();
                    ArrayList<String> topTracksReleaseDates = SpotifyHandler.getTopTrackReleaseDateData();
                    Log.i("Artists",topArtists.toString());
                    Log.i("Images",topArtistsImages.toString());
                    Log.i("Followers",topArtistsFollowers.toString());
                    Log.i("Tracks",topTracks.toString());
                    Log.i("Release Dates",topTracksReleaseDates.toString());
                    Log.i("Track URLS",topTrackImages.toString());


                    int index = (int)(Math.random() * topArtists.size());
                }
            }
        });

        return root;
    }
}
