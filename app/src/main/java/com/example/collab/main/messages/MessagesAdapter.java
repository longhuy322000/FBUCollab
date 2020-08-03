package com.example.collab.main.messages;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.ItemMessageBinding;
import com.example.collab.models.Message;
import com.example.collab.models.User;
import com.example.collab.shared.Helper;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private Context context;
    private List<Message> messages;

    public MessagesAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageBinding binding = ItemMessageBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemMessageBinding binding;

        public ViewHolder(ItemMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Message message) {
            try {
                ParseUser user = message.getUser().fetchIfNeeded();
                if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername()))
                    binding.rlContainer.setGravity(Gravity.RIGHT);
                else binding.rlContainer.setGravity(Gravity.LEFT);

                Glide.with(context)
                        .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                        .into(binding.ivUserImage);
                binding.tvUserFullName.setText(user.getString(User.KEY_FULL_NAME));
                binding.tvMessage.setText(message.getMessage());
                binding.tvTimestamp.setText(Helper.getRelativeTimeAgo(message.getCreatedAt()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }
}
