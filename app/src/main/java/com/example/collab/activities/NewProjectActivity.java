package com.example.collab.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collab.helpers.CameraHelper;
import com.example.collab.helpers.Helper;
import com.example.collab.databinding.ActivityNewProjectBinding;
import com.example.collab.models.Project;
import com.example.collab.repositories.HomeProjectsRepository;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewProjectActivity extends AppCompatActivity {

    private static final String TAG = "NewProjectActivity";
    private static final int TAKE_IMAGE_ACTIVITY_REQUEST_CODE = 10;
    private static final int PICK_PHOTO_REQUEST_CODE = 30;

    private ActivityNewProjectBinding binding;
    private List<String> skillList;
    private ParseFile photoFile;
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewProjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                savePost();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void savePost() {
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
                HomeProjectsRepository.getInstance().addProject(project, 0);
                Intent intent = new Intent(NewProjectActivity.this, ProjectDetailsActivity.class);
                intent.putExtra(Project.class.getName(), project);
                intent.putExtra(Project.KEY_PROJECT_POSITION, 0);
                intent.putExtra(Project.KEY_USER_LIKE, project.getUserLike());
                intent.putExtra(Project.KEY_LIKES_NUM, project.getLikesNum());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_IMAGE_ACTIVITY_REQUEST_CODE) {
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
            if ((data != null) && requestCode == PICK_PHOTO_REQUEST_CODE) {
                Uri photoUri = data.getData();

                // Load the image located at photoUri into selectedImage
                Bitmap selectedImage = cameraHelper.loadFromUri(photoUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] bitmapBytes = stream.toByteArray();

                photoFile = new ParseFile(bitmapBytes);

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