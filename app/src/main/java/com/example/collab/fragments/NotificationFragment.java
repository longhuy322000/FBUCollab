package com.example.collab.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.collab.R;
import com.example.collab.adapters.NotificationsAdapter;
import com.example.collab.adapters.NotificationsAdapter.NotificationsAdapterListener;
import com.example.collab.databinding.FragmentNotificationBinding;
import com.example.collab.dialog_fragments.ProcessRequestDialogFragment;
import com.example.collab.models.Notification;
import com.example.collab.models.Request;
import com.example.collab.viewmodels.NotificationsViewModel;
import com.example.collab.viewmodels.ProjectsViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment implements ProcessRequestDialogFragment.ProcessRequestDialogListener {

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

        NotificationsAdapterListener notificationsAdapterListener = new NotificationsAdapterListener() {
            @Override
            public void displayProcessRequestDialog(Request request) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ProcessRequestDialogFragment dialog = ProcessRequestDialogFragment.newInstance(request);
                dialog.setTargetFragment(NotificationFragment.this, 300);
                dialog.show(fm, "fragment_process_request_dialog");
            }
        };

        notifications = new ArrayList<>();
        adapter = new NotificationsAdapter(getContext(), notifications, notificationsAdapterListener, notificationsViewModel);
        binding.rvNotifications.setAdapter(adapter);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationsViewModel.queryNotifications();
        notificationsViewModel.notifications.observe(getViewLifecycleOwner(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notificationsFromDatabase) {
                Log.i(TAG, "loading notifications from model");
                if (!notificationsFromDatabase.isEmpty()) {
                    binding.tvNoNotifications.setVisibility(View.GONE);
                    notifications.clear();
                    notifications.addAll(notificationsFromDatabase);
                    adapter.notifyDataSetChanged();
                }
                else {
                    binding.tvNoNotifications.setVisibility(View.VISIBLE);
                    notifications.clear();
                }
            }
        });
    }

    @Override
    public void onFinishDialog(String text, Boolean success) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}