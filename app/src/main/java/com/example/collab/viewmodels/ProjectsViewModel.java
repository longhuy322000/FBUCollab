package com.example.collab.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Like;
import com.example.collab.repositories.DataRepository;
import com.example.collab.models.Project;
import com.parse.ParseUser;

import java.util.Hashtable;
import java.util.List;

public class ProjectsViewModel extends ViewModel {

    private MutableLiveData<List<Project>> allProjects;
    private MutableLiveData<Integer> position;

    public ProjectsViewModel() {
        allProjects = DataRepository.getInstance().getAllProjects();
    }

    public MutableLiveData<List<Project>> getProjects() {
        return allProjects;
    }
}
