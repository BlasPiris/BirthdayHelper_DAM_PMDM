package com.example.birthdayhelper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.birthdayhelper.CLASS.Contacto;
import com.example.birthdayhelper.DB.DBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AlarmReciver extends BroadcastReceiver {
    DBManager dbManager;
    ArrayList<Contacto> contactos;

    @Override
    public void onReceive(Context context, Intent intent) {
        dbManager=new DBManager(context,null,null,1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);

        contactos=dbManager.getBithdayContacts(fecha);
        sendNotification(context,intent,contactos);
        sendSMS(context,intent,contactos);
        contactos.clear();

    }



    public void sendNotification(Context context, Intent intent, ArrayList<Contacto> contactos){
        Intent intent1=new Intent(context,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent1,0);

        if(contactos.size()>0){
            String nombres="";
            for(int i=0;i<contactos.size();i++) {
                if (i == 0) {
                    nombres += contactos.get(i).getNombre();
                } else {
                    nombres += ", " + contactos.get(i).getNombre();
                }
            }




        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"birthdayAlert")
                .setSmallIcon(R.drawable.icon_256x256)
                .setContentTitle("Cumpleaños de Hoy")
                .setContentText("Hoy cumplen años: "+nombres)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0,builder.build());
        }
        }

    private void sendSMS(Context context, Intent intent, ArrayList<Contacto> contactos) {

        for (int i = 0; i < contactos.size(); i++) {
            if(contactos.get(i).getTipoNot()==1) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(contactos.get(i).getTelefono(), null, contactos.get(i).getMensaje(), null, null);
                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(),
                            "SMS no enviado, por favor, inténtalo otra vez.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }
}