package com.example.collab.shared;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GithubClient {

    public static final String GITHUB_BASE_URL = "https://github.com/";
    public static final String API_BASE_URL = "https://api.github.com/";
    public static final String API_GET_USER = "users/%s";
    public static final String API_CREATE_NEW_REPO = "user/repos";

    public static final String KEY_REPO_NAME = "name";
    public static final String KEY_REPO_DESCRIPTION = "description";
    public static final String KEY_REPO_PRIVATE = "";

    public static void getUser(String username, JsonHttpResponseHandler handler) {
        String apiUrl = String.format(API_BASE_URL + API_GET_USER, username);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(apiUrl, handler);
    }

    public static void createNewRepo(String token, JSONObject body, JsonHttpResponseHandler handler) {
        String apiUrl = String.format(API_BASE_URL + API_CREATE_NEW_REPO);
        AsyncHttpClient client = new AsyncHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, body.toString());
        RequestHeaders requestHeaders = new RequestHeaders();
        requestHeaders.put("Authorization", "token " + token);
        client.post(apiUrl, requestHeaders, new RequestParams(), requestBody, handler);
    }

    public static void checkValidUser(String token, JsonHttpResponseHandler handler) {
        String apiUrl = API_BASE_URL + "user";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestHeaders requestHeaders = new RequestHeaders();
        requestHeaders.put("Authorization", "token " + token);
        client.get(apiUrl, requestHeaders, new RequestParams(), handler);
    }
}
