package com.example.project2.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private static boolean firstTimeVisited = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (firstTimeVisited) {
            BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
            if (navBar != null) {
                navBar.setVisibility(View.VISIBLE);
            }
        }

        if (!firstTimeVisited) {
            firstTimeVisited = true;
            // Necessary because Android Studio makes navBar null the first time the home fragment is opened
        }

        // Find the "View Wrapped" button and set an OnClickListener to navigate to WrappedFragment
        Button viewWrappedButton = root.findViewById(R.id.viewWrappedButton);
        viewWrappedButton.setOnClickListener(v -> {
            // Navigate to WrappedFragment when the button is clicked
            Navigation.findNavController(v).navigate(R.id.navigation_wrapped_intro);
        });
      
        Button viewPastWraps = root.findViewById(R.id.viewPastWrappedButton);
        viewPastWraps.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_past_wraps));

        LinearLayout layout = root.findViewById(R.id.recap_layout);
        populateRecapLayout(layout, inflater,
                SpotifyHandler.getTopArtistImageData(), SpotifyHandler.getTopArtistNameData());

        Button recapTracks = root.findViewById(R.id.top_tracks_recap_button);
        recapTracks.setOnClickListener(v -> populateRecapLayout(layout, inflater,
                SpotifyHandler.getTopTrackImageData(), SpotifyHandler.getTopTrackNameData()));
        Button recapArtists = root.findViewById(R.id.top_artists_recap_button);
        recapArtists.setOnClickListener(v -> populateRecapLayout(layout, inflater,
                SpotifyHandler.getTopArtistImageData(), SpotifyHandler.getTopArtistNameData()));

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void populateRecapLayout(ViewGroup vg, LayoutInflater inflater, ArrayList<String> images, ArrayList<String> names) {
        vg.removeAllViews();
        for (int i = 0; i < 5; i++) {
            View recap = inflater.inflate(R.layout.recap_grid, null, false);
            ImageView image = recap.findViewById(R.id.recap_image);
            Glide.with(requireContext()).load(images.get(i)).into(image);
            TextView text = recap.findViewById(R.id.recap_text);
            text.setText(names.get(i));
            vg.addView(recap);
        }
    }
}

