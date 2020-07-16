package com.example.collab;

import android.app.Application;

import com.example.collab.models.Comment;
import com.example.collab.models.Like;
import com.example.collab.models.Notification;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Project.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Request.class);
        ParseObject.registerSubclass(Notification.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("longhuyn-collab") // should correspond to APP_ID env variable
                .clientKey("CodepathFBU2020")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://longhuyn-collab.herokuapp.com/parse/").build());
    }
}
