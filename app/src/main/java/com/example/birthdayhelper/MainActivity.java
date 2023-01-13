package com.example.birthdayhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.birthdayhelper.CLASS.Contacto;
import com.example.birthdayhelper.DB.DBManager;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    EditText searchName;
    RecyclerView recyclerView;
    int PERMISSIONS_REQUEST_READ_CONTACTS=100;

    DBManager dbManager;
    AlarmManager alarmManager;

    ArrayList<Contacto> arrayContactos;
    ArrayList<Contacto> arrayContactosdb;
    AdapterContactos adapterContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CREACION DEL CANAL DE NOTIFICACIONES
        createNotificactionChannel();

        //INSTANCIAMOS LA CLASE DE LA BASE DE DATOS
        dbManager=new DBManager(MainActivity.this,null,null,1);


        //RECYCLERVIEW
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //REQUERIR PERMISOS PARA OBTENER CONTACTOS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        //REQUERIR PERMISOS PARA ENVIAR SMS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

     getContact();



        //EDITTEXT QUE BUSCARÁ LOS CONTACTOS POR NOMBRE
        searchName = (EditText) findViewById(R.id.searchName);
        searchName.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable search) {
                ArrayList<Contacto> searchContact=searchContact(search.toString());
                AdapterContactos adapterContactos=new AdapterContactos(searchContact);
                recyclerView.setAdapter(adapterContactos);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getContact();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getContact();
    }

    //MÉTODO QUE OBTIENE Y ALMACENA  LOS CONTACTOS Y LOS INSERTA EN LOS EN RECYCLERVIEW
    private void getContact() {
        arrayContactosdb=dbManager.getContacts();

        if(arrayContactosdb.size()!=0){
            adapterContactos = new AdapterContactos(arrayContactosdb);
            recyclerView.setAdapter(adapterContactos);
            arrayContactos=getContactList();
            dbManager.addContacts(arrayContactos);
            ArrayList<Contacto> arrayContactosFinal=
                    setImage(arrayContactos,arrayContactosdb);
            arrayContactos=getContactList();
            adapterContactos = new AdapterContactos(arrayContactosFinal);
            recyclerView.setAdapter(adapterContactos);
        }else{
            arrayContactos=getContactList();
            adapterContactos = new AdapterContactos(arrayContactos);
            recyclerView.setAdapter(adapterContactos);
            dbManager.addContacts(arrayContactos);
        }
    }

    //METODO QUE INTRODUCE UN MENÚ EN EL ACTIVITY
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //METODO QUE ABRE EL TIMEPICKER
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        mostrarTimePicker();
        return super.onOptionsItemSelected(item);
    }

    private void mostrarTimePicker() {
      //CREAMOS EL TIMEPIKER
        MaterialTimePicker   picker = new MaterialTimePicker.Builder()
                //Le doy formato
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(0)
                .setMinute(0)
                .setTitleText("Hora envío notificaciones")
                .build();

        //MOSTRAMOS EL TIMEPICKER
        picker.show(getSupportFragmentManager(), "Hora envio notificiaciones");

        //EVENTO CUANDO PULSEMOS EL BOTON POSITIVO
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GUARDAMOS LA HORA
                Calendar horaNotificacion = Calendar.getInstance();
                horaNotificacion.set(Calendar.HOUR_OF_DAY, picker.getHour());
                horaNotificacion.set(Calendar.MINUTE, picker.getMinute());
                horaNotificacion.set(Calendar.SECOND, 0);
                horaNotificacion.set(Calendar.MILLISECOND, 0);

                //UTILIZAMOS EL SERVICIO DE ALARMMANAGER, QUE REALIZARÁ LAS FUNICIONES DE NOTIFICACIÓN
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent=new Intent(MainActivity.this,AlarmReciver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, horaNotificacion.getTimeInMillis(),pendingIntent);
                Toast.makeText(MainActivity.this, "ALARMA ACTIVADA", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //MÉTODO QUE DEVUELVE LOS CONTACTOS DEL TELÉFONO
    private ArrayList getContactList() {
        ArrayList<Contacto> contactoArrayList=new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                 String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                 String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

              String photo = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                Uri photoUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(id), photo);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        //OBTENEMOS TODOS LOS DATOS DEL CONTACTO Y LOS GUADAMOS EN UN OBJETO DE LA
                        //CLASE CONTACTOS
                        Contacto newContacto=new Contacto(Integer.parseInt(id),name,phoneNo);



                        //EN CASO DE QUE EL CONTACTO TENGA IMAGEN, LA CONVERTIMOS Y LA AÑADIMOS AL OBJETO
                            if(photoUri!=null){
                                Bitmap foto = null;
                               try {
                                   InputStream input =
                                           ContactsContract.Contacts.openContactPhotoInputStream(
                                                   getContentResolver(),
                                                   ContentUris.withAppendedId(
                                                           ContactsContract.Contacts.CONTENT_URI,
                                                           Long.parseLong(id))
                                           );
                                   if (input != null) {
                                       foto = BitmapFactory.decodeStream(input);
                                       input.close();
                                   }

                               } catch (IOException iox) { }
                            newContacto.setAvatar(foto);
                            }

                        //AÑADIMOS EL CONTACTO A UN ARRAY DE CONTACTOS
                            contactoArrayList.add(newContacto);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }

        //DEVOLVEMOS EL ARRAY
        return contactoArrayList;
    }

    //MÉTODO QUE NOS AÑADE LA IMAGEN A LOS DATOS ALMACENADOS DE LA BASE DE DATOS
    private ArrayList<Contacto> setImage(ArrayList<Contacto> arrayContactos, ArrayList<Contacto> arrayContactosdb) {

    if(arrayContactosdb.size()<=arrayContactos.size()){
        for(int i=0;i<arrayContactosdb.size();i++){

            arrayContactosdb.get(i).setAvatar(arrayContactos.get(i).getAvatar());
        }
    }else{
        for(int i=0;i<arrayContactos.size();i++){

            arrayContactosdb.get(i).setAvatar(arrayContactos.get(i).getAvatar());
        }
    }
        return arrayContactosdb;
    }

    //MÉTODO QUE CREA EL CANAL DE NOTIFICACIÓN
    private  void createNotificactionChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("birthdayAlert",
                    "Birthday Helper Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal de Birthday Helper");

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //METODO QUE DEVUELVE LOS CONTACTOS QUE TIENEN UN PARECIDO CON LA BUSQUEDA
    private ArrayList<Contacto> searchContact(String search){
        ArrayList<Contacto> searchContact=new ArrayList<>();

        for(int i=0;i<arrayContactos.size();i++){
            if(arrayContactos.get(i).getNombre().contains(search) || arrayContactos.get(i).getTelefono().contains(search)){
                searchContact.add(arrayContactos.get(i));
            }
        }
        return searchContact;
    }
    
}