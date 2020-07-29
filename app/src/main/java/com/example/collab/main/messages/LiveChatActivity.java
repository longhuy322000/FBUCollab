package com.example.collab.main.messages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.example.collab.R;
import com.example.collab.databinding.ActivityLiveChatBinding;
import com.example.collab.models.ChatRoom;
import com.example.collab.models.Message;
import com.example.collab.models.Project;
import com.example.collab.shared.Helper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.ArrayList;
import java.util.List;

public class LiveChatActivity extends AppCompatActivity {

    private static final String TAG = "LiveChatActivity";
    private ActivityLiveChatBinding binding;
    private Project project;
    private List<Message> messages;
    private MessagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        project = getIntent().getParcelableExtra(Project.class.getName());
        binding.tvMenuAppName.setText(project.getProjectName());
        messages = new ArrayList<>();
        adapter = new MessagesAdapter(LiveChatActivity.this, messages);
        binding.rvMessages.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        binding.rvMessages.setLayoutManager(linearLayoutManager);
        queryMessages();
        subscribeToLiveMessages();

        binding.btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.setMessage(binding.etMessage.getText().toString());
                message.setProject(project);
                message.setUser(ParseUser.getCurrentUser());
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with saving message", e);
                            return;
                        }
                    }
                });
                Helper.hideKeyboard(LiveChatActivity.this);
                binding.etMessage.setText("");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        return super.onSupportNavigateUp();
    }

    private void queryMessages() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.addDescendingOrder(Message.KEY_CREATED_AT);
        query.include(Message.KEY_USER);
        query.whereEqualTo(Message.KEY_PROJECT, project);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with querying message", e);
                    return;
                }
                messages.addAll(objects);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void subscribeToLiveMessages() {
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.include(Message.KEY_USER);
        query.addDescendingOrder(Message.KEY_CREATED_AT);
        query.whereEqualTo(Message.KEY_PROJECT, project);

        parseLiveQueryClient.subscribe(query).handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message object) {
                        Log.i(TAG, "new message " + object.getMessage());
                        messages.add(0, object);

                        // RecyclerView updates need to be run on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                binding.rvMessages.scrollToPosition(0);
                            }
                        });
                    }
                });
    }
}