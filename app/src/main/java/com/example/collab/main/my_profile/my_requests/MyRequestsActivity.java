package com.example.collab.main.my_profile.my_requests;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.collab.R;
import com.example.collab.databinding.ActivityMyRequestsBinding;
import com.example.collab.models.Request;

import java.util.ArrayList;
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {

    private static final String TAG = "MyRequestsActivity";

    private ActivityMyRequestsBinding binding;
    private RequestsViewModel requestsViewModel;
    private RequestsAdapter adapter;
    private List<Request> requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        requests = new ArrayList<>();
        adapter = new RequestsAdapter(this, requests);
        binding.rvRequests.setAdapter(adapter);
        binding.rvRequests.setLayoutManager(new LinearLayoutManager(this));

        requestsViewModel = new ViewModelProvider(this).get(RequestsViewModel.class);
        requestsViewModel.requests.observe(this, new Observer<List<Request>>() {
            @Override
            public void onChanged(List<Request> requestsFromRepository) {
                Log.i(TAG, "load data from requestsRepository");
                if (requestsFromRepository != null) {
                    if (requestsFromRepository.isEmpty()) {
                        binding.tvNoRequests.setVisibility(View.VISIBLE);
                        return;
                    }

                    binding.tvNoRequests.setVisibility(View.GONE);
                    requests.addAll(requestsFromRepository);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        return super.onSupportNavigateUp();
    }
}