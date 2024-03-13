package com.example.project2.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.project2.R;
import com.example.project2.databinding.ActivityAuthenticationBinding;

// this file is the first page that the user sees when they open the app and has either log in or create account options
// ACTUALLY i guess we dont need to go to new fragments for create account/login and you can toggle either on by pressing a button on this screen? but idk figure out later
public class AuthenticationActivity extends AppCompatActivity {

//    private AppBarConfiguration appBarConfiguration;
    private ActivityAuthenticationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button createAccountButton = findViewById(R.id.create_account_fragment_button);
        Button loginButton = findViewById(R.id.login_fragment_button);

//        appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_create_account, R.id.navigation_login).build();

        createAccountButton.setOnClickListener(view1 -> {
            Intent i = new Intent(this, CreateAccountActivity.class);
            startActivity(i);
        });

        loginButton.setOnClickListener(view1 -> {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        });



//        final TextView textView = binding.textAuthentication;
//        authenticationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
    }

//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Button createAccountButton = view.findViewById(R.id.create_account_fragment_button);
//        Button loginButton = view.findViewById(R.id.login_fragment_button);
//
//        createAccountButton.setOnClickListener(view1 -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_authFragment_to_createAccountFragment);
//        });
//
//        loginButton.setOnClickListener(view1 -> {
//            NavController navController = Navigation.findNavController(view);
//            navController.navigate(R.id.action_authFragment_to_loginFragment);
//        });
//
//    }


//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

}
