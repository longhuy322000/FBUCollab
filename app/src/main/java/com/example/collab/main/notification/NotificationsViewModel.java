package com.example.collab.main.notification;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.Notification;

import java.util.List;

public class NotificationsViewModel extends ViewModel {

    private NotificationsRepository notificationsRepository;
    public MutableLiveData<List<Notification>> notifications;
    public MutableLiveData<Integer> unseenCount;

    public NotificationsViewModel() {
        notificationsRepository = new NotificationsRepository();
        notifications = notificationsRepository.getNotifications();
        unseenCount = notificationsRepository.getUnseenNofiticationsCount();
    }

    public void queryNotifications() {
        notificationsRepository.queryAllNotifications();
    }

    public void countUnseenNotifications() {
        notificationsRepository.countUnseenNotifications();
    }

    public void markSeen(int position) {
        List<Notification> temp = notifications.getValue();
        temp.get(position).setSeen();
        notifications.setValue(temp);
        unseenCount.setValue(unseenCount.getValue() - 1);
    }
}
