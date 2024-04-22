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

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.TimeRange;
import com.example.project2.databinding.FragmentArtistBinding;
import com.example.project2.databinding.FragmentSongsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class SongsFragment extends Fragment {
    private FragmentSongsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        ArrayList<String> topSongs = SpotifyHandler.getTopTrackNameData();
        ArrayList<String> topSongsImages = SpotifyHandler.getTopTrackImageData();

        TextView song1name = root.findViewById(R.id.songOneName);
        song1name.setText(topSongs.get(0));

        TextView song2name = root.findViewById(R.id.songTwoName);
        song2name.setText(topSongs.get(1));

        TextView song3name = root.findViewById(R.id.songThreeName);
        song3name.setText(topSongs.get(2));

        TextView song4name = root.findViewById(R.id.songFourName);
        song4name.setText(topSongs.get(3));

        TextView song5name = root.findViewById(R.id.songFiveName);
        song5name.setText(topSongs.get(4));

        ImageView song1Image = root.findViewById(R.id.songOneImage);
        Glide.with(requireContext()).load(topSongsImages.get(0)).into(song1Image);

        ImageView song2Image = root.findViewById(R.id.songTwoImage);
        Glide.with(requireContext()).load(topSongsImages.get(1)).into(song2Image);

        ImageView song3Image = root.findViewById(R.id.songThreeImage);
        Glide.with(requireContext()).load(topSongsImages.get(2)).into(song3Image);

        ImageView song4Image = root.findViewById(R.id.songFourImage);
        Glide.with(requireContext()).load(topSongsImages.get(3)).into(song4Image);

        ImageView song5Image = root.findViewById(R.id.songFiveImage);
        Glide.with(requireContext()).load(topSongsImages.get(4)).into(song5Image);

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button genres = view.findViewById(R.id.genresButton);
        Button artists = view.findViewById(R.id.backToArtistsButton);

        genres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_genres);
            }
        });

        artists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
            }
        });
    }
}