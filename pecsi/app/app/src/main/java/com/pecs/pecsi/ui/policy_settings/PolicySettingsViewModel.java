package com.pecs.pecsi.ui.policy_settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PolicySettingsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PolicySettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is policy_settings fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}