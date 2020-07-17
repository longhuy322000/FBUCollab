package com.example.collab.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.collab.models.Project;

public class CommentsViewModelFactory implements ViewModelProvider.Factory  {

    private Project project;

    public CommentsViewModelFactory(Project project) {
        this.project = project;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new CommentsViewModel(this.project);
    }

}
