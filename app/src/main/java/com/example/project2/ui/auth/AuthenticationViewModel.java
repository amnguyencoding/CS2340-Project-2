package com.example.project2.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthenticationViewModel extends ViewModel {

        private final MutableLiveData<String> mText;

        public AuthenticationViewModel() {
            mText = new MutableLiveData<>();
//            mText.setValue("This is authentication fragment");
        }

        public LiveData<String> getText() {
            return mText;
        }
}
