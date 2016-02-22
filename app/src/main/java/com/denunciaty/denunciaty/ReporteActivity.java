package com.denunciaty.denunciaty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.services.network.HttpRequest;

public class ReporteActivity extends AppCompatActivity {

    Toolbar tB;
    View cardView;
    ImageView img;
    TextView descripcionTV,ubicacionTV,tipoTV,textDenunciado;
    FloatingActionButton shareButton;
    String descripcion, ubicacion, tipo,titulo;
    Integer usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        shareButton = (FloatingActionButton)findViewById(R.id.shareButton);
        tB = (Toolbar)findViewById(R.id.main_toolbar);
        img = (ImageView)findViewById(R.id.main_backdrop);
        setSupportActionBar(tB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardView = (Card)findViewById(R.id.card_lyt);
        descripcionTV = (TextView) cardView.findViewById(R.id.descripcionTV);
        ubicacionTV = (TextView) cardView.findViewById(R.id.ubicacionTV);
        tipoTV = (TextView) cardView.findViewById(R.id.tipoTV);
        textDenunciado = (TextView)cardView.findViewById(R.id.textDenunciado);
        Bundle b = getIntent().getExtras();
        if(b!=null){
            descripcion = b.getString("descripcionIntent");
            ubicacion = b.getString("ubicacionIntent");
            tipo = b.getString("tipoIntent");
            titulo = b.getString("tituloIntent");
            usuario = b.getInt("usuario");
            Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory() +
                            "/DenunciatyPics/" + titulo + ".jpg");
            if (bMap==null) {

            }else{
                img.setImageBitmap(bMap);
            }
            descripcionTV.setText(descripcion);
            ubicacionTV.setText(ubicacion);
            new GetUsuarioNombre().execute(usuario);
            switch (tipo) {
                case "0":
                    tipoTV.setText("Limpieza");
                    break;
                case "1":
                    tipoTV.setText("Señalización");
                    break;
                case "2":
                    tipoTV.setText("Vehículo");
                    break;
                case "3":
                    tipoTV.setText("Iluminación");
                    break;
                case "4":
                    tipoTV.setText("Mobiliario");
                    break;
                case "5":
                    tipoTV.setText("Vía Pública");
                    break;
                case "6":
                    tipoTV.setText("Arbolada");
                    break;
                case "7":
                    tipoTV.setText("Transporte Público");
                    break;
                case "8":
                    tipoTV.setText("Otros");
                    break;
            }
            getSupportActionBar().setTitle(titulo);
        }

        tB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                //Mensaje de texto para compartir con contactos.
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Écha un vistazo este reporte: "+titulo+" se trate de un "+tipo+" en la calle: "+ubicacion+" Descripción: "+descripcion+" Denunciaty");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

    }

    private class GetUsuarioNombre extends AsyncTask<Integer,Void,String>{

        @Override
        protected String doInBackground(Integer... params) {
            InputStream iS = null;
            String data = "";

            try {
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/usuario/datos/"+params[0].toString()).openConnection();
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
                String nombreUsuario = JSONUsuario(data);
                return nombreUsuario;
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textDenunciado.setText(s);
        }
    }

    public String JSONUsuario(String s){
        String nombre_usuario="";
        try {
            JSONObject e = new JSONObject(s);
            nombre_usuario= e.getString("nombre_usuario");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nombre_usuario;
    }

    public void onBackPressed(){
        finish();
    }
}
