package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.SeriesA;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderSeries extends RecyclerView.ViewHolder  {
    View mView;

    private ViewHolderSeries.ClickListener mClicklistener;

    public interface ClickListener{
        void OnItemClick(View view,int position); /*admin tiene presionado normal el item*/
        void OnItemLogClick (View view, int position);/*admin tiene presionado el item*/


    }

    public void setOnClicklistener(ViewHolderSeries.ClickListener clicklistener){
        mClicklistener=clicklistener;

    }


    public ViewHolderSeries(@NonNull View itemView) {
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
    public void SeteoSeries(Context context, String nombre, int vista, String imagen){
        ImageView Imagen_Serie;
        TextView NombreImagen_Serie;
        TextView Vista_Serie;

        //Conexion con el item
        Imagen_Serie = mView.findViewById(R.id.Imagen_Serie);
        NombreImagen_Serie = mView.findViewById(R.id.NombreImagen_Serie);
        Vista_Serie = mView.findViewById(R.id.Vista_Serie);

        NombreImagen_Serie.setText(nombre);

        //convertir a string el parametro vista
        String VistaString = String.valueOf(vista);
        Vista_Serie.setText(VistaString);

        //Controlar posibles errores
        try{
            //Si la imagen fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(Imagen_Serie);

        }catch (Exception e){
            //si la imagen no es traida exitosamente
            Picasso.get().load(R.drawable.categoria).into(Imagen_Serie);

        }

    }
}
