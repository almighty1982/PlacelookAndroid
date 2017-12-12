package com.placelook;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.placelook.Constants.ID_NAME;

/**
 * Created by victor on 04.11.17.
 */

public class Settings {
    private Context context;
    private SharedPreferences sh;
    private SharedPreferences.Editor editor;

    public Settings(Context context) {
        this.context = context;
        sh = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String id = null;
        try {
            id = Base64.encodeToString(UUID.randomUUID().toString().getBytes("UTF-8"), Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            id = "noid";
        }
        editor = sh.edit();
        editor.putString(ID_NAME, id);
        editor.commit();
    }

    public String getID() {
        return sh.getString(ID_NAME, "noid");
    }
}
