package com.example.project2.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project2.AuthenticationActivity;
import com.example.project2.MainActivity;
import com.example.project2.R;
import com.example.project2.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = user.getUid();

        db.collection("users").document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                }
            });

        // need a "change spotify account" button? prob not right
        Button changeEmailButton = view.findViewById(R.id.change_email);
        changeEmailButton.setOnClickListener(view1 -> {
            // i guess all of this should be in a new method, perhaps even a new file
//            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogBoxTheme);
//            builder.setTitle("Add Class");

//            View customLayout = inflater.inflate(R.layout.uhhh??, null);
//            builder.setView(customLayout);

//            builder.setPositiveButton("OK", (dialog, which) -> {

//            });

//            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

//            AlertDialog dialog = builder.create();
//            dialog.show();

        });

        Button changePasswordButton = view.findViewById(R.id.change_password);
        changePasswordButton.setOnClickListener(view1 -> {
            // implementation here
        });

        Button logoutButton = view.findViewById(R.id.logout);
        logoutButton.setOnClickListener(view1 -> {
            logoutUser(mAuth);
        });

        Button deleteAccountButton = view.findViewById(R.id.delete_account);
        deleteAccountButton.setOnClickListener(view1 -> {
            deleteUser(user, db, uid);
        });

    }

    private void logoutUser(FirebaseAuth mAuth) {
        mAuth.signOut();

        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), AuthenticationActivity.class);
        startActivity(i);
    }

    private void deleteUser(FirebaseUser user, FirebaseFirestore db, String uid) {
        user.delete();
        db.collection("users").document(uid).delete();

        Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), AuthenticationActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}