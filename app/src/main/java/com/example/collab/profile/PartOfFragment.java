package com.example.collab.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.collab.main.home.HomeFragment;
import com.example.collab.models.Project;
import com.example.collab.main.home.ProjectsViewModel;
import com.parse.ParseUser;

import java.util.List;

public class PartOfFragment extends HomeFragment {

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

        binding.addButton.setVisibility(View.GONE);

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                projectViewModel.getPartOfProjects(user);
            }
        });

        binding.progressBar.setVisibility(View.VISIBLE);
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
        projectViewModel.getProjects().observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projectsFromModel) {
                if (projectsFromModel != null) {
                    binding.progressBar.setVisibility(View.GONE);
                    if (projectsFromModel.isEmpty()) {
                        binding.tvNoProjects.setVisibility(View.VISIBLE);
                        clearProjects();
                        return;
                    }

                    binding.tvNoProjects.setVisibility(View.GONE);
                    Log.i(TAG, "got projects from model ");
                    binding.tvNoProjects.setVisibility(View.GONE);
                    clearProjects();
                    addAllProjects(projectsFromModel);
                    binding.swipeContainer.setRefreshing(false);
                }
            }
        });
    }
}
