package com.example.birthdayhelper.CLASS;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Contacto implements Serializable {
        int idContacto;
        String nombre;
        String mensaje;
        String telefono;
        String fechaNac;
        Bitmap avatar;
        int tipoNot;


        public  Contacto(){

        }

        public Contacto(int idContacto, String nombre, String telefono) {
                this.idContacto = idContacto;
                this.nombre = nombre;
                this.telefono = telefono;
                this.fechaNac="";
        }

        public Contacto(int idContacto, String nombre, String telefono, Bitmap avatar) {
                this.idContacto = idContacto;
                this.nombre = nombre;
                this.telefono = telefono;
                this.avatar = avatar;
                this.fechaNac="";
        }

        public int getIdContacto() {
                return idContacto;
        }

        public void setIdContacto(int idContacto) {
                this.idContacto = idContacto;
        }

        public String getNombre() {
                return nombre;
        }

        public void setNombre(String nombre) {
                this.nombre = nombre;
        }

        public String getMensaje() {
                return mensaje;
        }

        public void setMensaje(String mensaje) {
                this.mensaje = mensaje;
        }

        public String getTelefono() {
                return telefono;
        }

        public void setTelefono(String telefono) {
                this.telefono = telefono;
        }

        public String getFechaNac() {
                return fechaNac;
        }

        public void setFechaNac(String fechaNac) {
                this.fechaNac = fechaNac;
        }

        public Bitmap getAvatar() {
                return avatar;
        }

        public void setAvatar(Bitmap avatar) {
                this.avatar = avatar;
        }

        public int getTipoNot() {return tipoNot;}

        public void setTipoNot(int tipoNot) {
                this.tipoNot = tipoNot;
        }


}
