package com.example.collab.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.collab.R;
import com.example.collab.databinding.ActivityMainBinding;
import com.example.collab.fragments.HomeFragment;
import com.example.collab.fragments.MyProfileFragment;
import com.example.collab.fragments.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final FragmentManager fragmentManager = getSupportFragmentManager();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                binding.bottomNavigation.getMenu().getItem(0).setIcon(R.drawable.ic_baseline_home_24);
                binding.bottomNavigation.getMenu().getItem(1).setIcon(R.drawable.ic_baseline_notifications_24);
                binding.bottomNavigation.getMenu().getItem(2).setIcon(R.drawable.ic_baseline_person_24);
                Fragment fragment = new Fragment();
                switch (item.getItemId()) {
                    case R.id.action_home:
                        binding.tvMenuAppName.setText("Collab");
                        fragment = new HomeFragment();
                        item.setIcon(R.drawable.ic_baseline_home_24);
                        break;
                    case R.id.action_notification:
                        binding.tvMenuAppName.setText("Notifications");
                        fragment = new NotificationFragment();
                        item.setIcon(R.drawable.ic_baseline_notifications_24);
                        break;
                    case R.id.action_profile:
                        binding.tvMenuAppName.setText("My Profile");
                        fragment = new MyProfileFragment();
                        item.setIcon(R.drawable.ic_baseline_person_24);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }
}