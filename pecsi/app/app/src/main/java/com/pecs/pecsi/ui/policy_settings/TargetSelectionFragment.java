package com.pecs.pecsi.ui.policy_settings;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pecs.pecsi.R;

public class TargetSelectionFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_target_selection, container, false);

        Button btnGlobal = rootView.findViewById(R.id.btn_global);
        Button btnTarget = rootView.findViewById(R.id.btn_target);

        btnGlobal.setOnClickListener(v -> navigateToPresetSelection(v, "global"));
        btnTarget.setOnClickListener(v -> navigateToApplicationList(v, "target"));

        return rootView;
    }

    private void navigateToPresetSelection(View view, String selectionType) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectionType", selectionType);
        editor.putString("selectedApplications", null);
        editor.apply();
        Log.d(getTag(), "Toggle selectionType changed: " + selectionType);

        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_nav_target_selection_to_preset_selection);
    }

    private void navigateToApplicationList(View view, String selectionType) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("selectionType", selectionType);
        editor.putString("selectedApplications", null);
        editor.apply();
        Log.d(getTag(), "Toggle selectionType changed: " + selectionType);

        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_nav_target_selection_to_application_list);
    }
}
