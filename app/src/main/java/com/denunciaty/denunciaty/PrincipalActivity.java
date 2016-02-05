package com.denunciaty.denunciaty;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.denunciaty.denunciaty.JavaClasses.Reporte;
import com.denunciaty.denunciaty.JavaClasses.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.services.network.HttpRequest;

public class PrincipalActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    FloatingActionButton fB;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng valencia = new LatLng(39.4699075, -0.3762881000000107);
    ArrayList<Reporte> reportes;
    Usuario usuario;
    private CameraPosition posicionCamara  = new CameraPosition.Builder().target(valencia)
            .zoom(12.5f)
            .bearing(0)
            .tilt(0)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        //Recupero al usuario logueado
        usuario = (Usuario)getIntent().getExtras().getSerializable("usuario");


        //set up the drawer
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        setUpMapIfNeeded();

        fB = (FloatingActionButton) findViewById(R.id.fab);
        fB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAddReporte fragmentAddReporte = new FragmentAddReporte();
                FragmentManager fragmentManager = getSupportFragmentManager();
                //empieza transaccion
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.framelayout, fragmentAddReporte);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(posicionCamara));
        new CargarMarcadoresMapa().execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    private class CargarMarcadoresMapa extends AsyncTask<Void,Void,List<Reporte>>{

        @Override
        protected List<Reporte> doInBackground(Void... params) {
            InputStream iS = null;
            String data = "";
            try {
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/reporte/").openConnection();
                //con.setReadTimeout(10000);
                //con.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Basic " + encoded);
                connection.setDoInput(true);
                connection.connect();

                iS = new BufferedInputStream(connection.getInputStream());
                connection.getResponseCode();
                if (iS != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iS));
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null)
                        data += line;
                }
                iS.close();
                reportes = parseaJSON(data);
                return reportes;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (iS != null) {
                    try {
                        iS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Reporte> rep) {
            super.onPostExecute(rep);
            for (Reporte reporte: rep) {
                LatLng posicion = new LatLng(reporte.getLatitud(),reporte.getLongitud());
                mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion()));
            }
        }
    }
    public ArrayList<Reporte> parseaJSON(String s){
        ArrayList<Reporte>reportes= new ArrayList<Reporte>();
        try {
            JSONArray json = new JSONArray(s);
            for(int i=0;i < json.length();i++) {
                JSONObject e = json.getJSONObject(i);
                Integer id = e.getInt("id");

                String titulo = e.getString("titulo");
                String descripcion = e.getString("descripcion");
                String ubicacion = e.getString("ubicacion");
                Integer tipo_id = e.getInt("tipo_id");
                String tipo = tipo_id.toString();
                Integer sol_id = e.getInt("solucionado");
                Boolean solucionado = false;
                Double latitud = e.getDouble("latitud");
                Double longitud = e.getDouble("longitud");
                Integer usuario_id = e.getInt("usuario_id");

                if (sol_id == 0) {
                    solucionado = false;
                }
                if (sol_id == 1) {
                    solucionado = true;
                }
                Log.d("Reporte", titulo + "-" + descripcion + "-" + ubicacion + "-" + tipo_id + "-" + sol_id + "-" + solucionado);
                Reporte rep = new Reporte(id, R.mipmap.ic_launcher, titulo, descripcion, ubicacion, tipo, solucionado, latitud, longitud, usuario_id);
                reportes.add(rep);
                return reportes;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reportes;
    }
}
