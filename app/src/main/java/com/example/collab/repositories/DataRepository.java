package com.example.collab.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Hashtable;
import java.util.List;

public class DataRepository {

    private static final String TAG = "DataRepository";

    public static DataRepository dataRepository;
    public MutableLiveData<List<Project>> projects;

    public static DataRepository getInstance() {
        if (dataRepository == null) {
            dataRepository = new DataRepository();
        }
        return dataRepository;
    }

    public DataRepository() {
        projects = new MutableLiveData<>();
        queryAllProjects();
    }

    public MutableLiveData<List<Project>> getAllProjects() {
        return projects;
    }

    public void setProjects(List<Project> listProjects) {
        projects.setValue(listProjects);
    }

    public void updateProjectAtPosition(Project project, int position) {
        List<Project> temp = projects.getValue();
        temp.set(position, project);
        projects.setValue(temp);
    }

    public void queryAllProjects() {
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.include(Project.KEY_OWNER);
        query.addDescendingOrder(Project.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(final List<Project> projectsFromDatabase, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with query projects", e);
                    return;
                }
                for (int i=0; i<projectsFromDatabase.size(); i++) {
                    final int finalI = i;

                    // query for likesCount
                    ParseQuery<Like> countQuery = ParseQuery.getQuery(Like.class);
                    countQuery.whereEqualTo(Like.KEY_PROJECT, projectsFromDatabase.get(i));
                    countQuery.countInBackground(new CountCallback() {
                        @Override
                        public void done(int count, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issues with getting likesNum", e);
                                return;
                            }
                            Log.i(TAG, projectsFromDatabase.get(finalI).getObjectId() + " " + count);
                            projectsFromDatabase.get(finalI).setLikesNum(count);
                            projects.setValue(projectsFromDatabase);
                        }
                    });

                    ParseQuery<Like> likedQuery = ParseQuery.getQuery(Like.class);
                    likedQuery.whereEqualTo(Like.KEY_OWNER, ParseUser.getCurrentUser());
                    likedQuery.whereEqualTo(Like.KEY_PROJECT, projectsFromDatabase.get(i));
                    likedQuery.findInBackground(new FindCallback<Like>() {
                        @Override
                        public void done(List<Like> objects, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issues with getting likesNum", e);
                                return;
                            }
                            Log.i(TAG, projectsFromDatabase.get(finalI).getObjectId() + " " + objects.isEmpty());
                            projectsFromDatabase.get(finalI).setLiked(!objects.isEmpty());
                            projectsFromDatabase.get(finalI).setUserLike(!objects.isEmpty() ? objects.get(0) : null);
                            projects.setValue(projectsFromDatabase);
                        }
                    });
                }
            }
        });
    }
}
