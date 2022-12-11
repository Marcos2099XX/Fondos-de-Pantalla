package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.VideojuegosA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderVideojuegos extends RecyclerView.ViewHolder {
    View mView;

    private ViewHolderVideojuegos.ClickListener mClicklistener;

    public interface ClickListener{
        void OnItemClick(View view,int position); /*admin tiene presionado normal el item*/
        void OnItemLogClick (View view, int position);/*admin tiene presionado el item*/


    }

    public void setOnClicklistener(ViewHolderVideojuegos.ClickListener clicklistener){
        mClicklistener=clicklistener;

    }


    public ViewHolderVideojuegos(@NonNull View itemView) {
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
                mClicklistener.OnItemLogClick(view,getAdapterPosition());
                return true;
            }
        });

    }
    public void SeteoVideojuegos(Context context, String nombre, int vista, String imagen){
        ImageView ImagenVideojuegos;
        TextView NombreImagen_Videojuegos;
        TextView Vista_Videojuegos;

        //Conexion con el item
        ImagenVideojuegos = mView.findViewById(R.id.ImagenVideojuegos);
        NombreImagen_Videojuegos = mView.findViewById(R.id.NombreImagen_Videojuegos);
        Vista_Videojuegos = mView.findViewById(R.id.Vista_Videojuegos);

        NombreImagen_Videojuegos.setText(nombre);

        //convertir a string el parametro vista
        String VistaString = String.valueOf(vista);
        Vista_Videojuegos.setText(VistaString);

        //Controlar posibles errores
        try{
            //Si la imagen fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(ImagenVideojuegos);

        }catch (Exception e){
            //si la imagen no es traida exitosamente
            Picasso.get().load(R.drawable.categoria).into(ImagenVideojuegos);

        }

    }
}
