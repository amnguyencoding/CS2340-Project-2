package com.example.project2.ui.dashboard;

import android.os.Bundle;

import com.bumptech.glide.Glide;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentSongReleaseDateBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class SongReleaseDateFragment extends Fragment {

    private FragmentSongReleaseDateBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongReleaseDateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        Button returnButton = root.findViewById(R.id.return_to_dashboard_from_song_game_button);
        returnButton.setOnClickListener((v)-> {
            Navigation.findNavController(v).navigate(R.id.navigation_dashboard);
        });

        ArrayList<String> names = SpotifyHandler.getTopTrackNameData();
        ArrayList<String> dates = SpotifyHandler.getTopTrackReleaseDateData();
        ArrayList<String> images = SpotifyHandler.getTopTrackImageData();

        int index = (int) (Math.random() * names.size());
        int dateAnswer = Integer.parseInt(dates.get(index).substring(0,4));

        TextView gameText = root.findViewById(R.id.song_game_text);
        gameText.setText("What year was the song " + names.get(index) + " released?");

        ImageView songImage = root.findViewById(R.id.song_image);
        Glide.with(requireContext()).load(images.get(index)).into(songImage);

        Button submitGuess = root.findViewById(R.id.song_submit_guess);
        submitGuess.setOnClickListener((v) -> {
            EditText guessEditText = root.findViewById(R.id.song_game_year_guess);
            String guess = guessEditText.getText().toString();
            if (guess.length() != 4 || !isNumeric(guess)) {
                Log.i("guess length", String.valueOf(guess.length()));
                Log.i("isnumeric", String.valueOf(isNumeric(guess)));
                Toast.makeText(getContext(), "Please enter a valid guess", Toast.LENGTH_SHORT).show();
            } else {
                int dateGuess = Integer.parseInt(guess);
                if (dateGuess == dateAnswer) {
                    Toast.makeText(getContext(), "Correct guess!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Incorrect guess, try again!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private boolean isNumeric(String str) {
        try {
            int num = Integer.parseInt(str);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }
}