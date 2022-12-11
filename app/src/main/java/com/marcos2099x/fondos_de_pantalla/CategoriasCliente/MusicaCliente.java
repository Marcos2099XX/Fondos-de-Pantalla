package com.marcos2099x.fondos_de_pantalla.CategoriasCliente;

import static com.marcos2099x.fondos_de_pantalla.R.id.Agregar;
import static com.marcos2099x.fondos_de_pantalla.R.id.Vista;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.MusicaA.AgregarMusica;
import com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.MusicaA.Musica;
import com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.MusicaA.MusicaA;
import com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.MusicaA.ViewHolderMusica;
import com.marcos2099x.fondos_de_pantalla.DetalleCLiente.DetalleCliente;
import com.marcos2099x.fondos_de_pantalla.R;

public class MusicaCliente extends AppCompatActivity {

    RecyclerView recyclerViewMusicaC;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Musica, ViewHolderMusica> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Musica> options;

    SharedPreferences sharedPreferences;
    Dialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musica_cliente);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Musica");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerViewMusicaC = findViewById(R.id.recyclerViewMusicaC);
        recyclerViewMusicaC.setHasFixedSize(true);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("MUSICA");

        dialog = new Dialog(MusicaCliente.this);

        ListarImagenesMusica();
    }

    private void ListarImagenesMusica() {

        options= new FirebaseRecyclerOptions.Builder<Musica>().setQuery(mRef,Musica.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Musica, ViewHolderMusica>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderMusica viewHolderMusica, int i, @NonNull Musica musica) {
                viewHolderMusica.SeteoMusica(
                        getApplicationContext(),
                        musica.getNombre(),
                        musica.getVistas(),
                        musica.getImagen()
                );
            }

            @NonNull
            @Override
            public ViewHolderMusica onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflar el Item
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musica,parent,false);

                ViewHolderMusica viewHolderMusica = new ViewHolderMusica(itemView);
                viewHolderMusica.setOnClicklistener(new ViewHolderMusica.ClickListener() {
                    @Override
                    public void OnItemClick(View view, int position) {
                        //obtener los datos de la imagen
                        String Imagen = getItem(position).getImagen();
                        String Nombres = getItem(position).getNombre();
                        int Vistas= getItem(position).getVistas();
                        //convertir a String la vista
                        String VistaString =String.valueOf(Vistas);

                        //Pasamos a la actividad detalle cliente
                        Intent intent =new Intent(MusicaCliente.this,DetalleCliente.class);

                        //datos a pasar
                        intent.putExtra("Imagen", Imagen);
                        intent.putExtra("Nombres", Nombres);
                        intent.putExtra("Vista", VistaString);

                        startActivity(intent);

                    }

                    @Override
                    public void OnItemLogClick(View view, int position) {

                    }
                });
                return viewHolderMusica;
            }
        };
        /*Al iniciar la actividad se va a listar en dos columnas*/
        sharedPreferences =MusicaCliente.this.getSharedPreferences("MUSICA",MODE_PRIVATE);
        String ordenar_en= sharedPreferences.getString("Ordenar","Dos");

        //Elegir el tipo de vista
        if (ordenar_en.equals("Dos")){
            recyclerViewMusicaC.setLayoutManager(new GridLayoutManager(MusicaCliente.this,2));
            firebaseRecyclerAdapter.startListening();
            recyclerViewMusicaC.setAdapter(firebaseRecyclerAdapter);
        }
        else if (ordenar_en.equals("Tres")){
            recyclerViewMusicaC.setLayoutManager(new GridLayoutManager(MusicaCliente.this,3));
            firebaseRecyclerAdapter.startListening();
            recyclerViewMusicaC.setAdapter(firebaseRecyclerAdapter);
        }
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
        menuInflater.inflate(R.menu.menu_vista,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == Vista) {
            Ordenar_Imagenes();
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