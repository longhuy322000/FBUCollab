package com.example.collab.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Notification;
import com.example.collab.models.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class NotificationsRepository {

    private static final String TAG = "NotificationsRepository";
    public MutableLiveData<List<Notification>> notifications;

    public NotificationsRepository() {
        notifications = new MutableLiveData<>();
        queryAllNotifications();
    }

    public MutableLiveData<List<Notification>> getNotifications() {

        return notifications;
    }

    public void queryAllNotifications() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo(Notification.KEY_DELIVER_TO, ParseUser.getCurrentUser());
        query.include(Notification.KEY_REQUEST);
        query.include(Notification.KEY_REQUEST + "." + Request.KEY_PROJECT);
        query.include(Notification.KEY_REQUEST + "." + Request.KEY_REQUESTED_USER);
        query.addDescendingOrder(Notification.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting notifications", e);
                    return;
                }
                notifications.setValue(objects);
            }
        });
    }
}
