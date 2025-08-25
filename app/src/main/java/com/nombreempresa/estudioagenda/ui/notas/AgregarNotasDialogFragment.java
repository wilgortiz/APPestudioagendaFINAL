package com.nombreempresa.estudioagenda.ui.notas;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Calificaciones;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarNotasDialogFragment extends DialogFragment {

    private NotasViewModel notasViewModel;
    private Spinner spMaterias;
    private EditText etNotas;
    private List<Materia> listaMaterias = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialog_agregar_notas_materia, null);

        spMaterias = vista.findViewById(R.id.spinnerMateriaNota);
        etNotas = vista.findViewById(R.id.etNotas);

//        notasViewModel = new ViewModelProvider(requireActivity()).get(NotasViewModel.class);
        notasViewModel = new ViewModelProvider(requireParentFragment()).get(NotasViewModel.class);

        // Configurar y mostrar ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando materias...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Obtener todas las materias desde la API
        obtenerMaterias();

        builder.setView(vista)
                .setTitle("Agregar Calificación")
                .setPositiveButton("Guardar", (dialog, id) -> {
                    if (spMaterias.getSelectedItem() != null && !etNotas.getText().toString().isEmpty()) {
                        Materia materiaSeleccionada = (Materia) spMaterias.getSelectedItem();
                        String calificacionStr = etNotas.getText().toString();

                        try {
                            double calificacion = Double.parseDouble(calificacionStr);

                            Calificaciones nuevaNota = new Calificaciones();
                            nuevaNota.setIdMateria(materiaSeleccionada.getIdMateria());
                            nuevaNota.setCalificacion((float) calificacion);

                            if (nuevaNota.getIdMateria() == 0) {
                                Toast.makeText(getActivity(), "Error: ID de materia inválido", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (calificacion < 0 || calificacion > 10) {
                                Toast.makeText(getActivity(), "La calificación debe estar entre 0 y 10", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Mostrar progreso mientras se guarda
                            progressDialog.setMessage("Guardando nota...");
                            progressDialog.show();

                            // Agregar nota y luego actualizar la lista completa
                            notasViewModel.agregarNota(nuevaNota, () -> {
                                progressDialog.dismiss();

                                // Notifica al fragment padre que debe actualizarse
                                if (getParentFragment() instanceof NotasFragment) {
                                    ((NotasFragment) getParentFragment()).actualizarLista();
//                                // Actualizar la lista completa de notas
//                                notasViewModel.obtenerNotas();
//                                dismiss(); // Cerrar el diálogo
                                }
                            });

                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Ingrese una calificación válida", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Seleccione una materia e ingrese una calificación", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void obtenerMaterias() {
        String token = ApiClient.leerToken(requireContext());
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Call<List<Materia>> callMaterias = api.obtenerMaterias(token);

        callMaterias.enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    listaMaterias = response.body();
                    Log.d("Materias", "Total materias: " + listaMaterias.size());
                    configurarSpinner();
                } else {
                    Toast.makeText(getActivity(), "Error al obtener materias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Materia>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error de conexión al obtener materias", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Error: " + t.getMessage());
            }
        });
    }

    private void configurarSpinner() {
        ArrayAdapter<Materia> adapter = new ArrayAdapter<Materia>(requireContext(),
                android.R.layout.simple_spinner_item, listaMaterias) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                Materia materia = listaMaterias.get(position);
                view.setText(materia.getNombre());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                Materia materia = listaMaterias.get(position);
                view.setText(materia.getNombre());
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMaterias.setAdapter(adapter);

        if (listaMaterias.isEmpty()) {
            Toast.makeText(getActivity(), "No hay materias disponibles", Toast.LENGTH_SHORT).show();
        }
    }

}