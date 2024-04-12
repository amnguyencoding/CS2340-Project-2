package com.example.project2.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.TimeRange;
import com.example.project2.databinding.FragmentDisplayPastWrapBinding;
import com.example.project2.databinding.FragmentSummaryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Map;

public class DisplayPastWrapFragment extends Fragment {
    private FragmentDisplayPastWrapBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDisplayPastWrapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        int wrapIndex = getArguments().getInt("wrapIndex");

        db.collection("users/"+uid+"/summaryData").document(Integer.toString(wrapIndex)).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Object> wrapData = task.getResult().getData();
                        ArrayList<String> topArtists = (ArrayList<String>) wrapData.get("topArtists");
                        ArrayList<String> topSongs = (ArrayList<String>) wrapData.get("topSongs");
                        String topGenre = (String) wrapData.get("topGenre");
                        String topArtistImage = (String) wrapData.get("topArtistImage");
                        Log.i("topArtists", topArtists.toString());
                        Log.i("topSongs", topSongs.toString());
                        Log.i("topGenre", topGenre);
                        Log.i("topArtistImage", topArtistImage);

                        TextView artist1name = root.findViewById(R.id.summaryWrappedArtist1);
                        artist1name.setText("1. " + topArtists.get(0));


                        TextView artist2name = root.findViewById(R.id.summaryWrappedArtist2);
                        artist2name.setText("2. " + topArtists.get(1));


                        TextView artist3name = root.findViewById(R.id.summaryWrappedArtist3);
                        artist3name.setText("3. " + topArtists.get(2));


                        TextView artist4name = root.findViewById(R.id.summaryWrappedArtist4);
                        artist4name.setText("4. " + topArtists.get(3));


                        TextView artist5name = root.findViewById(R.id.summaryWrappedArtist5);
                        artist5name.setText("5. " + topArtists.get(4));

                        TextView song1name = root.findViewById(R.id.summaryWrappedSong1);
                        song1name.setText("1. " + topSongs.get(0));

                        TextView song2name = root.findViewById(R.id.summaryWrappedSong2);
                        song2name.setText("2. " + topSongs.get(1));


                        TextView song3name = root.findViewById(R.id.summaryWrappedSong3);
                        song3name.setText("3. " + topSongs.get(2));


                        TextView song4name = root.findViewById(R.id.summaryWrappedSong4);
                        song4name.setText("4. " + topSongs.get(3));


                        TextView song5name = root.findViewById(R.id.summaryWrappedSong5);
                        song5name.setText("5. " + topSongs.get(4));

                        TextView genre1 = root.findViewById(R.id.summaryWrappedTopGenre);
                        genre1.setText(topGenre);

                        ImageView song1Image = root.findViewById(R.id.summaryWrappedImage);
                        Glide.with(requireContext()).load(topArtistImage).into(song1Image);

                    }
                });

        Button summaryToHomeButton = root.findViewById(R.id.summaryWrappedHomeButton);
        summaryToHomeButton.setOnClickListener((v)-> {
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });

        return root;
    }
}