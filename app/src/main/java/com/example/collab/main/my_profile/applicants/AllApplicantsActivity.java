package com.example.collab.main.my_profile.applicants;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.collab.R;
import com.example.collab.databinding.ActivityAllApplicantsBinding;
import com.example.collab.databinding.ActivityMyRequestsBinding;
import com.example.collab.main.my_profile.my_requests.RequestsAdapter;
import com.example.collab.main.my_profile.my_requests.RequestsViewModel;
import com.example.collab.models.Notification;
import com.example.collab.models.Request;

import java.util.ArrayList;
import java.util.List;

public class AllApplicantsActivity extends AppCompatActivity {

    private static final String TAG = "MyRequestsActivity";

    private ActivityAllApplicantsBinding binding;
    private ApplicantsViewModel applicantsViewModel;
    private ApplicantsAdapter adapter;
    private List<Notification> applicants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllApplicantsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // show back button
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        applicants = new ArrayList<>();
        adapter = new ApplicantsAdapter(this, applicants);
        binding.rvRequests.setAdapter(adapter);
        binding.rvRequests.setLayoutManager(new LinearLayoutManager(this));

        applicantsViewModel = new ViewModelProvider(this).get(ApplicantsViewModel.class);
        applicantsViewModel.applicants.observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                if (notifications != null) {
                    if (notifications.isEmpty()) {
                        binding.tvNoApplicants.setVisibility(View.VISIBLE);
                        return;
                    }

                    binding.tvNoApplicants.setVisibility(View.GONE);
                    applicants.clear();
                    applicants.addAll(notifications);
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