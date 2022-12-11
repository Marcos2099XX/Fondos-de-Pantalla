package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.PeliculasA;
import static com.marcos2099x.fondos_de_pantalla.R.id.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.marcos2099x.fondos_de_pantalla.R;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class PeliculasA extends AppCompatActivity {

    RecyclerView recyclerViewPelicula;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Pelicula,ViewHolderPelicula>firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Pelicula>options;

    SharedPreferences sharedPreferences;
    Dialog dialog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Peliculas");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewPelicula = findViewById(R.id.recyclerViewPelicula);
        recyclerViewPelicula.setHasFixedSize(true);
        

        mFirebaseDatabase= FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("PELICULAS");

        dialog = new Dialog(PeliculasA.this);

        ListarImagenesPeliculas();


    }

    private void ListarImagenesPeliculas(){
        options= new FirebaseRecyclerOptions.Builder<Pelicula>().setQuery(mRef,Pelicula.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pelicula, ViewHolderPelicula>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderPelicula viewHolderPelicula, int i, @NonNull Pelicula pelicula) {
               viewHolderPelicula.SeteoPeliculas(
                       getApplicationContext(),
                       pelicula.getNombre(),
                       pelicula.getVistas(),
                       pelicula.getImagen()
               );
            }

            @NonNull
            @Override
            public ViewHolderPelicula onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflar el Item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelicula,parent,false);

                ViewHolderPelicula viewHolderPelicula = new ViewHolderPelicula(itemView);
                viewHolderPelicula.setOnClicklistener(new ViewHolderPelicula.ClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        Toast.makeText(PeliculasA.this, "ITEM CLICK", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void OnItemLogClick(View view, int position) {

                        String Nombre= getItem(position).getNombre();
                        String Imagen = getItem(position).getImagen();
                       int Vista= getItem(position).getVistas();
                       final String VistaString = String.valueOf(Vista);

                        AlertDialog.Builder builder = new AlertDialog.Builder(PeliculasA.this);

                        String [] opciones = {"Actualizar","Eliminar"};
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    Intent intent = new Intent(PeliculasA.this,AgregarPelicula.class);
                                    intent.putExtra("NombreEnviado",Nombre);
                                    intent.putExtra("ImagenEnviada",Imagen);
                                    intent.putExtra("VistaEnviada",VistaString);

                                    startActivity(intent);

                                }
                                if(i==1){
                                    EliminarDatos(Nombre,Imagen);

                                }
                            }
                        });

                        builder.create().show();

                    }
                });
                return viewHolderPelicula;
            }
        };
        /*Al iniciar la actividad se va a listar en dos columnas*/
        sharedPreferences =PeliculasA.this.getSharedPreferences("PELICULAS",MODE_PRIVATE);
        String ordenar_en= sharedPreferences.getString("Ordenar","Dos");

        //Elegir el tipo de vista
        if (ordenar_en.equals("Dos")){
            recyclerViewPelicula.setLayoutManager(new GridLayoutManager(PeliculasA.this,2));
            firebaseRecyclerAdapter.startListening();
            recyclerViewPelicula.setAdapter(firebaseRecyclerAdapter);
        }
        else if (ordenar_en.equals("Tres")){
            recyclerViewPelicula.setLayoutManager(new GridLayoutManager(PeliculasA.this,3));
            firebaseRecyclerAdapter.startListening();
            recyclerViewPelicula.setAdapter(firebaseRecyclerAdapter);
        }
    }

    private void EliminarDatos(final String NombreActual,final String ImagenActual){
        AlertDialog.Builder builder = new AlertDialog.Builder(PeliculasA.this);
        builder.setTitle("Eliminar");
        builder.setMessage("¿Desea eliminar la imagen?");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*Eliminar imagen de la base de datos*/

                Query query = mRef.orderByChild("nombre").equalTo(NombreActual);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();

                        }
                        Toast.makeText(PeliculasA.this, "La imagen ha sido eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PeliculasA.this, error.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

                StorageReference ImagenSeleccionada = getInstance().getReferenceFromUrl(ImagenActual);
                ImagenSeleccionada.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PeliculasA.this, "ELiminado", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PeliculasA.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(PeliculasA.this, "Cancelado por Administrador", Toast.LENGTH_SHORT).show();

            }
        });

        builder.create().show();
    }


    protected void onStart(){
        super.onStart();
        if (firebaseRecyclerAdapter !=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar,menu);
        menuInflater.inflate(R.menu.menu_vista,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case Agregar:
                startActivity(new Intent(PeliculasA.this,AgregarPelicula.class));
                finish();
                break;
            case Vista:
                Ordenar_Imagenes();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Ordenar_Imagenes(){

        //Declaramos las vistas
        TextView OrdenarTXT;
        Button Dos_columnas;
        Button Tres_columnas;

        //Conexion con cuadro de dialogo
        dialog.setContentView(R.layout.dialog_ordenar);

        //Inicializar las vistas
        OrdenarTXT= dialog.findViewById(R.id.OrdenarTXT);
        Dos_columnas=dialog.findViewById(R.id.Dos_columnas);
        Tres_columnas= dialog.findViewById(R.id.Tres_columnas);

        Dos_columnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("Ordenar","Dos");
                editor.apply();
                recreate();
                dialog.dismiss();

            }
        });

        //evento 3 columnas
        Tres_columnas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("Ordenar","Tres");
                editor.apply();
                recreate();
                dialog.dismiss();

            }
        });

        dialog.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}