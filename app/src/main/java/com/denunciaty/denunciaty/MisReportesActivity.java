package com.denunciaty.denunciaty;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.denunciaty.denunciaty.JavaClasses.AdaptadorSpinner;
import com.denunciaty.denunciaty.JavaClasses.Reporte;

import java.util.ArrayList;

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



        //Pruebas
        final ArrayList<Reporte> reportes= new ArrayList<Reporte>();
        reportes.add(new Reporte(0,R.mipmap.ic_launcher,"Señal dañada","Una señal dañada por el viento","C/Maestro Serrano","Daño del moviliario",false));
        reportes.add(new Reporte(1,R.mipmap.ic_launcher,"Señal dañada","Una señal dañada por el viento","C/Maestro Serrano","Daño del moviliario",false));
        lV.setAdapter(new AdaptadorSpinner(this,R.layout.lista,reportes) {
            @Override
            public void onEntrada(Object entrada, View view) {
                if(entrada != null){
                    ImageView iV = (ImageView) view.findViewById(R.id.iV);
                    if(iV !=null){
                        iV.setImageResource(((Reporte) entrada).getImagen());
                    }
                    TextView titulo = (TextView) view.findViewById(R.id.tituloTV);
                    if(titulo !=null){
                        titulo.setText(((Reporte) entrada).getTitulo());
                    }
                    TextView descripcion = (TextView) view.findViewById(R.id.descripcionTV);
                    if(descripcion !=null){
                        descripcion.setText(((Reporte) entrada).getDescripcion());
                    }
                    TextView ubicacion = (TextView) view.findViewById(R.id.ubiTV);
                    if(ubicacion!=null){
                        ubicacion.setText(((Reporte)entrada).getUbicacion());
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mis_reportes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
