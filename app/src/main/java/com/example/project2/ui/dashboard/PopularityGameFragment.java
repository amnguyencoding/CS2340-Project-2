package com.example.project2.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.TimeRange;
import com.example.project2.databinding.FragmentPopularitygameBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PopularityGameFragment extends Fragment {
    private String mAccessToken;

    private FragmentPopularitygameBinding binding;
    private static Pair<String, String>[] artistOne;
    private static Pair<String, String>[] artistTwo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPopularitygameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        BottomNavigationView navBar = getActivity().findViewById(R.id.nav_view);
        navBar.setVisibility(View.GONE);

        Button returnButton = root.findViewById(R.id.return_to_dashboard_from_popularity_button);
        returnButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_dashboard));

        Button playAgainButton = root.findViewById(R.id.play_popularity_game_again_button);
        playAgainButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.navigation_popularitygame));
        playAgainButton.setVisibility(View.GONE);

        ArrayList<String> topArtists = SpotifyHandler.getTopArtistNameData();
        ArrayList<String> topArtistsImages = SpotifyHandler.getTopArtistImageData();
        ArrayList<Integer> topArtistsFollowers = SpotifyHandler.getTopArtistFollowerData();

        int indexOne = (int) (Math.random() * topArtists.size());
        int indexTwo = (int) (Math.random() * topArtists.size());
        while (indexOne == indexTwo) {
            indexTwo = (int) (Math.random() * topArtists.size());
        }
        int followersOne = topArtistsFollowers.get(indexOne);
        int followersTwo = topArtistsFollowers.get(indexTwo);
        String artistOneString = topArtists.get(indexOne);
        String artistTwoString = topArtists.get(indexTwo);

        TextView gameText = root.findViewById(R.id.popularity_game_text);
        gameText.setText("Which of these artists in your top 20 is more popular (by followers): "
                + topArtists.get(indexOne) + " or " + topArtists.get(indexTwo) + "?");

        ImageButton artistOne = root.findViewById(R.id.popular_artist_image_one);
        Glide.with(requireContext()).load(topArtistsImages.get(indexOne)).into(artistOne);

        TextView artistOneText = root.findViewById(R.id.popular_artist_one_text);
        artistOneText.setText(artistOneString);

        ImageButton artistTwo = root.findViewById(R.id.popular_artist_image_two);
        Glide.with(requireContext()).load(topArtistsImages.get(indexTwo)).into(artistTwo);

        TextView artistTwoText = root.findViewById(R.id.popular_artist_two_text);
        artistTwoText.setText(artistTwoString);

        TextView resultText = root.findViewById(R.id.popularity_game_result_text);
        resultText.setVisibility(View.GONE);

        DecimalFormat formatter = new DecimalFormat("#,###");
        String followerOneFormat = formatter.format(followersOne);
        String followerTwoFormat = formatter.format(followersTwo);

        ImageButton winner, loser;
        String winnerString, loserString;

        if (followersOne >= followersTwo) {
            winner = artistOne;
            loser = artistTwo;
            winnerString = "Correct! " + artistOneString + " has " + followerOneFormat
                    + " followers, while " + artistTwoString + " has " + followerTwoFormat
                    + " followers. Play again?";
            loserString = "Wrong, sorry! " + artistTwoString + " has " + followerTwoFormat
                    + " followers, while " + artistOneString + " has " + followerOneFormat
                    + " followers. Play again?";
        } else {
            winner = artistTwo;
            loser = artistOne;
            winnerString = "Correct! " + artistTwoString + " has " + followerTwoFormat
                    + " followers, while " + artistOneString + " has " + followerOneFormat
                    + " followers. Play again?";
            loserString = "Wrong, sorry! " + artistOneString + " has " + followerOneFormat
                    + " followers, while " + artistTwoString + " has " + followerTwoFormat
                    + " followers. Play again?";
        }

        winner.setOnClickListener(v -> {
            resultText.setVisibility(View.VISIBLE);
            resultText.setText(winnerString);
            playAgainButton.setVisibility(View.VISIBLE);
            loser.setClickable(false);
            winner.setClickable(false);
        });
        loser.setOnClickListener(v -> {
            resultText.setVisibility(View.VISIBLE);
            resultText.setText(loserString);
            playAgainButton.setVisibility(View.VISIBLE);
            loser.setClickable(false);
            winner.setClickable(false);
        });

        return root;
    }
}
