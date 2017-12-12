package com.placelook.fragments.operator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.placelook.BaseActivity;

public abstract class BaseFragment extends Fragment {
    protected BaseActivity activity;

    @Override
    public void onViewCreated(View view, Bundle data) {
        activity = (BaseActivity) getActivity();
    }
}

