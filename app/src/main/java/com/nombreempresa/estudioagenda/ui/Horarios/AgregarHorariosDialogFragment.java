package com.nombreempresa.estudioagenda.ui.Horarios;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Horarios;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarHorariosDialogFragment extends DialogFragment {
    private HorariosViewModel horariosViewModel;
    private Spinner spMateria, spDiaSemana;
    private EditText etHoraInicio, etHoraFin;
    private String diaSemanaGuardado;
    private List<Materia> listaMaterias = new ArrayList<>();
    private ProgressDialog progressDialog;
    private boolean isEditMode = false;
    private int idHorarioEdit = -1;

//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//        View view = inflater.inflate(R.layout.dialog_agregar_horarios, null);
//
//        spMateria = view.findViewById(R.id.spinnerMaterias);
//        spDiaSemana = view.findViewById(R.id.spinnerDia);
//        etHoraInicio = view.findViewById(R.id.etHoraInicio);
//        etHoraFin = view.findViewById(R.id.etHoraFin);
//
//        horariosViewModel = new ViewModelProvider(requireActivity()).get(HorariosViewModel.class);
//
//        // Configurar ProgressDialog
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Cargando materias...");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        // Configurar spinner de días
//        configurarSpinnerDias();
//
//        // Obtener materias disponibles
//        obtenerMateriasDisponibles();
//
//        // Validaciones
//        configurarValidacionHora(etHoraInicio);
//        configurarValidacionHora(etHoraFin);
//
//        // Verificar si estamos en modo edición
//        Bundle args = getArguments();
//        if (args != null) {
//            isEditMode = true;
//            idHorarioEdit = args.getInt("idHorario", -1);
//            String nombreMateria = args.getString("nombreMateria", "");
//            int idMateria = args.getInt("idMateria", -1);
//            String diaSemana = args.getString("diaSemana", "");
//            String horaInicio = args.getString("horaInicio", "");
//            String horaFin = args.getString("horaFin", "");
//
//            // Configurar los campos con los valores existentes
//            etHoraInicio.setText(horaInicio);
//            etHoraFin.setText(horaFin);
//        }
//
//        builder.setView(view)
//                .setTitle(isEditMode ? "Editar Horario" : "Agregar Horario")
//                .setPositiveButton(isEditMode ? "Actualizar" : "Guardar", (dialog, id) -> guardarHorario())
//                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());
//
//        return builder.create();
//    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_agregar_horarios, null);

        spMateria = view.findViewById(R.id.spinnerMaterias);
        spDiaSemana = view.findViewById(R.id.spinnerDia);
        etHoraInicio = view.findViewById(R.id.etHoraInicio);
        etHoraFin = view.findViewById(R.id.etHoraFin);

        horariosViewModel = new ViewModelProvider(requireActivity()).get(HorariosViewModel.class);

        // Configurar ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando materias...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Verificar si estamos en modo edición primero
        Bundle args = getArguments();
        if (args != null) {
            isEditMode = true;
            idHorarioEdit = args.getInt("idHorario", -1);
            String nombreMateria = args.getString("nombreMateria", "");
            int idMateria = args.getInt("idMateria", -1);
            diaSemanaGuardado = args.getString("diaSemana", ""); // Nuevo campo para guardar el día
            String horaInicio = args.getString("horaInicio", "");
            String horaFin = args.getString("horaFin", "");

            // Configurar los campos con los valores existentes
            etHoraInicio.setText(horaInicio);
            etHoraFin.setText(horaFin);
        }

        // Configurar spinner de días (esto debe hacerse después de obtener diaSemanaGuardado)
        configurarSpinnerDias();

        // Obtener materias disponibles
        obtenerMateriasDisponibles();

        // Validaciones
        configurarValidacionHora(etHoraInicio);
        configurarValidacionHora(etHoraFin);

        builder.setView(view)
                .setTitle(isEditMode ? "Editar Horario" : "Agregar Horario")
                .setPositiveButton(isEditMode ? "Actualizar" : "Guardar", (dialog, id) -> guardarHorario())
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void configurarSpinnerDias() {
        String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, dias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDiaSemana.setAdapter(adapter);

        // Si estamos editando, seleccionar el día después de configurar el adapter
        if (isEditMode && diaSemanaGuardado != null && !diaSemanaGuardado.isEmpty()) {
            spDiaSemana.post(() -> {
                for (int i = 0; i < dias.length; i++) {
                    if (dias[i].equalsIgnoreCase(diaSemanaGuardado)) {
                        spDiaSemana.setSelection(i);
                        break;
                    }
                }
            });
        }
    }

//    private void configurarSpinnerDias() {
//        String[] dias = {"lunes","martes","miércoles","jueves","viernes","sábado","domingo"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
//                android.R.layout.simple_spinner_item, dias);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spDiaSemana.setAdapter(adapter);
//
//        // Si estamos editando, seleccionar el día después de configurar el adapter
//        if (isEditMode && getArguments() != null) {
//            String diaGuardado = getArguments().getString("diaSemana", "");
//            Log.d("DIALOG_DEBUG", "Configurando spinner para día: " + diaGuardado);
//
//            if (!diaGuardado.isEmpty()) {
//                // Espera a que el spinner esté listo
//                spDiaSemana.post(() -> {
//                    for (int i = 0; i < dias.length; i++) {
//                        if (dias[i].equalsIgnoreCase(diaGuardado)) {
//                            spDiaSemana.setSelection(i);
//                            Log.d("DIALOG_DEBUG", "Día seleccionado en posición: " + i);
//                            break;
//                        }
//                    }
//                });
//            }
//        }
//    }

    private void obtenerMateriasDisponibles() {
        String token = ApiClient.leerToken(requireContext());
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Call<List<Materia>> call = api.obtenerMaterias(token);
        call.enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    listaMaterias = response.body();
                    configurarSpinnerMaterias();

                    // Si estamos editando, seleccionar la materia correspondiente
                    if (isEditMode && getArguments() != null) {
                        int idMateria = getArguments().getInt("idMateria", -1);
                        for (int i = 0; i < listaMaterias.size(); i++) {
                            if (listaMaterias.get(i).getIdMateria() == idMateria) {
                                spMateria.setSelection(i);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Error al obtener materias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSpinnerMaterias() {
        ArrayAdapter<Materia> adapter = new ArrayAdapter<Materia>(requireContext(),
                android.R.layout.simple_spinner_item, listaMaterias) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Materia materia = listaMaterias.get(position);
                ((TextView) view).setText(materia.getNombre());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                Materia materia = listaMaterias.get(position);
                ((TextView) view).setText(materia.getNombre());
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMateria.setAdapter(adapter);
    }

    private void configurarValidacionHora(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                if (!input.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
                    editText.setError("Formato inválido. Use HH:mm (00-23:00-59)");
                }
            }
        });
    }

    private void guardarHorario() {
        if (spMateria.getSelectedItem() == null || spDiaSemana.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "Seleccione todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String horaInicio = etHoraInicio.getText().toString().trim();
        String horaFin = etHoraFin.getText().toString().trim();

        if (horaInicio.isEmpty() || horaFin.isEmpty()) {
            Toast.makeText(getActivity(), "Ingrese hora de inicio y fin", Toast.LENGTH_SHORT).show();
            return;
        }

        Materia materiaSeleccionada = (Materia) spMateria.getSelectedItem();
        String diaSemana = spDiaSemana.getSelectedItem().toString();

        if (isEditMode && idHorarioEdit != -1) {
            // Modo edición
            Horarios horario = new Horarios();
            horario.setIdMateria(materiaSeleccionada.getIdMateria());
            horario.setDiaSemana(diaSemana);
            horario.setHoraInicio(horaInicio);
            horario.setHoraFin(horaFin);

            horariosViewModel.actualizarHorario(idHorarioEdit, horario, () -> dismiss());
        } else {
            // Modo creación
            horariosViewModel.agregarHorario(
                    materiaSeleccionada.getNombre(),
                    materiaSeleccionada.getIdMateria(),
                    diaSemana,
                    horaInicio,
                    horaFin,
                    () -> dismiss()
            );
        }
    }

}