package com.denunciaty.denunciaty.JavaClasses;

public class Reporte {
    private int id;
    private int imagen;
    private String titulo;
    private String descripcion;
    private String ubicacion;
    private String tipoIncidente;
    private boolean solucionado;
    private double latitud;
    private double longitud;
    private int usuario_id;

    public Reporte(int id, int imagen,String titulo, String descripcion, String ubicacion, String tipoIncidente, Boolean solucionado,Double latitud,Double longitud,Integer usuario_id){
        this.id=id;
        this.imagen=imagen;
        this.titulo=titulo;
        this.descripcion=descripcion;
        this.ubicacion=ubicacion;
        this.tipoIncidente=tipoIncidente;
        this.solucionado=solucionado;
        this.latitud=latitud;
        this.longitud=longitud;
        this.usuario_id=usuario_id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTipoIncidente() {
        return tipoIncidente;
    }

    public void setTipoIncidente(String tipoIncidente) {
        this.tipoIncidente = tipoIncidente;
    }

    public boolean isSolucionado() {
        return solucionado;
    }

    public void setSolucionado(boolean solucionado) {
        this.solucionado = solucionado;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
