package com.example.collab.main.my_profile.applicants;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.ItemApplicantBinding;
import com.example.collab.main.notification.ProcessRequestDialogFragment;
import com.example.collab.models.Notification;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class ApplicantsAdapter extends RecyclerView.Adapter<ApplicantsAdapter.ViewHolder> {

    private static final String TAG = "ApplicantsAdapter";

    private Context context;
    private List<Notification> applicants;

    public ApplicantsAdapter(Context context, List<Notification> applicants) {
        this.context = context;
        this.applicants = applicants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemApplicantBinding binding = ItemApplicantBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(applicants.get(position));
    }

    @Override
    public int getItemCount() {
        return applicants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemApplicantBinding binding;

        public ViewHolder(ItemApplicantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notification notification) {
            final Request request = notification.getRequest();
            Project project = request.getProject();
            ParseUser requestedUser = request.getRequestedUser();
            Glide.with(context)
                    .load(requestedUser.getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivUserImage);
            binding.tvUserFullName.setText(requestedUser.getString(User.KEY_FULL_NAME));
            binding.tvProjectName.setText(project.getProjectName());
            Glide.with(context)
                    .load(project.getImage().getUrl())
                    .into(binding.ivProjectImage);
            binding.tvDescription.setText(project.getDescription());
            binding.tvRequestContent.setText(request.getDescription());

            binding.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    request.setStatus(Request.KEY_DECLINED_STATUS);
                    saveRequest(request);
                }
            });

            changeStatusButtons(request);

            binding.btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    request.setStatus(Request.KEY_APPROVED_STATUS);
                    saveRequest(request);
                    final Project project = request.getProject();
                    project.setSpots(project.getSpots() + 1);
                    project.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issues with changing spots in project", e);
                                return;
                            }
                        }
                    });
                }
            });
        }

        private void saveRequest(final Request request) {
            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issues with saving decline status in request", e);
                        return;
                    }

                    // Create in notification
                    Notification notification = new Notification();
                    notification.setRequest(request);
                    notification.setDeliverTo(request.getRequestedUser());
                    notification.setType(Notification.KEY_APPLICANT_RECEIVE_RESULT);
                    notification.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issues with creating new notification", e);
                                return;
                            }
                        }
                    });

                    changeStatusButtons(request);
                }
            });
        }

        private void changeStatusButtons(Request request) {
            switch (request.getStatus()) {
                case Request.KEY_PENDING_STATUS:
                    binding.btnApprove.setVisibility(View.VISIBLE);
                    binding.btnDecline.setVisibility(View.VISIBLE);
                    binding.btnStatus.setVisibility(View.GONE);
                    break;
                case Request.KEY_APPROVED_STATUS:
                    binding.btnApprove.setVisibility(View.GONE);
                    binding.btnDecline.setVisibility(View.GONE);
                    binding.btnStatus.setVisibility(View.VISIBLE);
                    binding.btnStatus.setText("Approved");
                    break;
                case Request.KEY_DECLINED_STATUS:
                    binding.btnApprove.setVisibility(View.GONE);
                    binding.btnDecline.setVisibility(View.GONE);
                    binding.btnStatus.setVisibility(View.VISIBLE);
                    binding.btnStatus.setText("Declined");
                    break;
            }
        }
    }
}
