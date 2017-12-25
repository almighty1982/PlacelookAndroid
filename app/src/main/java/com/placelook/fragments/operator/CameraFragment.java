package com.placelook.fragments.operator;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.placelook.R;
import com.placelook.camera.CameraHelper;

public class CameraFragment extends BaseFragment {

    private static final String TAG = CameraFragment.class.getSimpleName();
    private CameraHelper helper;
    private String[] idsCameras;
    private CameraManager manager;
    private TextureView txMain;
    private OnCameraListener listener;

    public CameraFragment() {
    }

    public static CameraFragment newInstance() {
        CameraFragment fragment = new CameraFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle data) {
        txMain = (TextureView) view.findViewById(R.id.txMain);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCameraListener) {
            listener = (OnCameraListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void initCameras() {
        try {
            idsCameras = manager.getCameraIdList();
            for (String id : idsCameras) {
                Log.i(TAG, "Camera ID: " + id);
                CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(id);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) != CameraCharacteristics.LENS_FACING_FRONT) {
                    helper = new CameraHelper(manager, id);
                    helper.setTextureView(txMain);
                    //helper.setOnFrameListener(onFrame);
                    Log.i(TAG, "Back camera: " + id);
                    return;
                }
            }
            helper = null;
        } catch (CameraAccessException e) {
            idsCameras = null;
            helper = null;
            Log.i(TAG, "Camera not found");
            e.printStackTrace();
        }
    }

    public void startCamera() {
        new CameraStarter().execute();
    }

    public void stopCameras() {
        if (helper != null && helper.isOpened()) {
            helper.close();
            helper = null;
        }
    }


    public interface OnCameraListener {
        void onInit();
    }

    private class CameraStarter extends AsyncTask<Void, Void, CameraHelper> {
        @Override
        protected CameraHelper doInBackground(Void... voids) {
            initCameras();
            return helper;
        }

        @Override
        protected void onPostExecute(CameraHelper h) {
            if (helper != null) {
                helper.open(640, 480);
            } else {
                Log.e(TAG, "Camera helper not initialized");
            }
        }
    }
}
