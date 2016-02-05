package com.denunciaty.denunciaty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MiPerfil extends AppCompatActivity {

    EditText usuario, nombre, apellido, email, localidad;
    ImageView foto;
    ImageButton edita;
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


        //set up the drawer
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);


        usuario = (EditText) findViewById(R.id.et_usuario);
        usuario.setEnabled(false);
        nombre = (EditText) findViewById(R.id.et_nombre);
        nombre.setEnabled(false);
        apellido = (EditText) findViewById(R.id.et_apellidos);
        apellido.setEnabled(false);
        email = (EditText) findViewById(R.id.et_email);
        email.setEnabled(false);
        localidad = (EditText) findViewById(R.id.et_localidad);
        localidad.setEnabled(false);
        foto = (ImageView) findViewById(R.id.iv_avatar);
        foto.setEnabled(false);

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogFoto();
            }
        });

        edita = (ImageButton) findViewById(R.id.ib_edit);
        edita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiar_contraseña.setVisibility(View.VISIBLE);
                guardar.setVisibility(View.VISIBLE);
                usuario.setEnabled(true);
                nombre.setEnabled(true);
                apellido.setEnabled(true);
                email.setEnabled(true);
                localidad.setEnabled(true);
                foto.setEnabled(true);

            }
        });

        guardar = (Button) findViewById(R.id.bt_guardar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiar_contraseña.setVisibility(View.INVISIBLE);
                guardar.setVisibility(View.INVISIBLE);
                usuario.setEnabled(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mi_perfil, menu);
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

    protected void showInputDialogFoto(){
        LayoutInflater layout = LayoutInflater.from(MiPerfil.this);
        View view = layout.inflate(R.layout.elegir_foto, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MiPerfil.this);
        alertDialog.setView(view);

        final Button hazFoto = (Button) view.findViewById(R.id.bt_hacerFoto);
        final Button eligeFoto = (Button) view.findViewById(R.id.bt_eligeFoto);

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
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String antigua = passAntigua.getText().toString();
                        String nueva = passNueva.getText().toString();
                        String nuevaRepetida = passNuevaRepetida.getText().toString();

                        if (antigua.isEmpty() || nueva.isEmpty() || nuevaRepetida.isEmpty()) {
                            Toast.makeText(MiPerfil.this, "Hay campos vacíos", Toast.LENGTH_SHORT).show();
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
}
