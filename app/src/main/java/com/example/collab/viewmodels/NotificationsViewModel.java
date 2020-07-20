package com.example.collab.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Notification;
import com.example.collab.repositories.NotificationsRepository;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    public MutableLiveData<List<Notification>> notifications;
    NotificationsRepository notificationsRepository;

    public NotificationsViewModel() {
        notificationsRepository = new NotificationsRepository();
        notifications = notificationsRepository.getNotifications();
    }
}
