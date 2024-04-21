package com.example.project2.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.project2.R;
import com.example.project2.databinding.FragmentGameBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GameFragment extends Fragment {
    private String mAccessToken;

    private FragmentGameBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

        Button returnButton = root.findViewById(R.id.return_to_dashboard_button);
        returnButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_game));

        // Note for later: use the click listeners to pass info about the prompt to the GPT thing
        // Also: should change loading text based on if it is a prediction or a recommendation

        Button recommendedArtistsButton = root.findViewById(R.id.recommended_artists_prompt_button);
        recommendedArtistsButton.setOnClickListener((v)-> {
            Bundle bundle = new Bundle();
            bundle.putString("prompt", "artists");
            Navigation.findNavController(v).navigate(R.id.navigation_loading, bundle);
        });

        Button songReleaseGameButton = root.findViewById(R.id.play_song_release_date_button);
        songReleaseGameButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_songreleasedate));

        Button popularityGameButton = root.findViewById(R.id.play_popularity_guess_button);
        popularityGameButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_popularitygame));

        return root;
    }
}