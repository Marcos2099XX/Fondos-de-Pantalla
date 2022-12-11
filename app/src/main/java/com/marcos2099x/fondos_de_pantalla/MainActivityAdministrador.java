package com.marcos2099x.fondos_de_pantalla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.marcos2099x.fondos_de_pantalla.FragmentosAdministrador.InicioAdmin;
import com.marcos2099x.fondos_de_pantalla.FragmentosAdministrador.ListaAdmin;
import com.marcos2099x.fondos_de_pantalla.FragmentosAdministrador.PerfilAdmin;
import com.marcos2099x.fondos_de_pantalla.FragmentosAdministrador.RegistrarAdmin;

public class MainActivityAdministrador extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

DrawerLayout drawerLayout;
FirebaseAuth firebaseAuth;
FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_administrador);

        Toolbar toolbar = findViewById(R.id.toobarA);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_A);


        NavigationView navigationView = findViewById(R.id.nav_viewA);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        //fragmento por defecto
        if(savedInstanceState == null){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                    new InicioAdmin()).commit();
                navigationView.setCheckedItem(R.id.InicioAdmin);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){

            case R.id.InicioAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                        new InicioAdmin()).commit();
                break;

            case R.id.PerfilAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                        new PerfilAdmin()).commit();
                break;


            case R.id.RegistrarAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                        new RegistrarAdmin()).commit();
                break;


            /*case R.id.ListarAdmin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containerA,
                        new ListaAdmin()).commit();
                break;*/


            case R.id.Salir:
                CerrarSesion();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void ComprobandoInicioSesion(){
        if (user != null){
            // Si el administrador ha iniciado sesion
            Toast.makeText(this, "Se ha iniciado sesion :)", Toast.LENGTH_SHORT).show();
        }else{
            //Si no se ha iniciado sesion es por que el usuario es un cliente
            startActivity(new Intent(MainActivityAdministrador.this,MainActivity.class));
            finish();
        }

    }

    private void CerrarSesion(){
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivityAdministrador.this,MainActivity.class));
        Toast.makeText(this, "Cerraste Sesion Exitosamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        ComprobandoInicioSesion();
        super.onStart();
    }
}