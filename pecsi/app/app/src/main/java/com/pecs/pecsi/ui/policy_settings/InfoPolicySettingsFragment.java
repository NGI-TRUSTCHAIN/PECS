package com.pecs.pecsi.ui.policy_settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.pecs.pecsi.R;

public class InfoPolicySettingsFragment extends Fragment {

    public InfoPolicySettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info_policy_settings, container, false);

        Spinner spinnerAlertType = view.findViewById(R.id.spinner_alert_type);
        Button submitButton = view.findViewById(R.id.info_btn_submit);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.alert_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlertType.setAdapter(adapter);

        submitButton.setOnClickListener(v -> {
            String selectedAlertType = spinnerAlertType.getSelectedItem().toString();
            int alertType;
            switch (selectedAlertType){
                case "Video":
                    alertType = 0;
                    break;
                case "Haptic":
                    alertType = 2;
                    break;
                default:    // Audio is 1
                    alertType = 1;
            }
            SharedPreferences sharedPref = getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("alertType", alertType);
            editor.apply();

            Toast.makeText(getContext(), "Selected Alert Type: " + selectedAlertType, Toast.LENGTH_SHORT).show();

            // Navigate to PolicySettingsFragment
            NavHostFragment.findNavController(InfoPolicySettingsFragment.this)
                    .navigate(R.id.action_nav_info_to_preset_selection);
        });

        return view;
    }

}
