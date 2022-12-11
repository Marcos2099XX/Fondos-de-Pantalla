package com.marcos2099x.fondos_de_pantalla.Categorias;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.VideojuegosA.Videojuegos;
import com.marcos2099x.fondos_de_pantalla.CategoriasCliente.MusicaCliente;
import com.marcos2099x.fondos_de_pantalla.CategoriasCliente.PeliculasCliente;
import com.marcos2099x.fondos_de_pantalla.CategoriasCliente.SeriesCliente;
import com.marcos2099x.fondos_de_pantalla.CategoriasCliente.VideojuegosCliente;
import com.marcos2099x.fondos_de_pantalla.R;

public class ControladorCD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlador_cd);

        String CategoriaRecuperada = getIntent().getStringExtra("Categoria");
        if (CategoriaRecuperada.equals("Peliculas")){
            startActivity(new Intent(ControladorCD.this, PeliculasCliente.class));
            finish();
        }
        if (CategoriaRecuperada.equals("Series")){
            startActivity(new Intent(ControladorCD.this, SeriesCliente.class));
            finish();
        }
        if (CategoriaRecuperada.equals("Musica")){
            startActivity(new Intent(ControladorCD.this, MusicaCliente.class));
            finish();
        }
        if (CategoriaRecuperada.equals("Videojuegos")){
            startActivity(new Intent(ControladorCD.this, VideojuegosCliente.class));
            finish();
        }
    }
}