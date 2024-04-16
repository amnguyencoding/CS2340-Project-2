package com.example.project2.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2.R;
import com.example.project2.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {
    private ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button goToLoginButton = findViewById(R.id.go_to_login_button);
        goToLoginButton.setOnClickListener(v -> {
            Intent i = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(i);
        });

        Button goToCreateAccount = findViewById(R.id.go_to_create_account_button);
        goToCreateAccount.setOnClickListener(v -> {
            Intent i = new Intent(IntroActivity.this, CreateAccountActivity.class);
            startActivity(i);
        });
    }
}
