package com.marcos2099x.fondos_de_pantalla.DetalleCLiente;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.marcos2099x.fondos_de_pantalla.MainActivity;
import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class DetalleCliente extends AppCompatActivity {
    ImageView ImagenDetalle;
    TextView NombreImagenDetalle;
    TextView VistaDetalle;


    FloatingActionButton fabDescargar, fabCompartir, fabEstablecer;

    Bitmap bitmap;

    //solicitud de almacenamiento
    private static final int CODIGO_DE_ALMACENAMIENTO=1;

    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cliente);

        ActionBar actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Detalle");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ImagenDetalle=findViewById(R.id.ImagenDetalle);
        NombreImagenDetalle=findViewById(R.id.NombreImagenDetalle);
        VistaDetalle=findViewById(R.id.VistaDetalle);

        fabDescargar=findViewById(R.id.fabDescargar);
        //fabCompartir=findViewById(R.id.fabCompartir);
        fabEstablecer=findViewById(R.id.fabEstablecer);

        String imagen = getIntent().getStringExtra("Imagen");
        String Nombre = getIntent().getStringExtra("Nombre");
        String Vista = getIntent().getStringExtra("Vista");

        try {
            Picasso.get().load(imagen).placeholder(R.drawable.detalle_imagen).into(ImagenDetalle);

        }catch (Exception e){
            Picasso.get().load(R.drawable.detalle_imagen).into(ImagenDetalle);

        }

        NombreImagenDetalle.setText(Nombre);
        VistaDetalle.setText(Vista);

        fabDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*SI LA VERSIÓN DE ANDROID ES MAYOR O IGUAL A ANDROID 11*/
               if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
                   /*Código implementado en actualización*/
                   /*si el permiso es condedido, se descarga la imagen*/
                   if (ContextCompat.checkSelfPermission(
                           DetalleCliente.this,
                           Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                           PackageManager.PERMISSION_GRANTED) {
                       DescargarImagenAndroid_11();
                   }
                    /*Si el permiso no es condedido, que aparezca nuevamente
                        el cuadro del permiso*/
                   else {
                       /*Código implementado en actualización*/
                       SolicitudPermisoDescargaAndroid_11_o_Superior.launch(
                               Manifest.permission.WRITE_EXTERNAL_STORAGE);
                   }
               }
                   /*SI LA VERSIÓN DE ANDROID ES MAYOR O IGUAL A 6*/
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                       /*Código implementado en actualización*/
                       /*si el permiso es condedido, se descarga la imagen*/
                       if (ContextCompat.checkSelfPermission(
                               DetalleCliente.this,
                               Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                               PackageManager.PERMISSION_GRANTED){
                           DescargarImagen();
                       }
                        /*Si el permiso no es condedido, que aparezca nuevamente
                        el cuadro del permiso*/
                       else {
                           /*Código implementado en actualización*/
                           SolicitudPermisoDescarga.launch(
                                   Manifest.permission.WRITE_EXTERNAL_STORAGE);
                       }
                   }
                   /*SI LA VERSIÓN DE ANDROID ES MENOR A 6*/
                   else {
                       DescargarImagen();
                   }
               }
            });

        /*fabCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompartirImagen_Actualizado();
            }
        });*/

        fabEstablecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EstablecerImagen();

            }
        });
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

    }

    private void DescargarImagen(){
        //Obtener mapas de bit
        bitmap=((BitmapDrawable)ImagenDetalle.getDrawable()).getBitmap();

        String FechaDescarga = new SimpleDateFormat( "'Fecha Descarga: ' yyyy_MM_dd 'Hora: ' HH:mm:ss",
                Locale.getDefault()).format(System.currentTimeMillis());
        //Definir La ruta de almacenamiento
        File ruta = Environment.getExternalStorageDirectory();
        //Definir nombre de la carpeta
        File NombreCarpeta = new File(ruta+"/Fondos/");
        NombreCarpeta.mkdir();
        //Definir el nombre de la imagen descargada
        String ObtenerNombreImagen = NombreImagenDetalle.getText().toString();
        String NombreImagen= ObtenerNombreImagen + " " + FechaDescarga + "JPEG";
        File file = new File(NombreCarpeta, NombreImagen);
        OutputStream outputStream;

        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(this, "La imagen se ha descargado con exito", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void DescargarImagenAndroid_11() {
        bitmap = ((BitmapDrawable)ImagenDetalle.getDrawable()).getBitmap();
        OutputStream fos;
        String NombreImagen = NombreImagenDetalle.getText().toString();

        try {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, NombreImagen);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"Image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES+ File.separator+"/MI APLICACIÓN/");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Objects.requireNonNull(fos);
            Toast.makeText(this, "La imagen se ha descargado con éxito", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(this, "No guardado", Toast.LENGTH_SHORT).show();
        }
    }

    /*Código implementado en actualización*/
    /*Solicitud para descargar imagen con Android menor a 11*/
    private ActivityResultLauncher<String> SolicitudPermisoDescarga =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

                //Si el permiso para descargar la imagen es exitosa
                if (isGranted){
                    DescargarImagen();
                }else {
                    //Si el permiso fue denegado
                    Toast.makeText(this, "El permiso a sido denegado", Toast.LENGTH_SHORT).show();
                }

            });


    /*Código implementado en actualización*/
    /*Solicitud para descargar imagen con Android superior a 11*/
    private ActivityResultLauncher<String> SolicitudPermisoDescargaAndroid_11_o_Superior =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{

                //Si el permiso para descargar la imagen es exitosa
                if (isGranted){
                    DescargarImagenAndroid_11();
                }else {
                    //Si el permiso fue denegado
                    Toast.makeText(this, "El permiso a sido denegado", Toast.LENGTH_SHORT).show();
                }
            });;

    /*Código añadido en actualización*/
    private void CompartirImagen_Actualizado(){
        Uri contentUri = getContentUri();

        Intent sharedIntent = new Intent(Intent.ACTION_SEND);

        sharedIntent.setType("image/jpeg");
        sharedIntent.putExtra(Intent.EXTRA_SUBJECT,"Asunto");
        sharedIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        sharedIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(sharedIntent);
    }

    /*Código añadido en actualización*/
    private Uri getContentUri(){
        BitmapDrawable bitmapDrawable = (BitmapDrawable)ImagenDetalle.getDrawable();
        bitmap = bitmapDrawable.getBitmap();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                ImageDecoder.Source source =
                        ImageDecoder.createSource(getContentResolver(), imageUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }else {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }
        }

        catch (Exception e){

        }

        File imageFolder = new File(getCacheDir(), "images");
        Uri contentUri = null;

        try {
            String NombreImagen = NombreImagenDetalle.getText().toString();
            imageFolder.mkdirs();
            File file = new File(imageFolder,"shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
            stream.flush();
            stream.close();
            contentUri = FileProvider.getUriForFile(
                    this, "com.marcos2099x.fondos_de_pantalla.fileprovider",file);
        }
        catch (Exception e){

        }

        return contentUri;

    };

    private void EstablecerImagen(){
        //OBTENER EL MAPA DE BITS
        bitmap = ((BitmapDrawable) ImagenDetalle.getDrawable()).getBitmap();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());

        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this, "Establecido con éxito", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}