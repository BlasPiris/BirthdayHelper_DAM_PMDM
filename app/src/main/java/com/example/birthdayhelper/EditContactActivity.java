package com.example.birthdayhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.birthdayhelper.CLASS.Contacto;
import com.example.birthdayhelper.DB.DBManager;

import java.util.ArrayList;

public class EditContactActivity extends AppCompatActivity {

    EditText name,fechaNac,msj;
    Spinner tel;
    Switch smsCheck;
    Button openContact,saveContact;
    Contacto contacto;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Intent i = getIntent();
        contacto = (Contacto)i.getSerializableExtra("contacto");


        openContact=findViewById(R.id.verContacto);
        openContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContact(contacto.getIdContacto());
            }
        });


        name=findViewById(R.id.name);
        name.setText(contacto.getNombre());

        tel=findViewById(R.id.tel);
        ArrayList<String> arraySpinner=new ArrayList<>();
        arraySpinner.add(contacto.getTelefono());



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tel.setAdapter(adapter);

        fechaNac=findViewById(R.id.fechaNac);
        fechaNac.setText(contacto.getFechaNac());

        msj=findViewById(R.id.msj);
        smsCheck=findViewById(R.id.switchSms);
        msj.setText(contacto.getMensaje());
        if(contacto.getTipoNot()!=0){
            smsCheck.setChecked(true);
            msj.setEnabled(true);
        }else{
            smsCheck.setChecked(false);
            msj.setEnabled(false);
        }

        smsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    msj.setEnabled(true);
                }else{
                    msj.setEnabled(false);
                }

            }
        });

        saveContact=findViewById(R.id.guardarContacto);
        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                saveContact();
            }
        });




    }

    public void openContact(int idContacto){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(idContacto));
        intent.setData(uri);
        startActivity(intent);
    }

    private void saveContact() {

        contacto.setNombre(name.getText().toString());
        contacto.setTelefono(tel.getSelectedItem().toString());
        contacto.setFechaNac(fechaNac.getText().toString());
        if(smsCheck.isChecked()){
            contacto.setTipoNot(1);
        }else{
            contacto.setTipoNot(0);
        }
        contacto.setMensaje(msj.getText().toString());

        DBManager dbManager=new DBManager(EditContactActivity.this,null,null,1);
        dbManager.updateContact(contacto);
        Intent intent = new Intent(EditContactActivity.this, MainActivity.class);
        startActivity(intent);
    }







}