package com.example.collab.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.collab.activities.ProjectDetailsActivity;
import com.example.collab.helpers.Helper;
import com.example.collab.R;
import com.example.collab.databinding.ItemProjectBinding;
import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.example.collab.models.User;
import com.google.android.material.button.MaterialButton;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {

    private static final String TAG = "ProjectAdapter";
    Context context;
    List<Project> projects;

    public ProjectsAdapter(Context context, List<Project> projects) {
        this.context = context;
        this.projects = projects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProjectBinding binding = ItemProjectBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ItemProjectBinding binding;

        public ViewHolder(ItemProjectBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bind(Project project) {
            Glide.with(context)
                    .load(project.getOwner().getParseFile(User.KEY_IMAGE).getUrl())
                    .into(binding.ivUserImage);
            binding.tvUserFullName.setText(project.getOwner().getString(User.KEY_FULL_NAME));
            binding.tvProjectName.setText(project.getProjectName());
            Glide.with(context)
                    .load(project.getImage().getUrl())
                    .into(binding.ivProjectImage);
            binding.tvDescription.setText(project.getDescription());
            binding.tvSpots.setText(project.getSpotsStringDisplay());
            binding.tvRelativeTimestamp.setText(Helper.getRelativeTimeAgo(project.getCreatedAt().toString()));
            binding.tvSkillsList.setText(Helper.listToString(project.getSkillsList()));
            binding.tvDuration.setText(project.getDuration());
            if (project.getLiked() != null && project.getLiked() == true)
                setLikeActive();
            else setLikeInactive();
            setLikeslabel();

            binding.btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likeOnClick();
                }
            });

            binding.getRoot().setOnClickListener(this);
        }

        private void likeOnClick() {
            if (!projects.get(getAdapterPosition()).getLiked()) {
                final Like like = new Like();
                like.setOwner(ParseUser.getCurrentUser());
                like.setProject(projects.get(getAdapterPosition()));
                like.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with saving like", e);
                            return;
                        }
                        projects.get(getAdapterPosition()).setLiked(true);
                        projects.get(getAdapterPosition()).incrementLikesNum();
                        projects.get(getAdapterPosition()).setUserLike(like);
                        setLikeActive();
                        setLikeslabel();
                    }
                });
            }
            else {
                Like like = projects.get(getAdapterPosition()).getUserLike();
                like.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "issues with deleting like", e);
                            return;
                        }
                        projects.get(getAdapterPosition()).setLiked(false);
                        projects.get(getAdapterPosition()).decrementLikesNum();
                        projects.get(getAdapterPosition()).setUserLike(null);
                        setLikeInactive();
                        setLikeslabel();
                    }
                });
            }
        }

        public void setLikeActive() {
            ((MaterialButton) binding.btnLike).setIcon(context.getDrawable(R.drawable.ufi_heart_active));
            ((MaterialButton) binding.btnLike).setIconTintResource(R.color.red);
        }

        public void setLikeInactive() {
            ((MaterialButton) binding.btnLike).setIcon(context.getDrawable(R.drawable.ufi_heart));
            ((MaterialButton) binding.btnLike).setIconTintResource(R.color.black);
        }

        private void setLikeslabel() {
            if (projects.get(getAdapterPosition()).getLikesNum() <= 1) {
                binding.tvLikesLabel.setText(projects.get(getAdapterPosition()).getLikesNum() + " like");
            }
            else binding.tvLikesLabel.setText(projects.get(getAdapterPosition()).getLikesNum() + " likes");
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ProjectDetailsActivity.class);
            intent.putExtra(Project.class.getName(), projects.get(getAdapterPosition()));
            intent.putExtra(Project.KEY_PROJECT_POSITION, getAdapterPosition());
            intent.putExtra(Project.KEY_USER_LIKE, projects.get(getAdapterPosition()).getUserLike());
            intent.putExtra(Project.KEY_LIKES_NUM, projects.get(getAdapterPosition()).getLikesNum());
            context.startActivity(intent);
        }
    }
}
