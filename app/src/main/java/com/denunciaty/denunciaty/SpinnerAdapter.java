package com.denunciaty.denunciaty;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class SpinnerAdapter extends ArrayAdapter<ElementoSpinner> {
    private Context context;

    List<ElementoSpinner> datos = null;

    public SpinnerAdapter(Context context, List<ElementoSpinner> datos) {
        //se debe indicar el layout para el item que seleccionado (el que se muestra sobre el botón del botón)
        super(context, R.layout.spinner_selected_item, datos);
        this.context = context;
        this.datos = datos;
    }

    //este método establece el elemento seleccionado sobre el botón del spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.spinner_selected_item, null);
        }
        ((TextView) convertView.findViewById(R.id.texto)).setText(datos.get(position).getNombre());
        ((ImageView) convertView.findViewById(R.id.icono)).setBackgroundResource(datos.get(position).getIcono());

        return convertView;
    }

    //gestiona la lista usando el View Holder Pattern. Equivale a la típica implementación del getView
    //de un Adapter de un ListView ordinario
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.spinner_list_item, parent, false);
        }

        if (row.getTag() == null) {
            SocialNetworkHolder redSocialHolder = new SocialNetworkHolder();
            redSocialHolder.setIcono((ImageView) row.findViewById(R.id.icono));
            redSocialHolder.setTextView((TextView) row.findViewById(R.id.texto));
            row.setTag(redSocialHolder);
        }

        //rellenamos el layout con los datos de la fila que se está procesando
        ElementoSpinner redSocial = datos.get(position);
        ((SocialNetworkHolder) row.getTag()).getIcono().setImageResource(redSocial.getIcono());
        ((SocialNetworkHolder) row.getTag()).getTextView().setText(redSocial.getNombre());

        return row;
    }

    /**
     * Holder para el Adapter del Spinner*
     */
    private static class SocialNetworkHolder {

        private ImageView icono;

        private TextView textView;

        public ImageView getIcono() {
            return icono;
        }

        public void setIcono(ImageView icono) {
            this.icono = icono;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }

    }
}