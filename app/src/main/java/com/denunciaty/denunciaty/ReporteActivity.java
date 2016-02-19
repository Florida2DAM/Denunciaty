package com.denunciaty.denunciaty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class ReporteActivity extends AppCompatActivity {

    Toolbar tB;
    View cardView;
    TextView descripcionTV,ubicacionTV,tipoTV,textDenunciado;
    FloatingActionButton shareButton;
    String descripcion, ubicacion, tipo,titulo,usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        shareButton = (FloatingActionButton)findViewById(R.id.shareButton);
        tB = (Toolbar)findViewById(R.id.main_toolbar);
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
            usuario = b.getString("usuario");

            textDenunciado.setText(usuario);
            descripcionTV.setText(descripcion);
            ubicacionTV.setText(ubicacion);
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

    public void onBackPressed(){
        finish();
    }
}
