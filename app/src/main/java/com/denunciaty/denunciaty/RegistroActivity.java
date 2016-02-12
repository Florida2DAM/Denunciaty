package com.denunciaty.denunciaty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denunciaty.denunciaty.JavaClasses.SQLite;
import com.denunciaty.denunciaty.JavaClasses.Usuario;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import io.fabric.sdk.android.services.network.HttpRequest;


public class RegistroActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    TwitterLoginButton twitterLogIn;
    SignInButton googleLogIn;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private int tw_sign_in = 0;
    TextView app;
    Button iniciar;
    Usuario usuario;
    String passInput = null;
    String passBBDD = null;
    String idPlus;
    String urlImagen;
    private SQLite bbdd;
    String imagenCodificada;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        bbdd = new SQLite(getApplicationContext());
        bbdd.open();

        if(bbdd.recuperarLogueado().equals("true")){
            Intent i = new Intent(getApplicationContext(), PrincipalActivity.class);
            startActivity(i);
            finish();
        }

        twitterLogIn = (TwitterLoginButton) findViewById(R.id.twitterLogIn);
        app = (TextView) findViewById(R.id.app);
        iniciar = (Button) findViewById(R.id.iniciar);

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleLogIn = (SignInButton) findViewById(R.id.googleLogIn);
        googleLogIn.setSize(SignInButton.SIZE_STANDARD);
        googleLogIn.setScopes(gso.getScopeArray());


        //Twitter
        twitterLogIn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()

                TwitterSession session = result.data;
                // Remove toast and use the TwitterSession's userID
                // with your app's user model
                TwitterSession twitterSession = result.data;
                Log.d("twitter",""+twitterSession.getUserId()+"-"+twitterSession.getUserName()+"-"+twitterSession.getId()+"-"+twitterSession.getAuthToken());

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        twitterLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tw_sign_in = 1;
            }
        });

        googleLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regitroApp();
            }
        });

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
    }

    public void regitroApp() {
        Intent i = new Intent(getApplicationContext(), RegistroAppActivity.class);
        startActivity(i);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (tw_sign_in == 1) {
            twitterLogIn.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Fallo", "onConnectionFailed:" + connectionResult);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("Conectado", "Conectado");
            idPlus = acct.getId();
            String personName = acct.getDisplayName();
            urlImagen = acct.getPhotoUrl().toString();
            String email = acct.getEmail();
            //new DescargaImagenTask().execute();
            Log.d("DATA", personName + "-" + email + "-" + urlImagen + "-" + idPlus);

            RegistroRRSS registroRRSS = new RegistroRRSS();
            //Consulta por email
            registroRRSS.execute(personName,email,urlImagen);
        }
    }

    /*
    public void signOutGoogle(){

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {

            }
        });

    }*/

    protected void showInputDialog() {
        LayoutInflater layout = LayoutInflater.from(RegistroActivity.this);
        View view = layout.inflate(R.layout.login, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegistroActivity.this);
        alertDialog.setView(view);

        final EditText email = (EditText) view.findViewById(R.id.email);
        final EditText pass = (EditText) view.findViewById(R.id.pass);

        alertDialog.setCancelable(true)
                .setPositiveButton(R.string.iniciasesion_registroapp, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String emailInput = email.getText().toString();
                        String contrasenya = pass.getText().toString();

                        if (emailInput.isEmpty() || contrasenya.isEmpty()) {
                            Toast.makeText(RegistroActivity.this, "Hay campos vacíos", Toast.LENGTH_SHORT).show();
                        } else {
                            //Encriptacion pass
                            try {
                                passInput = SHA1(pass.getText().toString());
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            UsuariosTask async = new UsuariosTask();
                            //Consulta por email
                            async.execute(emailInput);
                        }


                    }
                })
                .setNegativeButton(R.string.cancelar_registroapp, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(RegistroActivity.this, R.string.cancelando_registroapp, Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }


    public Boolean compruebaContraseña(String passBBDD, String passInput) {
        if (passBBDD.equals(passInput)) {
            return true;
        } else {
            return false;
        }
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

    @Override
    public void onConnected(Bundle arg0) {
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    //AsyncTask
    private class UsuariosTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            InputStream iS = null;
            String data = "";

            try {
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/usuario/datos/0/" + email).openConnection();
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
                iS.close();

                return data;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (iS != null) {
                    try {
                        iS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject e = new JSONObject(s);

                String id = e.getString("id");
                String nombre = e.getString("nombre");
                String apellidos = e.getString("apellidos");
                String nombre_usuario = e.getString("nombre_usuario");
                String emailUser = e.getString("email");
                String password = e.getString("password");
                String foto = e.getString("foto");
                String ingreso = e.getString("ingreso_at");
                String localidad = e.getString("localidad");

                usuario = new Usuario(id, nombre, apellidos, nombre_usuario, emailUser, password, foto, ingreso, localidad);

                Log.d("Usuario", nombre + "-" + apellidos + "-" + nombre_usuario + "-" + emailUser + "-" + password + "-" + foto + "-"
                        + ingreso + "-" + localidad + "-" + id);

                //Comprobamos pass
                passBBDD = usuario.getPassword();

                if (compruebaContraseña(passBBDD, passInput)) {

                    bbdd.usuario(id, nombre, apellidos, nombre_usuario, emailUser, password, foto, ingreso, localidad);

                    bbdd.logueado("true");

                    Intent i = new Intent(getApplicationContext(), PrincipalActivity.class);
                    //i.putExtra("usuario", usuario);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(RegistroActivity.this, "La dirección de correo y contraseña no coinciden", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DescargaImagenTask extends AsyncTask<Void, Void, Bitmap> {
        Bitmap imagenPerfil;
        @Override
        protected Bitmap doInBackground(Void... params) {
            InputStream iS = null;
            try {
                URL url = new URL(urlImagen);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();

                iS = con.getInputStream();

                imagenPerfil = BitmapFactory.decodeStream(iS);

                return imagenPerfil;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
        protected void onPostExecute(Bitmap imagenPerfil) {
            super.onPostExecute(imagenPerfil);
            imagenCodificada = convertirBase64(imagenPerfil);
            Log.d("IMAGEN", "Imagen Codificada: " + imagenCodificada);
        }
    }

    public String convertirBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray,Base64.DEFAULT);
        return encoded;
    }



    private class RegistroRRSS extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String nombre = params[0];
            String email = params[1];
            String foto = params[2];

            InputStream iS = null;
            String data = "";

            try {
                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL(
                        "http://denunciaty.florida.com.mialias.net/api/usuario/nuevo/"+nombre+"///"+email+"//"+foto+"/0/").openConnection();
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
                iS.close();

                return data;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (iS != null) {
                    try {
                        iS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Intent i = new Intent(getApplicationContext(), PrincipalActivity.class);
            startActivity(i);
            finish();
        }

    }

}
