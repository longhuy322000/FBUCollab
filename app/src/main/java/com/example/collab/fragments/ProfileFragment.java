package com.example.collab.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.collab.R;
import com.example.collab.activities.LoginActivity;
import com.example.collab.databinding.FragmentProfileBinding;
import com.example.collab.models.User;
import com.example.collab.profile.AboutFragment;
import com.example.collab.profile.PartOfFragment;
import com.example.collab.profile.ProfileProjectsFragment;
import com.example.collab.profile.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabLayout;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ProfileFragment extends Fragment {

    private static final String TAG = "MyProfileFragment";
    private static final int KEY_ABOUT_TAB = 0;
    private static final int KEY_PROJECTS_TAB = 1;
    private static final int KEY_PART_OF_TAB = 2;

    private UserViewModel userViewModel;
    private GoogleSignInClient googleSignInClient;
    private FragmentProfileBinding binding;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel.currentUser.observe(getViewLifecycleOwner(), new Observer<ParseUser>() {
            @Override
            public void onChanged(ParseUser parseUser) {
                if (parseUser != null) {
                    bindUserUI(parseUser);
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInClient.signOut();

                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null ){
                            Log.e(TAG, "Issues with logging out", e);
                            Toast.makeText(getContext(), "Unable to log out", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        });

        final FragmentManager fragmentManager = getChildFragmentManager();
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = new Fragment();
                switch (tab.getPosition()) {
                    case KEY_ABOUT_TAB:
                        fragment = new AboutFragment();
                        break;
                    case KEY_PROJECTS_TAB:
                        fragment = new ProfileProjectsFragment();
                        break;
                    case KEY_PART_OF_TAB:
                        fragment = new PartOfFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        fragmentManager.beginTransaction().replace(R.id.flContainer, new AboutFragment()).commit();
    }

    private void bindUserUI(ParseUser user) {
        Glide.with(getContext())
                .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                .into(binding.ivUserImage);
        binding.tvUserFullName.setText(user.getString(User.KEY_FULL_NAME));
    }
}