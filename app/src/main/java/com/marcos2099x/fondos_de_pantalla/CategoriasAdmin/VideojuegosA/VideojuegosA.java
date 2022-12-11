package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.VideojuegosA;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.PeliculasA.PeliculasA;
import com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.SeriesA.SeriesA;
import com.marcos2099x.fondos_de_pantalla.R;

public class VideojuegosA extends AppCompatActivity {

    RecyclerView recyclerViewVideojuegos;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Videojuegos, ViewHolderVideojuegos> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Videojuegos> options;

    SharedPreferences sharedPreferences;
    Dialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videojuegos);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Videojuegos");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewVideojuegos = findViewById(R.id.recyclerViewVideojuegos);
        recyclerViewVideojuegos.setHasFixedSize(true);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("VIDEOJUEGOS");

        dialog = new Dialog(VideojuegosA.this);

        ListarImagenesVideojuegos();

    }

    private void ListarImagenesVideojuegos() {
        options= new FirebaseRecyclerOptions.Builder<Videojuegos>().setQuery(mRef,Videojuegos.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Videojuegos, ViewHolderVideojuegos>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderVideojuegos viewHolderSeries, int i, @NonNull Videojuegos videojuegos) {
                viewHolderSeries.SeteoVideojuegos(
                        getApplicationContext(),
                        videojuegos.getNombre(),
                        videojuegos.getVistas(),
                        videojuegos.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderVideojuegos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflar el Item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videojuegos,parent,false);

                ViewHolderVideojuegos viewHolderSeries = new ViewHolderVideojuegos(itemView);
                viewHolderSeries.setOnClicklistener(new ViewHolderVideojuegos.ClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        Toast.makeText(VideojuegosA.this, "ITEM CLICK", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void OnItemLogClick(View view, int position) {

                        String Nombre= getItem(position).getNombre();
                        String Imagen = getItem(position).getImagen();
                        int Vista= getItem(position).getVistas();
                        final String VistaString = String.valueOf(Vista);

                        AlertDialog.Builder builder = new AlertDialog.Builder(VideojuegosA.this);

                        String [] opciones = {"Actualizar","Eliminar"};
                        builder.setItems(opciones, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0){
                                    Intent intent = new Intent(VideojuegosA.this, AgregarVideojuegos.class);
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
                return viewHolderSeries;
            }
        };
        /*Al iniciar la actividad se va a listar en dos columnas*/
        sharedPreferences = VideojuegosA.this.getSharedPreferences("VIDEOJUEGOS",MODE_PRIVATE);
        String ordenar_en= sharedPreferences.getString("Ordenar","Dos");

        //Elegir el tipo de vista
        if (ordenar_en.equals("Dos")){
            recyclerViewVideojuegos.setLayoutManager(new GridLayoutManager(VideojuegosA.this,2));
            firebaseRecyclerAdapter.startListening();
            recyclerViewVideojuegos.setAdapter(firebaseRecyclerAdapter);
        }
        else if (ordenar_en.equals("Tres")){
            recyclerViewVideojuegos.setLayoutManager(new GridLayoutManager(VideojuegosA.this,3));
            firebaseRecyclerAdapter.startListening();
            recyclerViewVideojuegos.setAdapter(firebaseRecyclerAdapter);
        }
    }

    private void EliminarDatos(final String NombreActual,final String ImagenActual){
        AlertDialog.Builder builder = new AlertDialog.Builder(VideojuegosA.this);
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
                        Toast.makeText(VideojuegosA.this, "La imagen ha sido eliminada", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(VideojuegosA.this, error.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

                StorageReference ImagenSeleccionada = getInstance().getReferenceFromUrl(ImagenActual);
                ImagenSeleccionada.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VideojuegosA.this, "ELiminado", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideojuegosA.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(VideojuegosA.this, "Cancelado por Administrador", Toast.LENGTH_SHORT).show();

            }
        });

        builder.create().show();
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

            case R.id.Agregar:
                //Toast.makeText(this, "Agregar Imagen", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VideojuegosA.this, AgregarVideojuegos.class));
                finish();
                break;
            case R.id.Vista:
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
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();

    }
}