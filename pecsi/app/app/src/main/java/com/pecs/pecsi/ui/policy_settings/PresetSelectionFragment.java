package com.pecs.pecsi.ui.policy_settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.pecs.pecsi.R;

public class PresetSelectionFragment extends Fragment {

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_preset_selection, container, false);
        radioGroup = rootView.findViewById(R.id.radioGroup);

        rootView.findViewById(R.id.btn_next).setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = rootView.findViewById(selectedId);
            if (radioButton != null) {
                String selectedPreset = radioButton.getText().toString();
                // Save the selected preset to SharedPreferences
                getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE).edit()
                        .putString("selectedPreset", selectedPreset).apply();
                Log.d(getTag(), "Toggle selectedPreset changed: " + selectedPreset);

                // Navigate to PolicySettingsFragment
                NavHostFragment.findNavController(PresetSelectionFragment.this)
                        .navigate(R.id.action_preset_selection_to_policy_settings);
            }
        });

        return rootView;
    }
}