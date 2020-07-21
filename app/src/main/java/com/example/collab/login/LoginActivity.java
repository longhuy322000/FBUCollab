package com.example.collab.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.collab.main.MainActivity;
import com.example.collab.shared.Helper;
import com.example.collab.databinding.ActivityLoginBinding;
import com.example.collab.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 42;
    private static final String TAG = "LoginActivity";

    GoogleSignInAccount account;
    GoogleSignInClient googleSignInClient;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            googleSignInClient.signOut();
        }

        binding.btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        binding.btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.hideKeyboard(LoginActivity.this);
                loginUserNormal(binding.tvUsername.getText().toString(), binding.tvPassword.getText().toString());
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.hideKeyboard(LoginActivity.this);
                String username = binding.tvUsername.getText().toString();
                String password = binding.tvPassword.getText().toString();
                ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.put(User.KEY_FULL_NAME, username);
                signUpUser(user, username, password);
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i(TAG, "Google login account id " + account.getId());
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo(User.KEY_GOOGLE_ID, account.getId());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issues querying user", e);
                        return;
                    }
                    if (users.isEmpty()) {
                        ParseUser user = new ParseUser();
                        user.setUsername(account.getEmail());
                        user.setPassword(account.getId());
                        user.put(User.KEY_GOOGLE_ID, account.getId());
                        user.put(User.KEY_FULL_NAME, account.getDisplayName());
                        signUpUser(user, account.getEmail(), account.getId());
                    }
                    else {
                        loginUserNormal(account.getEmail(), account.getId());
                    }
                }
            });
        } catch (ApiException e) {
            Log.w(TAG, "signInResult: failed code = " + e.getStatusCode());
            Toast.makeText(this, "Unable to sign in with Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void signUpUser(final ParseUser user, final String username, final String password) {
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(LoginActivity.this, "Issue with sign up!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with signup", e);
                    return;
                }
                loginUserNormal(username, password);
            }
        });
    }

    private void loginUserNormal(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                // Navigate to main activity
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT);
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}