package com.denunciaty.denunciaty.JavaClasses;

/**
 * Created by Nach on 19/02/2016.
 */
public class PuntoAcceso {
    private int id;
    private String descripcion;
    private double longitud;
    private double latitud;

    public PuntoAcceso(int id, String descripcion, double longitud, double latitud) {
        this.id = id;
        this.descripcion = descripcion;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
}
