package com.example.collab.main.messages;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.collab.models.ChatRoom;
import com.example.collab.models.Message;
import com.example.collab.models.Project;
import com.example.collab.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatRoomRepository {

    private static final String TAG = "ChatRoomRepository";
    private MutableLiveData<List<ChatRoom>> chatRooms;

    public ChatRoomRepository() {
        chatRooms = new MutableLiveData<>();
        queryChatRooms();
    }

    public MutableLiveData<List<ChatRoom>> getChatRooms() {
        return chatRooms;
    }

    public void queryChatRooms() {
        ParseQuery<ChatRoom> query = ParseQuery.getQuery(ChatRoom.class);
        query.include(ChatRoom.KEY_PROJECT);
        query.addDescendingOrder(ChatRoom.KEY_CREATED_AT);
        query.whereEqualTo(ChatRoom.KEY_USER, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ChatRoom>() {
            @Override
            public void done(final List<ChatRoom> chatRoomsFromDB, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting chatrooms", e);
                    return;
                }

                if (chatRoomsFromDB == null || chatRoomsFromDB.isEmpty()) {
                    chatRooms.setValue(chatRoomsFromDB);
                    return;
                }

                // get last message for each room
                for (int i=0; i<chatRoomsFromDB.size(); i++) {
                    ParseQuery<Message> messageQuery = ParseQuery.getQuery(Message.class);
                    messageQuery.whereEqualTo(Message.KEY_PROJECT, chatRoomsFromDB.get(i).getProject());
                    messageQuery.addDescendingOrder(Message.KEY_CREATED_AT);
                    messageQuery.include(Message.KEY_USER);
                    messageQuery.setLimit(1);
                    final int finalI = i;
                    messageQuery.findInBackground(new FindCallback<Message>() {
                        @Override
                        public void done(List<Message> messages, ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issues with getting last message", e);
                                return;
                            }
                            if (messages.isEmpty())
                                chatRoomsFromDB.get(finalI).setLastMessage(null);
                            else chatRoomsFromDB.get(finalI).setLastMessage(messages.get(0));

                            // sort data and set value
                            if (finalI == chatRoomsFromDB.size() - 1) {
                                Collections.sort(chatRoomsFromDB, new ChatRoomComparator());
                                chatRooms.setValue(chatRoomsFromDB);
                            }
                        }
                    });
                }
            }
        });
    }

    public class ChatRoomComparator implements Comparator<ChatRoom> {
        @Override
        public int compare(ChatRoom room1, ChatRoom room2) {
            return room2.getCreatedAt().compareTo(room1.getCreatedAt());
        }
    }
}
