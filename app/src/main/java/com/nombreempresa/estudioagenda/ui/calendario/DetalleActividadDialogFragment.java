package com.nombreempresa.estudioagenda.ui.calendario;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Actividades;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetalleActividadDialogFragment extends DialogFragment {
    private List<Actividades> actividades;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private CalendarioViewModel viewModel;
    private Spinner spinnerActividades;
    private int selectedActivityPosition = 0;
    private TextView tvContenido;

    public DetalleActividadDialogFragment(List<Actividades> actividades) {
        this.actividades = actividades;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CalendarioViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detalle_actividad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTitulo = view.findViewById(R.id.tv_titulo_dialog);
        tvContenido = view.findViewById(R.id.tv_contenido_dialog);
        Button btnCerrar = view.findViewById(R.id.btn_cerrar_dialog);
        Button btnEditar = view.findViewById(R.id.btn_editar_dialog);
        Button btnEliminar = view.findViewById(R.id.btn_eliminar_dialog);
        spinnerActividades = view.findViewById(R.id.spinner_actividades);

        if (actividades.size() == 1) {
            // Para una actividad, no necesitamos el selector
            spinnerActividades.setVisibility(View.GONE);
            tvTitulo.setText("Detalle de la actividad");
            mostrarDetalleActividad(actividades.get(0));
        } else {
            // Configurar el spinner para múltiples actividades
            tvTitulo.setText("Actividades del día (" + actividades.size() + ")");

            // Crear un adaptador para el spinner con los títulos/tipos de actividades
            List<String> titulosActividades = new ArrayList<>();
            for (Actividades actividad : actividades) {
                String titulo = actividad.getTipo_Evento();
                if (actividad.getDescripcion() != null && !actividad.getDescripcion().isEmpty()) {
                    titulo += ": " + actividad.getDescripcion();
                }
                titulosActividades.add(titulo);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    titulosActividades
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerActividades.setAdapter(adapter);

            // Manejar la selección del spinner
            spinnerActividades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedActivityPosition = position;
                    mostrarDetalleActividad(actividades.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // No hacer nada
                }
            });
        }

        // Configurar botones
        btnEditar.setOnClickListener(v -> {
            // Abrir diálogo de edición para la actividad seleccionada
            Actividades actividadSeleccionada = actividades.get(selectedActivityPosition);
            DialogFragment dialog = AgregarActividadDialogFragment.newInstanceForEdit(actividadSeleccionada);
            dialog.show(getParentFragmentManager(), "EditarActividadDialog");
            dismiss();
        });

        btnEliminar.setOnClickListener(v -> {
            Actividades actividadSeleccionada = actividades.get(selectedActivityPosition);
            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar actividad")
                    .setMessage("¿Estás seguro de que quieres eliminar esta actividad?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        viewModel.eliminarActividad(actividadSeleccionada.getIdEvento());
                        if (actividades.size() == 1) {
                            dismiss();
                        } else {
                            actividades.remove(selectedActivityPosition);
                            if (actividades.isEmpty()) {
                                dismiss();
                            } else {
                                // Actualizar spinner
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerActividades.getAdapter();
                                String removedItem = adapter.getItem(selectedActivityPosition);
                                adapter.remove(removedItem);
                                adapter.notifyDataSetChanged();

                                // Seleccionar el primer elemento si está disponible
                                if (adapter.getCount() > 0) {
                                    spinnerActividades.setSelection(0);
                                }

                                // Actualizar título
                                tvTitulo.setText("Actividades del día (" + actividades.size() + ")");

                                Toast.makeText(requireContext(), "Actividad eliminada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnCerrar.setOnClickListener(v -> dismiss());
    }

    private void mostrarDetalleActividad(Actividades actividad) {
        String contenido = "Tipo: " + actividad.getTipo_Evento() + "\n\n" +
                "Fecha: " + dateFormat.format(actividad.getFechaEvento()) + "\n\n" +
                "Descripción: " + (actividad.getDescripcion() != null ?
                actividad.getDescripcion() : "Sin descripción");

        if (actividad.isRecordatorio() && actividad.getFechaRecordatorio() != null) {
            contenido += "\n\nRecordatorio: " + dateFormat.format(actividad.getFechaRecordatorio());
        }

        tvContenido.setText(contenido);
    }
}