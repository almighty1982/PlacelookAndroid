package com.placelook.fragments.splash;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.placelook.R;

public class LoginFragment extends Fragment {

    private boolean isSignUp;
    private OnLoginListener listener;

    public LoginFragment() {
    }

    public static LoginFragment newInstance(boolean isSignUp) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putBoolean("isSignUp", isSignUp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.isSignUp = getArguments().getBoolean("isSignUp");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle data) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginListener) {
            listener = (OnLoginListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnLoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnLoginListener {
        void onLogin();

        void onLogout();
    }
}
