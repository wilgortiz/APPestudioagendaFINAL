package com.nombreempresa.estudioagenda.ui.apuntes;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Apuntes;
import java.util.ArrayList;
import java.util.List;

public class ApuntesAdapter extends RecyclerView.Adapter<ApuntesAdapter.ApuntesViewHolder> {

    private List<Apuntes> apuntes = new ArrayList<>(); // Inicializar lista vacía
    private Context context;
    private ApuntesViewModel apuntesViewModel;

    public ApuntesAdapter(Context context, ApuntesViewModel apuntesViewModel) {
        this.context = context;
        this.apuntesViewModel = apuntesViewModel;
    }

    public static class ApuntesViewHolder extends RecyclerView.ViewHolder {
        public TextView tituloApunte;
        public TextView descripcionApunte;
        public TextView fechaApunte;

        public ApuntesViewHolder(View itemView) {
            super(itemView);
            tituloApunte = itemView.findViewById(R.id.tituloApunte);
            descripcionApunte = itemView.findViewById(R.id.textoApunte);


        }
    }

    @NonNull
    @Override
    public ApuntesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apunte, parent, false);
        return new ApuntesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApuntesViewHolder holder, int position) {
        Apuntes apunte = apuntes.get(position);

        // Validaciones para evitar null pointer exceptions
        if(apunte.getTitulo() != null) {
            holder.tituloApunte.setText(apunte.getTitulo());
        } else {
            holder.tituloApunte.setText("Sin título");
        }

        if(apunte.getDescripcion() != null) {
            holder.descripcionApunte.setText(apunte.getDescripcion());
        } else {
            holder.descripcionApunte.setText("Sin descripción");
        }



        holder.itemView.setOnClickListener(v -> {
            mostrarDialogoAcciones(apunte, position);
        });
    }

    private void mostrarDialogoAcciones(Apuntes apunte, int position) {
        try {
            CharSequence[] opciones = {"Ver detalles", "Editar", "Eliminar", "Cancelar"};

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Opciones para '" + apunte.getTitulo() + "'")
                    .setItems(opciones, (dialog, which) -> {
                        switch (which) {
                            case 0:
                                mostrarDetallesApunte(apunte);
                                break;
                            case 1:
                                if(context instanceof FragmentActivity) {
                                    DialogFragment dialogFragment = AgregarApunteDialogFragment.newInstanceForEdit(apunte);
                                    dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "EditarApunteDialog");
                                }
                                break;
                            case 2:
                                mostrarDialogoConfirmacionEliminar(apunte.getIdApunte(), position);
                                break;
                            case 3:
                                dialog.dismiss();
                                break;
                        }
                    });
            builder.create().show();
        } catch (Exception e) {
            Log.e("ApuntesAdapter", "Error en diálogo de acciones", e);
        }
    }

    private void mostrarDetallesApunte(Apuntes apunte) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(apunte.getTitulo() != null ? apunte.getTitulo() : "Apunte")
                    .setMessage((apunte.getDescripcion() != null ? apunte.getDescripcion() : "Sin descripción") +
                            "\n\nFecha: " + (apunte.getFechaCreacion() != null ? apunte.getFechaCreacion() : "No disponible"))
                    .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                    .show();
        } catch (Exception e) {
            Log.e("ApuntesAdapter", "Error mostrando detalles", e);
        }
    }

    private void mostrarDialogoConfirmacionEliminar(int idApunte, int position) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle("Eliminar apunte")
                    .setMessage("¿Estás seguro de que quieres eliminar este apunte?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        if(apuntesViewModel != null) {
                            apuntesViewModel.eliminarApunte(idApunte);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } catch (Exception e) {
            Log.e("ApuntesAdapter", "Error en diálogo de confirmación", e);
        }
    }

    @Override
    public int getItemCount() {
        return apuntes != null ? apuntes.size() : 0;
    }

    public void actualizarApuntes(List<Apuntes> nuevosApuntes) {
        if(nuevosApuntes != null) {
            this.apuntes = new ArrayList<>(nuevosApuntes);
            notifyDataSetChanged();
        }
    }
}