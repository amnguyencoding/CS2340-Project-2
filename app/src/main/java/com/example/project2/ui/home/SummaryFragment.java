package com.example.project2.ui.home;

import android.os.Bundle;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.project2.R;
import com.example.project2.SpotifyHandler;
import com.example.project2.databinding.FragmentArtistBinding;
import com.example.project2.databinding.FragmentSummaryBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArtistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {
    private FragmentSummaryBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArtistFragment.
     */
    public static ArtistFragment newInstance(String param1, String param2) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private String mAccessToken;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSummaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
                    SpotifyHandler.populateArtistAndTrackData(mAccessToken);
                    ArrayList<String> topArtists = SpotifyHandler.getTopArtistNameData();
                    ArrayList<String> topSongs = SpotifyHandler.getTopTrackNameData();


                    TextView artist1name = root.findViewById(R.id.summaryWrappedArtist1);
                    artist1name.setText("1. " + topArtists.get(0));


                    TextView artist2name = root.findViewById(R.id.summaryWrappedArtist2);
                    artist2name.setText("2. " +topArtists.get(1));


                    TextView artist3name = root.findViewById(R.id.summaryWrappedArtist3);
                    artist3name.setText("3. " +topArtists.get(2));


                    TextView artist4name = root.findViewById(R.id.summaryWrappedArtist4);
                    artist4name.setText("4. " +topArtists.get(3));


                    TextView artist5name = root.findViewById(R.id.summaryWrappedArtist5);
                    artist5name.setText("5. " +topArtists.get(4));

                    TextView song1name = root.findViewById(R.id.summaryWrappedSong1);
                    song1name.setText("1. " + topSongs.get(0));

                    TextView song2name = root.findViewById(R.id.summaryWrappedSong2);
                    song2name.setText("2. " + topSongs.get(1));


                    TextView song3name = root.findViewById(R.id.summaryWrappedSong3);
                    song3name.setText("3. " + topSongs.get(2));


                    TextView song4name = root.findViewById(R.id.summaryWrappedSong4);
                    song4name.setText("4. " + topSongs.get(3));


                    TextView song5name = root.findViewById(R.id.summaryWrappedSong5);
                    song5name.setText("5. " + topSongs.get(4));

                    TextView genre1 = root.findViewById(R.id.summaryWrappedTopGenre);
                    genre1.setText(SpotifyHandler.getTopGenres().get(0));

                    ImageView song1Image = root.findViewById(R.id.summaryWrappedImage);
                    Glide.with(requireContext()).load(SpotifyHandler.getTopArtistImageData().get(0)).into(song1Image);




                }
            }
        });



        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button home = view.findViewById(R.id.summaryWrappedHomeButton);
        Button restart = view.findViewById(R.id.summaryWrappedRestartButton);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WrappedFragment when the button is clicked
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_intro);
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to WrappedFragment when the button is clicked
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
            }
        });
    }
}