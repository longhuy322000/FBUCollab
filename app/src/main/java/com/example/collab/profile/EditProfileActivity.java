package com.example.collab.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.databinding.ActivityEditProfileBinding;
import com.example.collab.main.MainActivity;
import com.example.collab.main.ProfileFragment;
import com.example.collab.models.User;
import com.example.collab.shared.CameraHelper;
import com.example.collab.shared.Helper;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    ParseUser user;
    List<String> skills;
    CameraHelper cameraHelper;
    ParseFile photoFile;
    UserViewModel userViewModel;
    ActivityEditProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cameraHelper = new CameraHelper(EditProfileActivity.this);
        user = getIntent().getParcelableExtra(ParseUser.class.getName());
        Glide.with(EditProfileActivity.this)
                .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                .into(binding.ivUserImage);
        binding.etFullName.setText(user.getString(User.KEY_FULL_NAME));
        binding.etDescription.setText(user.getString(User.KEY_DESCRIPTION));
        binding.etSchool.setText(user.getString(User.KEY_SCHOOL));
        skills = user.getList(User.KEY_SKILLS);
        if (skills == null)
            skills = new ArrayList<>();
        binding.tvSkillList.setText(Helper.listToString(skills));

        binding.btnAddSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String skillName = binding.etAddSkill.getText().toString();
                if (skillName.isEmpty()) {
                    return;
                }
                if (skills.isEmpty()) {
                    binding.tvSkillList.setText(skillName);
                }
                else {
                    binding.tvSkillList.setText(binding.tvSkillList.getText().toString() + ", " + skillName);
                }
                skills.add(skillName);
                binding.etAddSkill.setText("");
            }
        });

        binding.btnRemoveSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (skills.isEmpty()) {
                    return;
                }
                else {
                    skills.remove(skills.size() - 1);
                    binding.tvSkillList.setText(Helper.listToString(skills));
                }
                binding.etAddSkill.setText("");
            }
        });

        binding.btnChangeUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraHelper.launchCamera();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoFile != null)
                    user.put(User.KEY_IMAGE, photoFile);
                user.put(User.KEY_FULL_NAME, binding.etFullName.getText().toString());
                user.put(User.KEY_DESCRIPTION, binding.etDescription.getText().toString());
                user.put(User.KEY_SCHOOL, binding.etSchool.getText().toString());
                user.put(User.KEY_SKILLS, skills);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with saving user", e);
                            return;
                        }
                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                        intent.putExtra(Fragment.class.getName(), ProfileFragment.class.getName());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CameraHelper.TAKE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // crop image
                Bitmap takenImage = BitmapFactory.decodeFile(cameraHelper.photoFile.getAbsolutePath());
                takenImage = CameraHelper.cropToSquare(takenImage);
                takenImage = CameraHelper.getCircularBitmap(takenImage);

                // convert image to ParseFile
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                takenImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
                byte[] bitmapBytes = stream.toByteArray();
                photoFile = new ParseFile(bitmapBytes);

                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                binding.ivUserImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(EditProfileActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}