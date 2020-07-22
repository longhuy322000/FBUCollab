package com.example.collab.main.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.collab.databinding.FragmentProcessRequestDialogBinding;
import com.example.collab.models.Notification;
import com.example.collab.models.Project;
import com.example.collab.models.Request;
import com.example.collab.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProcessRequestDialogFragment extends DialogFragment {

    private static final String TAG = "ProcessRequestDialog";

    Request request;
    FragmentProcessRequestDialogBinding binding;

    public interface ProcessRequestDialogListener {
        void onFinishDialog(String text, Boolean success);
    }

    public ProcessRequestDialogFragment() {

    }

    public static ProcessRequestDialogFragment newInstance(Request request) {
        ProcessRequestDialogFragment frag = new ProcessRequestDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(Request.class.getName(), request);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProcessRequestDialogBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        request = getArguments().getParcelable(Request.class.getName());
        ParseUser user = request.getRequestedUser();

        Glide.with(getContext())
                .load(user.getParseFile(User.KEY_IMAGE).getUrl())
                .into(binding.ivUserImage);
        binding.tvUsername.setText(user.getString(User.KEY_FULL_NAME));
        binding.tvContent.setText(request.getDescription());

        if (request.getStatus() != Request.KEY_PENDING_STATUS) {
            binding.btnApprove.setVisibility(View.GONE);
            binding.btnDecline.setVisibility(View.GONE);
            binding.btnProcessed.setVisibility(View.VISIBLE);
            if (request.getStatus() == Request.KEY_APPROVED_STATUS) {
                binding.btnProcessed.setText("Approved");
            }
            else if (request.getStatus() == Request.KEY_DECLINED_STATUS) {
                binding.btnProcessed.setText("Declined");
            }
        }

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setStatus(Request.KEY_DECLINED_STATUS);
                saveRequest();
            }
        });

        binding.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setStatus(Request.KEY_APPROVED_STATUS);
                saveRequest();
                final Project project = request.getProject();
                project.setSpots(project.getSpots() + 1);
                project.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with changing spots in project", e);
                            return;
                        }
                    }
                });
            }
        });
    }

    private void saveRequest() {
        request.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                final ProcessRequestDialogListener listener = (ProcessRequestDialogListener) getTargetFragment();
                if (e != null) {
                    Log.e(TAG, "Issues with saving decline status in request", e);
                    listener.onFinishDialog("Unable to process request", false);
                }
                else {
                    listener.onFinishDialog("Successfully processed request", false);
                }

                // Create in notification
                Notification notification = new Notification();
                notification.setRequest(request);
                notification.setDeliverTo(request.getRequestedUser());
                notification.setType(Notification.KEY_APPLICANT_RECEIVE_RESULT);
                notification.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issues with creating new notification", e);
                            return;
                        }
                        dismiss();
                    }
                });
            }
        });
    }
}
