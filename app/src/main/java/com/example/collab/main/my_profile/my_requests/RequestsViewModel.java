package com.example.collab.main.my_profile.my_requests;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Request;

import java.util.List;

public class RequestsViewModel extends ViewModel {

    public MutableLiveData<List<Request>> requests;
    private RequestsRepository requestsRepository;

    public RequestsViewModel() {
        requestsRepository = new RequestsRepository();
        requests = requestsRepository.getRequests();
    }
}
