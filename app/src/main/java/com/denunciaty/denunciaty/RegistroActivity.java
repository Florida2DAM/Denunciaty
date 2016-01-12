package com.denunciaty.denunciaty;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class RegistroActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    TwitterLoginButton twitterLogIn;
    SignInButton googleLogIn;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private int tw_sign_in = 0;
    TextView app;
    Button iniciar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

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
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                tw_sign_in = 0;
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

    public void regitroApp(){
        Intent i = new Intent(getApplicationContext(),RegistroAppActivity.class);
        startActivity(i);
    }

    private void signIn(){
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

        if(tw_sign_in == 1){
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
            Log.d("Conectado","Conectado");
        }

    }

    protected void showInputDialog(){
        LayoutInflater layout = LayoutInflater.from(RegistroActivity.this);
        View view = layout.inflate(R.layout.login, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegistroActivity.this);
        alertDialog.setView(view);

        final EditText email = (EditText) view.findViewById(R.id.email);
        final EditText contra = (EditText) view.findViewById(R.id.pass);

        alertDialog.setCancelable(false)
                .setPositiveButton("Iniciar Sesión", new DialogInterface.OnClickListener(){
                  public void onClick(DialogInterface dialog, int id){
                      Toast.makeText(RegistroActivity.this, "Iniciando", Toast.LENGTH_SHORT).show();
                      Intent i = new Intent(getApplicationContext(),PrincipalActivity.class);
                      startActivity(i);
                  }
                })
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        Toast.makeText(RegistroActivity.this, "Cancelando", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.show();
    }


}
