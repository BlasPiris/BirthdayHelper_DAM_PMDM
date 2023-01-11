package com.example.birthdayhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.example.birthdayhelper.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.sql.SQLData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    EditText searchName;
    RecyclerView recyclerView;
    int PERMISSIONS_REQUEST_READ_CONTACTS=100;
    DBManager dbManager;

    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificactionChannel();







        dbManager=new DBManager(MainActivity.this,null,null,1);

        searchName = (EditText) findViewById(R.id.searchName);
        searchName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable name) {
                AdapterContactos adapterContactos=new AdapterContactos(dbManager.getSearchContacts(name));
                recyclerView.setAdapter(adapterContactos);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

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




        if(dbManager.getContacts().size()!=0) {
            AdapterContactos adapterContactos = new AdapterContactos(dbManager.getContacts());
            recyclerView.setAdapter(adapterContactos);
            dbManager.addContacts(getContactList());
        }else{
            dbManager.addContacts(getContactList());
            AdapterContactos adapterContactos = new AdapterContactos(dbManager.getContacts());
            recyclerView.setAdapter(adapterContactos);
        }


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
                Calendar horaNotificacion = Calendar.getInstance();
                horaNotificacion.set(Calendar.HOUR_OF_DAY, picker.getHour());
                horaNotificacion.set(Calendar.MINUTE, picker.getMinute());
                horaNotificacion.set(Calendar.SECOND, 0);
                horaNotificacion.set(Calendar.MILLISECOND, 0);


                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent intent=new Intent(MainActivity.this,AlarmReciver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, horaNotificacion.getTimeInMillis(),pendingIntent);
                Toast.makeText(MainActivity.this, "ALARMA ACTIVADA", Toast.LENGTH_SHORT).show();




            }
        });
    }


    private ArrayList getContactList() {
        ArrayList<Contacto> contactoArrayList=new ArrayList<>();
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
                            Contacto newContacto=new Contacto(Integer.parseInt(id),name,phoneNo);
                            contactoArrayList.add(newContacto);
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

    private  void createNotificactionChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel("birthdayAlert",
                    "Birthday Helper Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal de Birthday Helper");

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

}