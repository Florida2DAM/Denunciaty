package com.denunciaty.denunciaty.JavaClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class AdaptadorSpinner extends BaseAdapter {
    private Context context;
    private int idLayout;
    private ArrayList<?> entradas;

    public AdaptadorSpinner (Context context, int idLayout, ArrayList<?> entradas) {
        super();
        this.context = context;
        this.entradas = entradas;
        this.idLayout = idLayout;
    }

    @Override

    public int getCount() {
        // TODO Auto-generated method stub
        return entradas.size();
    }

    @Override
    //torna l'Element position de l'arrayList
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return entradas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            //Accedim al servei que ens permet unflar Layouts
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Unflem el layout
            convertView = vi.inflate(idLayout, null);
        }

        onEntrada (entradas.get(position), convertView); //cridem al mètode abstracte que definirem més avant
        return convertView;
    }

    // Mètode abstracte
    public abstract void onEntrada (Object entrada, View view);

}

