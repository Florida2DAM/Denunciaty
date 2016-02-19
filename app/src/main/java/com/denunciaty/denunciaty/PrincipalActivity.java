package com.denunciaty.denunciaty;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denunciaty.denunciaty.JavaClasses.PuntoAcceso;
import com.denunciaty.denunciaty.JavaClasses.Reporte;
import com.denunciaty.denunciaty.JavaClasses.SQLite;
import com.denunciaty.denunciaty.JavaClasses.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.services.network.HttpRequest;

public class PrincipalActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    private SQLite bbdd;
    private Boolean mostrados=true;
    FloatingActionButton fB;
    Toolbar tbReporte;
    ImageView iVReporte;
    TextView tVReporte,tVReporteUbi;
    LinearLayout lY;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng valencia = new LatLng(39.4699075, -0.376288);
    ArrayList<Reporte> reportes;
    ArrayList<PuntoAcceso> puntosAcceso;
    Usuario usuario=null;
    ArrayList<Marker> limpieza,senyalizacion,vehiculo,via_publica,transporte,iluminacion,mobiliario,arbolado,otros,puntosMarker;
    String id_selec;
    Context wrapper;
    WifiManager administrador_wifi;

    HashMap<String, String[]> haspMap = new HashMap <String, String[]>();
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
        lY = (LinearLayout)findViewById(R.id.rep_selec);
        tbReporte = (Toolbar)findViewById(R.id.rep_tb);
        iVReporte = (ImageView)findViewById(R.id.img_reporte_selec);
        tVReporte = (TextView)findViewById(R.id.tv_reporte_selec);
        tVReporteUbi = (TextView)findViewById(R.id.tv_reporte_ub);
        limpieza = new ArrayList<Marker>();
        senyalizacion = new ArrayList<Marker>();
        vehiculo = new ArrayList<Marker>();
        via_publica = new ArrayList<Marker>();
        transporte = new ArrayList<Marker>();
        iluminacion = new ArrayList<Marker>();
        mobiliario = new ArrayList<Marker>();
        arbolado = new ArrayList<Marker>();
        otros = new ArrayList<Marker>();
        puntosMarker = new ArrayList<Marker>();
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        //Recupero al usuario logueado
        bbdd = new SQLite(getApplicationContext());
        bbdd.open();
        usuario = bbdd.recuperarUsuario();
        Toast.makeText(PrincipalActivity.this, usuario.getId(), Toast.LENGTH_SHORT).show();
        //if(usuario==null) {
          //  usuario = (Usuario) getIntent().getExtras().getSerializable("usuario");
        //}

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
                fB.setVisibility(View.GONE);
            }
        });

        administrador_wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        comprobarClicks();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        comprobarPreferencias();
    }

    @Override
    protected void onPause() {
        super.onPause();
        comprobarPreferencias();
    }

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

    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(posicionCamara));
        new CargarMarcadoresMapa().execute();
        new CargarPuntosAcceso().execute();
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
        protected void onPostExecute(final List<Reporte> rep) {
            super.onPostExecute(rep);
            for (final Reporte reporte: rep) {
                LatLng posicion = new LatLng(reporte.getLatitud(),reporte.getLongitud());
                String tipo = reporte.getTipoIncidente();
                Marker m;
                String imagen;
                switch (tipo) {
                    case "0":
                        imagen ="limpieza";
                                m =mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.limpieza)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        limpieza.add(m);
                        break;
                    case "1":
                        imagen ="senyalizacion";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.senyalizacion)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        senyalizacion.add(m);
                        break;
                    case "2":
                        imagen ="vehiculo";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehiculo)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        vehiculo.add(m);
                        break;
                    case "3":
                        imagen ="iluminacion";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iluminacion)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        iluminacion.add(m);
                        break;
                    case "4":
                        imagen ="mobiliario";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mobiliario)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        mobiliario.add(m);
                        break;
                    case "5":
                        imagen ="via_publica";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.via_publica)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        via_publica.add(m);
                        break;
                    case "6":
                        imagen ="arbolada";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arbolada)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        arbolado.add(m);
                        break;
                    case "7":
                        imagen ="transporte_publico";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.transporte_publico)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        transporte.add(m);
                        break;
                    case "8":
                        imagen ="otros";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.otros)));
                        haspMap.put(m.getId(), new String[]{imagen, String.valueOf(reporte.getId())});
                        otros.add(m);
                        break;
                }
            }
            Toast.makeText(getApplicationContext(), "FIN marcadores", Toast.LENGTH_SHORT).show();
        }
    }

    private class CargarPuntosAcceso extends AsyncTask<Void,Void,List<PuntoAcceso>>{

        @Override
        protected List<PuntoAcceso> doInBackground(Void... params) {
            InputStream iS = null;
            String data = "";
            try {
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/index/puntos").openConnection();
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
                puntosAcceso = parseaPuntosJSON(data);
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
            return puntosAcceso;
        }

        @Override
        protected void onPostExecute(final List<PuntoAcceso> puntos) {
            super.onPostExecute(puntos);
            for (final PuntoAcceso punto: puntos) {
                LatLng posicion = new LatLng(punto.getLatitud(),punto.getLongitud());
                Marker m;
                m =mMap.addMarker(new MarkerOptions().position(posicion).title(punto.getDescripcion())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.wifi)));
                puntosMarker.add(m);
            }
        }
    }

    public ArrayList<PuntoAcceso> parseaPuntosJSON(String s){
        ArrayList<PuntoAcceso>puntos= new ArrayList<PuntoAcceso>();
        try {
            JSONArray json = new JSONArray(s);
            for(int i=0;i < json.length();i++) {
                JSONObject e = json.getJSONObject(i);
                String descripcion = e.getString("descripcion");
                Double latitud = e.getDouble("latitud");
                Double longitud = e.getDouble("longitud");
                PuntoAcceso puntoAcceso = new PuntoAcceso(i,descripcion,latitud,longitud);
                puntos.add(puntoAcceso);
            }
            return puntos;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntos;
    }

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }
    public void comprobarClicks(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String value = "";
                tVReporte.setText(marker.getTitle());
                tVReporteUbi.setText(marker.getSnippet());
                tbReporte.setVisibility(View.VISIBLE);
                for (String key : haspMap.keySet()) {
                    Log.d("Reporte", key + "-" + marker.getId());
                    if (marker.getId().equals(key)) {
                        tbReporte.setVisibility(View.VISIBLE);
                        String[] c = haspMap.get(key);
                        String imagen = c[0];
                        id_selec = c[1];
                        iVReporte.setImageResource(getImageId(getApplication(), imagen));
                        Log.d("Reporte", "Reporte seleccionado: " + id_selec);
                    }
                }
                return true;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                tbReporte.setVisibility(View.INVISIBLE);
            }
        });
        lY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<reportes.size();i++){
                    Reporte rep = reportes.get(i);
                    if(rep.getId()==Integer.parseInt(id_selec.toString())){
                        Intent intent = new Intent(getApplicationContext(),ReporteActivity.class);
                        intent.putExtra("idIntent",rep.getTitulo());
                        intent.putExtra("tituloIntent",rep.getTitulo());
                        intent.putExtra("descripcionIntent",rep.getDescripcion());
                        intent.putExtra("ubicacionIntent",rep.getUbicacion());
                        intent.putExtra("tipoIntent",rep.getTipoIncidente());
                        intent.putExtra("solIntent",rep.isSolucionado());
                        startActivity(intent);
                    }
                }
            }
        });
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
            }
            return reportes;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reportes;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }


    public void comprobarPreferencias(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref!=null){
            switch(pref.getString("cambiarVista","")){
                case "normal":
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    break;
                case "satelite":
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    break;
                case "hibrida":
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
            }
            if(pref.getBoolean("accederWifi",false)){
                administrador_wifi.setWifiEnabled(!administrador_wifi.isWifiEnabled());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.limpieza:
                Log.d("Reporte","Limpieza");
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                break;
            case R.id.senyalizacion:
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Señalizacion");
                break;
            case R.id.vehiculo:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Vehículo");
                break;
            case R.id.iluminacion:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Iluminacion");
                break;
            case R.id.mobiliario:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Mobiliario");
                break;
            case R.id.via_publica:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Vía pública");
                break;
            case R.id.arbolado:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Arbolado");
                break;
            case R.id.transp_publico:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Transporte Público");
                break;
            case R.id.otros:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(false);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Transporte Público");
                break;
            case R.id.todos:
                for(Marker m:senyalizacion){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:limpieza){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:vehiculo){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:mobiliario){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:via_publica){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:arbolado){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:iluminacion){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:transporte){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                for(Marker m:otros){
                    if(m!=null) {
                        m.setVisible(true);
                    }
                }
                Log.d("Reporte","Todos");
                break;
            case R.id.accesos:
                if(!mostrados){
                    for (Marker m:puntosMarker){
                        if (m!=null){
                            m.setVisible(true);
                        }
                    }
                    mostrados=true;
                }
                if (mostrados==true) {
                    for (Marker m : puntosMarker) {
                        if (m != null) {
                            m.setVisible(false);
                        }
                        mostrados = false;
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
