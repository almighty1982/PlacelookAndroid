package com.placelook;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.placelook.util.NetworkType;
import com.placelook.util.NetworkUtil;

import java.util.Timer;
import java.util.TimerTask;

public class BaseActivity extends AppCompatActivity {

    public interface OnLocation {
        void onGPSOnOff(boolean onoff);

        void onUpdateLocation(Location location);
    }

    public interface OnNetworkState {
        void onWifiStateChange(boolean OnOff);

        void on3GStateChange(boolean OnOff);
    }


    private static final String TAG = BaseActivity.class.getSimpleName();

    private Location lastLocation = null;
    private boolean isGPS = false;
    private OnLocation location;
    private boolean isWifiEnable;
    private boolean is3GEnable;
    private OnNetworkState networkState;
    private boolean placeReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 1);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!placeReceiverRegistered) {
            IntentFilter netIF = new IntentFilter();
            netIF.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            netIF.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            registerReceiver(receiverNetwork, netIF);
            placeReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (placeReceiverRegistered) {
            unregisterReceiver(receiverNetwork);
            placeReceiverRegistered = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission get location allowed");

                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            new GetLocation().execute();
                        }
                    }, 0, 3000);

                } else {
                    Log.i(TAG, "Permission get location not allowed");
                }
                return;
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    @RequiresPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
    protected void updateLastLocation() {
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();
                            if (location != null) location.onUpdateLocation(lastLocation);
                            isGPS = true;
                            if (location != null) location.onGPSOnOff(true);
                            Log.i(TAG, "My location: (" + lastLocation.getLatitude() + ":" + lastLocation.getLongitude() + ")");
                        } else {
                            if (location != null) location.onGPSOnOff(false);
                            isGPS = false;
                            Log.i(TAG, "GPS is off");
                        }
                    }
                });
    }

    public void showError(int errorCode) {
        Toast.makeText(this, "Error " + String.valueOf(errorCode), Toast.LENGTH_SHORT).show();
    }

    public String getID() {
        return App.getApp().getID();
    }

    public void setOnLocationListener(OnLocation loc) {
        this.location = loc;
    }

    private boolean isWifiEnable() {
        NetworkType nw = NetworkUtil.getConnectivityType(this);
        isWifiEnable = (nw == NetworkType.PREF_WIFI);
        if (networkState != null) networkState.onWifiStateChange(isWifiEnable);
        return isWifiEnable;
    }

    private boolean is3GEnable() {
        NetworkType nw = NetworkUtil.getConnectivityType(this);
        is3GEnable = (nw == NetworkType.PREF_MOBILE);
        if (networkState != null) networkState.on3GStateChange(is3GEnable);
        return is3GEnable;
    }

    public void setNetworkStateListener(OnNetworkState state) {
        this.networkState = state;
    }

    private class GetLocation extends AsyncTask<Void, Void, Void> {

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void... params) {
            updateLastLocation();
            return null;
        }
    }

    private BroadcastReceiver receiverNetwork = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            is3GEnable();
            isWifiEnable();
        }
    };
}
