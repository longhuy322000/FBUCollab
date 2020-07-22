package com.example.collab.main.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Project;
import com.parse.ParseUser;

import java.util.List;

public class ProjectsViewModel extends ViewModel {

    private static final String TAG = "ProjectsViewModel";
    private MutableLiveData<List<Project>> projects;
    private MutableLiveData<Integer> position;

    public ProjectsViewModel() {
        projects = ProjectsRepository.getInstance().getProjects();
    }

    public void getAllProjects() {
        Log.i(TAG, "getAllProjects");
        ProjectsRepository.getInstance().queryAllProjects();
    }

    public void getUserProjects(ParseUser user) {
        Log.i(TAG, "getUserProjects");
        ProjectsRepository.getInstance().queryUserProjects(user);
    }

    public void getPartOfProjects(ParseUser user) {
        Log.i(TAG, "getPartOfProjects");
        ProjectsRepository.getInstance().queryPartofProjects(user);
    }

    public MutableLiveData<List<Project>> getProjects() {
        return projects;
    }
}
