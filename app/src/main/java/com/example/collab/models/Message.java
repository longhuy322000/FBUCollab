package com.example.collab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_MESSAGE = "message";

    // getters start here

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public Project getProject() { return (Project) getParseObject(KEY_PROJECT); }

    public String getMessage() { return getString(KEY_MESSAGE); }

    // setters start here

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public void setProject(Project project) { put(KEY_PROJECT, project); }

    public void setMessage(String message) { put(KEY_MESSAGE, message); }
}
