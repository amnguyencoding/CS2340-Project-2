package com.example.project2.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.project2.GPTHandler;
import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentLlmresponseBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.util.Pair;

public class LLMResponseFragment extends Fragment {
    private String mAccessToken;
    private FragmentLlmresponseBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLlmresponseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        String response = getArguments().getString("response");
        TextView responseText = root.findViewById(R.id.llm_response_text);
        responseText.setText(response);

        Button returnButton = root.findViewById(R.id.return_from_response_button);
        returnButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_dashboard));

        return root;
    }
}
