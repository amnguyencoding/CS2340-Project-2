package com.example.project2.ui.home;

import android.os.Bundle;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentArtistBinding;
import com.example.project2.databinding.FragmentRecommendedArtistsWrappedBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class RecommendedArtists extends Fragment {
    private FragmentRecommendedArtistsWrappedBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecommendedArtistsWrappedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        ArrayList<String> rec = SpotifyHandler.getRecommendedArtistNames();

        TextView rec1 = root.findViewById(R.id.recc);
        rec1.setText("Hey there! Based on your diverse listening preferences, we've crafted a selection of recommended artists just for you. Delve into captivating music from " + rec.get(0) +
                ", explore the unique sounds of " + rec.get(1) + ", vibe to the beats of " + rec.get(2)+ ", discover hidden gems with " + rec.get(3)+ ", and immerse yourself in the artistry of " + rec.get(4) +". Happy exploring! ");

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button genres = view.findViewById(R.id.backtoGenresButton);
        Button llmWrapped = view.findViewById(R.id.LLMWrappedButton);

        genres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WrappedFragment when the button is clicked
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_genres);
            }
        });

        llmWrapped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WrappedFragment when the button is clicked
                Navigation.findNavController(v).navigate(R.id.navigation_chooseLLMWrapped);
            }
        });
    }
}

