package com.example.collab.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.View;

import com.example.collab.R;
import com.example.collab.new_project.NewProjectActivity;
import com.example.collab.models.Project;
import com.example.collab.shared.ProjectsFragment;

import java.util.List;

public class HomeFragment extends ProjectsFragment {

    private static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bind();

        binding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                projectViewModel.getAllProjects();
            }
        });

        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewProjectActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        projectViewModel.getAllProjects();
        projectViewModel.getProjects().observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projectsFromModel) {
                if (!projectsFromModel.isEmpty()){
                    Log.i(TAG, "got data from model ");
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