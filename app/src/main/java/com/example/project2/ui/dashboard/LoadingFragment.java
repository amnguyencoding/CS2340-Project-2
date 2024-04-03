package com.example.project2.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import com.example.project2.databinding.FragmentLoadingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoadingFragment extends Fragment {
    private String mAccessToken;

    private FragmentLoadingBinding binding;
    private static String[] gptResponses = new String[3];
    private int index;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoadingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        Button continueButton = root.findViewById(R.id.loading_screen_continue_button);
        continueButton.setVisibility(View.GONE);

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

                    SpotifyHandler LLMResponseHandler = new SpotifyHandler();
                    ArrayList<String> topArtists = LLMResponseHandler.getUserProfileData(SpotifyHandler.TOP_ARTISTS_URL,
                            mAccessToken);

                    String promptInput = getArguments().getString("prompt");

                    Pair<String, Integer> prompt = pickPrompt(promptInput, topArtists);

                    String gptPrompt = prompt.first;
                    index = prompt.second;

                    gptPrompt = gptPrompt + " Tell me in 2nd person point of view and in three short sentences. " +
                            "Please sprinkle references to the artists in the prompt throughout your response.";
                    if (index == 2) gptPrompt = gptPrompt + " Do not suggest artists that are already in the list.";

                    Log.i("gptPrompt", gptPrompt);
                    if (gptResponses[index] == null) {
                        gptResponses[index] = getGPTResponse(gptPrompt);
                        Log.i("gptPromptInner", gptPrompt);
                    }
                    continueButton.setVisibility(View.VISIBLE);
                }
            }
        });

        continueButton.setOnClickListener((v)-> {
            Bundle bundle = new Bundle();
            bundle.putString("response", gptResponses[index]);
            Navigation.findNavController(v).navigate(R.id.navigation_llmresponse, bundle);
        });

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
                gptPrompt = "I listen to " + topArtists + ". What kind of personality do I have?";
                index = 0;
                break;
            case "fashion":
                gptPrompt = "I listen to " + topArtists + ". What kind of fashion do I have?";
                index = 1;
                break;
            case "artists":
                gptPrompt = "I listen to " + topArtists + ". What other artists would I like?";
                index = 2;
                break;
        }
        return new Pair<>(gptPrompt, index);
    }
}
