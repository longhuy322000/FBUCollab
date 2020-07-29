package com.example.collab.main.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProjectsRepository {

    private static final String TAG = "ProjectsRepository";

    private static ParseUser user;
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
                if (projectsFromDatabase.isEmpty()) {
                    projects.setValue(projectsFromDatabase);
                }
                else getCurrentUser(projectsFromDatabase);
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
                if (projectsFromDatabase.isEmpty()) {
                    projects.setValue(projectsFromDatabase);
                }
                else queryLikesForProjects(projectsFromDatabase);
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
                if (temp.isEmpty()) {
                    projects.setValue(temp);
                }
                else queryLikesForProjects(temp);
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
                    projectsFromDatabase.get(finalI).setLiked(!objects.isEmpty());
                    projectsFromDatabase.get(finalI).setUserLike(!objects.isEmpty() ? objects.get(0) : null);
                    projects.setValue(projectsFromDatabase);
                }
            });
        }
    }

    private void getCurrentUser(final List<Project> projects) {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting current user");
                    return;
                }
                user = object;
                Collections.sort(projects, new HomeProjectsComparator());
                queryLikesForProjects(projects);
            }
        });
    }

    public class HomeProjectsComparator implements Comparator<Project> {
        @Override
        public int compare(Project p1, Project p2) {
            int num1 = 0, num2 = 0;
            if (user.getList(User.KEY_SKILLS) == null)
                return 0;
            for (int i=0; i<p1.getSkillsList().size(); i++) {
                for (int j=0; j<user.getList(User.KEY_SKILLS).size(); j++) {
                    if (p1.getSkillsList().get(i).toLowerCase().equals(user.<String>getList(User.KEY_SKILLS).get(j).toLowerCase()))
                        num1++;
                }
            }
            for (int i=0; i<p2.getSkillsList().size(); i++) {
                for (int j=0; j<user.getList(User.KEY_SKILLS).size(); j++) {
                    if (p2.getSkillsList().get(i).toLowerCase().equals(user.<String>getList(User.KEY_SKILLS).get(j).toLowerCase()))
                        num2++;
                }
            }
            if (p1.getCapacity() == p1.getSpots() || p1.getOwner().getUsername().equals(user.getUsername()))
                return 1;
            else if (p2.getCapacity() == p2.getSpots() || p2.getOwner().getUsername().equals(user.getUsername()))
                return -1;
            return Integer.valueOf(num2).compareTo(Integer.valueOf(num1));
        }
    }
}
