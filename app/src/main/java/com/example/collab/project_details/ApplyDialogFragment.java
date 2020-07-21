package com.example.collab.project_details;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.FragmentApplyDialogBinding;
import com.example.collab.models.Notification;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ApplyDialogFragment extends DialogFragment {

    private static final String TAG = "ApplyDialogFragment";

    FragmentApplyDialogBinding binding;

    public interface ApplyDialogListenter {
        void onFinishDialog(String text, Boolean done);
    }

    public ApplyDialogFragment() {

    }

    public static ApplyDialogFragment newInstance(Project project, ParseUser user) {
        ApplyDialogFragment frag = new ApplyDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(Project.class.getName(), project);
        args.putParcelable(User.class.getName(), user);
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
        final Project project = getArguments().getParcelable(Project.class.getName());
        final ParseUser user = getArguments().getParcelable(User.class.getName());
        getDialog().setTitle("Apply");
        Glide.with(getContext())
                .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                .into(binding.ivUserImage);
        binding.tvUsername.setText(user.getString(User.KEY_FULL_NAME));
        binding.tvWhyLabel.setText("Why are you interested in being part of " + project.getProjectName() + "?");
        // Show soft keyboard automatically and request focus to field
        binding.etDescription.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSubmit.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);

                final Request request = new Request();
                request.setDescription(binding.etDescription.getText().toString());
                request.setProject(project);
                request.setRequestedUser(user);
                request.setStatus(Request.KEY_PENDING_STATUS);
                request.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        final ApplyDialogListenter listener = (ApplyDialogListenter) getActivity();
                        if (e != null) {
                            Log.e(TAG, "Issues with saving request", e);
                            listener.onFinishDialog("Issues with creating request", false);
                            dismiss();
                            return;
                        }
                        Notification notification = new Notification();
                        notification.setDeliverTo(project.getOwner());
                        notification.setRequest(request);
                        notification.setType(Notification.KEY_NEED_OWNER_CONFIRM);
                        notification.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                            if (e != null){
                                Log.e(TAG, "Issues with saving notification", e);
                                return;
                            }
                            listener.onFinishDialog("Successfully submitted the request",true);
                            binding.progressBar.setVisibility(View.GONE);
                            dismiss();
                            }
                        });
                    }
                });
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
