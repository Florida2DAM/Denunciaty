package com.denunciaty.denunciaty;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

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
        comprobarPreferencias();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.unregisterOnSharedPreferenceChangeListener(this);
        comprobarPreferencias();
    }


    public static void changeLocale(Resources res,String locale){
        Configuration config;
        config = new Configuration(res.getConfiguration());

        switch (locale) {
            case "ca":
                config.locale = new Locale("ca");
                break;
            case "en":
                config.locale = new Locale("en");
                break;
            default:
                config.locale = new Locale("");
                break;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public void comprobarPreferencias(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref!=null){
            switch(pref.getString("cambiarIdioma","")){
                case "ingles":
                    changeLocale(getResources(),"en");
                    //Toast.makeText(getApplicationContext(), "Has cambiado el idioma a ingles", Toast.LENGTH_SHORT).show();
                    break;
                case "valenciano":
                    changeLocale(getResources(),"ca");
                    //Toast.makeText(getApplicationContext(),"Has cambiado el idioma a valenciano",Toast.LENGTH_SHORT).show();
                    break;
                case "castellano":
                    changeLocale(getResources(),"");
                    //Toast.makeText(getApplicationContext(),"Has cambiado el idioma a español",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        registrarPrefs(key);
    }

    private void registrarPrefs(String key){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Preference connectionPref = findPreference(key);
        switch (key){
            case "notificaciones":
                Boolean a = pref.getBoolean(key, false);
                connectionPref.setSummary("¿Quieres recibir notificaciones?");
                break;
            case "cambiarIdioma":
                connectionPref.setSummary(pref.getString(key, ""));
                break;
            case "accederWifi":
                Boolean b = pref.getBoolean(key, false);
                connectionPref.setSummary("Acceder a la aplicacion solo con wifi");
                break;
            case "cambiarVista":
                connectionPref.setSummary(pref.getString(key, ""));
                break;
        }
    }

    private void registrarPrefsInicio(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        Preference connectionPref = findPreference("cambiarIdioma");
        connectionPref.setSummary(pref.getString("cambiarIdioma", ""));

        connectionPref = findPreference("notificaciones");
        Boolean a =pref.getBoolean("notificaciones", false);
        connectionPref.setSummary("¿Quieres recibir notificaciones?");

        connectionPref = findPreference("accederWifi");
        Boolean b =pref.getBoolean("accederWifi", false);
        connectionPref.setSummary("Acceder a la aplicacion solo con wifi");

        connectionPref = findPreference("cambiarVista");
        connectionPref.setSummary(pref.getString("cambiarVista", ""));

    }


}
