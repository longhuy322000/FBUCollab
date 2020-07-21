package com.example.collab.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collab.activities.NewProjectActivity;
import com.example.collab.adapters.ProjectsAdapter;
import com.example.collab.databinding.FragmentProjectsBinding;
import com.example.collab.models.Like;
import com.example.collab.models.Project;
import com.example.collab.repositories.ProjectsRepository;
import com.example.collab.viewmodels.ProjectsViewModel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    public ProjectsViewModel projectViewModel;
    public FragmentProjectsBinding binding;
    public ProjectsAdapter adapter;
    public List<Project> projects;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectViewModel = new ViewModelProvider(getActivity()).get(ProjectsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProjectsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
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

    public void bind() {
        projects = new ArrayList<>();
        adapter = new ProjectsAdapter(getContext(), projects);
        binding.rvProjects.setAdapter(adapter);
        binding.rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void clearProjects() {
        projects.clear();
        adapter.notifyDataSetChanged();
    }

    public void addAllProjects(List<Project> projectsFromDatabase) {
        projects.addAll(projectsFromDatabase);
        adapter.notifyDataSetChanged();
    }
}