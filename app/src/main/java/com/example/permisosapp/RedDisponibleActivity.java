package com.example.permisosapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.example.ejemploscftic.R;

//IMPORTANTE ANIADIR LOS PERMISOS DE RED <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//LISTA COMPLETA DE PERMISOS https://stackoverflow.com/a/36937109/4067559 PELIGROSOS Y NO
public class RedDisponibleActivity extends AppCompatActivity {


    //metodo copiado de https://stackoverflow.com/a/34741193/4067559

    public static boolean isWifiAvailable (Context context)
    {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_WIFI));

        return br;
    }


    public static boolean hayConexion3G4G (Context context)
    {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_MOBILE));

        return br;
    }

    public static boolean hayInternet(Context context) {

        boolean hay_internet = false;

        ConnectivityManager con_manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = con_manager.getActiveNetworkInfo();

        if (ni!=null)
        {

            hay_internet = ni.isAvailable()&&ni.isConnected();
        }

        return hay_internet;

    }


    public void comprobar (View v)
    {
        finish();
        startActivity(new Intent(this, RedDisponibleActivity.class));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_disponible);


        RadioButton rbci = findViewById(R.id.tienes_internet);
        RadioButton rbsi = findViewById(R.id.no_tienes_internet);


        boolean hayred = hayInternet(this);
        rbci.setChecked(hayred);
        rbsi.setChecked(!hayred);

        if (hayred)
        {
            RadioButton rbwifi = findViewById(R.id.con_wifi);
            RadioButton rbmovil = findViewById(R.id.por_red_movil);
            RadioButton rbo = findViewById(R.id.por_otra);

            boolean redwifi = isWifiAvailable(this);
            boolean redmovil = hayConexion3G4G(this);

            rbwifi.setChecked(redwifi);
            rbmovil.setChecked(redmovil);

            //caso especial, puede haber internet por bridge, ethernet, u otra
            rbo.setChecked(!redmovil&&!redwifi);

        }


    }
}
