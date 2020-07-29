package com.example.collab.project_details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.ItemCommentBinding;
import com.example.collab.shared.Helper;
import com.example.collab.models.Comment;
import com.example.collab.models.User;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    Context context;
    List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemCommentBinding binding;

        public ViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(Comment comment) {
            Glide.with(context)
                    .load(comment.getOwner().getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivUserImage);
            binding.tvComment.setText(comment.getComment());
            binding.tvUserFullName.setText(comment.getOwner().getString(User.KEY_FULL_NAME));
            binding.tvTimestamp.setText(Helper.getRelativeTimeAgo(comment.getCreatedAt().toString()));
        }

        @Override
        public void onClick(View view) {

        }
    }
}
