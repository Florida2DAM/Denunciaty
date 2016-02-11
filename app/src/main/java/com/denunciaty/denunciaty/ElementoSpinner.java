package com.denunciaty.denunciaty;

/**
 * Created by Julian on 11/02/2016.
 */
public class ElementoSpinner {
    private String name;

    private int icon;

    public ElementoSpinner(String nombre, int icono) {
        super();
        this.name = nombre;
        this.icon = icono;
    }

    public String getNombre() {
        return name;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public int getIcono() {
        return icon;
    }

    public void setIcono(int icono) {
        this.icon = icono;
    }

}