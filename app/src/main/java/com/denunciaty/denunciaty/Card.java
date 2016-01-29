package com.denunciaty.denunciaty;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Card extends RelativeLayout{
    TextView descripcionTV,ubicacionTV,tipoTV;
    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.card_layout, this, true);

        descripcionTV = (TextView) findViewById(R.id.descripcionTV);
        ubicacionTV = (TextView) findViewById(R.id.ubicacionTV);
        tipoTV = (TextView) findViewById(R.id.tipoTV);
    }

    public void setTextos(String descripcion,String ubicacion,String tipo){
        descripcionTV.setText(descripcion);
        ubicacionTV.setText(ubicacion);
        tipoTV.setText(tipo);
    }
}
