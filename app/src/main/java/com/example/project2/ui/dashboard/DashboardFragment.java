package com.example.project2.ui.dashboard;

import android.os.Bundle;
import okhttp3.Call;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentDashboardBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardFragment extends Fragment {
    private String mAccessToken;

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mAccessToken = document.getString("spotifyToken");

                        //fetch data and store to arraylist -- figure out better way to store data later
//                        SpotifyHandler mainActivityHandler = new SpotifyHandler(call);
//                        mainActivityHandler.getUserProfileData(MainActivity.this, SpotifyHandler.TOP_ARTISTS_URL,
//                                mAccessToken);//maybe make this method return an arraylist idk
                        /*SpotifyHandler dashboardHandlerTest = new SpotifyHandler();
                        dashboardHandlerTest.getUserProfileData(SpotifyHandler.TOP_ARTISTS_URL,
                                mAccessToken);
                        TextView dataTest = (TextView) root.findViewById(R.id.text_dashboard);
                        if (dashboardHandlerTest.getTopData() != null) {
                            dataTest.setText(dashboardHandlerTest.getTopData().toString());
                        }*/
                        // davis comments:
                        // move the above 2 lines inside the if statement cause it was running too early asynchronously
                        // maybe move those lines to a method
//                        topDataTest = mainActivityHandler.getTopData();
                    } else {
                        // document does not exist (but we should never reach this point since we guaranteed the login
                        Log.i("lots of errors", "No such document");
                    }
                } else {
                    // could not get the document (some other error)
                    Log.i("lots of ", "get failed with ", task.getException());
                }
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}