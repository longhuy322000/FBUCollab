package com.example.collab.main.home;

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

        binding.progressBar.setVisibility(View.VISIBLE);
        projectViewModel.getAllProjects();
        projectViewModel.getProjects().observe(getViewLifecycleOwner(), new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projectsFromModel) {
                if (!projectsFromModel.isEmpty()){
                    Log.i(TAG, "got data from model ");
                    clearProjects();
                    addAllProjects(projectsFromModel);
                    binding.swipeContainer.setRefreshing(false);
                    binding.progressBar.setVisibility(View.GONE);
                }
                else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    clearProjects();
                }
            }
        });
    }

//    private void testLiveQuery() {
//        Log.i(TAG," yoo");
//        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
//        ParseQuery<Project> query = ParseQuery.getQuery(Project.class);
//// Add query constraints here
//        parseLiveQueryClient.subscribe(query).handleEvents(new SubscriptionHandling.HandleEventsCallback<Project>() {
//            @Override
//            public void onEvents(ParseQuery<Project> query, final SubscriptionHandling.Event event, final Project parseObjectSubclass) {
//                Handler refresh = new Handler(Looper.getMainLooper());
//                refresh.post(new Runnable() {
//                    public void run() {
//                        if (event == SubscriptionHandling.Event.CREATE) {
//                            Log.i(TAG, "create");
//                            // Handle new parseObjectSubclass here
//                        } else if (event == SubscriptionHandling.Event.DELETE) {
//                            Log.i(TAG, "delete");
//                        }
//                        else if (event == SubscriptionHandling.Event.UPDATE) {
//                            Log.i(TAG, "update");
//                        }
//                    }
//                });
//            }
//        });
//    }
}