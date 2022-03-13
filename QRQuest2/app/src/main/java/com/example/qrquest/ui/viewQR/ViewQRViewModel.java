package com.example.qrquest.ui.viewQR;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewQRViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ViewQRViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the QR viewing page");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
