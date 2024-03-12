package com.example.project2.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project2.R;
import com.example.project2.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

        private FragmentLoginBinding binding;

        public View onCreateView(@NonNull LayoutInflater inflater,
                                ViewGroup container, Bundle savedInstanceState) {
            LoginViewModel loginViewModel =
                    new ViewModelProvider(this).get(LoginViewModel.class);

            binding = FragmentLoginBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            return root;
        }



        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
}
