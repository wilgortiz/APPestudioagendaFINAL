package com.nombreempresa.estudioagenda.ui.faltas;

import static androidx.core.content.ContentProviderCompat.requireContext;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Faltas;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.request.ApiClient;
import com.nombreempresa.estudioagenda.ui.faltas.FaltasViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarFaltasDialogFragment extends DialogFragment {
    private FaltasViewModel faltasViewModel;
    private Spinner spMateria;
    private List<Materia> listaMaterias = new ArrayList<>();
    private ProgressDialog progressDialog;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialog_agregar_faltas_materia, null);

        spMateria = vista.findViewById(R.id.spinnerMaterias);

        faltasViewModel = new ViewModelProvider(requireActivity()).get(FaltasViewModel.class);

        // Configurar y mostrar ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando materias...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Obtener materias desde la API
        obtenerMateriasDisponibles();

        builder.setView(vista)
                .setTitle("Agregar Materia")
                .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (spMateria.getSelectedItem() != null) {
                            Materia materiaSeleccionada = (Materia) spMateria.getSelectedItem();
                            Log.d("AGREGAR_FALTA", "Materia seleccionada - ID: " + materiaSeleccionada.getIdMateria() +
                                    ", Nombre: " + materiaSeleccionada.getNombre());

                            Faltas nuevaFalta = new Faltas();
                            nuevaFalta.setIdMateria(materiaSeleccionada.getIdMateria());
                            nuevaFalta.setCantidad(0); // Inicializar con 0 faltas

                            // Verificar que el idMateria no sea 0 antes de enviar
                            if (nuevaFalta.getIdMateria() == 0) {
                                Toast.makeText(getActivity(), "Error: ID de materia inválido", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            faltasViewModel.AgregarMateriaFalta(nuevaFalta, null);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
//
//    private void obtenerMateriasDisponibles() {
//        String token = ApiClient.leerToken(requireContext());
//        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
//
//        Call<List<Materia>> call = api.obtenerMaterias(token);
//        call.enqueue(new Callback<List<Materia>>() {
//            @Override
//            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
//                progressDialog.dismiss();
//                if (response.isSuccessful() && response.body() != null) {
//                    listaMaterias = response.body();
//                    configurarSpinner();
//                } else {
//                    Toast.makeText(getActivity(), "Error al obtener materias", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Materia>> call, Throwable t) {
//                progressDialog.dismiss();
//                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    private void obtenerMateriasDisponibles() {
        String token = ApiClient.leerToken(requireContext());
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Call<List<Materia>> callMaterias = api.obtenerMaterias(token);
        Call<List<Faltas>> callFaltas = api.obtenerFaltas(token);

        callMaterias.enqueue(new Callback<List<Materia>>() {
            @Override
            public void onResponse(Call<List<Materia>> call, Response<List<Materia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMaterias = response.body();
                    Log.d("Materias", "Total materias: " + listaMaterias.size());

                    callFaltas.enqueue(new Callback<List<Faltas>>() {
                        @Override
                        public void onResponse(Call<List<Faltas>> call, Response<List<Faltas>> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                List<Faltas> listaFaltas = response.body();
                                Log.d("Faltas", "Total registros de faltas: " + listaFaltas.size());

                                // Crear lista de IDs de materias con faltas
                                Set<Integer> idsMateriasConFaltas = new HashSet<>();
                                for (Faltas falta : listaFaltas) {
                                    idsMateriasConFaltas.add(falta.getIdMateria());
                                }

                                // Filtrar materias disponibles (las que NO están en faltas)
                                List<Materia> materiasDisponibles = new ArrayList<>();
                                for (Materia materia : listaMaterias) {
                                    if (!idsMateriasConFaltas.contains(materia.getIdMateria())) {
                                        materiasDisponibles.add(materia);
                                    }
                                }

                                // Actualizar lista y configurar spinner
                                listaMaterias = materiasDisponibles;
                                Log.d("MateriasDisponibles", "Total: " + listaMaterias.size());
                                configurarSpinner();
                            } else {
                                Toast.makeText(getActivity(), "Error al obtener faltas", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Faltas>> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    progressDialog.dismiss();
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



    private void configurarSpinner() {
        ArrayAdapter<Materia> adapter = new ArrayAdapter<Materia>(requireContext(),
                android.R.layout.simple_spinner_item, listaMaterias) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                Materia materia = listaMaterias.get(position);
                view.setText(materia.getNombre() + " - " + materia.getIdMateria());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                Materia materia = listaMaterias.get(position);
                view.setText(materia.getNombre() + " - " + materia.getIdMateria());
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMateria.setAdapter(adapter);
    }
}