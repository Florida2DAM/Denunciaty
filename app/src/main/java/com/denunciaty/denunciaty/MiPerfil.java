package com.denunciaty.denunciaty;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.services.network.HttpRequest;

public class MiPerfil extends AppCompatActivity implements NavigationDrawerCallbacks {

    private SQLite bbdd;
    Usuario usuario=null;
    EditText usu, nombre, apellido, email, localidad;
    CircleImageView foto;
    Button guardar, cambiar_contraseña;
    String passInput_Antigua = null;
    String passInput_Nueva = null;
    String passInput_Repetida = null;
    String antigua,nueva;


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
        usu.setText(usuario.getNombre_usuario());

        nombre = (EditText) findViewById(R.id.et_nombre);
        nombre.setEnabled(false);
        nombre.setText(usuario.getNombre());

        apellido = (EditText) findViewById(R.id.et_apellidos);
        apellido.setEnabled(false);
        apellido.setText(usuario.getApellidos());

        email = (EditText) findViewById(R.id.et_email);
        email.setEnabled(false);
        email.setText(usuario.getEmail());

        localidad = (EditText) findViewById(R.id.et_localidad);
        localidad.setEnabled(false);
        localidad.setText(usuario.getLocalidad());

        foto = (CircleImageView) findViewById(R.id.iv_avatar);
        foto.setEnabled(false);
        //foto.setText(usuario.getFoto());
        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layout = LayoutInflater.from(MiPerfil.this);
                View view = layout.inflate(R.layout.elegir_foto, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MiPerfil.this);
                alertDialog.setView(view);

                final Button hazfoto = (Button) view.findViewById(R.id.bt_hacerfoto);
                hazfoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //imagen
                        Intent cameraIntent = new Intent(
                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        //Creamos una carpeta en la memeria del terminal
                        File imagesFolder = new File(
                                Environment.getExternalStorageDirectory(), "DenunciatyProfile");
                        imagesFolder.mkdirs();
                        //añadimos el nombre de la imagen
                        File image = new File(imagesFolder, "perfil.jpg");
                        Uri uriSavedImage = Uri.fromFile(image);
                        //Le decimos al Intent que queremos grabar la imagen
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                        //Lanzamos la aplicacion de la camara con retorno (forResult)
                        startActivityForResult(cameraIntent, 1);
                    }
                });

                final Button eligefoto = (Button) view.findViewById(R.id.bt_seleccionafoto);
                eligefoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                AlertDialog alert = alertDialog.create();
                alert.show();
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
                new editarUsuario().execute(nombre.getText().toString(),apellido.getText().toString(),localidad.getText().toString(),email.getText().toString(),usu.getText().toString());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //Creamos un bitmap con la imagen recientemente
            //almacenada en la memoria
            Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory() +
                            "/DenunciatyProfile/" + "perfil.jpg");
            //Añadimos el bitmap al imageView para
            //mostrarlo por pantalla
            foto.setImageBitmap(bMap);
        }
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
                        antigua = passAntigua.getText().toString();
                        nueva = passNueva.getText().toString();
                        String nuevaRepetida = passNuevaRepetida.getText().toString();

                        if (antigua.isEmpty() || nueva.isEmpty() || nuevaRepetida.isEmpty()) {
                            Toast.makeText(MiPerfil.this, R.string.camposVacios, Toast.LENGTH_SHORT).show();
                        } else {
                            //Encriptacion pass
                            try {
                                passInput_Antigua = SHA1(passAntigua.getText().toString());
                                Log.d("Antigua", passInput_Antigua);
                                Log.d("BBDD", usuario.getPassword());
                                passInput_Nueva = SHA1(passNueva.getText().toString());
                                passInput_Repetida = SHA1(passNuevaRepetida.getText().toString());

                                if (usuario.getPassword().equals(passInput_Antigua)) {
                                    if (nueva.equals(nuevaRepetida)) {
                                        new cambiarContrasenya().execute();
                                    }else {
                                        Toast.makeText(MiPerfil.this, "Contraseñas incorrectas", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MiPerfil.this, "Contraseñas incorrectas", Toast.LENGTH_SHORT).show();
                                }


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


    public class cambiarContrasenya extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            InputStream iS = null;
            String data = "";

            try {

                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        "http://denunciaty.florida.com.mialias.net/api/usuario/cambiarPass/"+usuario.getId()+"/"+antigua+"/"+nueva).openConnection();
                Log.d("URL",""+connection);
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

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error","No se ha registrado");
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bbdd.resetUsuario();
            bbdd.usuario(usuario.getId(), usuario.getNombre(), usuario.getApellidos(), usuario.getNombre_usuario(), usuario.getEmail(), passInput_Nueva, usuario.getFoto(), usuario.getIngreso(), usuario.getLocalidad());
            Toast.makeText(getApplicationContext(), "Contraseña cambiada", Toast.LENGTH_SHORT).show();
        }
    }

    public class editarUsuario extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... params) {
            InputStream iS = null;
            String data = "";

            try {
                String nombre = URLEncoder.encode(params[0], "UTF-8");
                String apellidos = URLEncoder.encode(params[1],"UTF-8");
                String usu = URLEncoder.encode(params[4],"UTF-8");
                String email = URLEncoder.encode(params[3],"UTF-8");
                String localidad = URLEncoder.encode(params[2],"UTF-8");

                usuario.setNombre(params[0]);
                usuario.setApellidos(params[1]);
                usuario.setNombre_usuario(params[4]);
                usuario.setEmail(params[3]);
                usuario.setLocalidad(params[2]);


                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        "http://denunciaty.florida.com.mialias.net/api/usuario/editar/"+usuario.getId()+"?nombre="+nombre+"&apellidos="+apellidos+"&nombre_usuario="+usu+"&email="+email+"&localidad="+localidad).openConnection();
                Log.d("URL",""+connection);
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

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Error","No se ha registrado");
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bbdd.resetUsuario();
            bbdd.usuario(usuario.getId(), usuario.getNombre(), usuario.getApellidos(), usuario.getNombre_usuario(), usuario.getEmail(), usuario.getPassword(), usuario.getFoto(), usuario.getIngreso(), usuario.getLocalidad());

            Intent i = new Intent(getApplicationContext(),MiPerfil.class);
            startActivity(i);
            finish();
        }
    }

}
