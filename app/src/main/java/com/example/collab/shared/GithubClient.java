package com.example.collab.shared;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GithubClient {

    public static final String GITHUB_BASE_URL = "https://github.com/";
    public static final String API_BASE_URL = "https://api.github.com/";
    public static final String API_GET_USER = "users/%s";
    public static final String API_CREATE_NEW_REPO = "user/repos";
    public static final String API_ADD_COLLABORATOR = "repos/%s/%s/collaborators/%s"; // /repos/:owner/:repo/collaborators/:username

    public static final String KEY_REPO_NAME = "name";
    public static final String KEY_REPO_DESCRIPTION = "description";
    public static final String KEY_REPO_PRIVATE = "";

    public static void getUser(Context context, String username, Response.Listener<JSONObject> onResponse,
                               Response.ErrorListener errorListener) {
        String url = String.format(API_BASE_URL + API_GET_USER, username);;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, onResponse, errorListener);
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void createNewRepo(Context context, final String token, final String body, Response.Listener<JSONObject> onResponse,
                                         Response.ErrorListener errorListener) {
        String url = String.format(API_BASE_URL + API_CREATE_NEW_REPO);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, onResponse, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "token " + token);
                return header;
            }

            @Override
            public byte[] getBody() {
                try {
                    return body == null ? null : body.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", body, "utf-8");
                    return null;
                }
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void checkValidUser(Context context, final String token, Response.Listener<JSONObject> onResponse,
                                      Response.ErrorListener errorListener) {
        String url = API_BASE_URL + "user";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, onResponse, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "token " + token);
                return header;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void addCollaboratorToProject(final Context context, final String token, String owner, String repo, String username,
                                                Response.Listener<JSONObject> onResponse,
                                                Response.ErrorListener errorListener) {
        String url = String.format(API_BASE_URL + API_ADD_COLLABORATOR, owner, repo, username);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, url, null, onResponse, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "token " + token);
                return header;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static String getUserUrl(String username) {
        return GITHUB_BASE_URL + username;
    }
}
