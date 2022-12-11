package com.marcos2099x.fondos_de_pantalla.Categorias.Cat_Dispositivo;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.marcos2099x.fondos_de_pantalla.R;
import com.squareup.picasso.Picasso;

public class ViewHolderCD extends RecyclerView.ViewHolder {
    View mView;

    private ViewHolderCD.ClickListener mClicklistener;

    public interface ClickListener{
        void OnItemClick(View view,int position); /*admin tiene presionado normal el item*/


    }

    public void setOnClicklistener(ViewHolderCD.ClickListener clicklistener){
        mClicklistener=clicklistener;

    }


    public ViewHolderCD(@NonNull View itemView) {
        super(itemView);
        mView=itemView;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClicklistener.OnItemClick(view,getBindingAdapterPosition());
            }
        });

    }
    public void SeteoCategoriaD(Context context, String categoria, String imagen){
        ImageView ImagenCategoriaD;
        TextView NombreCategoriaD;

        //Conexion con el item
        ImagenCategoriaD = mView.findViewById(R.id.ImagenCategoriaD);
        NombreCategoriaD = mView.findViewById(R.id.NombreCategoriaD);

        NombreCategoriaD.setText(categoria);


        //Controlar posibles errores
        try{
            //Si la imagen fue traida exitosamente
            Picasso.get().load(imagen).placeholder(R.drawable.categoria).into(ImagenCategoriaD);

        }catch (Exception e){
            //si la imagen no es traida exitosamente
            //Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            Picasso.get().load(R.drawable.categoria).into(ImagenCategoriaD);

        }

    }
}
