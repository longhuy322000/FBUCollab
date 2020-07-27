package com.example.collab.main.my_profile.applicants;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Notification;

import java.util.List;

public class ApplicantsViewModel extends ViewModel {

    private ApplicantsRepository applicantsRepository;
    public MutableLiveData<List<Notification>> applicants;

    public ApplicantsViewModel() {
        applicantsRepository = new ApplicantsRepository();
        applicants = applicantsRepository.getApplicants();
    }
}
