package com.example.collab.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.collab.databinding.FragmentAboutBinding;
import com.example.collab.shared.Helper;
import com.example.collab.models.User;
import com.parse.ParseUser;

public class AboutFragment extends Fragment {

    private UserViewModel userViewModel;
    private static final String TAG = "AboutFragment";
    private FragmentAboutBinding binding;

    public AboutFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userViewModel.currentUser.observe(getViewLifecycleOwner(), new Observer<ParseUser>() {
            @Override
            public void onChanged(ParseUser parseUser) {
                if (parseUser != null) {
                    bind(parseUser);
                }
            }
        });
    }

    private void bind(ParseUser user) {
        binding.tvSchool.setText(user.getString(User.KEY_SCHOOL));
        binding.tvSkillsList.setText(Helper.listToString(user.<String>getList(User.KEY_SKILLS)));
    }
}
