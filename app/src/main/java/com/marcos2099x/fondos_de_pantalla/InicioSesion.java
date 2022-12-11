package com.marcos2099x.fondos_de_pantalla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicioSesion extends AppCompatActivity {

    EditText Correo,Password;
    Button Acceder;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        ActionBar actionBar= getSupportActionBar();//creamos el actionbar
        assert actionBar != null;//afirmamos que el action bar no sea nulo
        actionBar.setTitle("Inicio Sesión");//Le asignamos un titulo
        actionBar.setDisplayHomeAsUpEnabled(true);//Habilitamos el boton de retroceso
        actionBar.setDisplayShowHomeEnabled(true);

        Correo = findViewById(R.id.Correo);
        Password= findViewById(R.id.Password);
        Acceder= findViewById(R.id.Acceder);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(InicioSesion.this);
        progressDialog.setCancelable(false);

        Acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //convertimos a string los editText tanto de correo como de contraseña
                String correo = Correo.getText().toString();
                String pass = Password.getText().toString();


                    //Validacion de correo electronico
                    if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                        Correo.setError("Correo Invalido");
                        Correo.setFocusable(true);
                    }
                    else if(pass.length()<6){
                        Password.setError("Contraseña debe ser mayor o igual a 6");
                        Password.setFocusable(true);
                    } else{
                        LogeoAdministradores(correo,pass);

                    }
            }

            private void LogeoAdministradores(String correo, String pass) {
                progressDialog.show();
                progressDialog.setCancelable(false);
                firebaseAuth.signInWithEmailAndPassword(correo, pass)
                        .addOnCompleteListener(InicioSesion.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    //si el inicio de sesion fue exitoso
                                    progressDialog.dismiss();
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    startActivity(new Intent(InicioSesion.this, MainActivityAdministrador.class));
                                    Toast.makeText(InicioSesion.this, "Bienvenido(a)" + user.getEmail(), Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    progressDialog.dismiss();
                                    UsuarioInvalido();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                UsuarioInvalido();

                            }
                        });
            }
        });

    }

    private void UsuarioInvalido() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InicioSesion.this);
        builder.setCancelable(false);
        builder.setTitle("!HA OCURRIDO UN ERROR!");
        builder.setMessage("Verifique que los correos y contraseñas sean los correctos")
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}