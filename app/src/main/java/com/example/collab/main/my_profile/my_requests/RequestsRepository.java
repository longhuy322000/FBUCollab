package com.example.collab.main.my_profile.my_requests;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class RequestsRepository {

    private static final String TAG = "RequestsRepository";

    private MutableLiveData<List<Request>> requests;

    public RequestsRepository() {
        requests = new MutableLiveData<>();
        queryMyRequests();
    }

    public MutableLiveData<List<Request>> getRequests() {
        return requests;
    }

    private void queryMyRequests() {
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.whereEqualTo(Request.KEY_REQUESTED_USER, ParseUser.getCurrentUser());
        query.addDescendingOrder(Request.KEY_CREATED_AT);
        query.include(Request.KEY_PROJECT);
        query.include(Request.KEY_PROJECT + "." + Project.KEY_OWNER);
        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting user requests", e);
                    return;
                }
                requests.setValue(objects);
            }
        });
    }

}
