package com.example.project2.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.TimeRange;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PastWrapFragment extends Fragment {
    private static ArrayList<String> pastWrappedSpinnerItems = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_past_wraps, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView pastWrapText = view.findViewById(R.id.past_wrap_count_text);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        db.collection("users/"+uid+"/summaryData").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int wrapCount = task.getResult().size();


                pastWrapText.setText("You currently have " + wrapCount + " saved wraps\nWhich wrap would you like to see?");

                Log.i("wrapCount", Integer.toString(wrapCount));

                Button showPastWrapButton = view.findViewById(R.id.show_past_wrap_button);
                showPastWrapButton.setOnClickListener((v)-> {
                    EditText wrapIndexText = view.findViewById(R.id.past_wrap_pick_digit);

                    if (wrapIndexText.getText().toString().isEmpty()) Toast.makeText(getContext(), "Please enter a number", Toast.LENGTH_LONG).show();
                    else {
                        int wrapIndex = Integer.parseInt(wrapIndexText.getText().toString());
                        if (wrapIndex < 1 || wrapIndex > wrapCount) Toast.makeText(getContext(), "Please enter valid number for how many wraps you have", Toast.LENGTH_LONG).show();
                        else {
                            Bundle bundle = new Bundle();
                            bundle.putInt("wrapIndex", wrapIndex);
                            Navigation.findNavController(view).navigate(R.id.navigation_display_past_wrap, bundle);
                        }
                    }
                });
            }}
        );

        Button homeButton = view.findViewById(R.id.past_wrapped_intro_to_home_button);
        homeButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_home));

    }
}