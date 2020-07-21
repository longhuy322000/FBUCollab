package com.example.collab.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.collab.main.HomeFragment;
import com.example.collab.models.Project;
import com.example.collab.main.ProjectsViewModel;
import com.parse.ParseUser;

import java.util.List;

public class ProfileProjectsFragment extends HomeFragment {

    private static final String TAG = "ProfileProjectsFragment";

    private ParseUser user;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectViewModel = new ViewModelProvider(getActivity()).get(ProjectsViewModel.class);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        bind();

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                projectViewModel.getUserProjects(user);
            }
        });

        userViewModel.currentUser.observe(getViewLifecycleOwner(), new Observer<ParseUser>() {
            @Override
            public void onChanged(ParseUser parseUser) {
                Log.i(TAG, "got user from model ");
                if (parseUser != null) {
                    user = parseUser;
                    bindDataToRecyclerView(user);
                }
            }
        });
    }

    private void bindDataToRecyclerView(ParseUser user) {
        projectViewModel.getUserProjects(user);
        projectViewModel.getProjects().observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projectsFromModel) {
                if (!projectsFromModel.isEmpty()){
                    Log.i(TAG, "got projects from model ");
                    clearProjects();
                    addAllProjects(projectsFromModel);
                    binding.swipeContainer.setRefreshing(false);
                }
                else {
                    clearProjects();
                }
            }
        });
    }
}
