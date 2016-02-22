package com.denunciaty.denunciaty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.services.network.HttpRequest;

public class RegistroAppActivity extends Activity {

    CircleImageView imagen;
    EditText usuario, nombre, apellidos, email, localidad, contraseña, repite_contraseña;
    Button enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_app);

        imagen = (CircleImageView) findViewById(R.id.iv_avatar);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layout = LayoutInflater.from(RegistroAppActivity.this);
                View view = layout.inflate(R.layout.elegir_foto, null);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegistroAppActivity.this);
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
                                Environment.getExternalStorageDirectory(), "DenuncityProfile");
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

        //EditText con datos del usuario
        nombre = (EditText) findViewById(R.id.et_nombre);
        apellidos = (EditText) findViewById(R.id.et_apellidos);
        localidad = (EditText) findViewById(R.id.et_localidad);
        email = (EditText) findViewById(R.id.et_email);
        usuario = (EditText) findViewById(R.id.et_usuario);
        contraseña = (EditText) findViewById(R.id.et_contraseña);
        repite_contraseña = (EditText) findViewById(R.id.et_contraseñaRepetida);


        //Botón para enviar
        enviar = (Button) findViewById(R.id.bt_submit);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datosLlenos();
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
                            "/DenunciatyPics/" + "perfil.jpg");
            //Añadimos el bitmap al imageView para
            //mostrarlo por pantalla
            imagen.setImageBitmap(bMap);
        }
    }

    //Comprueba que tenga todos los datos llenos
    protected void datosLlenos() {
        String nom = nombre.getText().toString();
        String apell = apellidos.getText().toString();
        String loc = localidad.getText().toString();
        String correo = email.getText().toString();
        String usu = usuario.getText().toString();
        String contra = null;
        String repitcontra = null;
        try {
            contra = SHA1(contraseña.getText().toString());
            repitcontra = SHA1(repite_contraseña.getText().toString());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (nom.isEmpty() || apell.isEmpty() || loc.isEmpty() || correo.isEmpty() || usu.isEmpty() || contra.isEmpty() || repitcontra.isEmpty()) {
            Toast.makeText(RegistroAppActivity.this, "Hay campos vacíos", Toast.LENGTH_SHORT).show();
        } else {
            if (contra.equals(repitcontra)) {
                //Ejecuta
                UsuariosTask async = new UsuariosTask();
                //Consulta por email
                async.execute(nom, apell, loc, correo, usu, contra);
            }else{
                Toast.makeText(RegistroAppActivity.this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void registroCorrecto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroAppActivity.this);
        builder.setMessage(R.string.registro_correctamente)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(RegistroAppActivity.this, R.string.registro_correcto, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), RegistroActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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


    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
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

    //Conexión con webservice
    private class UsuariosTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            InputStream iS = null;
            String data = "";

            try {
                String nombre = URLEncoder.encode(params[0],"UTF-8");
                String apellidos = URLEncoder.encode(params[1],"UTF-8");
                String usuario = URLEncoder.encode(params[4],"UTF-8");
                String email = URLEncoder.encode(params[3],"UTF-8");
                String contraseña = params[5];
                String localidad = URLEncoder.encode(params[2],"UTF-8");

                Log.d("Datos",nombre+"-"+apellidos+"-"+usuario+"-"+email+"-"+contraseña+"-"+localidad);


                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        "http://denunciaty.florida.com.mialias.net/api/usuario/nuevo/"+nombre+"/"+apellidos+"/"+usuario+"/"+email+"/"+contraseña+"/0/0/"+localidad).openConnection();
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
                        return data;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            registroCorrecto();
        }

    }
}

