package com.example.collab.main.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.databinding.ItemChatroomBinding;
import com.example.collab.models.ChatRoom;
import com.example.collab.models.Message;
import com.example.collab.models.Project;
import com.example.collab.models.User;
import com.example.collab.shared.Helper;

import java.util.List;

public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> {

    Context context;
    List<ChatRoom> chatRooms;

    public ChatRoomsAdapter(Context context, List<ChatRoom> chatRooms) {
        this.context = context;
        this.chatRooms = chatRooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatroomBinding binding = ItemChatroomBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(chatRooms.get(position));
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ChatRoom chatRoom;
        private ItemChatroomBinding binding;

        public ViewHolder(ItemChatroomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this);
        }

        public void bind(ChatRoom chatRoom) {
            this.chatRoom = chatRoom;
            Message lastMessage = chatRoom.getLastMessage();
            binding.tvProjectName.setText(chatRoom.getProject().getProjectName());
            if (lastMessage == null) {
                binding.tvNoMessage.setVisibility(View.VISIBLE);
                binding.rlLastComment.setVisibility(View.GONE);
                return;
            }

            binding.tvNoMessage.setVisibility(View.GONE);
            binding.rlLastComment.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(lastMessage.getUser().getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivUserImage);
            binding.tvUserFullName.setText(lastMessage.getUser().getString(User.KEY_FULL_NAME));
            binding.tvLastMessage.setText(lastMessage.getMessage());
            binding.tvTimeStamp.setText(Helper.getRelativeTimeAgo(lastMessage.getCreatedAt().toString()));
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, LiveChatActivity.class);
            intent.putExtra(Project.class.getName(), chatRoom.getProject());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
    }
}
