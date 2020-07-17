package com.example.collab.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.FragmentApplyDialogBinding;
import com.example.collab.models.Project;
import com.example.collab.models.User;

public class ApplyDialogFragment extends DialogFragment {

    FragmentApplyDialogBinding binding;

    public interface ApplyDialogListenter {
        void onFinishDialog(String text);
    }

    public ApplyDialogFragment() {

    }

    public static ApplyDialogFragment newInstance(String fullName, String projectName, String url) {
        ApplyDialogFragment frag = new ApplyDialogFragment();
        Bundle args = new Bundle();
        args.putString(User.KEY_FULL_NAME, fullName);
        args.putString(User.KEY_IMAGE, url);
        args.putString(Project.KEY_PROJECT_NAME, projectName);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentApplyDialogBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch arguments from bundle and set title
        String projectName = getArguments().getString(Project.KEY_PROJECT_NAME, "Project Name");
        String fullName = getArguments().getString(User.KEY_FULL_NAME, "Full Name");
        String imageUrl = getArguments().getString(User.KEY_IMAGE, "no image");
        getDialog().setTitle("Apply");
        Glide.with(getContext())
                .load(imageUrl)
                .into(binding.ivUserImage);
        binding.tvUsername.setText(fullName);
        binding.tvWhyLabel.setText("Why are you interested in being part of " + projectName);
        // Show soft keyboard automatically and request focus to field
        binding.etDescription.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplyDialogListenter listenter = (ApplyDialogListenter) getActivity();
                listenter.onFinishDialog("Successfully applied");
                dismiss();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
