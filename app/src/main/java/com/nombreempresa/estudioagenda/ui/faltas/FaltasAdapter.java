package com.nombreempresa.estudioagenda.ui.faltas;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.MainActivity;
import com.nombreempresa.estudioagenda.R;

import com.nombreempresa.estudioagenda.modelos.Faltas;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.ui.materias.AgregarMateriasDialogFragment;
import com.nombreempresa.estudioagenda.ui.materias.MateriasAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;




//anda
public class FaltasAdapter extends RecyclerView.Adapter<FaltasAdapter.FaltasViewHolder> {

    private List<Map<String, Object>> materias;
    private Context context;
    private FaltasViewModel faltasViewModel;

    public FaltasAdapter(List<Map<String, Object>> materias, Context context, FaltasViewModel faltasViewModel) {
        this.materias = materias;
        this.context = context;
        this.faltasViewModel = faltasViewModel;
    }

    @NonNull
    @Override
    public FaltasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_faltas, parent, false);
        return new FaltasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaltasViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Map<String, Object> materia = materias.get(position);

        // Configurar vista con datos de la materia
        configurarVista(holder, materia);

        // Configurar listeners de los botones
        configurarBotones(holder, position, materia);

        holder.itemView.setOnClickListener(v -> {
            // Mostrar diálogo de acciones (editar/eliminar)
            mostrarDialogoAcciones(materia, position);
        });
    }



    private void mostrarDialogoAcciones(Map<String, Object> materia, int position) {
        CharSequence[] opciones = {"Editar", "Eliminar", "Cancelar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Opciones para " + materia.get("nombre"))
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            // Crear un diálogo específico para editar faltas
                            //DialogFragment dialogFragment = EditarFaltaDialogFragment.newInstance(materia);
                            //dialogFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "EditarFaltaDialog");
                            break;
                        case 1: // Eliminar
                            mostrarDialogoConfirmacionEliminar((int) materia.get("id_falta"), position);
                            break;
                        case 2: // Cancelar
                            dialog.dismiss();
                            break;
                    }
                });
        builder.create().show();
    }

    private void mostrarDialogoConfirmacionEliminar(int idFalta, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar falta")
                .setMessage("¿Estás seguro de que quieres eliminar esta falta?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    faltasViewModel.eliminarFaltas(idFalta);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }





    private void configurarVista(FaltasViewHolder holder, Map<String, Object> materia) {
        String nombreMateria = (String) materia.get("nombre");
        if (nombreMateria == null || nombreMateria.isEmpty()) {
            nombreMateria = "Materia " + materia.get("idMateria");
        }

        holder.tvMateria.setText(nombreMateria);
        holder.tvFaltas.setText(String.valueOf(materia.get("cantidad")));
        //holder.tvFaltasPermitidas.setText(String.valueOf(faltas.get("faltas_permitidas")));
    }

    private void configurarBotones(FaltasViewHolder holder, int position, Map<String, Object> materia) {
        holder.btnAgregarFalta.setOnClickListener(v -> {
            int idFalta = obtenerIdFalta(materia);
            int idMateria = (int) materia.get("idMateria");
            int cantidadActual = (int) materia.get("cantidad");
            int nuevaCantidad = cantidadActual + 1;

            actualizarFaltaLocalYRemota(holder, position, materia, idFalta, idMateria, nuevaCantidad);
        });

        holder.btnQuitarFalta.setOnClickListener(v -> {
            int cantidadActual = (int) materia.get("cantidad");
            if (cantidadActual > 0) {
                int idFalta = obtenerIdFalta(materia);
                int idMateria = (int) materia.get("idMateria");
                int nuevaCantidad = cantidadActual - 1;

                actualizarFaltaLocalYRemota(holder, position, materia, idFalta, idMateria, nuevaCantidad);
            } else {
                Toast.makeText(context, "No se pueden tener faltas negativas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int obtenerIdFalta(Map<String, Object> materia) {
        if (!materia.containsKey("id_falta")) {
            throw new IllegalStateException("Missing id_falta in materia: " + materia.toString());
        }
        return (int) materia.get("id_falta");
    }

    private void actualizarFaltaLocalYRemota(FaltasViewHolder holder, int position,
                                             Map<String, Object> materia,
                                             int idFalta, int idMateria,
                                             int nuevaCantidad) {
        try {
            // 1. Validate input
            if (materia == null) {
                throw new IllegalArgumentException("Materia map is null");
            }

            // 2. Update local data
            materia.put("cantidad", nuevaCantidad);

            // 3. Update UI
            holder.tvFaltas.setText(String.valueOf(nuevaCantidad));
            notifyItemChanged(position);

            // 4. Update in ViewModel
            faltasViewModel.modificarCantidadFalta(idFalta,idMateria, nuevaCantidad);

            Log.d("FALTA_ACTUALIZADA",

                    "ID Falta: " + idFalta +
                            " | ID Materia: " + idMateria +
                            " | Nueva cantidad: " + nuevaCantidad);
        } catch (Exception e) {
            Log.e("FALTA_ERROR", "Error updating falta", e);
            Toast.makeText(context, "Error al actualizar falta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return materias != null ? materias.size() : 0;
    }
    public void actualizarFaltas(List<Map<String, Object>> nuevasMaterias) {
        Log.d("ADAPTER_UPDATE", "Actualizando con " + (nuevasMaterias != null ? nuevasMaterias.size() : 0) + " items");
        this.materias = nuevasMaterias != null ? nuevasMaterias : new ArrayList<>();
        notifyDataSetChanged();
    }



    public static class FaltasViewHolder extends RecyclerView.ViewHolder {
        TextView tvMateria;
        TextView tvFaltasPermitidas;
        TextView tvFaltas;
        Button btnAgregarFalta;
        Button btnQuitarFalta;

        public FaltasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMateria = itemView.findViewById(R.id.tvMateria);
            tvFaltas = itemView.findViewById(R.id.tvFaltas);
            btnAgregarFalta = itemView.findViewById(R.id.btnAgregarFalta);
            btnQuitarFalta = itemView.findViewById(R.id.btnQuitarFalta);
            tvFaltasPermitidas= itemView.findViewById(R.id.tvFaltasPermitidas);
        }
    }
}



