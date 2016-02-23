package com.denunciaty.denunciaty.JavaClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLite {
    // Definiciones y constantes
    private static final String DATABASE_NAME = "denunciaty.db";

    private static final String TABLA_USUARIO = "usuario";
    private static final String TABLA_LOGUEADO = "logueado";

    private static final int DATABASE_VERSION = 1;

    private static final String ID = "id";
    private static final String NOMBRE = "nombre";
    private static final String APELLIDOS = "apellidos";
    private static final String NOMBRE_USUARIO = "nombre_usuario";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String FOTO = "foto";
    private static final String INGRESO = "ingreso";
    private static final String LOCALIDAD = "localidad";
    private static final String LOGUEADO = "logueado";
    private static final String GOOGLE = "google";



    private static final String DATABASE_CREATE = "CREATE TABLE "+TABLA_USUARIO+" (id text primary key,nombre text,apellidos text,nombre_usuario text,email text,password text,foto text,ingreso text,localidad text);";
    private static final String DATABASE_CREATE2 = "CREATE TABLE "+TABLA_LOGUEADO+" (logueado text primary key,google text);";

    private static final String DATABASE_DROP = "DROP TABLE IF EXISTS "+TABLA_USUARIO+";";
    private static final String DATABASE_DROP2 = "DROP TABLE IF EXISTS "+TABLA_LOGUEADO+";";

    // Contexto de la aplicación que usa la base de datos
    private final Context context;
    // Clase SQLiteOpenHelper para crear/actualizar la base de datos
    private MyDbHelper dbHelper;
    // Instancia de la base de datos
    private SQLiteDatabase db;

    public SQLite (Context c){
        context = c;
        dbHelper = new MyDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Método para abrir la base de datos
    public void open(){
        try{
            db = dbHelper.getWritableDatabase();
        }catch(SQLiteException e){
            db = dbHelper.getReadableDatabase();
        }
    }

    //Método para guardar el usuario logueado
    public void usuario(String id,String nombre,String apellidos,String nombre_usuario,String email,String password,String foto,String ingreso,String localidad){
        //Creamos un nuevo registro de valores a insertar
        ContentValues newValues = new ContentValues();
        //Asignamos los valores de cada campo
        newValues.put(ID,id);
        newValues.put(NOMBRE,nombre);
        newValues.put(APELLIDOS,apellidos);
        newValues.put(NOMBRE_USUARIO,nombre_usuario);
        newValues.put(EMAIL,email);
        newValues.put(PASSWORD,password);
        newValues.put(FOTO,foto);
        newValues.put(INGRESO,ingreso);
        newValues.put(LOCALIDAD,localidad);

        //insertamos
        db.insert(TABLA_USUARIO,null,newValues);

    }

    // Inserta true (Logueado) false (No logueado)
    public void logueado(String logueado,String google){
        //Creamos un nuevo registro de valores a insertar
        ContentValues newValues = new ContentValues();
        //Asignamos los valores de cada campo
        newValues.put(LOGUEADO,logueado);
        newValues.put(GOOGLE,google);


        //insertamos
        db.insert(TABLA_LOGUEADO,null,newValues);

    }

    //Recuperar true (Logueado) false (No logueado)
    public String recuperarLogueado(){
        String logueado = "";

        //Recuperamos en un cursor la consulta realizada
        Cursor cursor;

        cursor = db.query(TABLA_LOGUEADO,null,null,null,null,null,null);

        //Recorremos el cursor
        if (cursor != null && cursor.moveToFirst()){
            do{
                logueado =cursor.getString(0);
            }while (cursor.moveToNext());
        }


        return logueado;
    }

    //Recuperar true (Logueado) false (No logueado)
    public String recuperarGoogle(){
        String google = "";

        //Recuperamos en un cursor la consulta realizada
        Cursor cursor;

        cursor = db.query(TABLA_LOGUEADO,null,null,null,null,null,null);

        //Recorremos el cursor
        if (cursor != null && cursor.moveToFirst()){
            do{
                google =cursor.getString(1);
            }while (cursor.moveToNext());
        }


        return google;
    }



    //Método para recuperar el usuario
    public Usuario recuperarUsuario(){
        Usuario usuario = null;

        //Recuperamos en un cursor la consulta realizada
        Cursor cursor;

        cursor = db.query(TABLA_USUARIO,null,null,null,null,null,null);

        //Recorremos el cursor
        if (cursor != null && cursor.moveToFirst()){
            do{
                usuario = new Usuario(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
            }while (cursor.moveToNext());
        }

        return usuario;
    }

    //Resetea la tabla usuarios
    public void resetUsuario()
    {
        db.delete(TABLA_USUARIO, null, null);

    }

    //Resetea la tabla usuarios
    public void resetLogueado()
    {
        db.delete(TABLA_LOGUEADO,null,null);
    }



    private static class MyDbHelper extends SQLiteOpenHelper {

        public MyDbHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DATABASE_DROP);
            db.execSQL(DATABASE_DROP2);
            onCreate(db);
        }

    }


}
