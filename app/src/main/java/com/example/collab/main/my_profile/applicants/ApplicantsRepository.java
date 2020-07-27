package com.example.collab.main.my_profile.applicants;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Notification;
import com.example.collab.models.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ApplicantsRepository {

    private static final String TAG = "ApplicantsRepository";

    private MutableLiveData<List<Notification>> applicants;

    public ApplicantsRepository() {
        applicants = new MutableLiveData<>();
        queryAllApplicants();
    }

    public MutableLiveData<List<Notification>> getApplicants() {
        return applicants;
    }

    private void queryAllApplicants() {
        ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
        query.include(Notification.KEY_REQUEST);
        query.include(Notification.KEY_REQUEST + "." + Request.KEY_REQUESTED_USER);
        query.include(Notification.KEY_REQUEST + "." + Request.KEY_PROJECT);
        query.whereEqualTo(Notification.KEY_TYPE, Notification.KEY_NEED_OWNER_CONFIRM);
        query.whereEqualTo(Notification.KEY_DELIVER_TO, ParseUser.getCurrentUser());
        query.addDescendingOrder(Notification.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting applicants");
                    return;
                }
                applicants.setValue(objects);
            }
        });
    }
}
