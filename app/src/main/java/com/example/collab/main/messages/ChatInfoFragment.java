package com.example.collab.main.messages;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.models.ChatRoom;
import com.example.collab.models.Project;
import com.example.collab.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import com.example.collab.databinding.FragmentChatInfoBinding;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChatInfoFragment extends DialogFragment {

    private static final String TAG = "ChatInfoFragment";

    private FragmentChatInfoBinding binding;
    private Project project;
    private List<ChatRoom> collaborators;
    private ChatInfoAdapter adapter;

    public ChatInfoFragment() {
        // Required empty public constructor
    }

    public static ChatInfoFragment newInstance(Project project) {
        ChatInfoFragment fragment = new ChatInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(Project.class.getName(), project);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            project = getArguments().getParcelable(Project.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatInfoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        collaborators = new ArrayList<>();
        adapter = new ChatInfoAdapter(getContext(), collaborators);
        binding.rvCollaborators.setAdapter(adapter);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvCollaborators.setLayoutManager(gridLayoutManager);
        getCollaborators();
    }

    private void getCollaborators() {
        ParseQuery<ChatRoom> query = ParseQuery.getQuery(ChatRoom.class);
        query.include(ChatRoom.KEY_USER);
        query.whereEqualTo(ChatRoom.KEY_PROJECT, project);
        query.addAscendingOrder(ChatRoom.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ChatRoom>() {
            @Override
            public void done(List<ChatRoom> collaboratorsFromDB, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting collaborators", e);
                    return;
                }
                collaborators.clear();

                // set owner's info
                ParseUser owner = collaboratorsFromDB.get(0).getUser();
                Glide.with(getContext())
                        .load(owner.getParseFile(User.KEY_IMAGE).getUrl())
                        .into(binding.ivOwnerImage);
                binding.tvOwner.setText(owner.getString(User.KEY_FULL_NAME));

                collaboratorsFromDB.remove(0); // remove owner from the list
                collaborators.addAll(collaboratorsFromDB);
                adapter.notifyDataSetChanged();
             }
        });
    }
}