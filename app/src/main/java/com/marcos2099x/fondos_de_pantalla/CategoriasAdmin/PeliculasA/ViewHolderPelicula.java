package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.PeliculasA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderPelicula  extends RecyclerView.ViewHolder {

    View mView;

    private ViewHolderPelicula.ClickListener mClicklistener;

    public interface ClickListener{
        void OnItemClick(View view,int position); /*admin tiene presionado normal el item*/
        void OnItemLogClick (View view, int position);/*admin tiene presionado el item*/


    }

    public void setOnClicklistener(ViewHolderPelicula.ClickListener clicklistener){
        mClicklistener=clicklistener;

    }


    public ViewHolderPelicula(@NonNull View itemView) {
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
    public void SeteoPeliculas(Context context, String nombre, int vista,String imagen){
        ImageView ImagenPelicula;
        TextView NombreImagenPelicula;
        TextView VistaPelicula;

        //Conexion con el item
        ImagenPelicula = mView.findViewById(R.id.ImagenPelicula);
        NombreImagenPelicula = mView.findViewById(R.id.NombreImagenPelicula);
        VistaPelicula = mView.findViewById(R.id.VistaPelicula);

        NombreImagenPelicula.setText(nombre);

        //convertir a string el parametro vista
        String VistaString = String.valueOf(vista);
        VistaPelicula.setText(VistaString);

        //Controlar posibles errores
        try{
            //Si la imagen fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(ImagenPelicula);

        }catch (Exception e){
            //si la imagen no es traida exitosamente
            Picasso.get().load(R.drawable.categoria).into(ImagenPelicula);

        }

    }
}
