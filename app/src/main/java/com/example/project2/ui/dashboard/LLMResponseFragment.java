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
    private static String[] gptResponses = new String[5];

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLlmresponseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

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

                        SpotifyHandler LLMResponseHandler = new SpotifyHandler();
                        ArrayList<String> topArtists = LLMResponseHandler.getUserProfileData(SpotifyHandler.TOP_ARTISTS_URL,
                                mAccessToken);

                        String promptInput = getArguments().getString("prompt");

                        Pair<String, Integer> prompt = pickPrompt(promptInput, topArtists);

                        String gptPrompt = prompt.first;
                        int index = prompt.second;

                        gptPrompt = gptPrompt + " Tell me in 2nd person point of view and in four sentences. Please sprinkle references to the artists in the prompt throughout your response.";
                        if (index == 3) gptPrompt = gptPrompt + " Do not suggest artists that are already in the list.";

                        TextView gptResponse = root.findViewById(R.id.llm_response_text);

                        if (gptResponses[index] == null) {
                            gptResponses[index] = getGPTResponse(gptPrompt);
                        }
                        gptResponse.setText(gptResponses[index]);

                    }
                }
            }
        });

        Button returnButton = root.findViewById(R.id.return_from_response_button);
        returnButton.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_dashboard));

        return root;
    }

    private String getGPTResponse(String gptPrompt) {
        String response;
        try {
            GPTHandler handler = new GPTHandler();
            response = handler.makeRequest(gptPrompt).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private Pair<String, Integer> pickPrompt(String prompt, ArrayList<String> topArtists){
        String gptPrompt = "";
        int index = 0;
        switch (prompt) {
            case "personality":
                gptPrompt = "What kind of personality would a person who listens to " + topArtists + " have?";
                index = 0;
                break;
            case "fashion":
                gptPrompt = "What would a person who listens to " + topArtists + " wear?";
                index = 1;
                break;
            case "future":
                gptPrompt = "What kind of future would a person who listens to " + topArtists + " have?";
                index = 2;
                break;
            case "artists":
                gptPrompt = "What kind of artists would a person who listens to " + topArtists + " like?";
                index = 3;
                break;
            case "songs":
                gptPrompt = "What kind of songs would a person who listens to " + topArtists + " like?";
                index = 4;
                break;
        }
        return new Pair<>(gptPrompt, index);
    }
}
