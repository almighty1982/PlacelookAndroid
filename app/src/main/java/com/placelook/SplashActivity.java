package com.placelook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.placelook.fragments.splash.NewUserFragment;

public class SplashActivity extends AppCompatActivity implements NewUserFragment.OnSplashListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportFragmentManager().beginTransaction().replace(R.id.frContainet, NewUserFragment.newInstance()).commit();
    }

    @Override
    public void onPlacelookLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
