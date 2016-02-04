package com.denunciaty.denunciaty;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import java.util.List;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        registrarPrefsInicio();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        registrarPrefs(key);
    }

    private void registrarPrefs(String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Preference connectionPref = findPreference(key);
        switch (key){
            case "cambiarIdioma":
                connectionPref.setSummary(pref.getString(key, ""));
                break;
            case "notificaciones":
                Boolean a = pref.getBoolean(key, false);
                connectionPref.setSummary(a.toString());
                break;
            case "accederWifi":
                Boolean b = pref.getBoolean(key, false);
                connectionPref.setSummary(b.toString());
                break;
        }
    }

    private void registrarPrefsInicio(){

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Preference connectionPref = findPreference("cambiarIdioma");
        connectionPref.setSummary(pref.getString("cambiarIdioma", ""));
        connectionPref = findPreference("notificaciones");
        Boolean a =pref.getBoolean("notificaciones", false);
        connectionPref.setSummary(a.toString());
        connectionPref = findPreference("accederWifi");
        Boolean b =pref.getBoolean("accederWifi", false);
        connectionPref.setSummary(b.toString());
    }

}
