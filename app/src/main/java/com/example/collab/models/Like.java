package com.example.collab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {

    public static final String KEY_OWNER = "owner";
    public static final String KEY_PROJECT = "project";

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public Project getProject() {
        return (Project) getParseObject(KEY_PROJECT);
    }

    public void setOwner(ParseUser user) {
        put(KEY_OWNER, user);
    }

    public void setProject(Project project) {
        put(KEY_PROJECT, project);
    }
}
