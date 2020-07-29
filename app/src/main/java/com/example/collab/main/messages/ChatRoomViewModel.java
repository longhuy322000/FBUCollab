package com.example.collab.main.messages;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.collab.models.ChatRoom;

import java.util.List;

public class ChatRoomViewModel extends ViewModel {

    private ChatRoomRepository repository;
    public MutableLiveData<List<ChatRoom>> chatRooms;

    public ChatRoomViewModel() {
        repository = new ChatRoomRepository();
        chatRooms = repository.getChatRooms();
    }

    public void queryChatRooms() {
        repository.queryChatRooms();
    }
}
