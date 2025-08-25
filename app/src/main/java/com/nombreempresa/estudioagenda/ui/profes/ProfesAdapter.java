package com.nombreempresa.estudioagenda.ui.profes;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.ui.materias.MateriasAdapter;
import com.nombreempresa.estudioagenda.ui.materias.MateriasViewModel;

import java.util.List;

public class ProfesAdapter extends RecyclerView.Adapter<ProfesAdapter.ProfesViewHolder> {

    private List<Profesor> profes;
    private Context context;
    private ProfesViewModel profesViewModel;

    public ProfesAdapter(List<Profesor> profes, Context context, ProfesViewModel profesViewModel) {
        this.profes = profes;
        this.context = context;
        this.profesViewModel = profesViewModel;
    }

    public class ProfesViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreProfe;
        //public TextView apellidoProfe;
        public TextView emailProfe;
        public TextView contactoProfe;

        public TextView periodoProfe;

        public ProfesViewHolder(View itemView) {
            super(itemView);
            nombreProfe = itemView.findViewById(R.id.nombreProfe);
            //apellidoProfe= itemView.findViewById(R.id.apellidoProfe);
            emailProfe= itemView.findViewById(R.id.emailProfe);
            contactoProfe= itemView.findViewById(R.id.contactoProfe);
            //periodoProfe = itemView.findViewById(R.id.periodoMateria);
        }
    }

    @Override
    public ProfesAdapter.ProfesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_profes, parent, false);
        return new ProfesAdapter.ProfesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfesAdapter.ProfesViewHolder holder, int position) {
//        Profesor profe = profes.get(position);
//        holder.nombreProfe.setText(profe.getNombre());
//       // holder.apellidoProfe.setText(profe.getApellido());
//        holder.emailProfe.setText(profe.getEmail());
//        holder.contactoProfe.setText(profe.getCelular());
        Profesor profe = profes.get(position);
        String nombreCompleto = profe.getNombre() + " " + profe.getApellido();
        holder.nombreProfe.setText(nombreCompleto);
        holder.emailProfe.setText(profe.getEmail());
        holder.contactoProfe.setText(profe.getCelular());

        holder.itemView.setOnClickListener(v -> {
            // Mostrar diálogo de acciones (editar/eliminar)
            mostrarDialogoAcciones(profe, position);
        });
    }

    private void mostrarDialogoAcciones(Profesor profesor, int position) {
        CharSequence[] opciones = {"Editar", "Eliminar", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Opciones para " + profesor.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            DialogFragment dialogFragment = AgregarProfesDialogFragment.newInstanceForEdit(profesor);
                            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "EditarProfesorDialog");
                            break;
                        case 1: // Eliminar
                            mostrarDialogoConfirmacionEliminar(profesor.getIdProfesor(), position);
                            break;
                        case 2: // Cancelar
                            dialog.dismiss();
                            break;
                    }
                });
        builder.create().show();
    }

    private void mostrarDialogoConfirmacionEliminar(int idProfesor, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar profesor")
                .setMessage("¿Estás seguro de que quieres eliminar este profesor?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    profesViewModel.eliminarProfe(idProfesor);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return profes.size();
    }

    public void actualizarProfes(List<Profesor> nuevosProfes) {
        profes.clear();
        profes.addAll(nuevosProfes);
        notifyDataSetChanged();
    }
}