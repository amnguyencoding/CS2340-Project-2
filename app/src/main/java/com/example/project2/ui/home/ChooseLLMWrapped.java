package com.example.project2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.project2.R;
import com.example.project2.databinding.FragmentChooseLlmWrappedBinding;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChooseLLMWrapped extends Fragment {
    private String mAccessToken;

    private FragmentChooseLlmWrappedBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChooseLlmWrappedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        Button backButton = root.findViewById(R.id.backToRecommendedArtistsButton);
        backButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_recommendedArtists));

        Button personalityButton = root.findViewById(R.id.personalityWrappedButton);
        personalityButton.setOnClickListener((v)-> {
            Bundle bundle = new Bundle();
            bundle.putString("prompt", "personality");
            Navigation.findNavController(v).navigate(R.id.navigation_loading, bundle);
        });

        Button fashionButton = root.findViewById(R.id.fashionWrappedButton);
        fashionButton.setOnClickListener((v)-> {
            Bundle bundle = new Bundle();
            bundle.putString("prompt", "fashion");
            Navigation.findNavController(v).navigate(R.id.navigation_loading, bundle);
        });

        return root;
    }
}
