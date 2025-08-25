package com.nombreempresa.estudioagenda.ui.notas;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Calificaciones;
import com.nombreempresa.estudioagenda.modelos.Materia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.NotasViewHolder> {

    private List<Map<String, Object>> materias;
    private Context context;
    private NotasViewModel notasViewModel;

    public NotasAdapter(List<Map<String, Object>> materias, Context context, NotasViewModel notasViewModel) {
        this.materias = materias;
        this.context = context;
        this.notasViewModel = notasViewModel;
    }

    @NonNull
    @Override
    public NotasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_notas, parent, false);
        return new NotasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotasViewHolder holder, int position) {
        try {
            Map<String, Object> item = materias.get(position);
            Materia materia = (Materia) item.get("materia");
            List<Calificaciones> calificaciones = (List<Calificaciones>) item.get("calificaciones");
            int cantidadNotas = calificaciones != null ? calificaciones.size() : 0;

            holder.tvMateria.setText(materia.getNombre());

            // Mostrar todas las notas en formato legible
            StringBuilder notasText = new StringBuilder();
            if (cantidadNotas > 0) {
                notasText.append(cantidadNotas).append(" nota").append(cantidadNotas != 1 ? "s" : "").append(": ");

                // Agregar cada nota separada por coma
                for (int i = 0; i < calificaciones.size(); i++) {
                    notasText.append(String.format("%.1f", calificaciones.get(i).getCalificacion()));
                    if (i < calificaciones.size() - 1) {
                        notasText.append(", ");
                    }
                }

                // Calcular y agregar el promedio
                float suma = 0;
                for (Calificaciones calificacion : calificaciones) {
                    suma += calificacion.getCalificacion();
                }
                float promedio = suma / cantidadNotas;
                //notasText.append("\n").append(" - Promedio: ").append(String.format("%.1f", promedio));
                notasText.append("  (Promedio): ").append(String.format("%.1f", promedio));
            } else {
                notasText.append("Sin notas");
            }

            holder.tvNota.setText(notasText.toString());

            // Configurar clic para mostrar detalle de notas
            holder.itemView.setOnClickListener(v -> {
                mostrarDialogoNotas(materia, calificaciones);
            });

        } catch (Exception e) {
            Log.e("ADAPTER_ERROR", "Error en onBindViewHolder", e);
            holder.tvMateria.setText("Error al cargar");
            holder.tvNota.setText("");
        }
    }

private void mostrarDialogoNotas(Materia materia, List<Calificaciones> calificaciones) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle("Notas de " + materia.getNombre());

    if (calificaciones == null || calificaciones.isEmpty()) {
        builder.setMessage("No hay notas registradas para esta materia.");
    } else {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < calificaciones.size(); i++) {
            Calificaciones calificacion = calificaciones.get(i);
            sb.append("Nota ").append(i + 1).append(": ")
                    .append(String.format("%.1f", calificacion.getCalificacion()));

            if (calificacion.getTipoEvaluacion() != null && !calificacion.getTipoEvaluacion().isEmpty()) {
                sb.append(" (").append(calificacion.getTipoEvaluacion()).append(")");
            }
            sb.append("\n");
        }

        float suma = 0;
        for (Calificaciones calificacion : calificaciones) {
            suma += calificacion.getCalificacion();
        }
        float promedio = suma / calificaciones.size();
        sb.append("\nPromedio: ").append(String.format("%.1f", promedio));

        builder.setMessage(sb.toString());
    }

    // Botones del diálogo
    builder.setPositiveButton("Cerrar", null)
            .setNeutralButton("Editar", (dialog, which) -> {
                mostrarDialogoEditarNota(materia, calificaciones);
            });

    // Solo mostrar opción de eliminar si hay notas
    if (calificaciones != null && !calificaciones.isEmpty()) {
        builder.setNegativeButton("Eliminar", (dialog, which) -> {
            mostrarDialogoEliminarNota(materia, calificaciones);
        });
    }

    builder.show();
}


    private void mostrarDialogoEliminarNota(Materia materia, List<Calificaciones> calificaciones) {
        if (calificaciones == null || calificaciones.isEmpty()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar notas de " + materia.getNombre());

        if (calificaciones.size() == 1) {
            // Solo una nota - eliminación simple
            builder.setMessage("¿Estás seguro que deseas eliminar esta nota?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        eliminarNota(calificaciones.get(0).getIdCalificacion());
                    })
                    .setNegativeButton("Cancelar", null);
        } else {
            // Múltiples notas - mostrar opciones
            CharSequence[] opciones = new CharSequence[calificaciones.size() + 1];
            for (int i = 0; i < calificaciones.size(); i++) {
                Calificaciones calificacion = calificaciones.get(i);
                opciones[i] = "Nota " + (i+1) + ": " + calificacion.getCalificacion() +
                        (calificacion.getTipoEvaluacion() != null ?
                                " (" + calificacion.getTipoEvaluacion() + ")" : "");
            }
            opciones[calificaciones.size()] = "Todas las notas";

            builder.setItems(opciones, (dialog, which) -> {
                        if (which == calificaciones.size()) {
                            // Eliminar todas
                            new AlertDialog.Builder(context)
                                    .setTitle("Confirmar eliminación")
                                    .setMessage("¿Estás seguro que deseas eliminar TODAS las notas de " + materia.getNombre() + "?")
                                    .setPositiveButton("Eliminar", (d, w) -> {
                                        for (Calificaciones calificacion : calificaciones) {
                                            eliminarNota(calificacion.getIdCalificacion());
                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        } else {
                            // Eliminar nota específica
                            eliminarNota(calificaciones.get(which).getIdCalificacion());
                        }
                    })
                    .setNegativeButton("Cancelar", null);
        }

        builder.show();
    }

    private void eliminarNota(int idCalificacion) {
        notasViewModel.eliminarNota(idCalificacion, () -> {
            Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show();
        });
    }
    private void mostrarDialogoEditarNota(Materia materia, List<Calificaciones> calificaciones) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_editar_nota, null);

        LinearLayout container = view.findViewById(R.id.container_notas_editables);
        container.removeAllViews();

        // Crear un campo de texto para cada nota
        for (Calificaciones calificacion : calificaciones) {
            EditText etNota = new EditText(context);
            etNota.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etNota.setText(String.valueOf(calificacion.getCalificacion()));
            etNota.setTag(calificacion.getIdCalificacion()); // Guardar el ID como tag

            // Configurar el hint con el tipo de evaluación si existe
            String hint = "Nota";
            if (calificacion.getTipoEvaluacion() != null && !calificacion.getTipoEvaluacion().isEmpty()) {
                hint += " (" + calificacion.getTipoEvaluacion() + ")";
            }
            etNota.setHint(hint);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            etNota.setLayoutParams(params);

            container.addView(etNota);
        }

        builder.setView(view)
                .setTitle("Editar Notas - " + materia.getNombre())
                .setPositiveButton("Guardar", (dialog, which) -> {
                    boolean error = false;
                    for (int i = 0; i < container.getChildCount(); i++) {
                        View child = container.getChildAt(i);
                        if (child instanceof EditText) {
                            EditText etNota = (EditText) child;
                            try {
                                float nuevaNota = Float.parseFloat(etNota.getText().toString());
                                if (nuevaNota < 0 || nuevaNota > 10) {
                                    Toast.makeText(context, "La nota debe estar entre 0 y 10", Toast.LENGTH_SHORT).show();
                                    error = true;
                                    break;
                                }

                                int idCalificacion = (int) etNota.getTag();

                                Calificaciones notaActualizada = new Calificaciones();
                                notaActualizada.setIdCalificacion(idCalificacion);
                                notaActualizada.setCalificacion(nuevaNota);
                                notaActualizada.setIdMateria(materia.getIdMateria());
                                notaActualizada.setTipoEvaluacion(calificaciones.get(i).getTipoEvaluacion());

                                boolean finalError = error;
                                int finalI = i;
                                notasViewModel.actualizarNotas(notaActualizada, () -> {
                                    if (finalI == container.getChildCount() - 1 && !finalError) {
                                        Toast.makeText(context, "Notas actualizadas", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (NumberFormatException e) {
                                Toast.makeText(context, "Ingrese una nota válida", Toast.LENGTH_SHORT).show();
                                error = true;
                                break;
                            }
                        }
                    }
                })
                .setNegativeButton("Cancelar", null);

        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return materias.size();
    }

    public void actualizarNotas(List<Map<String, Object>> nuevasNotas) {
        this.materias = nuevasNotas;
        notifyDataSetChanged();
    }

    public static class NotasViewHolder extends RecyclerView.ViewHolder {
        TextView tvMateria;
        TextView tvNota;

        public NotasViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMateria = itemView.findViewById(R.id.tvMateriaNota);
            tvNota = itemView.findViewById(R.id.tvNotaMateriaItem);
        }
    }
}

