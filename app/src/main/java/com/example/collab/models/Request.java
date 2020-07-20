package com.example.collab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Request")
public class Request extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_REQUESTED_USER = "requestedUser";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_STATUS = "status";

    public static final int KEY_APPROVED_STATUS = 1;
    public static final int KEY_DECLINED_STATUS = -1;
    public static final int KEY_PENDING_STATUS = 0;

    // getters start here

    public String getDescription() { return getString(KEY_DESCRIPTION); }

    public ParseUser getRequestedUser() {
        return getParseUser(KEY_REQUESTED_USER);
    }

    public Project getProject() {
        return (Project) getParseObject(KEY_PROJECT);
    }

    public int getStatus() {
        return getInt(KEY_STATUS);
    }

    // setters start here

    public void setDescription(String description) { put(KEY_DESCRIPTION, description); }

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
