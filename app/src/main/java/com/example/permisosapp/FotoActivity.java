package com.example.permisosapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

public class FotoActivity extends AppCompatActivity {

    //Para Pedir los permisos en el onCreate
    private static final String[] PERMISOS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    //vamos a generar un archivo
    private static final String PREFIJO_FOTOS = "CURSO_PIC_";
    private static final String SUFIJO_FOTOS = ".jpg";
    private String ruta_foto; // Nombre del nuestro fichero creado



    private  static  final int CODIGO_PETICION_SELECCIONAR_FOTO = 100;
    private  static  final int CODIGO_PETICION_PERMISOS = 150;
    private  static  final int CODIGO_PETICION_HACER_FOTO = 200;

    private ImageView imageView; // imagen seleccionada o la foto
    private Uri photo_uri; // para almacenar la ruta de la imagen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        this.imageView = findViewById(R.id.imageView);


        // pido los permisos y haremos un metodo (onRequestPermissionsResult)para recoger cuando vuelva
        ActivityCompat.requestPermissions(this, PERMISOS, CODIGO_PETICION_PERMISOS);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            Log.d("MIAPP", "Me ha concedido permisos");
        } else {
            Log.d("MIAPP", "No me ha concedido permisos");
            Toast.makeText(this, "NO puede continuar", Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    //Creamos el fichero donde irá la imagen capturada
    private Uri crearFicheroImagen (){

        Uri uri_destino = null; // Uri de retorno
        String momento_actual = null;
        String nombre_fichero = null;
        File file = null;



        momento_actual = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        nombre_fichero = PREFIJO_FOTOS+momento_actual+SUFIJO_FOTOS;


        // la ruta de la carpeta pictures de androdi + /nombre_fichero
        ruta_foto = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/" + nombre_fichero;
        Log.d("MIAPP", "Ruta Foto:  "+ ruta_foto);



        // hacemos el fichero
        file = new File(ruta_foto);


        //
        try {
            if (file.createNewFile()){
                Log.d("MIAPP", "Fichero Creado");
            }else {
                Log.d("MIAPP", "Fichero No Creado");
            }
        } catch (IOException e) {
            Log.e("MIAPP", "Error al crear Fichero", e);
            // e.printStackTrace();
        }

        //hacemos la Uri
        uri_destino = Uri.fromFile(file);
        Log.d("MIAPP", "Uri Foto:  "+ uri_destino.toString());


        return uri_destino; // Uri de retorno
    }



    private void  desactivarModoEstricto()
    {

        if (Build.VERSION.SDK_INT >= 24)
        {
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch (Exception e)
            {

            }
        }

    }


    // Tomar una foto
    public void tomarFoto(View view) {

        Log.d("MIAPP", "Quiere hacer una foto");
        Intent intent_foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        this.photo_uri = crearFicheroImagen ();
        intent_foto.putExtra(MediaStore.EXTRA_OUTPUT,photo_uri);
        desactivarModoEstricto();
        startActivityForResult(intent_foto,CODIGO_PETICION_HACER_FOTO);


    }

    public void seleccionarFoto(View view) {
        Log.d("MIAPP", "Quiere selecionar una foto");
        Intent intent_pide_foto = new Intent();
        //intent_pide_foto.setAction(Intent.ACTION_PICK);
        intent_pide_foto.setAction(Intent.ACTION_GET_CONTENT);

        intent_pide_foto.setType("image/*"); //tipo MIME

        startActivityForResult(intent_pide_foto, CODIGO_PETICION_SELECCIONAR_FOTO); // vamos a esperar que nos retorne una imagen de la galeria en este caso

    }




    private void setearImagenDesdeArchivo (int resultado, Intent data){

        switch (resultado){

            case RESULT_OK:
                Log.d("MIAPP", "La foto ha sido seleccionada");
                this.photo_uri = data.getData(); // obtenemos la Uri de la foto seleccionada
                this.imageView.setImageURI(photo_uri);
               // this.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                this.imageView.setMaxHeight(10);

                break;

            case RESULT_CANCELED:
                Log.d("MIAPP", "La foto NO ha sido seleccionada, canceló");

                break;


        }



    }

    private  void  setearImagenDesdeCamara (int resultado, Intent intent){

        switch (resultado)
        {
            case RESULT_OK:
                Log.d("MIAPP", "Tiró la foto bien");

                imageView.setImageURI(this.photo_uri);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,photo_uri));

                break;
            case RESULT_CANCELED:
                Log.d("MIAPP", "Canceló la foto");
                break;
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // este método será invocado a la vuelta de la peticion de la foto
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_PETICION_SELECCIONAR_FOTO){

            setearImagenDesdeArchivo (resultCode, data);

        } else if (requestCode == CODIGO_PETICION_HACER_FOTO){

            setearImagenDesdeCamara (resultCode, data);

        }


    }
}
