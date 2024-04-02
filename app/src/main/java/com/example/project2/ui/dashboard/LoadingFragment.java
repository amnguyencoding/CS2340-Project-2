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
import com.example.project2.databinding.FragmentLoadingBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoadingFragment extends Fragment {
    private String mAccessToken;

    private FragmentLoadingBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        Button continueButton = root.findViewById(R.id.loading_screen_continue_button);
        continueButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_llmresponse));

        return root;
    }
}
