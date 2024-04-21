package com.example.project2.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.TimeRange;
import com.example.project2.databinding.FragmentSongsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WrappedFragment extends Fragment {
    private static TimeRange selectedTimeRange;
    private String mAccessToken;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wrapped_intro, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button wrapped4Weeks = view.findViewById(R.id.wrapped4Weeks);
        Button wrapped6Months = view.findViewById(R.id.wrapped6Months);
        Button wrappedLastYear = view.findViewById(R.id.wrappedLastYear);
        Button home = view.findViewById(R.id.returnToHome);

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    mAccessToken = document.getString("spotifyToken");
                }
            }
        });

        wrapped4Weeks.setOnClickListener(v -> {
            selectedTimeRange = TimeRange.SHORT_TERM;
            populateDataBasedOnTimeRange();
            Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
        });
        wrapped6Months.setOnClickListener(v -> {
            selectedTimeRange = TimeRange.MEDIUM_TERM;
            populateDataBasedOnTimeRange();
            Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
        });
        wrappedLastYear.setOnClickListener(v -> {
            selectedTimeRange = TimeRange.LONG_TERM;
            populateDataBasedOnTimeRange();
            Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
        });

        home.setOnClickListener(v -> {
            selectedTimeRange = TimeRange.MEDIUM_TERM;
            populateDataBasedOnTimeRange();
            Navigation.findNavController(v).navigate(R.id.navigation_home);
        });
    }
    private void populateDataBasedOnTimeRange() {
        if (mAccessToken != null) {
            SpotifyHandler.populateArtistAndTrackData(mAccessToken, selectedTimeRange);
        }
    }

    public static TimeRange getSelectedTimeRange() {
        return selectedTimeRange;
    }
}



