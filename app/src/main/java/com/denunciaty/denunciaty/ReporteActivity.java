package com.denunciaty.denunciaty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ReporteActivity extends AppCompatActivity {

    Toolbar tB;
    View cardView;
    TextView descripcionTV,ubicacionTV,tipoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        tB = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(tB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardView = (Card)findViewById(R.id.card_lyt);
        descripcionTV = (TextView) cardView.findViewById(R.id.descripcionTV);
        ubicacionTV = (TextView) cardView.findViewById(R.id.ubicacionTV);
        tipoTV = (TextView) cardView.findViewById(R.id.tipoTV);

        Bundle b = getIntent().getExtras();
        if(b!=null){
            descripcionTV.setText(b.getString("descripcionIntent"));
            ubicacionTV.setText(b.getString("ubicacionIntent"));
            tipoTV.setText(b.getString("tipoIntent"));
            getSupportActionBar().setTitle(b.getString("tituloIntent"));
        }

        tB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void onBackPressed(){
        Intent setIntent = new Intent(getApplicationContext(),MisReportesActivity.class);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        finish();
    }
}
