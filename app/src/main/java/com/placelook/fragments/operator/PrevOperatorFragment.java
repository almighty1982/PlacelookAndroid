package com.placelook.fragments.operator;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.placelook.BaseActivity;
import com.placelook.R;

import java.io.IOException;
import java.util.List;

public class PrevOperatorFragment extends BaseFragment implements BaseActivity.OnLocation, BaseActivity.OnNetworkState {
    private static final String TAG = PrevOperatorFragment.class.getSimpleName();
    private PrevListener listener;
    private boolean isGPSOn = false;
    private ImageView ivLocation;
    private TextView tvLocation;
    private AppCompatEditText edTourName;
    private ImageView ivBlackLocation;
    private ImageView ivBlack3G;
    private ImageView ivBlackWiFi;
    private TextView tvStart;
    private RelativeLayout rlStart;
    private String country;
    private String city;

    private boolean edTourEnable = false;
    private boolean gpsTourEnable = false;

    public PrevOperatorFragment() {

    }

    public static PrevOperatorFragment newInstance() {
        PrevOperatorFragment fragment = new PrevOperatorFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        edTourEnable = false;
        gpsTourEnable = false;
        country = "";
        city = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prev_operator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle data) {
        super.onViewCreated(view, data);
        activity.setOnLocationListener(this);
        activity.setNetworkStateListener(this);
        ivLocation = view.findViewById(R.id.ivLocation);
        tvLocation = view.findViewById(R.id.tvTextLocation);
        edTourName = view.findViewById(R.id.edTourName);
        edTourName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 3) {
                    edTourEnable = true;
                    rlStart.setEnabled(edTourEnable && gpsTourEnable);
                    tvStart.setEnabled(edTourEnable && gpsTourEnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivBlackLocation = view.findViewById(R.id.ivBlackLocation);
        ivBlack3G = view.findViewById(R.id.ivBlack3G);
        ivBlackWiFi = view.findViewById(R.id.ivBlackWiFi);
        tvStart = view.findViewById(R.id.tvStart);
        tvStart.setEnabled(false);
        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Log.i(TAG, "Start tour");
                    listener.onStartTour();
                }
            }
        });
        rlStart = view.findViewById(R.id.rlStart);
        rlStart.setEnabled(false);
        rlStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Log.i(TAG, "Start tour");
                    listener.onStartTour();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PrevListener) {
            listener = (PrevListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement PrevListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onGPSOnOff(boolean onoff) {
        isGPSOn = onoff;
        ivLocation.setEnabled(isGPSOn);
        ivBlackLocation.setEnabled(isGPSOn);
    }

    @Override
    public void onUpdateLocation(Location location) {
        if (isGPSOn) {
            Geocoder geocoder = new Geocoder(activity);
            try {
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String country = list.get(0).getCountryName();
                String city = list.get(0).getLocality();
                tvLocation.setText(country + ", " + city);
                gpsTourEnable = true;
                rlStart.setEnabled(edTourEnable && gpsTourEnable);
                tvStart.setEnabled(edTourEnable && gpsTourEnable);
            } catch (IOException e) {
                country = "";
                city = "";
                tvLocation.setText(R.string.pleaseTurnOnGPS);
                e.printStackTrace();
            }
        } else {
            tvLocation.setText(R.string.pleaseTurnOnGPS);
        }
    }

    @Override
    public void onWifiStateChange(boolean OnOff) {
        ivBlackWiFi.setEnabled(OnOff);
    }

    @Override
    public void on3GStateChange(boolean OnOff) {
        ivBlack3G.setEnabled(OnOff);
    }

    public interface PrevListener {
        void onStartTour();

        void onExitTour();
    }
}
