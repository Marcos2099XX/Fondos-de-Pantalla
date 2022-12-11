package com.marcos2099x.fondos_de_pantalla.FragmentosCliente;

import static com.marcos2099x.fondos_de_pantalla.R.id.*;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.marcos2099x.fondos_de_pantalla.InicioSesion;
import com.marcos2099x.fondos_de_pantalla.R;
import com.marcos2099x.fondos_de_pantalla.R.id;

public class AcerDeCliente extends Fragment {
    Button Acceder;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_acer_de_cliente, container, false);

        Acceder = view.findViewById(id.Acceder);
        Acceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),InicioSesion.class));
            }
        });
        return view;
    }
}