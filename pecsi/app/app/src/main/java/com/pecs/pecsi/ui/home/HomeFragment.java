package com.pecs.pecsi.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pecs.pecsi.R;
import com.pecs.pecsi.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private TextView messageTextView;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        messageTextView = rootView.findViewById(R.id.text_home);

        // Retrieve selectedPreset from SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE);
        String selectedPreset = sharedPref.getString("selectedPreset", "No Preset");

        // Update the message based on selectedPreset value
        if (!"No Preset".equals(selectedPreset)) {
            // TODO: Show radar chart to inform user.
            messageTextView.setText("Policy settings enforced adopting preset " + selectedPreset);
        } else {
            messageTextView.setText("No preset selected");
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}