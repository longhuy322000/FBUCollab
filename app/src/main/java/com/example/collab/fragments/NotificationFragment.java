package com.example.collab.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collab.R;
import com.example.collab.adapters.NotificationsAdapter;
import com.example.collab.databinding.FragmentNotificationBinding;
import com.example.collab.models.Notification;
import com.example.collab.viewmodels.NotificationsViewModel;
import com.example.collab.viewmodels.ProjectsViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private static final String TAG = "NotificationFragment";

    NotificationsViewModel notificationsViewModel;
    FragmentNotificationBinding binding;
    NotificationsAdapter adapter;
    List<Notification> notifications;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationsViewModel = new ViewModelProvider(getActivity()).get(NotificationsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notifications = new ArrayList<>();
        adapter = new NotificationsAdapter(getContext(), notifications);
        binding.rvNotifications.setAdapter(adapter);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationsViewModel.notifications.observe(getViewLifecycleOwner(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notificationsFromDatabase) {
                Log.i(TAG, "loading notifications from model");
                if (!notificationsFromDatabase.isEmpty()) {
                    notifications.addAll(notificationsFromDatabase);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}