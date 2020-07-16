package com.example.collab.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.collab.databinding.ActivityCommentsBinding;

public class CommentsActivity extends AppCompatActivity {

    ActivityCommentsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}