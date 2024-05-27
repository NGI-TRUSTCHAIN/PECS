package com.pecs.pecsi.ui.alert_history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlertHistoryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AlertHistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is alert_history fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}