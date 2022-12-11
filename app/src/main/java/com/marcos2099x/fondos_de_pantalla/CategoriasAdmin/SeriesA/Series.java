package com.marcos2099x.fondos_de_pantalla.CategoriasAdmin.SeriesA;

public class Series {
    private String imagen;
    private String nombre;
    private int vistas;

    public Series() {

    }

    public Series(String imagen, String nombre, int vistas) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.vistas = vistas;
    }

    public String getImagen() {
        return imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVistas() {
        return vistas;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setVistas(int vistas) {
        this.vistas = vistas;
    }
}
