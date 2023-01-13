package com.example.birthdayhelper.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Editable;

import androidx.annotation.Nullable;

import com.example.birthdayhelper.CLASS.Contacto;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {


    public DBManager(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context,"BH_PMDM.db", null, 1);
    }


    //MÉTODO QUE CREA LA BASE DE DATOS EN CASO DE NO EXISTIR
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query="CREATE TABLE IF NOT EXISTS miscumples(ID integer PRIMARY KEY,TipoNotif char(1),Mensaje VARCHAR(160),Telefono" +
                " VARCHAR(15),FechaNacimiento VARCHAR(15), Nombre VARCHAR(128))";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query="DROP TABLE IF EXISTS miscumples";
        sqLiteDatabase.execSQL(query);
        onCreate(sqLiteDatabase);
    }

    //MÉTODO QUE ALMACENA LOS CONTACTOS EN LA BASE DE DATOS
    public void addContacts(ArrayList<Contacto> contactos){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        for(int i=0;i<contactos.size();i++){
            ContentValues cv=new ContentValues();
            cv.put("id",contactos.get(i).getIdContacto());
            cv.put("tipoNotif",contactos.get(i).getTipoNot());
            cv.put("mensaje",contactos.get(i).getMensaje());
            cv.put("telefono",contactos.get(i).getTelefono());
            cv.put("fechaNacimiento",contactos.get(i).getFechaNac());
            cv.put("nombre",contactos.get(i).getNombre());
            sqLiteDatabase.insert("miscumples",null,cv);
        }

    }

    //MÉTODO QUE OBTIENE LOS CONTACTOS DE LA BASE DE DATOS
    public ArrayList<Contacto> getContacts() {
        ArrayList <Contacto> contactos=new ArrayList<Contacto>();
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        cursor=sqLiteDatabase.rawQuery("SELECT * FROM miscumples ORDER BY nombre",null);

        if(cursor.moveToFirst()){
            do{
                Contacto contacto=new Contacto();
                contacto.setIdContacto(cursor.getInt(0));
                contacto.setTipoNot(cursor.getInt(1));
                contacto.setMensaje(cursor.getString(2));
                contacto.setTelefono(cursor.getString(3));
                contacto.setFechaNac(cursor.getString(4));
                contacto.setNombre(cursor.getString(5));
                contactos.add(contacto);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return contactos;
    }


    //MÉTODO QUE MODIFICA EL USUARIO EN LA BASE DE DATOS
    public void updateContact(Contacto contacto) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query="UPDATE miscumples SET" +
                " nombre = '"+contacto.getNombre()+"' ," +
                " telefono = '"+contacto.getTelefono()+"' ," +
                " FechaNacimiento = '"+contacto.getFechaNac()+"' ," +
                " TipoNotif = '"+contacto.getTipoNot()+"' ," +
                " Mensaje = '"+contacto.getMensaje()+"'" +
                " WHERE id LIKE '"+contacto.getIdContacto()+"'";
        sqLiteDatabase.execSQL(query);
    }

    //METODO QUE OBTIENE TODOS LOS USUARIOS QUE TIENEN FECHA DE NACIMIENTO COINCIDENTE CON HOY
    public ArrayList<Contacto> getBithdayContacts(String fecha) {
        ArrayList <Contacto> contactos=new ArrayList<Contacto>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM miscumples WHERE fechaNacimiento LIKE '"+fecha+"%'ORDER BY nombre", null);

        if(cursor.moveToFirst()){
            do{
                Contacto contacto=new Contacto();
                contacto.setIdContacto(cursor.getInt(0));
                contacto.setTipoNot(cursor.getInt(1));
                contacto.setMensaje(cursor.getString(2));
                contacto.setTelefono(cursor.getString(3));
                contacto.setFechaNac(cursor.getString(4));
                contacto.setNombre(cursor.getString(5));
                contactos.add(contacto);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return contactos;
    }
}
