package com.example.project2.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project2.R;
import com.example.project2.databinding.FragmentProfileBinding;
import com.example.project2.ui.authentication.IntroActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private static final int EMAIL_EDIT_DIALOG = 0;
    private static final int PASSWORD_EDIT_DIALOG = 1;
    private String uid;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uid = user.getUid();

        db.collection("users").document(uid)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String fullName = document.getString("name");
                            TextView userGreeting = view.findViewById(R.id.user_greeting);
                            String welcomeMessage = getString(R.string.welcome_message, fullName);
                            userGreeting.setText(welcomeMessage);
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
                });

        // need a "change spotify account" button? prob not right
        Button changeEmailButton = view.findViewById(R.id.change_email);
        changeEmailButton.setOnClickListener(view1 -> createEditProfileDialog(EMAIL_EDIT_DIALOG));

        Button changePasswordButton = view.findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(view1 -> createEditProfileDialog(PASSWORD_EDIT_DIALOG));

        Button logoutButton = view.findViewById(R.id.logout);
        logoutButton.setOnClickListener(view1 -> logoutUser());

        Button deleteAccountButton = view.findViewById(R.id.delete_account);
        deleteAccountButton.setOnClickListener(view1 -> deleteUser(user));

    }

    private void createEditProfileDialog(int dialogType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.DialogBoxTheme);
        if (dialogType == 0) builder.setTitle("Edit Email");
        else builder.setTitle("Edit Password");

        // set the custom layout
        View customLayout = this.getLayoutInflater().inflate(R.layout.edit_profile_field_dialog, null);
        builder.setView(customLayout);

        EditText profileFieldEdit = customLayout.findViewById(R.id.edit_profile_field);

        // add a button
        builder.setPositiveButton("OK", (dialog, which) -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String fieldText = profileFieldEdit.getText().toString();

            if (dialogType == 0 && checkEmailValid(profileFieldEdit)) {
                user.verifyBeforeUpdateEmail(fieldText);

                // if we dont store email and pass in firestore, we dont need this line below
                db.collection("users").document(uid).update("email", fieldText);
                Toast.makeText(this.getContext(), "Email updated. Check inbox for verification", Toast.LENGTH_LONG).show();

            } else if (dialogType == 1 && checkPassWordValid(profileFieldEdit)) {
                user.updatePassword(fieldText);

                // idem
                db.collection("users").document(uid).update("password", fieldText);
                Toast.makeText(this.getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean checkEmailValid(EditText emailEditText) {
        boolean emptyField = emailEditText.getText().toString().isEmpty();
        if (emptyField) {
            Toast.makeText(this.getContext(), "Please enter your email",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean incorrectFormat = !emailEditText.getText().toString().contains("@")
                || !emailEditText.getText().toString().contains(".");
        if (incorrectFormat) {
            Toast.makeText(this.getContext(), "Please enter a valid email",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkPassWordValid(EditText passwordEditText) {
        boolean emptyField = passwordEditText.getText().toString().isEmpty();
        if (emptyField) {
            Toast.makeText(this.getContext(), "Please enter your password",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean tooShort = passwordEditText.getText().toString().length() < 6;
        if (tooShort) {
            Toast.makeText(this.getContext(), "Please enter a password that is 6 characters or longer",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void logoutUser() {
        mAuth.signOut();

        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), IntroActivity.class);
        startActivity(i);
    }

    private void deleteUser(FirebaseUser user) {
        user.delete();
        db.collection("users").document(uid).delete();

        Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), IntroActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}