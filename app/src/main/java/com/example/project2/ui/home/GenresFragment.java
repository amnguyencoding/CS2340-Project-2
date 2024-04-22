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
import com.example.project2.databinding.FragmentGenresBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class GenresFragment extends Fragment {
    private FragmentGenresBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGenresBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        ArrayList<String> topGenres = SpotifyHandler.getTopGenres();

        TextView genre1 = root.findViewById(R.id.genre1);
        genre1.setText(topGenres.get(0));

        TextView genre2 = root.findViewById(R.id.genre2);
        genre2.setText(topGenres.get(1));

        TextView genre3 = root.findViewById(R.id.genre3);
        genre3.setText(topGenres.get(2));

        TextView genre4 = root.findViewById(R.id.genre4);
        genre4.setText(topGenres.get(3));

        TextView genre5 = root.findViewById(R.id.genre5);
        genre5.setText(topGenres.get(4));

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button recommended = view.findViewById(R.id.recommendedArtistsButton);
        Button songs = view.findViewById(R.id.backToSongsButton);

        recommended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WrappedFragment when the button is clicked
                Navigation.findNavController(v).navigate(R.id.navigation_recommendedArtists);
            }
        });

        songs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WrappedFragment when the button is clicked
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_songs);
            }
        });
    }
}