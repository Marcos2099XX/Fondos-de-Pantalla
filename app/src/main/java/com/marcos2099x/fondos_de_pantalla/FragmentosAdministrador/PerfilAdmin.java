package com.marcos2099x.fondos_de_pantalla.FragmentosAdministrador;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marcos2099x.fondos_de_pantalla.MainActivityAdministrador;
import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import javax.xml.transform.Result;

public class PerfilAdmin extends Fragment {

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference BASE_DE_DATOS_ADMINISTRADORES;

    StorageReference storageReference;
    String RutaDeAlmacenamiento= "Fotos_perfil_Administradores/*";

    //solicitudes
    private static final int CODIGO_DE_SOLICITUD_DE_CAMARA= 100;
    private static final int CODIGO_DE_CAMARA_DE_SELECCION_DE_IMAGENES= 200;

    //solicitudes de galeria
    private static final int CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO = 300;
    private static final int CODIGO_DE_GALERIA_DE_SELEECION_DE_IMAGEN = 400;

    //PERMISOS A SOLICITAR
    private String[] permisos_de_la_camara;
    private String[] permisos_de_alamacenamiento;

    private Uri imagen_uri;
    private String imagen_perfil;
    private ProgressDialog progressDialog;

    //Vistas
    ImageView FOTOPERFILIMG;
    TextView UIDPERFIl, NOMBRESPERFIL, APELLIDOSPERFIL, CORREOPERFIL, PASSWORDPERFIL, EDADPERFIL;
    Button ACTUALIZARPASS,ACTUALIZARDATOS;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil_admin, container, false);

        FOTOPERFILIMG= view.findViewById(R.id.FOTOPERFILIMG);
        UIDPERFIl= view.findViewById(R.id.UIDPERFIl);
        NOMBRESPERFIL= view.findViewById(R.id.NOMBRESPERFIL);
        APELLIDOSPERFIL= view.findViewById(R.id.APELLIDOSPERFIL);
        CORREOPERFIL= view.findViewById(R.id.CORREOPERFIL);
        PASSWORDPERFIL= view.findViewById(R.id.PASSWORDPERFIL);
        EDADPERFIL= view.findViewById(R.id.EDADPERFIL);

        ACTUALIZARPASS= view.findViewById(R.id.ACTUALIZARPASS);
        //ACTUALIZARDATOS= view.findViewById(R.id.ACTUALIZARDATOS);

        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        storageReference = getInstance().getReference();

        //inicializar los servicios
        permisos_de_la_camara= new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        permisos_de_alamacenamiento=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        progressDialog = new ProgressDialog(getActivity());


        BASE_DE_DATOS_ADMINISTRADORES = FirebaseDatabase.getInstance().getReference("BASE DE DATOS ADMINISTRADORES");

        BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    //obtener los datos

                    String uid = ""+snapshot.child("UID").getValue();
                    String nombre= ""+snapshot.child("NOMBRES").getValue();
                    String apellidos= ""+snapshot.child("APELLIDOS").getValue();
                    String correo= ""+snapshot.child("CORRREO").getValue();
                    String pass= ""+snapshot.child("PASSWORD").getValue();
                    String edad= ""+snapshot.child("EDAD").getValue();
                    String imagen= ""+snapshot.child("IMAGEN").getValue();

                    UIDPERFIl.setText(uid);
                    NOMBRESPERFIL.setText(nombre);
                    APELLIDOSPERFIL.setText(apellidos);
                    CORREOPERFIL.setText(correo);
                    PASSWORDPERFIL.setText(pass);
                    EDADPERFIL.setText(edad);

                    try {
                        //Si existe la imagen en la base de datos admin
                        Picasso.get().load(imagen).placeholder(R.drawable.perfil).into(FOTOPERFILIMG);

                    }catch (Exception e){
                        //No existe la imagen en la base de datos admin
                        Picasso.get().load(R.drawable.perfil).into(FOTOPERFILIMG);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       //nos dirige a la actividad cambiar contraseña
        ACTUALIZARPASS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),Cambio_Pass.class));
                getActivity().finish();
            }
        });

        FOTOPERFILIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CambiarImagenPerfilAdministrador();
            }

        });

        return view;
    }

    private void CambiarImagenPerfilAdministrador() {
        String [] opcion= {"Cambiar foto de perfil"};
        //crear el alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //asignar el titulo
        builder.setTitle("Elegir una opcion");
        builder.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    imagen_perfil = "IMAGEN";
                    ElegirFoto();
                }
            }

        });
        builder.create().show();
    }

    //comprobar los permisos de almacenamiento estan habilitados o no
    private boolean Comprobar_los_permisos_de_almacenamiento(){
        boolean resultado_uno = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return resultado_uno;
    }

    private void solicitar_los_permisos_de_almacenamiento(){
        requestPermissions(permisos_de_alamacenamiento,CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO);
    }

    //comprobar si los permisos de la camara estan activados si o no
    private boolean Comprobar_los_permisos_de_la_camara(){
        boolean resultado_uno = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);

        boolean resultado_dos = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);

        return resultado_uno && resultado_dos;
    }

    private void Solicitar_permisos_de_camara(){
        requestPermissions(permisos_de_la_camara,CODIGO_DE_SOLICITUD_DE_CAMARA);
    }

    private void ElegirFoto() {
        String [] opciones ={"camara","Galeria"};
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccionar Imagen de: ");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    //vamos a verificar si el permiso ya esta concedido
                    if(!Comprobar_los_permisos_de_la_camara()){
                        //si no esta concedido el permiso
                        Solicitar_permisos_de_camara();
                    }else {
                        Elegir_De_camara();
                    }
                }else if (i==1){
                    if(!Comprobar_los_permisos_de_almacenamiento()){
                        solicitar_los_permisos_de_almacenamiento();
                    }else {
                        Elegir_De_Galeria();
                    }

                }
            }


        });

        builder.create().show();


    }

    private void Elegir_De_Galeria() {
        Intent GaleriaIntent = new Intent(Intent.ACTION_PICK);
        GaleriaIntent.setType("Image/*");
        startActivityForResult(GaleriaIntent, CODIGO_DE_GALERIA_DE_SELEECION_DE_IMAGEN);


    }

    //metodo para abrir la camara
    private void Elegir_De_camara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Foto Temporal");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion Temporal");
        imagen_uri = (requireActivity()).getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Actividad para abrir la camara
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagen_uri);
        startActivityForResult(camaraIntent,CODIGO_DE_CAMARA_DE_SELECCION_DE_IMAGENES);

    }

    //este metodo se llamara despues de elegir la imagen de la camara de la galeria
    private void ActualizarImagenEnBD(Uri uri){
        String Ruta_de_archivo_y_nombre = RutaDeAlmacenamiento + "" + imagen_perfil+ "_" + user.getUid();
        StorageReference storageReference2 = storageReference .child(Ruta_de_archivo_y_nombre);
        storageReference2.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task <Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        if (uriTask.isSuccessful()){
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(imagen_perfil,downloadUri.toString());
                            BASE_DE_DATOS_ADMINISTRADORES.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            startActivity(new Intent(getActivity(), MainActivityAdministrador.class));
                                            getActivity().finish();
                                            Toast.makeText(getActivity(), "Imagen Cambiada Con Exito!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            Toast.makeText(getActivity(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    //resultado de los permisos de solicitud
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if (requestCode == CODIGO_DE_CAMARA_DE_SELECCION_DE_IMAGENES){
                ActualizarImagenEnBD(imagen_uri);
                progressDialog.setTitle("Procesando");
                progressDialog.setMessage("La imagen se esta cambiando, espere por favor...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
            if(requestCode == CODIGO_DE_GALERIA_DE_SELEECION_DE_IMAGEN){
                imagen_uri = data.getData();
                ActualizarImagenEnBD(imagen_uri);
                progressDialog.setTitle("Procesando");
                progressDialog.setMessage("La imagen se esta cambiando, espere por favor...");
                progressDialog.setCancelable(false);
                progressDialog.show();

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //este metodo se ejecuta cuando se permite o deniega el cuadro de dialogo
        switch (requestCode){
            case CODIGO_DE_SOLICITUD_DE_CAMARA:{
                //verifica si los permisos de camara y almacenamiento estan concedidos o no
                if (grantResults.length >0){
                    boolean CamaraAceptada = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean EscribirAlmacenamiento_Aceptada = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(CamaraAceptada && EscribirAlmacenamiento_Aceptada){
                        Elegir_De_camara();
                    }else{
                        Toast.makeText(getActivity(), "Por favor acepte los permisos de camara y almacenamiento", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO: {
                if (grantResults.length > 0) {
                    boolean EscribirAlmacenamiento_Aceptada = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (EscribirAlmacenamiento_Aceptada) {
                        Elegir_De_Galeria();

                    }else{
                        Toast.makeText(getActivity(), "Por Favor Acepte Los Permisos De Almacenamiento", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}