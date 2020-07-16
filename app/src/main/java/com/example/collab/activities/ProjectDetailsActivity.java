package com.example.collab.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.databinding.ActivityProjectDetailsBinding;
import com.example.collab.helpers.Helper;
import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.example.collab.models.User;
import com.example.collab.repositories.DataRepository;
import com.google.android.material.button.MaterialButton;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ProjectDetailsActivity";

    ActivityProjectDetailsBinding binding;
    Project project;
    int projectPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        project = Parcels.unwrap(getIntent().getParcelableExtra(Project.class.getName()));
        projectPos = getIntent().getIntExtra(Project.KEY_PROJECT_POSITION, 0);
        Like like = getIntent().getParcelableExtra(Project.KEY_USER_LIKE);
        project.setUserLike(like);
        project.setLiked(like == null ? false : true);
        project.setLikesNum(getIntent().getIntExtra(Project.KEY_LIKES_NUM, 0));
        bind();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void bind() {
        binding.tvUserFullName.setText(project.getOwner().getString(User.KEY_FULL_NAME));
        binding.tvProjectName.setText(project.getProjectName());
        Glide.with(ProjectDetailsActivity.this)
                .load(project.getImage().getUrl())
                .into(binding.ivProjectImage);
        binding.tvDescription.setText(project.getDescription());
        binding.tvSpots.setText(project.getAvailSpots() + "/" + project.getSpots());
        binding.tvRelativeTimestamp.setText(Helper.getRelativeTimeAgo(project.getCreatedAt().toString()));
        if (project.getLiked())
            setLikeActive();
        else setLikeInactive();
        setLikeslabel();

        binding.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeOnClick();
            }
        });

        if (project.getOwner().getUsername().equals(ParseUser.getCurrentUser().getUsername()))
            binding.btnApply.setVisibility(View.GONE);
        else binding.btnApply.setVisibility(View.VISIBLE);
    }

    private void likeOnClick() {
        if (!project.getLiked()) {
            final Like like = new Like();
            like.setOwner(ParseUser.getCurrentUser());
            like.setProject(project);
            like.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Issues with saving like", e);
                        return;
                    }
                    project.setLiked(true);
                    project.incrementLikesNum();
                    project.setUserLike(like);
                    setLikeActive();
                    setLikeslabel();
                }
            });
        }
        else {
            Like like = project.getUserLike();
            like.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "issues with deleting like", e);
                        return;
                    }
                    project.setLiked(false);
                    project.decrementLikesNum();
                    project.setUserLike(null);
                    setLikeInactive();
                    setLikeslabel();
                }
            });
        }
        DataRepository.getInstance().updateProjectAtPosition(project, projectPos);
    }

    public void setLikeActive() {
        ((MaterialButton) binding.btnLike).setIcon(getDrawable(R.drawable.ufi_heart_active));
        ((MaterialButton) binding.btnLike).setIconTintResource(R.color.red);
    }

    public void setLikeInactive() {
        ((MaterialButton) binding.btnLike).setIcon(getDrawable(R.drawable.ufi_heart));
        ((MaterialButton) binding.btnLike).setIconTintResource(R.color.black);
    }

    private void setLikeslabel() {
        if (project.getLikesNum() <= 1) {
            binding.tvLikesLabel.setText(project.getLikesNum() + " like");
        }
        else binding.tvLikesLabel.setText(project.getLikesNum() + " likes");
    }
}