package com.example.first_aid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;

public class InputFieldsFragment extends Fragment {

    private TextInputEditText etLogin, etPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_fields, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        etLogin = view.findViewById(R.id.etLogin);
        etPassword = view.findViewById(R.id.etPassword);
    }

    public String getLogin() {
        return etLogin != null ? etLogin.getText().toString().trim() : "";
    }

    public String getPassword() {
        return etPassword != null ? etPassword.getText().toString().trim() : "";
    }

    public void setLogin(String login) {
        if (etLogin != null) {
            etLogin.setText(login);
        }
    }

    public void setPassword(String password) {
        if (etPassword != null) {
            etPassword.setText(password);
        }
    }

}