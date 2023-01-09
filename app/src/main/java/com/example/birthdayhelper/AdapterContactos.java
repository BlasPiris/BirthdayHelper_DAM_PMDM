package com.example.birthdayhelper;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birthdayhelper.CLASS.Contacto;

import java.util.ArrayList;

public class AdapterContactos extends RecyclerView.Adapter<AdapterContactos.ViewHolderContactos> {

    ArrayList<Contacto> contactoArrayList;
    Context mContext;

    public AdapterContactos(ArrayList<Contacto> contactoArrayList) {
        this.contactoArrayList=contactoArrayList;
    }

    @NonNull
    @Override
    public ViewHolderContactos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,null,false);
        mContext = parent.getContext();
        return new  ViewHolderContactos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderContactos holder, int position) {
        Contacto contato=contactoArrayList.get(position);
        holder.asignarDatos(contato);
        holder.costrainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, EditContactActivity.class);
                intent.putExtra("contacto", contato);
                mContext.startActivity(intent);

            }

        });


    }

    @Override
    public int getItemCount() {
        return this.contactoArrayList.size();
    }



    public class ViewHolderContactos extends RecyclerView.ViewHolder {

        TextView name,number,date;
        ImageView avatar;
        ConstraintLayout costrainLayout;
        public ViewHolderContactos(@NonNull View itemView) {

            super(itemView);
            name=itemView.findViewById(R.id.TextView1);
            number=itemView.findViewById(R.id.phone);
            avatar=itemView.findViewById(R.id.avatar);
            date=itemView.findViewById(R.id.fechaNac);
            costrainLayout=itemView.findViewById(R.id.costrainLayout);

        }

        public void asignarDatos(Contacto contacto) {
            name.setText(contacto.getNombre());
            number.setText(contacto.getTelefono());
            if(contacto.getAvatar()!=null){
                avatar.setImageBitmap(contacto.getAvatar());
            }





        }
    }
}
