package com.example.birthdayhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.birthdayhelper.CLASS.Contacto;
import com.example.birthdayhelper.DB.DBManager;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.sql.SQLData;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    int PERMISSIONS_REQUEST_READ_CONTACTS=100;
    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));





        //If que da permiso a mi aplicación para leer los contactos de mi movil
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        //If que sirve para permitir que mi aplicación mande un sms a un contacto
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "Permisos denegados para mandar SMS");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }



        dbManager=new DBManager(MainActivity.this,null,null,1);
        dbManager.addContacts(getContactList());
        AdapterContactos adapterContactos=new AdapterContactos(dbManager.getContacts());








        recyclerView.setAdapter(adapterContactos);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        mostrarTimePicker();

        return super.onOptionsItemSelected(item);

    }

    private void mostrarTimePicker() {
        //Construyo un nuevo objeto MaterialTimePicker
        MaterialTimePicker   picker = new MaterialTimePicker.Builder()
                //Le doy formato
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Hora envío notificaciones")
                .build();
        //Muestro la ventana creada
        picker.show(getSupportFragmentManager(), "notificacionAlarma");
        //Si clicko en el botón Ok, hará lo siguiente
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Guardo la hora a la que se va a enviar la notificación
                Calendar horaNotificacionCumple = Calendar.getInstance();
                horaNotificacionCumple.set(Calendar.HOUR_OF_DAY, picker.getHour());
                horaNotificacionCumple.set(Calendar.MINUTE, picker.getMinute());
                horaNotificacionCumple.set(Calendar.SECOND, 0);
//                try {
//                    //Compruebo que contactos hacen hoy los años
//                    cumpleDeContactos = comprobarCumples();
//                    idCumpleDeContactos = guardarIdContactosCumple();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                //Lanzo la alarma si hay contactos que hagan los años (Si esta lleno el listado)
//                if(cumpleDeContactos != null && ! cumpleDeContactos.isEmpty()){
//                    startAlarma();
//                }

            }
        });
    }


    private ArrayList getContactList() {
        ArrayList<Contacto> contactoArrayList=new ArrayList<>();
        ArrayList<String> telefonos=new ArrayList<>();
        String lastName="";

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

              String photo = cur.getString(cur.getColumnIndex(
                       ContactsContract.Contacts.PHOTO_ID));


                Uri photoUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(id), photo);




                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));


                        //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                        //Contacto newContacto=new Contacto(Integer.parseInt(id),name,phoneNo,getPhoto(photoUri));

                        if(lastName.equals(name)){

                        }else{
                            Contacto newContacto=new Contacto(Integer.parseInt(id),name,phoneNo);
                            contactoArrayList.add(newContacto);
                        }


                    }

                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        return contactoArrayList;
    }


}