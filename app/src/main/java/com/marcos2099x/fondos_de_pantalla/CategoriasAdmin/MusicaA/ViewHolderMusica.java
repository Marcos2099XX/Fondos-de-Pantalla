package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.MusicaA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderMusica extends RecyclerView.ViewHolder {
    View mView;

    private ViewHolderMusica.ClickListener mClicklistener;

    public interface ClickListener{
        void OnItemClick(View view,int position); /*admin tiene presionado normal el item*/
        void OnItemLogClick (View view, int position);/*admin tiene presionado el item*/


    }

    public void setOnClicklistener(ViewHolderMusica.ClickListener clicklistener){
        mClicklistener=clicklistener;

    }


    public ViewHolderMusica(@NonNull View itemView) {
        super(itemView);
        mView=itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClicklistener.OnItemClick(view,getBindingAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClicklistener.OnItemLogClick(view,getBindingAdapterPosition());
                return true;
            }
        });

    }
    public void SeteoMusica(Context context, String nombre, int vista, String imagen){
        ImageView Imagen_Musica;
        TextView NombreImagen_Musica;
        TextView Vista_Musica;

        //Conexion con el item
        Imagen_Musica = mView.findViewById(R.id.Imagen_Musica);
        NombreImagen_Musica = mView.findViewById(R.id.NombreImagen_Musica);
        Vista_Musica = mView.findViewById(R.id.Vista_Musica);

        NombreImagen_Musica.setText(nombre);

        //convertir a string el parametro vista
        String VistaString = String.valueOf(vista);
        Vista_Musica.setText(VistaString);

        //Controlar posibles errores
        try{
            //Si la imagen fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(Imagen_Musica);

        }catch (Exception e){
            //si la imagen no es traida exitosamente
            Picasso.get().load(R.drawable.categoria).into(Imagen_Musica);

        }

    }
}
