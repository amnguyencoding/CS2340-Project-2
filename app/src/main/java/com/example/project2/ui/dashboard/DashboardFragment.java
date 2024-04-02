package com.example.project2.ui.dashboard;

import android.os.Bundle;
import okhttp3.Call;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {
    private String mAccessToken;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.VISIBLE);

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

                    //fetch data and store to arraylist -- figure out better way to store data later
//                        SpotifyHandler mainActivityHandler = new SpotifyHandler(call);
//                        mainActivityHandler.getUserProfileData(MainActivity.this, SpotifyHandler.TOP_ARTISTS_URL,
//                                mAccessToken);//maybe make this method return an arraylist idk
                    /*SpotifyHandler dashboardHandlerTest = new SpotifyHandler();
                    ArrayList<String> topArtists= dashboardHandlerTest.getUserProfileData(SpotifyHandler.TOP_ARTISTS_URL,
                            mAccessToken);*/
                    //TextView dataTest = (TextView) root.findViewById(R.id.text_dashboard);
                    //dataTest.setText(topArtists.toString());

                } else {
                    // document does not exist (but we should never reach this point since we guaranteed the login
                    Log.i("lots of errors", "No such document");
                }
            } else {
                // could not get the document (some other error)
                Log.i("lots of ", "get failed with ", task.getException());
            }
        });

        Button useLLMButton = root.findViewById(R.id.use_llm_button);
        useLLMButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_chooseprompt));

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}