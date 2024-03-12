package com.example.project2.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.project2.R;
import com.example.project2.databinding.FragmentAuthenticationBinding;

// this file is the first page that the user sees when they open the app and has either log in or create account options
// ACTUALLY i guess we dont need to go to new fragments for create account/login and you can toggle either on by pressing a button on this screen? but idk figure out later
public class AuthenticationFragment extends Fragment {

    private FragmentAuthenticationBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AuthenticationViewModel authenticationViewModel =
                new ViewModelProvider(this).get(AuthenticationViewModel.class);

        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textAuthentication;
//        authenticationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button createAccountButton = view.findViewById(R.id.create_account_fragment_button);
        Button loginButton = view.findViewById(R.id.login_fragment_button);

        createAccountButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_authFragment_to_createAccountFragment);
        });

        loginButton.setOnClickListener(view1 -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_authFragment_to_loginFragment);
        });

    }
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
