package com.example.collab.main.messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.ItemCollaboratorBinding;
import com.example.collab.models.ChatRoom;
import com.example.collab.models.User;
import com.parse.ParseUser;

import java.util.List;

public class ChatInfoAdapter extends RecyclerView.Adapter<ChatInfoAdapter.ViewHolder> {

    private Context context;
    private List<ChatRoom> collaborators;

    public ChatInfoAdapter(Context context, List<ChatRoom> collaborators) {
        this.context = context;
        this.collaborators = collaborators;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCollaboratorBinding binding = ItemCollaboratorBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(collaborators.get(position));
    }

    @Override
    public int getItemCount() {
        return collaborators.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemCollaboratorBinding binding;

        public ViewHolder(ItemCollaboratorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatRoom collaborator) {
            ParseUser user = collaborator.getUser();
            Glide.with(context)
                    .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivCollaboratorImage);
            binding.tvCollaboratorFullName.setText(user.getString(User.KEY_FULL_NAME));
        }
    }
}
