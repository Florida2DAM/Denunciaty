package com.denunciaty.denunciaty;

import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class AddReporte2 extends AppCompatActivity {

    FloatingActionButton bt_localizacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reporte2);
        bt_localizacion = (FloatingActionButton) findViewById(R.id.bt_localizacion);

        bt_localizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                //Si el GPS no está habilitado
                if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(getApplicationContext(),"Necesitas tener el GPS activado", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"GPS Activado", Toast.LENGTH_SHORT).show();

                    //acciones para obtener ubicacion

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reporte, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

