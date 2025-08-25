/*
package com.nombreempresa.estudioagenda.ui.calendario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Actividades;
import com.nombreempresa.estudioagenda.ui.apuntes.ApuntesViewModel;

import java.util.Arrays;
import java.util.List;

public class AgregarActividadDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_actividades, null);

        EditText etTituloActividad = view.findViewById(R.id.etTituloActividad);
        Spinner spinnerTipoActividad = view.findViewById(R.id.spinnerTipoActividad);
        DatePicker datePicker = view.findViewById(R.id.datePickerRecordatorio);

        // Lista de tipos de actividad
        List<String> tiposActividad = Arrays.asList("Reunión", "Entrega", "Tarea", "Otro");

        // Adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                tiposActividad
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoActividad.setAdapter(adapter);

        builder.setView(view)
                .setTitle("Agregar Actividad")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTituloActividad.getText().toString();
                    String tipo = spinnerTipoActividad.getSelectedItem().toString();
                    Log.d("MODAL", "Guardado: " + titulo + ", " + tipo);
                })
                .setNegativeButton("Cancelar", null);

        return builder.create();
    }
}

 */


package com.nombreempresa.estudioagenda.ui.calendario;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Actividades;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class AgregarActividadDialogFragment extends DialogFragment {
    private static final String ARG_MODO_EDICION = "modoEdicion";
    private static final String ARG_ACTIVIDAD = "actividad";

    private boolean modoEdicion;
    private Actividades actividadExistente;

    public static AgregarActividadDialogFragment newInstance() {
        return new AgregarActividadDialogFragment();
    }

    public static AgregarActividadDialogFragment newInstanceForEdit(Actividades actividad) {
        AgregarActividadDialogFragment fragment = new AgregarActividadDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_MODO_EDICION, true);
        args.putSerializable(ARG_ACTIVIDAD, actividad);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modoEdicion = getArguments().getBoolean(ARG_MODO_EDICION, false);
            actividadExistente = (Actividades) getArguments().getSerializable(ARG_ACTIVIDAD);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.modal_actividades, null);

        EditText etTituloActividad = view.findViewById(R.id.etTituloActividad);
        Spinner spinnerTipoActividad = view.findViewById(R.id.spinnerTipoActividad);
        DatePicker datePicker = view.findViewById(R.id.datePickerRecordatorio);
        TimePicker timePicker = view.findViewById(R.id.timePickerRecordatorio);
        // Configurar TimePicker en formato 24 horas
        timePicker.setIs24HourView(true);

        // Lista de tipos de actividad
        List<String> tiposActividad = Arrays.asList("Entrega", "Exámen", "Reunión", "Tarea", "Otro");

        // Adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                tiposActividad
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoActividad.setAdapter(adapter);

        // Si estamos en modo edición, cargamos los datos
        if (modoEdicion && actividadExistente != null) {
            etTituloActividad.setText(actividadExistente.getDescripcion());

            // Seleccionar el tipo en el spinner
            int posicionTipo = tiposActividad.indexOf(actividadExistente.getTipo_Evento());
            if (posicionTipo >= 0) {
                spinnerTipoActividad.setSelection(posicionTipo);
            }

            // Configurar la fecha
            Calendar cal = Calendar.getInstance();
            cal.setTime(actividadExistente.getFechaEvento());
            datePicker.updateDate(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );

            builder.setTitle("Editar Actividad");
        } else {
            builder.setTitle("Agregar Actividad");
        }

        builder.setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();

//        dialog.setOnShowListener(d -> {
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
//                String titulo = etTituloActividad.getText().toString().trim();
//                String tipo = spinnerTipoActividad.getSelectedItem().toString();
//
//                if (titulo.isEmpty()) {
//                    Toast.makeText(requireContext(), "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Obtener fecha del DatePicker
//                int day = datePicker.getDayOfMonth();
//                int month = datePicker.getMonth();
//                int year = datePicker.getYear();
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(year, month, day);
//
//                // Obtener hora del TimePicker
//                int hour, minute;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    hour = timePicker.getHour();
//                    minute = timePicker.getMinute();
//                } else {
//                    hour = timePicker.getCurrentHour();
//                    minute = timePicker.getCurrentMinute();
//                }
//
//                CalendarioViewModel viewModel = new ViewModelProvider(
//                        requireActivity(),
//                        new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
//                ).get(CalendarioViewModel.class);
//
//                if (modoEdicion && actividadExistente != null) {
//                    // Modo edición
//                    actividadExistente.setDescripcion(titulo);
//                    actividadExistente.setTipo_Evento(tipo);
//                    actividadExistente.setFechaEvento(calendar.getTime());
//
//                    viewModel.actualizarActividad(actividadExistente, dialog::dismiss);
//                } else {
//                    // Modo creación
//                    Actividades nuevaActividad = new Actividades();
//                    nuevaActividad.setDescripcion(titulo);
//                    nuevaActividad.setTipo_Evento(tipo);
//                    nuevaActividad.setFechaEvento(calendar.getTime());
//
//                    viewModel.crearActividad(nuevaActividad, dialog::dismiss);
//                }
//            });
//        });
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String titulo = etTituloActividad.getText().toString().trim();
                String tipo = spinnerTipoActividad.getSelectedItem().toString();

                if (titulo.isEmpty()) {
                    Toast.makeText(requireContext(), "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Obtener fecha del DatePicker
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                // Obtener hora del TimePicker
                int hour, minute;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                // Crear objeto Calendar con fecha y hora
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute, 0); // Asegurar segundos en 0

                // Validar que la fecha/hora elegida sea al menos un minuto después de la actual
                Calendar ahora = Calendar.getInstance();
                ahora.add(Calendar.MINUTE, 1); // Un minuto después de ahora
                if (!calendar.after(Calendar.getInstance())) {
                    Toast.makeText(requireContext(), "La hora debe ser al menos un minuto después de la actual", Toast.LENGTH_LONG).show();
                    return;
                }

                CalendarioViewModel viewModel = new ViewModelProvider(
                        requireActivity(),
                        new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())
                ).get(CalendarioViewModel.class);

                if (modoEdicion && actividadExistente != null) {
                    // Modo edición
                    actividadExistente.setDescripcion(titulo);
                    actividadExistente.setTipo_Evento(tipo);
                    actividadExistente.setFechaEvento(calendar.getTime());

                    viewModel.actualizarActividad(actividadExistente, dialog::dismiss);
                } else {
                    // Modo creación
                    Actividades nuevaActividad = new Actividades();
                    nuevaActividad.setDescripcion(titulo);
                    nuevaActividad.setTipo_Evento(tipo);
                    nuevaActividad.setFechaEvento(calendar.getTime());

                    viewModel.crearActividad(nuevaActividad, dialog::dismiss);
                }
            });
        });
        return dialog;
    }
}