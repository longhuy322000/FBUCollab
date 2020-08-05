package com.example.collab.project_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.databinding.ActivityProjectDetailsBinding;
import com.example.collab.shared.GithubClient;
import com.example.collab.shared.Helper;
import com.example.collab.models.Comment;
import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.example.collab.main.home.ProjectsRepository;
import com.example.collab.shared.OnDoubleTapListener;
import com.google.android.material.button.MaterialButton;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity implements ApplyDialogFragment.ApplyDialogListenter {

    private static final String TAG = "ProjectDetailsActivity";

    private ParseUser currentUser;
    private ActivityProjectDetailsBinding binding;
    private CommentsViewModel commentsViewModel;
    private CommentsAdapter adapter;
    private List<Comment> comments;
    private Project project;
    private int projectPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        project = getIntent().getParcelableExtra(Project.class.getName());
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
                // display ApplyDialogFragment
                FragmentManager fm = getSupportFragmentManager();
                ApplyDialogFragment applyDialogFragment = ApplyDialogFragment.newInstance(project, currentUser);
                applyDialogFragment.show(fm, "fragment_apply_dialog");
            }
        });

        comments = new ArrayList<>();
        adapter = new CommentsAdapter(this, comments);
        binding.rvComments.setAdapter(adapter);
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

        // observe comments in model
        commentsViewModel.comments.observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> commentsList) {
                Log.i(TAG, "loading comments from model");
                if (!commentsList.isEmpty()) {
                    // reload UI with new data
                    comments.clear();
                    comments.addAll(commentsList);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        return super.onSupportNavigateUp();
    }

    private void bind() {
        // bind data to UI
        Glide.with(ProjectDetailsActivity.this)
                .load(project.getOwner().getParseFile(User.KEY_IMAGE).getUrl())
                .into(binding.ivUserImage);
        binding.tvUserFullName.setText(project.getOwner().getString(User.KEY_FULL_NAME));
        binding.tvProjectName.setText(project.getProjectName());
        Glide.with(ProjectDetailsActivity.this)
                .load(project.getImage().getUrl())
                .into(binding.ivProjectImage);
        binding.tvDescription.setText(project.getDescription());
        binding.tvSpots.setText(project.getSpotsStringDisplay());
        binding.tvRelativeTimestamp.setText(Helper.getRelativeTimeAgo(project.getCreatedAt()));
        binding.tvSkillsList.setText(Helper.listToString(project.getSkillsList()));
        binding.tvDuration.setText(project.getDuration());
        if (project.getGithubRepoName() == null || project.getGithubRepoName().isEmpty()){
            binding.tvGithubRepo.setText("Not available");
        }
        else binding.tvGithubRepo.setText(GithubClient.GITHUB_BASE_URL + project.getOwner().getString(User.KEY_GITHUB_USERNAME) + "/" + project.getGithubRepoName());
        if (project.getLiked())
            setLikeActive();
        else setLikeInactive();
        setLikeslabel();

        // fetch currentUser to get image
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues getting current user");
                    return;
                }
                currentUser = user;
                Glide.with(ProjectDetailsActivity.this)
                        .load(currentUser.getParseFile(User.KEY_IMAGE).getUrl())
                        .into(binding.ivUserCommentImage);

                if (currentUser.getString(User.KEY_GITHUB_USERNAME) == null || currentUser.getString(User.KEY_GITHUB_USERNAME).isEmpty()) {
                    binding.tvNeedGithubAccount.setVisibility(View.VISIBLE);
                    binding.btnApply.setEnabled(false);
                } else {
                    binding.tvNeedGithubAccount.setVisibility(View.GONE);
                }
            }
        });

        // double tap to like
        binding.btnLike.setOnTouchListener(new OnDoubleTapListener(this) {
            @Override
            public void onDoubleTap(MotionEvent e) {
                likeOnClick();
            }
        });

        // check if the user is the owner of the project
        if (project.getOwner().getUsername().equals(ParseUser.getCurrentUser().getUsername()))
            binding.btnApply.setVisibility(View.GONE);
        else checkApplied();

        // postComment button onclick
        binding.btnPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.etComment.getText().toString();
                binding.etComment.setText("");
                Helper.hideKeyboard(ProjectDetailsActivity.this);
                binding.progressBar.setVisibility(View.VISIBLE);

                if (message == null || message.isEmpty()) {
                    Toast.makeText(ProjectDetailsActivity.this, "Comment can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Comment comment = new Comment();
                comment.setOwner(ParseUser.getCurrentUser());
                comment.setProject(project);
                comment.setComment(message);
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with saving comment");
                        }
                        else {
                            commentsViewModel.insertNewComment(comment);
                        }
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    // check whether user has liked the project
    private void checkApplied() {
        ParseQuery<Request> query = ParseQuery.getQuery(Request.class);
        query.whereEqualTo(Request.KEY_REQUESTED_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(Request.KEY_PROJECT, project);
        query.findInBackground(new FindCallback<Request>() {
            @Override
            public void done(List<Request> requests, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with counting requests");
                    return;
                }
                if (requests.isEmpty()) {
                    if (project.getStatus() == Project.KEY_STATUS_CLOSED)
                        binding.btnApply.setText("Project closed");
                    else binding.btnApply.setText("Apply");
                }
                else {
                    Request request = requests.get(0);
                    if (request.getStatus() == Request.KEY_PENDING_STATUS)
                        binding.btnApply.setText("Applied");
                    else if (request.getStatus() == Request.KEY_APPROVED_STATUS)
                        binding.btnApply.setText("Approved");
                    else if (request.getStatus() == Request.KEY_DECLINED_STATUS)
                        binding.btnApply.setText("Declined");
                    binding.btnApply.setEnabled(false);
                }
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
        // Notify data has changed in repository and model
        ProjectsRepository.getInstance().updateProjectAtPosition(project, projectPos);
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
    public void onFinishDialog(String text, Boolean done) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        if (!done)
            return;
        binding.btnApply.setText("Applied");
        binding.btnApply.setEnabled(false);
    }
}