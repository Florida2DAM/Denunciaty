package com.denunciaty.denunciaty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.denunciaty.denunciaty.JavaClasses.SQLite;
import com.denunciaty.denunciaty.JavaClasses.Usuario;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.services.network.HttpRequest;

public class FragmentAddReporte extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    FloatingActionButton fb;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    //TextView mLatitudeText, mLongitudeText;
    ImageView img_camara, img_cruz;
    RelativeLayout relativeLayout;
    TextView et_titulo, et_descripcion;
    Spinner sp_tipo;
    Button bt_publicar;
    SQLite bbdd;
    Usuario usuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bbdd = new SQLite(getActivity().getApplicationContext());
        bbdd.open();
        usuario = bbdd.recuperarUsuario();

    }


    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_add_reporte,
                container, false);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        onStart();
        //mLatitudeText = (TextView) view.findViewById(R.id.mLatitudeText);
        //mLongitudeText = (TextView) view.findViewById(R.id.mLongitudeText);

        fb = (FloatingActionButton)getActivity().findViewById(R.id.fab);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
        relativeLayout.setVisibility(View.VISIBLE);

        et_titulo = (TextView) view.findViewById(R.id.et_titulo);

        et_descripcion = (TextView) view.findViewById(R.id.et_descripcion);




        //Datos a Mostrar
        //NO CAMBIAR ORDEN
        List<ElementoSpinner> items = new ArrayList<ElementoSpinner>(15);
        items.add(new ElementoSpinner(getString(R.string.limpieza), R.drawable.icono_limpieza));
        items.add(new ElementoSpinner(getString(R.string.senyales), R.drawable.icono_senyalizacion));
        items.add(new ElementoSpinner(getString(R.string.vehiculo), R.drawable.icono_vehiculo));
        items.add(new ElementoSpinner(getString(R.string.alumnbrado), R.drawable.icono_iluminacion));
        items.add(new ElementoSpinner(getString(R.string.mobiliario), R.drawable.icono_mobiliario));
        items.add(new ElementoSpinner(getString(R.string.viaPublica), R.drawable.icono_via_publica));
        items.add(new ElementoSpinner(getString(R.string.arbolado), R.drawable.icono_arbolada));
        items.add(new ElementoSpinner(getString(R.string.transporteP), R.drawable.icono_transporte_publico));
        items.add(new ElementoSpinner(getString(R.string.otros), R.drawable.icono_otros));





        sp_tipo = (Spinner) view.findViewById(R.id.sp_tipo);
        //Creamos el adaptador
        //ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.tipo_reporte, android.R.layout.simple_spinner_item);
        //Añadimos el layout para el menú
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Le indicamos al spinner el adaptador a usar

        sp_tipo.setAdapter(new SpinnerAdapter(getActivity().getApplicationContext(), items));





        img_camara = (ImageView) view.findViewById(R.id.img_camara);
        img_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //Creamos una carpeta en la memeria del terminal
                File imagesFolder = new File(
                        Environment.getExternalStorageDirectory(), "DenunciatyPics");
                imagesFolder.mkdirs();
                //añadimos el nombre de la imagen
                File image = new File(imagesFolder, et_titulo.getText().toString()+".jpg");
                Uri uriSavedImage = Uri.fromFile(image);
                //Le decimos al Intent que queremos grabar la imagen
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
                //Lanzamos la aplicacion de la camara con retorno (forResult)
                startActivityForResult(cameraIntent, 1);
            }
        });

        //CERRAR EL FRAGMENT
        img_cruz = (ImageView) view.findViewById(R.id.img_cruz);
        img_cruz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarFragment();
            }
        });


        bt_publicar = (Button) view.findViewById(R.id.bt_publicar);
        bt_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_titulo.getText().toString().equals("") || et_descripcion.getText().toString().equals("")){
                    Toast.makeText(getActivity().getApplicationContext(), R.string.toast_falta_texto, Toast.LENGTH_SHORT).show();
                }else{
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                            mGoogleApiClient);

                    //recogemos los datos introducidos
                    String titulo = et_titulo.getText().toString();
                    String descripcion = et_descripcion.getText().toString();
                    String tipo = String.valueOf(sp_tipo.getSelectedItemPosition());
                    Double latitud = mLastLocation.getLatitude();
                    Double longitud = mLastLocation.getLongitude();
                    String ubicacion = ubicacion(latitud,longitud);

                    new CrearReporteTask().execute(titulo,descripcion,tipo,ubicacion,latitud.toString(),longitud.toString());
                }
            }
        });


        return view;
    }

    public void borrarFragment(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this).commit();
        fb.setVisibility(View.VISIBLE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //Creamos un bitmap con la imagen recientemente
            //almacenada en la memoria
            Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory() +
                            "/DenunciatyPics/" +et_titulo.getText()+".jpg");
            //Añadimos el bitmap al imageView para
            //mostrarlo por pantalla
            img_camara.setImageBitmap(bMap);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        /*if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Log.d("COOR", String.valueOf(mLastLocation.getLatitude() + " " + mLastLocation.getLongitude()));
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public String ubicacion(double latitud,double longitud){
        Geocoder geocoder = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
        List<Address> ubicacionCompleta;
        String calle = null;

        try {
            ubicacionCompleta = geocoder.getFromLocation(latitud,longitud,1);
            calle = ubicacionCompleta.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return calle;
    }



    private class CrearReporteTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            InputStream iS = null;
            String data = "";

            try {

                String titulo = URLEncoder.encode(params[0], "UTF-8");
                String descripcion = URLEncoder.encode(params[1], "UTF-8");
                String tipo = params[2];
                Double latitud = Double.parseDouble(params[4]);
                Double longitud = Double.parseDouble(params[5]);
                String ubicacion = URLEncoder.encode(params[3], "UTF-8");


                String encoded = HttpRequest.Base64.encode("denunc699" + ":" + "28WdV4Xq");
                HttpURLConnection connection = (HttpURLConnection) new URL("http://denunciaty.florida.com.mialias.net/api/reporte/nuevo/" + titulo + "/" + descripcion + "/"+ubicacion+"/"+longitud+"/"+latitud+"/"+tipo+"/"+usuario.getId()+"/"+0).openConnection();
                //con.setReadTimeout(10000);
                //con.setConnectTimeout(15000);
                Log.d("URL",""+connection);
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
                Log.d("Error","Error al insertar reporte");
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
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("true")){
                Intent i = new Intent(getActivity().getApplicationContext(),PrincipalActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        }





    }
}
