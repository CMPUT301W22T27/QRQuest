package com.example.sidebar_testing.ui.viewQR;

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
import com.example.sidebar_testing.databinding.FragmentQrviewBinding;

public class ViewQRFragment extends Fragment {

    private ViewQRViewModel viewQRViewModel;
    private FragmentQrviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewQRViewModel =
                new ViewModelProvider(this).get(ViewQRViewModel.class);

        binding = FragmentQrviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textViewQR;
        viewQRViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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

