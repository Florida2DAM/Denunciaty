package com.denunciaty.denunciaty;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.denunciaty.denunciaty.JavaClasses.AdaptadorSpinner;
import com.denunciaty.denunciaty.JavaClasses.Reporte;

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

import io.fabric.sdk.android.services.network.HttpRequest;

public class MisReportesActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    ListView lV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reportes);
        lV = (ListView) findViewById(R.id.listView);

        //navigation drawer
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);


        //set up the drawer
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        /*populate the navigationdrawer
        mnavigationDrawerFragment.setUserData("nombre", "correo", BitmapFactory.decodeResource(getResources()), R.drawable.avatar);*/

        new CargarMisReportesTask().execute();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    /**
     * PARSEO DE REPORTES DE UN USUARIO ESPECÍFICO: LOS REPORTES DE EJEMPLO SON DEL USUARIO_ID 1.
     */
    private class CargarMisReportesTask extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... params) {
            InputStream iS = null;
            String data = "";
            Integer numID_usuario=0;
            try {
                numID_usuario=13;
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/usuario/reps/1").openConnection();
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
            ArrayList<Reporte> reportes = parseaJSON(s);
            lV.setAdapter(new AdaptadorSpinner(getApplicationContext(), R.layout.lista, reportes) {
                @Override
                public void onEntrada(Object entrada, View view) {
                    if (entrada != null) {
                        ImageView iV = (ImageView) view.findViewById(R.id.iV);
                        if (iV != null) {
                            iV.setImageResource(((Reporte) entrada).getImagen());
                        }
                        TextView titulo = (TextView) view.findViewById(R.id.tituloTV);
                        if (titulo != null) {
                            titulo.setText(((Reporte) entrada).getTitulo());
                        }
                        TextView descripcion = (TextView) view.findViewById(R.id.descripcionTV);
                        if (descripcion != null) {
                            descripcion.setText(((Reporte) entrada).getDescripcion());
                        }
                        TextView ubicacion = (TextView) view.findViewById(R.id.ubiTV);
                        if (ubicacion != null) {
                            ubicacion.setText(((Reporte) entrada).getUbicacion());
                        }
                    }
                }
            });
        }
    }

    public ArrayList<Reporte> parseaJSON(String s){
        ArrayList<Reporte>reportes= new ArrayList<Reporte>();
        try {
            JSONArray json = new JSONArray(s);
            for(int i=0;i<json.length();i++){
                JSONObject e = json.getJSONObject(i);
                Integer id = e.getInt("id");

                String titulo = e.getString("titulo");
                String descripcion = e.getString("descripcion");
                String ubicacion = e.getString("ubicacion");
                Integer tipo_id = e.getInt("tipo_id");
                String tipo = tipo_id.toString();
                Integer sol_id = e.getInt("solucionado");
                Boolean solucionado =false;
                if(sol_id==0){
                    solucionado=false;
                }
                if(sol_id==1){
                    solucionado=true;
                }
                Log.d("Reporte",titulo+"-"+descripcion+"-"+ubicacion+"-"+tipo_id+"-"+sol_id+"-"+solucionado);
                Reporte rep = new Reporte(id,R.mipmap.ic_launcher,titulo,descripcion,ubicacion,tipo,solucionado);
                reportes.add(rep);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reportes;
    }
}