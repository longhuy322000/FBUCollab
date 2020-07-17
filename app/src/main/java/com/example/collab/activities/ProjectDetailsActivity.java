package com.example.collab.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.adapters.CommentsAdapter;
import com.example.collab.databinding.ActivityProjectDetailsBinding;
import com.example.collab.fragments.ApplyDialogFragment;
import com.example.collab.helpers.Helper;
import com.example.collab.models.Comment;
import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.example.collab.models.User;
import com.example.collab.repositories.HomeProjectsRepository;
import com.example.collab.viewmodels.CommentsViewModel;
import com.example.collab.viewmodels.CommentsViewModelFactory;
import com.example.collab.viewmodels.ProjectsViewModel;
import com.google.android.material.button.MaterialButton;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity implements ApplyDialogFragment.ApplyDialogListenter {

    private static final String TAG = "ProjectDetailsActivity";

    ActivityProjectDetailsBinding binding;
    CommentsViewModel commentsViewModel;
    CommentsAdapter adapter;
    List<Comment> comments;
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
        commentsViewModel = new ViewModelProvider(this, new CommentsViewModelFactory(project)).get(CommentsViewModel.class);
        projectPos = getIntent().getIntExtra(Project.KEY_PROJECT_POSITION, 0);
        Like like = getIntent().getParcelableExtra(Project.KEY_USER_LIKE);
        project.setUserLike(like);
        project.setLiked(like == null ? false : true);
        project.setLikesNum(getIntent().getIntExtra(Project.KEY_LIKES_NUM, 0));
        bind();

        binding.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ApplyDialogFragment applyDialogFragment = ApplyDialogFragment.newInstance(project.getOwner().getString(User.KEY_FULL_NAME),
                        project.getProjectName(), project.getOwner().getParseFile(User.KEY_IMAGE).getUrl());
                applyDialogFragment.show(fm, "fragment_apply_dialog");
            }
        });

        comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments);
        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

        commentsViewModel.comments.observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> commentsList) {
                Log.i(TAG, "loading comments from model");
                if (!commentsList.isEmpty()) {
                    comments.addAll(commentsList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
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

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser currentUser, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues getting current user");
                    return;
                }
                Glide.with(ProjectDetailsActivity.this)
                        .load(currentUser.getParseFile(User.KEY_IMAGE).getUrl())
                        .into(binding.ivUserCommentImage);
            }
        });

        binding.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeOnClick();
            }
        });

        if (project.getOwner().getUsername().equals(ParseUser.getCurrentUser().getUsername()))
            binding.btnApply.setVisibility(View.GONE);
        else binding.btnApply.setVisibility(View.VISIBLE);

        binding.btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.etComment.getText().toString();
                if (message == null || message.isEmpty()) {
                    Toast.makeText(ProjectDetailsActivity.this, "Comment can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                Comment comment = new Comment();
                comment.setOwner(ParseUser.getCurrentUser());
                comment.setProject(project);
                comment.setComment(message);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with saving comment");
                            return;
                        }
                    }
                });
            }
        });
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
        HomeProjectsRepository.getInstance().updateProjectAtPosition(project, projectPos);
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

    @Override
    public void onFinishDialog(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}