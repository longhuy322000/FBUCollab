package com.example.collab.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.collab.R;
import com.example.collab.databinding.ActivityMainBinding;
import com.example.collab.models.Project;
import com.example.collab.notification_main_fragment.NotificationsViewModel;
import com.example.collab.profile.UserViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int KEY_HOME_FRAGMENT = 0;
    private static final int KEY_NOTIFICATION_FRAGMENT = 1;
    private static final int KEY_MY_PROFILE_FRAGMENT = 2;

    private NotificationsViewModel notificationsViewModel;
    private ActivityMainBinding binding;
    private UserViewModel userViewModel;
    int previousTab;
    int currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set toolbar as action bar
        binding.toolbar.inflateMenu(R.menu.my_profile_menu);
        setSupportActionBar(binding.toolbar);

        // set notification tab badge number
        final BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.action_notification);
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        notificationsViewModel.unseenCount.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer num) {
                if (num == 0) {
                    badge.setVisible(false);
                }
                else {
                    badge.setVisible(true);
                    badge.setNumber(num);
                }
            }
        });

        fetchCurrentUser();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        final FragmentManager fragmentManager = getSupportFragmentManager();

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                binding.bottomNavigation.getMenu().getItem(KEY_HOME_FRAGMENT).setIcon(R.drawable.ic_baseline_home_24);
                binding.bottomNavigation.getMenu().getItem(KEY_NOTIFICATION_FRAGMENT).setIcon(R.drawable.ic_baseline_notifications_24);
                binding.bottomNavigation.getMenu().getItem(KEY_MY_PROFILE_FRAGMENT).setIcon(R.drawable.ic_baseline_person_24);
                Fragment fragment = new Fragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.action_home:
                        binding.tvMenuAppName.setText("Collab");
                        fragment = new HomeFragment();
                        item.setIcon(R.drawable.ic_baseline_home_24);
                        currentTab = KEY_HOME_FRAGMENT;
                        break;
                    case R.id.action_notification:
                        binding.tvMenuAppName.setText("Notifications");
                        fragment = new NotificationFragment();
                        item.setIcon(R.drawable.ic_baseline_notifications_24);
                        currentTab = KEY_NOTIFICATION_FRAGMENT;
                        break;
                    case R.id.action_profile:
                        binding.tvMenuAppName.setText("My Profile");
                        fragment = new ProfileFragment();
                        item.setIcon(R.drawable.ic_baseline_person_24);
                        currentTab = KEY_MY_PROFILE_FRAGMENT;
                        break;
                }
                if (currentTab != previousTab)
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                previousTab = currentTab;
                transaction.replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        String fragmentName = getIntent().getStringExtra(Fragment.class.getName());
        if (fragmentName != null && fragmentName.equals(ProfileFragment.class.getName())) {
            binding.bottomNavigation.setSelectedItemId(R.id.action_profile);
            previousTab = KEY_MY_PROFILE_FRAGMENT;
        }
        else {
            binding.bottomNavigation.setSelectedItemId(R.id.action_home);
            previousTab = KEY_HOME_FRAGMENT;
        }

    }

    private void fetchCurrentUser() {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issies with getting current user", e);
                    return;
                }
                userViewModel.currentUser.setValue(ParseUser.getCurrentUser());
            }
        });
    }
}