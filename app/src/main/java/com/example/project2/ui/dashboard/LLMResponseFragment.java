package com.example.project2.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.project2.R;
import com.example.project2.databinding.FragmentLlmresponseBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        Button returnToWrapped = root.findViewById(R.id.returnToWrappedLLMChooseButton);
        returnToWrapped.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_chooseLLMWrapped));

        Button nextToWrappedSummary = root.findViewById(R.id.wrappedSummaryButton);
        nextToWrappedSummary.setOnClickListener((v)-> Navigation.findNavController(v).navigate(R.id.navigation_wrapped_summary));

        return root;
    }
}
