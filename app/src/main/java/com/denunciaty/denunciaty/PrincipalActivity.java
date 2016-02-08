package com.denunciaty.denunciaty;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

import io.fabric.sdk.android.services.network.HttpRequest;

public class PrincipalActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    private SQLite bbdd;
    FloatingActionButton fB;
    Toolbar tbReporte;
    ImageView iVReporte;
    TextView tVReporte,tVReporteUbi;
    LinearLayout lY;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng valencia = new LatLng(39.4699075, -0.3762881000000107);
    ArrayList<Reporte> reportes;
    Usuario usuario=null;
    String id_selec;
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

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        //Recupero al usuario logueado
        bbdd = new SQLite(getApplicationContext());
        bbdd.open();
        usuario = bbdd.recuperarUsuario();
        Toast.makeText(PrincipalActivity.this, usuario.getNombre(), Toast.LENGTH_SHORT).show();
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
            }
        });
        comprobarClicks();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        comprobarPreferencias();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
                        haspMap.put(m.getId(),new String[]{imagen, String.valueOf(reporte.getId())});
                        break;
                    case "1":
                        imagen ="senyalizacion";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.senyalizacion)));
                        haspMap.put(m.getId(),new String[]{imagen,String.valueOf(reporte.getId())});
                        break;
                    case "2":
                        imagen ="vehiculo";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehiculo)));
                        haspMap.put(m.getId(),new String[]{imagen,String.valueOf(reporte.getId())});
                        break;
                    case "3":
                        imagen ="iluminacion";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.iluminacion)));
                        haspMap.put(m.getId(),new String[]{imagen,String.valueOf(reporte.getId())});
                        break;
                    case "4":
                        imagen ="mobiliario";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mobiliario)));
                        haspMap.put(m.getId(),new String[]{imagen,String.valueOf(reporte.getId())});
                        break;
                    case "5":
                        imagen ="via_publica";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.via_publica)));
                        haspMap.put(m.getId(),new String[]{imagen,String.valueOf(reporte.getId())});
                        break;
                    case "6":
                        imagen ="arbolada";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arbolada)));
                        haspMap.put(m.getId(),new String[]{imagen,String.valueOf(reporte.getId())});
                        break;
                    case "7":
                        imagen ="transporte_publico";
                        m=mMap.addMarker(new MarkerOptions().position(posicion).title(reporte.getTitulo()).snippet(reporte.getUbicacion())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.transporte_publico)));
                        haspMap.put(m.getId(),new String[]{imagen,String.valueOf(reporte.getId())});
                        break;
                }
            }
        }
    }

    private class CargarReporteSeleccionado extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... id) {
            InputStream iS = null;
            String data = "";
            Integer idReporteSeleccionado=0;
            try {
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/reporte/datos/"+idReporteSeleccionado).openConnection();
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

                return data;
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
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            reportes = parseaJSON(s);
        }
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

    public void notification(String titulo,String contenido){

        Notification.Builder n = new Notification.Builder(this);
        n.setContentTitle(titulo);
        n.setContentText(contenido);
        n.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        n.setSmallIcon(R.mipmap.ic_launcher);
        n.setDefaults(Notification.DEFAULT_VIBRATE);
        n.setDefaults(Notification.DEFAULT_SOUND) ;

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,n.build());
    }

    public void comprobarPreferencias(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref!=null){
            if(pref.getBoolean("notificaciones",true)){
                notification("NOTIFICACION","Bienvenido a nuestra app, en este mapa puedes podras añadir incidentes cercanos a tu localizacion");
            }
        }
    }
}
