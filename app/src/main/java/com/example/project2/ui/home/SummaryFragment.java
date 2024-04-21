package com.example.project2.ui.home;

import android.os.Bundle;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentSummaryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SummaryFragment extends Fragment {
    private FragmentSummaryBinding binding;
    private String mAccessToken;
    private String uid;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSummaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        ArrayList<String> topArtists = SpotifyHandler.getTopArtistNameData();
        ArrayList<String> topSongs = SpotifyHandler.getTopTrackNameData();


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
        genre1.setText(SpotifyHandler.getTopGenres().get(0));

        ImageView song1Image = root.findViewById(R.id.summaryWrappedImage);
        Glide.with(requireContext()).load(SpotifyHandler.getTopArtistImageData().get(0)).into(song1Image);

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button home = view.findViewById(R.id.summaryWrappedReturnButton);
        Button restart = view.findViewById(R.id.summaryWrappedRestartButton);
        Button save = view.findViewById(R.id.summaryWrappedSaveButton);
        home.setOnClickListener(v -> {
            // Navigate to WrappedFragment when the button is clicked
            Navigation.findNavController(v).navigate(R.id.navigation_wrapped_intro);
        });

        restart.setOnClickListener(v -> {
            // Navigate to WrappedFragment when the button is clicked
            Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
        });

        save.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Wrap Saved!", Toast.LENGTH_SHORT).show();
            saveDataToFirebase();
        });

    }

    private void saveDataToFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ArrayList<String> topArtists = SpotifyHandler.getTopArtistNameData();
        while (topArtists.size() > 5) topArtists.remove(topArtists.size() - 1);
        ArrayList<String> topSongs = SpotifyHandler.getTopTrackNameData();
        while (topSongs.size() > 5) topSongs.remove(topSongs.size() - 1);
        String topGenre = SpotifyHandler.getTopGenres().get(0);
        String topArtistImage = SpotifyHandler.getTopArtistImageData().get(0);

        Map<String, Object> summaryData = new HashMap<>();
        summaryData.put("topArtists", topArtists);
        summaryData.put("topSongs", topSongs);
        summaryData.put("topGenre", topGenre);
        summaryData.put("topArtistImage", topArtistImage);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        summaryData.put("date", sdf.format(new Date()));

            db.collection("users/"+uid+"/summaryData").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int index = task.getResult().size()+1;

                db.collection("users/"+uid+"/summaryData").document(index+"")
                        .set(summaryData)
                        .addOnSuccessListener(documentReference -> {
                            // Show success message or navigate to another fragment/activity
                            Log.d("TAG", "DocumentSnapshot added with ID: " + index);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("TAG", "Error adding document", e);
                            // Show error message or handle failure
                        });

            }
        });
    }
}