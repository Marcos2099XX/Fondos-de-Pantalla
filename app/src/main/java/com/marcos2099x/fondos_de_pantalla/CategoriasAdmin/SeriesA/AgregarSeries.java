package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.SeriesA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class AgregarSeries extends AppCompatActivity {

    TextView VistaSerie;
    EditText NombreSerie;
    ImageView ImagenAgregarSerie;
    Button PublicarSerie;

    String RutaDeAlmacenamiento = "Serie_Subida/";
    String RutaDeBaseDeDatos = "SERIE";
    Uri RutaArchivoUri;

    StorageReference mStorageReference;
    com.google.firebase.database.DatabaseReference DatabaseReference;
    StorageReference mStorageReference2;

    ProgressDialog progressDialog;

    String rNombre, rImagen,rVista;


    int CODIGO_DE_SOLICITUD_IMAGEN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_series);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Publicar");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        VistaSerie= findViewById(R.id.VistaSerie);
        NombreSerie= findViewById(R.id.NombreSerie);
        ImagenAgregarSerie= findViewById(R.id.ImagenAgregarSerie);
        PublicarSerie= findViewById(R.id.PublicarSerie);

        mStorageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference = FirebaseDatabase.getInstance().getReference(RutaDeBaseDeDatos);
        progressDialog = new ProgressDialog(AgregarSeries.this);

        Bundle intent =getIntent().getExtras();
        if(intent !=null){

            //recuperar los datos de la actividad anterior
            rNombre=intent.getString("NombreEnviado");
            rImagen=intent.getString("ImagenEnviada");
            rVista=intent.getString("VistaEnviada");

            //Setear
            NombreSerie.setText(rNombre);
            VistaSerie.setText(rVista);
            Picasso.get().load(rImagen).into(ImagenAgregarSerie);

            //cambiar el nombre actionbar
            actionBar.setTitle("Actualizar");
            String actualizar = "Actualizar";
            //cambiar el nombre del boton
            PublicarSerie.setText(actualizar);
        }

        ImagenAgregarSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "seleccionsr imagen"),CODIGO_DE_SOLICITUD_IMAGEN);
            }
        });


        PublicarSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PublicarSerie.getText().equals("Publicar")) {
                    //Metodo Subir Imagen
                    SubirImagen();
                } else {
                    EmpezarActualizacion();
                }
            }

            private void EmpezarActualizacion() {
                progressDialog.setTitle("Actualizando");
                progressDialog.setMessage("Espere por favor :D");
                progressDialog.show();
                progressDialog.setCancelable(false);
                EliminarImagenAnterior();
            }

            private void EliminarImagenAnterior() {
                StorageReference Imagen = getInstance().getReferenceFromUrl(rImagen);
                Imagen.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //si la imagen se elimino
                        Toast.makeText(AgregarSeries.this, "La imagen anterior a sido eliminada", Toast.LENGTH_SHORT).show();
                        SubirNuevaImagen();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AgregarSeries.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

            private void SubirNuevaImagen() {
                String nuevaImagen = System.currentTimeMillis()+".png";
                StorageReference StorageReference = mStorageReference2 = mStorageReference.child(RutaDeAlmacenamiento + nuevaImagen);
                Bitmap bitmap = ((BitmapDrawable)ImagenAgregarSerie.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                byte [] data= byteArrayOutputStream.toByteArray();
                UploadTask uploadTask = mStorageReference2.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AgregarSeries.this, "Nueva Imagen CargadaT", Toast.LENGTH_SHORT).show();
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();
                        ActualizarImagenBD(downloadUri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AgregarSeries.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            }

            private void ActualizarImagenBD(final String NuevaImagen) {
                final String nombreActualizar = NombreSerie.getText().toString();
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("SERIE");

                //CONSULTA
                Query query= databaseReference.orderByChild("nombre").equalTo(rNombre);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Datos Actualizar
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().child("nombre").setValue(nombreActualizar);
                            ds.getRef().child("imagen").setValue(NuevaImagen);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(AgregarSeries.this, "Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AgregarSeries.this, SeriesA.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            private void SubirImagen() {
                if (RutaArchivoUri != null) {
                    progressDialog.setTitle("Espere por favor");
                    progressDialog.setMessage("Subiendo Imagen SERIE...");
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    StorageReference storageReference2 = mStorageReference.child(RutaDeAlmacenamiento + System.currentTimeMillis() + "." + ObtenerExtencionDelArchivo(RutaArchivoUri));
                    storageReference2.putFile(RutaArchivoUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;

                                    Uri downloadURI = uriTask.getResult();

                                    String mNombre = NombreSerie.getText().toString();
                                    String mVista = VistaSerie.getText().toString();
                                    int VISTA = Integer.parseInt(mVista);

                                    Series series = new Series(downloadURI.toString(), mNombre, VISTA);
                                    String ID_IMAGEN = DatabaseReference.push().getKey();

                                    DatabaseReference.child(ID_IMAGEN).setValue(series);

                                    progressDialog.dismiss();
                                    Toast.makeText(AgregarSeries.this, "Subido exitosamente!", Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(AgregarSeries.this, SeriesA.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(AgregarSeries.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                    progressDialog.setTitle("Publicando");
                                    progressDialog.setCancelable(false);

                                }
                            });

                } else {
                    Toast.makeText(AgregarSeries.this, "DEBE ASIGNAR UNA IMAGEN", Toast.LENGTH_SHORT).show();
                }
            }

            private String ObtenerExtencionDelArchivo(Uri uri) {
                ContentResolver contentResolver = getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
            }
        });}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CODIGO_DE_SOLICITUD_IMAGEN
                && resultCode == RESULT_OK
                && data != null
                && data.getData() !=null){

            RutaArchivoUri= data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),RutaArchivoUri);
                ImagenAgregarSerie.setImageBitmap(bitmap);
            }catch (Exception e){
                Toast.makeText(AgregarSeries.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}