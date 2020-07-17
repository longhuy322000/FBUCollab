package com.example.collab.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Comment;
import com.example.collab.models.Project;
import com.example.collab.repositories.CommentsRepository;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    private CommentsRepository commentsRepository;
    private Project project;
    public MutableLiveData<List<Comment>> comments;

    public CommentsViewModel(Project project) {
        this.project = project;
        commentsRepository = new CommentsRepository(this.project);
        comments = commentsRepository.getComments();
    }
}
