package com.denunciaty.denunciaty;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class AddReporteActivity extends AppCompatActivity implements NavigationDrawerCallbacks{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reporte);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        //set up the drawer
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        /*populate the navigation drawer
        mnavigationDrawerFragment.setUserData("nombre", "correo", BitmapFactory.decodeResource(getResources()), R.drawable.avatar);*/

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}