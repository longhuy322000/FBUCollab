package com.example.collab.main;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProjectsRepository {

    private static final String TAG = "ProjectsRepository";

    public static ProjectsRepository projectsRepository;
    public MutableLiveData<List<Project>> projects;

    public static ProjectsRepository getInstance() {
        if (projectsRepository == null) {
            projectsRepository = new ProjectsRepository();
        }
        return projectsRepository;
    }

    public ProjectsRepository() {
        projects = new MutableLiveData<>();
    }

    public MutableLiveData<List<Project>> getProjects() {
        return projects;
    }

    public void addProject(Project project, int position) {
        List<Project> temp = projects.getValue();
        temp.add(0, project);
        projects.setValue(temp);
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
                queryLikesForProjects(projectsFromDatabase);
            }
        });
    }

    public void queryUserProjects(ParseUser user) {
        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
        query.include(Project.KEY_OWNER);
        query.addDescendingOrder(Project.KEY_CREATED_AT);
        query.whereEqualTo(Project.KEY_OWNER, user);
        query.findInBackground(new FindCallback<Project>() {
            @Override
            public void done(final List<Project> projectsFromDatabase, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with query projects", e);
                    return;
                }
                queryLikesForProjects(projectsFromDatabase);
            }
        });
    }

    public void queryPartofProjects(ParseUser user) {
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.whereEqualTo(Request.KEY_REQUESTED_USER, user);
        query.whereEqualTo(Request.KEY_STATUS, Request.KEY_APPROVED_STATUS);
        query.addDescendingOrder(Request.KEY_CREATED_AT);
        query.include(Request.KEY_PROJECT);
        query.include(Request.KEY_PROJECT + "." + Project.KEY_OWNER);
        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> requests, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting part of projects", e);
                    return;
                }
                List<Project> temp = new ArrayList<>();
                for (Request request: requests) {
                    temp.add(request.getProject());
                }
                queryLikesForProjects(temp);
            }
        });
    }

    private void queryLikesForProjects(final List<Project> projectsFromDatabase) {
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
}
