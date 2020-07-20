package com.example.collab.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String KEY_DELIVER_TO = "deliverTo";
    public static final String KEY_REQUEST = "request";
    public static final String KEY_TYPE = "type";

    public static final int KEY_NEED_OWNER_CONFIRM = 0;
    public static final int KEY_APPLICANT_RECEIVE_RESULT = 1;

    public ParseUser getDeliverTo() {
        return getParseUser(KEY_DELIVER_TO);
    }

    public Request getRequest() {
        return (Request) getParseObject(KEY_REQUEST);
    }

    public int getType() {
        return getInt(KEY_TYPE);
    }

    public void setDeliverTo(ParseUser user) {
        put(KEY_DELIVER_TO, user);
    }

    public void setRequest(Request request) {
        put(KEY_REQUEST, request);
    }

    public void setType(int type) {
        put(KEY_TYPE, type);
    }
}
