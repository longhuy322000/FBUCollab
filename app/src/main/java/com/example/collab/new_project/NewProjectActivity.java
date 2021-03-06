package com.example.collab.new_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.collab.R;
import com.example.collab.models.ChatRoom;
import com.example.collab.models.User;
import com.example.collab.project_details.ProjectDetailsActivity;
import com.example.collab.shared.CameraHelper;
import com.example.collab.shared.GithubClient;
import com.example.collab.shared.Helper;
import com.example.collab.databinding.ActivityNewProjectBinding;
import com.example.collab.models.Project;
import com.example.collab.main.home.ProjectsRepository;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewProjectActivity extends AppCompatActivity {

    private static final String TAG = "NewProjectActivity";

    private ParseUser currentUser;
    private ActivityNewProjectBinding binding;
    private List<String> skillList;
    private ParseFile photoFile;
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with getting current user", e);
                    return;
                }
                currentUser = object;
                if (currentUser.getString(User.KEY_GITHUB_TOKEN) == null || currentUser.getString(User.KEY_GITHUB_TOKEN).isEmpty()) {
                    binding.tvNeedGithub.setVisibility(View.VISIBLE);
                    binding.btnPost.setEnabled(false);
                }
                else {
                    binding.tvNeedGithub.setVisibility(View.GONE);
                    binding.btnPost.setEnabled(true);
                }
            }
        });

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        skillList = new ArrayList<>();
        cameraHelper = new CameraHelper(this);

        binding.etProjectName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvProjectNameLabel.setText(editable);
            }
        });

        binding.btnAddSkill.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String skillName = binding.etAddSkill.getText().toString();
                 if (skillName.isEmpty()) {
                     return;
                 }
                 if (skillList.isEmpty()) {
                     binding.tvSkillList.setText(skillName);
                 }
                 else {
                     binding.tvSkillList.setText(binding.tvSkillList.getText().toString() + ", " + skillName);
                 }
                 skillList.add(skillName);
                 binding.etAddSkill.setText("");
             }
        });

        binding.btnTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.launchCamera();
            }
        });

        binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.onPickPhoto();
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.hideKeyboard(NewProjectActivity.this);
                saveProject();
                createGithubRepo();
            }
        });
    }

    private void addUserToRoom(Project project) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setProject(project);
        chatRoom.setUser(ParseUser.getCurrentUser());
        chatRoom.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with saving chatroom", e);
                    return;
                }
            }
        });
    }

    private void createGithubRepo() {
        try {
            JSONObject body = new JSONObject();
            body.put(GithubClient.KEY_REPO_NAME, binding.etGithubRepoName.getText().toString());
            body.put(GithubClient.KEY_REPO_DESCRIPTION, binding.etDescription.getText().toString());
            body.put(GithubClient.KEY_REPO_PRIVATE, false);
            GithubClient.createNewRepo(this, currentUser.getString(User.KEY_GITHUB_TOKEN), body.toString(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "Successfully created Github repo");
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Unable to create Github repo", error);
                        }
                    });
        } catch (JSONException e) {
            Log.e(TAG, "Hit json exception while creating github repo", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        return super.onSupportNavigateUp();
    }

    private void saveProject() {
        binding.btnPost.setVisibility(View.GONE);

        binding.progressBar.setVisibility(View.VISIBLE);
        final Project project = new Project();
        project.setProjectName(binding.etProjectName.getText().toString());
        project.setDescription(binding.etDescription.getText().toString());
        project.setSkills(skillList);
        project.setCapacity(Integer.parseInt(binding.etCapacity.getText().toString()));
        project.setSpots(0);
        project.setDuration(binding.etTimeLength.getText().toString());
        project.setImage(photoFile);
        project.setStatus(Project.KEY_STATUS_OPEN);
        project.setOwner(ParseUser.getCurrentUser());
        project.setGithubRepoName(binding.etGithubRepoName.getText().toString());
        project.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issues with posting project", e);
                    Toast.makeText(NewProjectActivity.this, "Unable to post the project", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.progressBar.setVisibility(View.GONE);
                resetUI();
                ProjectsRepository.getInstance().addProject(project, 0);
                Intent intent = new Intent(NewProjectActivity.this, ProjectDetailsActivity.class);
                intent.putExtra(Project.class.getName(), project);
                intent.putExtra(Project.KEY_PROJECT_POSITION, 0);
                intent.putExtra(Project.KEY_USER_LIKE, project.getUserLike());
                intent.putExtra(Project.KEY_LIKES_NUM, project.getLikesNum());
                startActivity(intent);
                finish();
            }
        });

        addUserToRoom(project);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.TAKE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(cameraHelper.photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                binding.ivProjectImage.setImageBitmap(takenImage);
                binding.ivProjectImage.setVisibility(View.VISIBLE);

                photoFile = new ParseFile(cameraHelper.photoFile);
            } else { // Result was a failure
                Toast.makeText(NewProjectActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if ((data != null) && requestCode == CameraHelper.PICK_PHOTO_REQUEST_CODE) {
                Uri photoUri = data.getData();

                // Load the image located at photoUri into selectedImage
                Bitmap selectedImage = cameraHelper.loadFromUri(photoUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] bitmapBytes = stream.toByteArray();

                photoFile = new ParseFile("photo.jpg", bitmapBytes);

                // Load the selected image into a preview
                binding.ivProjectImage.setImageBitmap(selectedImage);
                binding.ivProjectImage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void resetUI() {
        binding.etProjectName.setText("");
        binding.etDescription.setText("");
        binding.ivProjectImage.setVisibility(View.GONE);
        binding.ivProjectImage.setImageDrawable(null);
        binding.etCapacity.setText("");
        binding.etTimeLength.setText("");
        binding.tvSkillList.setText("");
        skillList.clear();
        binding.etAddSkill.setText("");
    }
}