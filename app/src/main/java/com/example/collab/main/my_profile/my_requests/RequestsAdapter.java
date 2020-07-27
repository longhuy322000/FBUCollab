package com.example.collab.main.my_profile.my_requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.ItemRequestBinding;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.parse.ParseUser;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private Context context;
    private List<Request> requests;

    public RequestsAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRequestBinding binding = ItemRequestBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemRequestBinding binding;

        public ViewHolder(ItemRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void binding(Request request) {
            Project project = request.getProject();
            ParseUser owner = project.getOwner();
            binding.tvProjectName.setText(project.getProjectName());
            Glide.with(context)
                    .load(project.getImage().getUrl())
                    .into(binding.ivProjectImage);
            binding.tvDescription.setText(project.getDescription());
            Glide.with(context)
                    .load(owner.getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivUserImage);
            binding.tvUserFullName.setText(owner.getString(User.KEY_FULL_NAME));
            binding.tvRequestContent.setText(request.getDescription());

            switch (request.getStatus()) {
                case Request.KEY_APPROVED_STATUS:
                    binding.btnStatus.setText("Approved");
                    break;
                case Request.KEY_DECLINED_STATUS:
                    binding.btnStatus.setText("Declined");
                    break;
                case Request.KEY_PENDING_STATUS:
                    binding.btnStatus.setText("Pending");
                    break;
            }
        }
    }

}
