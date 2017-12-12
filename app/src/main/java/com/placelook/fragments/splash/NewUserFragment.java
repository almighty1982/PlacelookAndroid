package com.placelook.fragments.splash;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.placelook.R;

public class NewUserFragment extends Fragment {
    private OnSplashListener listener;

    public NewUserFragment() {
    }

    public static NewUserFragment newInstance() {
        NewUserFragment fragment = new NewUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_user, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle saved) {
        RelativeLayout rlLogin = (RelativeLayout) view.findViewById(R.id.rlLogIn);
        rlLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPlacelookLogin();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSplashListener) {
            listener = (OnSplashListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSplashListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnSplashListener {
        void onPlacelookLogin();
    }
}
