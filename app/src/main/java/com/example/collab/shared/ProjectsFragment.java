package com.example.collab.shared;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.collab.databinding.FragmentProjectsBinding;
import com.example.collab.main.ProjectsAdapter;
import com.example.collab.main.ProjectsViewModel;
import com.example.collab.models.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    public ProjectsViewModel projectViewModel;
    public FragmentProjectsBinding binding;
    public ProjectsAdapter adapter;
    public List<Project> projects;

    public ProjectsFragment() {
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