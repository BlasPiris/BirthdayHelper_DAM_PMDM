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

    public String addRecord(Contacto contacto){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("id",contacto.getIdContacto());
        cv.put("tipoNotif",contacto.getTipoNot());
        cv.put("mensaje",contacto.getMensaje());
        cv.put("telefono",contacto.getTelefono());
        cv.put("fechaNacimiento",contacto.getFechaNac());
        cv.put("nombre",contacto.getNombre());

        long res=sqLiteDatabase.insert("miscumples",null,cv);

        if(res!=1){
            return "Insertado";
        }else{
            return "Fallo insercion";
        }
    }

    public void addContacts(ArrayList<Contacto> contactos){

        for(int i=0;i<contactos.size();i++){
            SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();
            ContentValues cv=new ContentValues();
            cv.put("id",contactos.get(i).getIdContacto());
            cv.put("tipoNotif",contactos.get(i).getTipoNot());
            cv.put("mensaje",contactos.get(i).getMensaje());
            cv.put("telefono",contactos.get(i).getTelefono());
            cv.put("fechaNacimiento",contactos.get(i).getFechaNac());
            cv.put("nombre",contactos.get(i).getNombre());

            long res=sqLiteDatabase.insert("miscumples",null,cv);
        }

    }

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

    public ArrayList<Contacto> getSearchContacts(Editable name) {
        ArrayList <Contacto> contactos=new ArrayList<Contacto>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM miscumples WHERE nombre LIKE '%"+name+"%'ORDER BY nombre", null);

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

//    //Método encargado de actualizar contactos de la base de datos
//    public void updateContactoSQLite(Contacto c) {
//
//        openOrCreateDatabase();
//        ContentValues cv = new ContentValues();
//
//        //Al igual que en el método anterior, con el .put indico en que columna se va a hacer el update y le asigno un nuevo valor
//        cv.put("TipoNotif", c.getTipoNotif());
//        cv.put("Mensaje", c.getMensaje());
//        cv.put("Telefono", c.getTelefonos().get(0));
//        cv.put("FechaNacimiento", c.getFechaNacimiento());
//        cv.put("Nombre", c.getNombre());
//
//        //Aquí se crea la query que va a hacer el udate, extrayendo los datos aanteriores, comprueba con el ID de contacto, que contacto ha de actualizar
//        db.update("miscumples", cv, "ID = ?", new String[]{String.valueOf(c.getId())});
//        db.close();
//    }


}
