package com.denunciaty.denunciaty;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.services.network.HttpRequest;

public class AddReporteActivity extends AppCompatActivity implements NavigationDrawerCallbacks {
    Button boton;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reporte);
        tv = (TextView) findViewById(R.id.textView6);
        boton = (Button) findViewById(R.id.bt_publicar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CrearRegistroTask().execute();
            }
        });

        //set up the drawer
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        /*populate the navigation drawer
        mnavigationDrawerFragment.setUserData("nombre", "correo", BitmapFactory.decodeResource(getResources()), R.drawable.avatar);*/

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    private class CrearRegistroTask extends AsyncTask<String, Void, String> {
        /*
        EJEMPLO DE LA CREACIÓN DE UN REGISTRO
         */
        @Override
        protected String doInBackground(String... params) {
            InputStream iS = null;
            String data = "";
            try {
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/reporte/nuevo/Reporte1/Descripcion/1/Fuentelidiota/2/5/0").openConnection();
                //con.setReadTimeout(10000);
                //con.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Basic "+encoded);
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




            /*HttpURLConnection conn = null;
            String request        = "http://denunc699:28WdV4Xq@denunciaty.florida.com.mialias.net/api/reporte/nuevo/Repote senyal dañada/Ta to roto bto/1/Almussafes/1/5/senyal";
            URL    url            = null;
            try {
                url = new URL(request);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("charset", "UTF-8");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        */
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("code",""+s);
        }

        }

    }
