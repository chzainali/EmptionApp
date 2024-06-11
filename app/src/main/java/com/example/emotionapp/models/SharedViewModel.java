package com.example.emotionapp.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<Long> valueLiveData = new MutableLiveData<>();

    public void setValue(Long value) {
        valueLiveData.setValue(value);
    }

    public LiveData<Long> getValue() {
        return valueLiveData;
    }
}