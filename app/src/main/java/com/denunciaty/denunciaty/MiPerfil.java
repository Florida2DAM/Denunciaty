package com.denunciaty.denunciaty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.denunciaty.denunciaty.JavaClasses.SQLite;
import com.denunciaty.denunciaty.JavaClasses.Usuario;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MiPerfil extends AppCompatActivity implements NavigationDrawerCallbacks {

    private SQLite bbdd;
    Usuario usuario=null;
    EditText usu, nombre, apellido, email, localidad;
    ImageView foto;
    Button guardar, cambiar_contraseña;
    String passInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);

        //navigation drawer
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        getSupportActionBar().setElevation(0);

        //set up the drawer
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        //Recupero al usuario logueado
        bbdd = new SQLite(getApplicationContext());
        bbdd.open();
        usuario = bbdd.recuperarUsuario();

        usu = (EditText) findViewById(R.id.et_usuario);
        usu.setEnabled(false);
        usu.setText(usuario.getNombre());

        nombre = (EditText) findViewById(R.id.et_nombre);
        nombre.setEnabled(false);
        nombre.setText(usuario.getApellidos());

        apellido = (EditText) findViewById(R.id.et_apellidos);
        apellido.setEnabled(false);
        apellido.setText(usuario.getApellidos());

        email = (EditText) findViewById(R.id.et_email);
        email.setEnabled(false);
        email.setText(usuario.getEmail());

        localidad = (EditText) findViewById(R.id.et_localidad);
        localidad.setEnabled(false);
        localidad.setText(usuario.getLocalidad());

        foto = (ImageView) findViewById(R.id.iv_avatar);
        foto.setEnabled(false);
        //foto.setText(usuario.getFoto());
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogFoto();
            }
        });

        guardar = (Button) findViewById(R.id.bt_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiar_contraseña.setVisibility(View.INVISIBLE);
                guardar.setVisibility(View.INVISIBLE);
                usu.setEnabled(false);
                nombre.setEnabled(false);
                apellido.setEnabled(false);
                email.setEnabled(false);
                localidad.setEnabled(false);
                foto.setEnabled(false);
            }
        });

        cambiar_contraseña = (Button) findViewById(R.id.bt_cambia_password);
        cambiar_contraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
    }

    protected void showInputDialogFoto(){
        LayoutInflater layout = LayoutInflater.from(MiPerfil.this);
        View view = layout.inflate(R.layout.elegir_foto, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MiPerfil.this);
        alertDialog.setView(view);

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    protected void showInputDialog(){
        LayoutInflater layout = LayoutInflater.from(MiPerfil.this);
        View view = layout.inflate(R.layout.contrasenya, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MiPerfil.this);
        alertDialog.setView(view);

        final EditText passAntigua = (EditText) view.findViewById(R.id.et_passAntigua);
        final EditText passNueva = (EditText) view.findViewById(R.id.et_passNueva);
        final EditText passNuevaRepetida = (EditText) view.findViewById(R.id.et_passNuevaRepite);

        alertDialog.setCancelable(true)
                .setPositiveButton(R.string.guardar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String antigua = passAntigua.getText().toString();
                        String nueva = passNueva.getText().toString();
                        String nuevaRepetida = passNuevaRepetida.getText().toString();

                        if (antigua.isEmpty() || nueva.isEmpty() || nuevaRepetida.isEmpty()) {
                            Toast.makeText(MiPerfil.this, R.string.camposVacios, Toast.LENGTH_SHORT).show();
                        } else {
                            //Encriptacion pass
                            try {
                                passInput = SHA1(passAntigua.getText().toString());
                                passInput = SHA1(passNueva.getText().toString());
                                passInput = SHA1(passNuevaRepetida.getText().toString());
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }

                    }
                })
                .setNegativeButton(R.string.cancelar_registroapp, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MiPerfil.this, R.string.cancelando_registroapp, Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    //Encriptación
    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                }
                else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mi_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.edita:
                Log.d("Boton", "Edita");
                cambiar_contraseña.setVisibility(View.VISIBLE);
                guardar.setVisibility(View.VISIBLE);
                usu.setEnabled(true);
                nombre.setEnabled(true);
                apellido.setEnabled(true);
                email.setEnabled(true);
                localidad.setEnabled(true);
                foto.setEnabled(true);
        }
        return super.onOptionsItemSelected(item);
    }

}
