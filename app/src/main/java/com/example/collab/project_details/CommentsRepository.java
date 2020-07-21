package com.example.collab.project_details;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Comment;
import com.example.collab.models.Project;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class CommentsRepository {

    private static final String TAG = "CommentsRepository";

    public MutableLiveData<List<Comment>> comments;
    private Project project;

    public CommentsRepository(Project project) {
        this.project = project;
        comments = new MutableLiveData<>();
        queryComments();
    }

    public void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo(Comment.KEY_PROJECT, project);
        query.include(Comment.KEY_OWNER);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting comments", e);
                    return;
                }
                comments.setValue(objects);
            }
        });
    }

    public MutableLiveData<List<Comment>> getComments() {
        return comments;
    }
}
