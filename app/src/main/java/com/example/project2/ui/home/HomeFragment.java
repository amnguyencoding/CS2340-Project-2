package com.example.project2.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project2.GPTHandler;
import com.example.project2.R;
import com.example.project2.databinding.FragmentHomeBinding;

import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private String gptResponseText = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView gptResponse = view.findViewById(R.id.gpt_response);

        if (gptResponseText.isEmpty()) {
            GPTHandler handler = new GPTHandler();
            try {
                gptResponseText = handler.makeRequest().get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            Log.i("GPTHandler", gptResponseText);

            gptResponse.setText(gptResponseText);
        } else {
            gptResponse.setText(gptResponseText);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
