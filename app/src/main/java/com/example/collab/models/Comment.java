package com.example.collab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public static final String KEY_OWNER = "owner";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_COMMENT = "comment";

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public ParseUser getOwner() {
        return getParseUser(KEY_OWNER);
    }

    public void setComment(String comment) {
        put(KEY_COMMENT, comment);
    }

    public void setOwner(ParseUser user) {
        put(KEY_OWNER, user);
    }

    public void setKeyProject(Project project) {
        put(KEY_PROJECT, project);
    }
}
