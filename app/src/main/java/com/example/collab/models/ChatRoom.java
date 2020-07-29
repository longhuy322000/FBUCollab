package com.example.collab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ChatRoom")
public class ChatRoom extends ParseObject {

    public static final String KEY_PROJECT = "project";
    public static final String KEY_USER = "user";

    private Message lastMessage;

    // getters start here

    public Project getProject() { return (Project) getParseObject(KEY_PROJECT);}

    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public Message getLastMessage() {
        return lastMessage;
    }

    // setters start here

    public void setProject(Project project) { put(KEY_PROJECT, project); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
