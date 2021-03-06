package com.example.collab.main.notification;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Notification;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class NotificationsRepository {

    private static final String TAG = "NotificationsRepository";

    public MutableLiveData<List<Notification>> notifications;
    public MutableLiveData<Integer> unseenCount;

    public NotificationsRepository() {
        notifications = new MutableLiveData<>();
        unseenCount = new MutableLiveData<>();
        queryAllNotifications();
        countUnseenNotifications();
    }

    public MutableLiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public MutableLiveData<Integer> getUnseenNofiticationsCount() {
        return unseenCount;
    }

    public void queryAllNotifications() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo(Notification.KEY_DELIVER_TO, ParseUser.getCurrentUser());
        query.include(Notification.KEY_REQUEST);
        query.include(Notification.KEY_REQUEST + "." + Request.KEY_PROJECT);
        query.include(Notification.KEY_REQUEST + "." + Request.KEY_PROJECT + "." + Project.KEY_OWNER);
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

    public void countUnseenNotifications() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.whereEqualTo(Notification.KEY_DELIVER_TO, ParseUser.getCurrentUser());
        query.whereEqualTo(Notification.KEY_SEEN, false);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with counting unseen notifications", e);
                    return;
                }
                Log.i(TAG, "unseen notifications " + count);
                unseenCount.setValue(count);
            }
        });
    }
}
