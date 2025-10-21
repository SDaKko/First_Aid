package com.example.first_aid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ActionsFragment extends Fragment {

    private Button btnLogin;
    private TextView tvRegisterLink;

    public ActionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actions, container, false);
        initViews(view);
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        btnLogin = view.findViewById(R.id.btnLogin);
        tvRegisterLink = view.findViewById(R.id.tvRegisterLink);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> onLoginClicked());
        tvRegisterLink.setOnClickListener(v -> onRegisterClicked());
    }

    private void onLoginClicked() {
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).onLoginButtonClicked();
        }
    }

    private void onRegisterClicked() {
        if (getActivity() instanceof LoginActivity) {
            ((LoginActivity) getActivity()).onRegisterLinkClicked();
        }
    }
}