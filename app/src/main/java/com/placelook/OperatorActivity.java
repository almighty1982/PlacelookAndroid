package com.placelook;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.placelook.fragments.operator.BaseFragment;
import com.placelook.fragments.operator.CameraFragment;
import com.placelook.fragments.operator.PrevOperatorFragment;

import java.util.ArrayList;
import java.util.List;

public class OperatorActivity extends BaseActivity implements PrevOperatorFragment.PrevListener, CameraFragment.OnCameraListener {
    private static final String TAG = OperatorActivity.class.getSimpleName();
    private final int REQ_CAMERA = 1000;
    private ViewPager pager;
    private CameraFragment camFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator);
        Log.i(TAG, "Stream ID: " + getID());

        pager = (ViewPager) findViewById(R.id.vpOperator);
        pager.setAdapter(new OperatorPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) camFrag.startCamera();
                else camFrag.stopCameras();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onExitTour() {
    }

    @Override
    public void onStartTour() {
        pager.setCurrentItem(1);
    }

    @Override
    public void onInit() {
    }

    private class OperatorPagerAdapter extends FragmentPagerAdapter {
        private List<BaseFragment> list;

        public OperatorPagerAdapter(FragmentManager fm) {
            super(fm);
            list = new ArrayList<BaseFragment>();
            list.add(PrevOperatorFragment.newInstance());
            camFrag = CameraFragment.newInstance();
            list.add(camFrag);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        public BaseFragment getFragment(int position) {
            if (position <= list.size() - 1) return list.get(position);
            else throw new Resources.NotFoundException("Fragment not found");
        }
    }
}
