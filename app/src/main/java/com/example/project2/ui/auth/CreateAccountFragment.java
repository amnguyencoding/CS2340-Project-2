package com.example.project2.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project2.R;
import com.example.project2.databinding.FragmentCreateAccountBinding;

public class CreateAccountFragment extends Fragment {

    private FragmentCreateAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateAccountViewModel createAccountViewModel =
                new ViewModelProvider(this).get(CreateAccountViewModel.class);

        binding = FragmentCreateAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
