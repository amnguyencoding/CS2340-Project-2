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
import com.example.project2.TimeRange;
import com.example.project2.databinding.FragmentSongsBinding;

public class WrappedFragment extends Fragment {
    private static TimeRange selectedTimeRange;
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
        wrapped4Weeks.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedTimeRange = TimeRange.SHORT_TERM;
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
            }
        });
        wrapped6Months.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTimeRange = TimeRange.MEDIUM_TERM;
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
            }
        });
        wrappedLastYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTimeRange = TimeRange.LONG_TERM;
                Navigation.findNavController(v).navigate(R.id.navigation_wrapped_artists);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectedTimeRange = TimeRange.SHORT_TERM;
                Navigation.findNavController(v).navigate(R.id.navigation_home);
            }
        });
    }

    public static TimeRange getSelectedTimeRange() {
        return selectedTimeRange;
    }
}



