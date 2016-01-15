package com.denunciaty.denunciaty;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class RegistroAppActivity extends Activity {

    ImageView imagen;
    EditText usuario, nombre, apellidos, email;
    Button añadir, submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_app);

        //Imagen del usuario
        imagen = (ImageView) findViewById(R.id.iv_avatar);

        //EditText con datos del usuario
        usuario = (EditText) findViewById(R.id.et_usuario);
        nombre = (EditText) findViewById(R.id.et_nombre);
        apellidos = (EditText) findViewById(R.id.et_apellidos);
        email = (EditText) findViewById(R.id.et_email);

        //Botón para añadir usuario
        añadir = (Button) findViewById(R.id.bt_añadir);

        //Botón para entrar
        submit = (Button) findViewById(R.id.bt_submit);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro_app, menu);
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
}