package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.VideojuegosA;

public class Videojuegos {
    private String imagen;
    private String nombre;
    private int vistas;


    public Videojuegos(){

    }

    public Videojuegos(String imagen, String nombre, int vistas) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.vistas = vistas;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getVistas() {
        return vistas;
    }

    public void setVistas(int vistas) {
        this.vistas = vistas;
    }
}
