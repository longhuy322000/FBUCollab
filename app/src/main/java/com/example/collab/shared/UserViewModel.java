package com.example.collab.shared;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parse.ParseUser;

public class UserViewModel extends ViewModel {

    public MutableLiveData<ParseUser> currentUser;

    public UserViewModel() {
        currentUser = new MutableLiveData<>();
    }
}
