package com.example.collab.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.repositories.HomeProjectsRepository;
import com.example.collab.models.Project;

import java.util.List;

public class ProjectsViewModel extends ViewModel {

    private MutableLiveData<List<Project>> allProjects;
    private MutableLiveData<Integer> position;

    public ProjectsViewModel() {
        allProjects = HomeProjectsRepository.getInstance().getAllProjects();
    }

    public MutableLiveData<List<Project>> getProjects() {
        return allProjects;
    }
}
