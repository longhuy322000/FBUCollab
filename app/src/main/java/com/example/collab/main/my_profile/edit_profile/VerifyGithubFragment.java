package com.example.collab.main.my_profile.edit_profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collab.R;
import com.example.collab.databinding.FragmentVerifyGithubBinding;
import com.example.collab.models.User;
import com.example.collab.project_details.ApplyDialogFragment;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyGithubFragment extends DialogFragment {

    public interface VerifyGithubListener {
        void onFinishVerifyDialog(String username);
    }

    public static final String GITHUB_JSON = "githubJson";
    private static final String TAG = "VerifyGithubFragment";
    private static final String KEY_USERNAME = "login";
    private static final String KEY_DISPLAY_NAME = "name";
    private static final String KEY_URL = "html_url";

    FragmentVerifyGithubBinding binding;
    JSONObject jsonObject;
    String token;

    public VerifyGithubFragment() {
        // Required empty public constructor
    }
    public static VerifyGithubFragment newInstance(String token, String jsonString) {
        VerifyGithubFragment fragment = new VerifyGithubFragment();
        Bundle args = new Bundle();
        args.putString(User.KEY_GITHUB_TOKEN, token);
        args.putString(GITHUB_JSON, jsonString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                this.token = getArguments().getString(User.KEY_GITHUB_TOKEN);
                this.jsonObject = new JSONObject(getArguments().getString(GITHUB_JSON));
            } catch (JSONException e) {
                Log.e(TAG, "Issues with getting json object", e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerifyGithubBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            binding.tvUsername.setText(jsonObject.getString(KEY_USERNAME));
            binding.tvDisplayName.setText(jsonObject.getString(KEY_DISPLAY_NAME));
            binding.tvUrl.setText(jsonObject.getString(KEY_URL));
        } catch (JSONException e) {
            Log.e(TAG, "Hit json exception", e);
        }

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser user = ParseUser.getCurrentUser();
                try {
                    final String username = jsonObject.getString(KEY_USERNAME);
                    user.put(User.KEY_GITHUB_USERNAME, username);
                    user.put(User.KEY_GITHUB_TOKEN, token);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issues with Github account to user", e);
                                return;
                            }
                            final VerifyGithubListener listener = (VerifyGithubListener) getActivity();
                            listener.onFinishVerifyDialog(username);
                            dismiss();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}