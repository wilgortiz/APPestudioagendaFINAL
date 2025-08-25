package com.nombreempresa.estudioagenda.ui.materias;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.modelos.ProfesorMateria;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MateriasAdapter extends RecyclerView.Adapter<MateriasAdapter.MateriaViewHolder> {

    private List<Materia> materias;
    private Context context;
    private MateriasViewModel materiasViewModel;

    public MateriasAdapter(List<Materia> materias, Context context, MateriasViewModel materiasViewModel) {
        this.materias = materias;
        this.context = context;
        this.materiasViewModel = materiasViewModel;
    }

//    public class MateriaViewHolder extends RecyclerView.ViewHolder {
//        public TextView nombreMateria;
//        public TextView periodoMateria;
//
//        public MateriaViewHolder(View itemView) {
//            super(itemView);
//            nombreMateria = itemView.findViewById(R.id.nombreMateria);
//            periodoMateria = itemView.findViewById(R.id.periodoMateria);
//        }
//    }


    public class MateriaViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreMateria;
        public TextView periodoMateria;
        public TextView profesoresMateria;

        public MateriaViewHolder(View itemView) {
            super(itemView);
            nombreMateria = itemView.findViewById(R.id.nombreMateria);
            periodoMateria = itemView.findViewById(R.id.periodoMateria);
            profesoresMateria = itemView.findViewById(R.id.profesoresMateria);
        }
    }


    @Override
    public MateriaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_materias, parent, false);
        return new MateriaViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MateriaViewHolder holder, int position) {
        Materia materia = materias.get(position);
        holder.nombreMateria.setText(materia.getNombre());
        holder.periodoMateria.setText("Periodo: " + materia.getPeriodo());

        // Mostrar profesores
        if (materia.getProfesorMateria() != null && !materia.getProfesorMateria().isEmpty()) {
            StringBuilder profesores = new StringBuilder("Profesores: ");
            for (ProfesorMateria pm : materia.getProfesorMateria()) {
                if (pm.getProfesor() != null) {
                    profesores.append(pm.getProfesor().getNombre())
                            .append(" ")
                            .append(pm.getProfesor().getApellido())
                            .append(", ");
                }
            }
            // Eliminar la última coma y espacio
            if (profesores.length() > 11) {
                profesores.setLength(profesores.length() - 2);
            }
            holder.profesoresMateria.setText(profesores.toString());
        } else {
            holder.profesoresMateria.setText("Sin profesores asignados");
        }

        holder.itemView.setOnClickListener(v -> {
            mostrarDialogoAcciones(materia, position);
        });
    }


//    @Override
//    public void onBindViewHolder(MateriaViewHolder holder, int position) {
//        Materia materia = materias.get(position);
//        holder.nombreMateria.setText(materia.getNombre());
//        holder.periodoMateria.setText("Periodo: " + materia.getPeriodo());
//
//        holder.itemView.setOnClickListener(v -> {
//            // Mostrar diálogo de acciones (editar/eliminar)
//            mostrarDialogoAcciones(materia, position);
//        });
//    }



    private void mostrarDialogoAcciones(Materia materia, int position) {
        CharSequence[] opciones = {"Editar", "Eliminar", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Opciones para " + materia.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            DialogFragment dialogFragment = AgregarMateriasDialogFragment.newInstanceForEdit(materia);
                            dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "EditarMateriaDialog");
                            break;
                        case 1: // Eliminar
                            mostrarDialogoConfirmacionEliminar(materia.getIdMateria(), position);
                            break;
                        case 2: // Cancelar
                            dialog.dismiss();
                            break;
                    }
                });
        builder.create().show();
    }

    private void mostrarDialogoConfirmacionEliminar(int idMateria, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar materia")
                .setMessage("¿Estás seguro de que quieres eliminar esta materia?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    materiasViewModel.eliminarMateria(idMateria);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return materias.size();
    }

    public void actualizarMaterias(List<Materia> nuevasMaterias) {
        materias.clear();
        materias.addAll(nuevasMaterias);
        notifyDataSetChanged();
    }
}