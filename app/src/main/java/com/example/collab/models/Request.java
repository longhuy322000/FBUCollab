package com.example.collab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Request")
public class Request extends ParseObject {

    public static final String KEY_REQUESTED_USER = "requestedUser";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_STATUS = "status";

    public static final int KEY_ACCEPT_STATUS = 1;
    public static final int KEY_DECLINE_STATUS = -1;
    public static final int KEY_PENDING_STATUS = 0;

    public ParseUser getRequestedUser() {
        return getParseUser(KEY_REQUESTED_USER);
    }

    public Project getProject() {
        return (Project) getParseObject(KEY_PROJECT);
    }

    public int getStatus() {
        return getInt(KEY_STATUS);
    }

    public void setRequestedUser(ParseUser user) {
        put(KEY_REQUESTED_USER, user);
    }

    public void setProject(Project project) {
        put(KEY_PROJECT, project);
    }

    public void setStatus(int status) {
        put(KEY_STATUS, status);
    }

}
