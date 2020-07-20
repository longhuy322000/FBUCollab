package com.example.collab.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.ItemNotificationBinding;
import com.example.collab.models.Notification;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.parse.ParseUser;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    Context context;
    List<Notification> notifications;

    public NotificationsAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemNotificationBinding binding;

        public ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Notification notification) {
            Request request = notification.getRequest();
            ParseUser user = request.getRequestedUser();
            Project project = request.getProject();
            Glide.with(context)
                    .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivUserImage);
//            binding.tvUserFullName.setText(user.getString(User.KEY_FULL_NAME));
            switch (notification.getType()) {
                case Notification.KEY_NEED_OWNER_CONFIRM:
                    binding.tvContent.setText(user.getString(User.KEY_FULL_NAME) + " wants to be part of " + project.getProjectName());
                    break;
                case Notification.KEY_APPLICANT_RECEIVE_RESULT:
                    switch (request.getStatus()) {
                        case Request.KEY_ACCEPT_STATUS:
                            binding.tvContent.setText(user.getString(User.KEY_FULL_NAME) + " has accepted your request " + project.getProjectName());
                            break;
                        case Request.KEY_DECLINE_STATUS:
                            binding.tvContent.setText(user.getString(User.KEY_FULL_NAME) + " has declined your request " + project.getProjectName());
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            // Open ProcessRequestDialog

        }
    }
}
