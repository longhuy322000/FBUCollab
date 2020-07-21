package com.example.collab.notification_main_fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.databinding.ItemNotificationBinding;
import com.example.collab.shared.Helper;
import com.example.collab.models.Notification;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private static final String TAG = "NotificationsAdapter";

    // Use this interface to open dialog from fragment
    public interface NotificationsAdapterListener {
        void displayProcessRequestDialog(Request request);
    }

    Context context;
    NotificationsAdapterListener notificationsAdapterListener;
    List<Notification> notifications;
    NotificationsViewModel viewModel;

    public NotificationsAdapter(Context context, List<Notification> notifications, NotificationsAdapterListener notificationsAdapterListener, NotificationsViewModel viewModel) {
        this.context = context;
        this.notifications = notifications;
        this.notificationsAdapterListener = notificationsAdapterListener;
        this.viewModel = viewModel;
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

        @SuppressLint("ResourceType")
        public void bind(Notification notification) {
            Request request = notification.getRequest();
            ParseUser user = request.getRequestedUser();
            Project project = request.getProject();

            if (!notification.getSeen()) {
                binding.getRoot().setBackgroundColor(Color.parseColor(context.getString(R.color.boxColor)));
            }
            else {
                binding.getRoot().setBackgroundColor(Color.WHITE);
            }
            binding.tvCreatedAt.setText(Helper.getRelativeTimeAgo(notification.getCreatedAt().toString()));

            Glide.with(context)
                    .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivUserImage);
            String str;
            switch (notification.getType()) {
                case Notification.KEY_NEED_OWNER_CONFIRM:
                    str = "<b>" + user.getString(User.KEY_FULL_NAME) + "</b>" + " wants to be part of your " + " <b>"
                            + project.getProjectName() + "</b> project";
                    binding.tvContent.setText(Html.fromHtml(str));
                    break;
                case Notification.KEY_APPLICANT_RECEIVE_RESULT:
                    switch (request.getStatus()) {
                        case Request.KEY_APPROVED_STATUS:
                            str = "<b>" + user.getString(User.KEY_FULL_NAME) + "</b>" + " has accepted your request to join " + " <b>"
                                    + project.getProjectName() + "</b> project";
                            binding.tvContent.setText(Html.fromHtml(str));
                            break;
                        case Request.KEY_DECLINED_STATUS:
                            str = "<b>" + user.getString(User.KEY_FULL_NAME) + "</b>" + " has declined your request to join " + " <b>"
                                    + project.getProjectName() + "</b> project";
                            binding.tvContent.setText(Html.fromHtml(str));
                            break;
                    }
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            // Display ProcessRequestDialog
            final Notification notification = notifications.get(getAdapterPosition());
            if (!notification.getSeen()) {
                notification.setSeen();
                notification.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with saving seen for notification", e);
                            return;
                        }
                        NotificationsRepository.getInstance().decrementUnseen();
                        viewModel.markSeen(getAdapterPosition());
                    }
                });
            }
            if (notification.getType() == Notification.KEY_NEED_OWNER_CONFIRM) {
                notificationsAdapterListener.displayProcessRequestDialog(notification.getRequest());
            }
        }
    }
}
