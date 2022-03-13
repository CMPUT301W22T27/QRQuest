package com.example.sidebar_testing.ui.scoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.sidebar_testing.R;
import com.example.sidebar_testing.databinding.FragmentScoreboardBinding;
import com.example.sidebar_testing.ui.scoreboard.ScoreboardViewModel;

public class ScoreboardFragment extends Fragment {

    private ScoreboardViewModel scoreboardViewModel;
    private FragmentScoreboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scoreboardViewModel =
                new ViewModelProvider(this).get(ScoreboardViewModel.class);

        binding = FragmentScoreboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textScoreboard;
        scoreboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
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

