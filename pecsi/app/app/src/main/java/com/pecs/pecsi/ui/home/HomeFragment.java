package com.pecs.pecsi.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pecs.pecsi.R;
import com.pecs.pecsi.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private TextView messageTextView;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView messageTextView = rootView.findViewById(R.id.text_home);
        ImageView settingsImageView = rootView.findViewById(R.id.settings_image);

        // Retrieve selectedPreset from SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences("PolicySettingsPrefs", Context.MODE_PRIVATE);
        String selectedPreset = sharedPref.getString("selectedPreset", "No Preset");
        int alertType = sharedPref.getInt("alertType", 1);

        // Update the message based on selectedPreset value
        if (!"No Preset".equals(selectedPreset)) {
            // Load image from the specified paths
            Bitmap settingsBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/Download/" + selectedPreset + ".png");
            // Set the image to the ImageView
            settingsImageView.setImageBitmap(settingsBitmap);

            messageTextView.setText("The current situation for your privacy policy settings is summarised in this page.\n\n" +
                    "Preferred alert type: " + alertType + "\n" +
                    "Policy settings enforced adopting preset: " + selectedPreset);
        } else {
            messageTextView.setText("The current situation for your privacy policy settings is summarised in this page.\n\n" +
                    "Preferred alert type: " + alertType + "\n" +
                    "No preset selected.");
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
