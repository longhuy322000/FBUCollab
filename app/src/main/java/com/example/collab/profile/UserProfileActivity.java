package com.example.collab.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.collab.R;
import com.example.collab.databinding.ActivityUserProfileBinding;
import com.example.collab.main.my_profile.ProfileFragment;
import com.example.collab.main.home.ProjectsRepository;
import com.parse.ParseUser;

public class UserProfileActivity extends AppCompatActivity {

    ActivityUserProfileBinding binding;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ParseUser user = getIntent().getParcelableExtra(ParseUser.class.getName());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.currentUser.setValue(user);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContainer, new ProfileFragment()).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        ProjectsRepository.getInstance().queryAllProjects();
        return super.onSupportNavigateUp();
    }
}