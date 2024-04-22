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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.TimeRange;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PastWrapFragment extends Fragment {
    private static ArrayList<String> pastWrappedSpinnerItems = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_past_wraps, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        db.collection("users/"+uid+"/summaryData").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                pastWrappedSpinnerItems.clear();
                for (DocumentSnapshot document : documents) {
                    Map<String, Object> wrapData = document.getData();
                    pastWrappedSpinnerItems.add((String) wrapData.get("date"));
                }

                Spinner spinner = view.findViewById(R.id.past_wrapped_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), R.layout.spinner_item, pastWrappedSpinnerItems);
                adapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(adapter);

                Button showPastWrapButton = view.findViewById(R.id.show_past_wrap_button);
                showPastWrapButton.setOnClickListener((v)-> {
                    int wrapIndex = spinner.getSelectedItemPosition() + 1;
                    if (wrapIndex < 1 || wrapIndex > task.getResult().size()) Toast.makeText(getContext(), "Please select a wrapped to view", Toast.LENGTH_LONG).show();
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putInt("wrapIndex", wrapIndex);
                        Navigation.findNavController(view).navigate(R.id.navigation_display_past_wrap, bundle);
                    }
                });
            }}
        );

        Button homeButton = view.findViewById(R.id.past_wrapped_intro_to_home_button);
        homeButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_home));

    }
}