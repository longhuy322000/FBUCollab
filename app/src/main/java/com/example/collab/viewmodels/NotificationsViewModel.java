package com.example.collab.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Notification;
import com.example.collab.repositories.NotificationsRepository;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    public MutableLiveData<List<Notification>> notifications;
    public MutableLiveData<Integer> unseenCount;

    public NotificationsViewModel() {
        notifications = NotificationsRepository.getInstance().getNotifications();
        unseenCount = NotificationsRepository.getInstance().getUnseenNofiticationsCount();
    }

    public void queryNotifications() {
        NotificationsRepository.getInstance().queryAllNotifications();
    }

    public void markSeen(int position) {
        List<Notification> temp = notifications.getValue();
        temp.get(position).setSeen();
        notifications.setValue(temp);
    }
}
