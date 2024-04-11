package com.example.project2.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.project2.R;
import com.example.project2.databinding.FragmentHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
            navBar.setVisibility(View.VISIBLE);
        }

        if (!firstTimeVisited) {
            firstTimeVisited = true;
            // Necessary because Android Studio freaks out and
            // makes navBar null cause it's dumb and can't find it
            // the first time the home fragment is opened
        }

        // Find the "View Wrapped" button and set an OnClickListener to navigate to WrappedFragment
        Button viewWrappedButton = root.findViewById(R.id.viewWrappedButton);
        viewWrappedButton.setOnClickListener(v -> {
            // Navigate to WrappedFragment when the button is clicked
            Navigation.findNavController(v).navigate(R.id.navigation_wrapped_intro);
        });
      
        Button viewPastWraps = view.findViewById(R.id.viewPastWrappedButton);
        viewPastWraps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.navigation_past_wraps);
            }
        });
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}

