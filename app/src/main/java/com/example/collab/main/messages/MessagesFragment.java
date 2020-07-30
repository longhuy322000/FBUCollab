package com.example.collab.main.messages;

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
import com.example.collab.databinding.FragmentMessagesBinding;
import com.example.collab.models.ChatRoom;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private static final String TAG = "MessagesFragment";
    private ChatRoomViewModel chatRoomViewModel;
    private FragmentMessagesBinding binding;
    private ChatRoomsAdapter adpater;
    private List<ChatRoom> chatRooms;

    public MessagesFragment() {
        // Required empty public constructor
    }

    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatRoomViewModel = new ViewModelProvider(getActivity()).get(ChatRoomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentMessagesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatRooms = new ArrayList<>();
        adpater = new ChatRoomsAdapter(getContext(), chatRooms);
        binding.rvChatRooms.setAdapter(adpater);
        binding.rvChatRooms.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.progressBar.setVisibility(View.VISIBLE);
        chatRoomViewModel.queryChatRooms();
        chatRoomViewModel.chatRooms.observe(getViewLifecycleOwner(), new Observer<List<ChatRoom>>() {
            @Override
            public void onChanged(List<ChatRoom> chatRoomsFromModel) {
                binding.progressBar.setVisibility(View.GONE);
                Log.i(TAG, "load chatrooms from model");
                if (chatRoomsFromModel == null || chatRoomsFromModel.isEmpty()) {
                    binding.tvNoMessages.setVisibility(View.VISIBLE);
                    return;
                }

                binding.tvNoMessages.setVisibility(View.GONE);
                chatRooms.clear();
                chatRooms.addAll(chatRoomsFromModel);
            }
        });
    }
}