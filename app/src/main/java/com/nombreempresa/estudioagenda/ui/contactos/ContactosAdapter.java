package com.nombreempresa.estudioagenda.ui.contactos;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Contactos;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.ui.profes.AgregarProfesDialogFragment;

import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactosAdapter.ContactosViewHolder> {

    private List<Contactos> contactos;
    private Context context;
    private ContactosViewModel contactosViewModel;

    public ContactosAdapter(List<Contactos> contactos, Context context, ContactosViewModel contactosViewModel) {
        this.contactos = contactos;
        this.context = context;
        this.contactosViewModel = contactosViewModel;
        Log.d("ContactosAdapter", "Tamaño de la lista: " + contactos.size());
    }

    public class ContactosViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreContacto;
        public TextView apellidoContacto;
        public TextView emailContacto;
        public TextView celularContacto;

        public ContactosViewHolder(View itemView) {
            super(itemView);
            nombreContacto = itemView.findViewById(R.id.nombreContacto);
            apellidoContacto = itemView.findViewById(R.id.apellidoContacto);
            emailContacto = itemView.findViewById(R.id.emailContacto);
            celularContacto = itemView.findViewById(R.id.celularContacto);
        }
    }

    @Override
    public ContactosAdapter.ContactosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_contactos, parent, false);
        return new ContactosAdapter.ContactosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactosAdapter.ContactosViewHolder holder, int position) {
        Contactos contacto = contactos.get(position);
        if (contacto != null) {
            holder.nombreContacto.setText(contacto.getNombre());
            holder.apellidoContacto.setText(contacto.getApellido());
            holder.emailContacto.setText(contacto.getEmail());
            holder.celularContacto.setText(contacto.getCelular());
            Log.d("ContactosAdapter", "Contacto en la posición " + position + ": " + contacto.getNombre() + " " + contacto.getApellido());
        } else {
            Log.e("ContactosAdapter", "Contacto es null en la posición " + position);
        }

        holder.itemView.setOnClickListener(v -> {
            // Mostrar diálogo de acciones (editar/eliminar)
            mostrarDialogoAcciones(contacto, position);
        });
    }

    private void mostrarDialogoAcciones(Contactos contacto, int position) {
        CharSequence[] opciones = {"Editar", "Eliminar", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Opciones para " + contacto.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            DialogFragment dialogFragment = AgregarContactosDialogFragment.newInstanceForEdit(contacto);
                            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "EditarContactoDialog");
                            break;
                        case 1: // Eliminar
                            mostrarDialogoConfirmacionEliminar(contacto.getIdContacto(), position);
                            break;
                        case 2: // Cancelar
                            dialog.dismiss();
                            break;
                    }
                });
        builder.create().show();
    }

    private void mostrarDialogoConfirmacionEliminar(int Id_Contacto, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar profesor")
                .setMessage("¿Estás seguro de que quieres eliminar este profesor?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    contactosViewModel.eliminarContactos(Id_Contacto);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public void actualizarContactos(List<Contactos> nuevoContacto) {
        contactos.clear();
        contactos.addAll(nuevoContacto);
        Log.d("ContactosAdapter", "Tamaño de la lista después de actualizar: " + contactos.size());
        notifyDataSetChanged();
    }
}