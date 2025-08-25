package com.nombreempresa.estudioagenda.ui.materias;


import android.app.AlertDialog;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.databinding.DialogAgregarMateriaBinding;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.modelos.ProfesorMateria;
import com.nombreempresa.estudioagenda.request.ApiClient;
import com.nombreempresa.estudioagenda.ui.materias.MateriasViewModel;
import com.nombreempresa.estudioagenda.ui.profes.ProfesViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//
//public class AgregarMateriasDialogFragment extends DialogFragment {
//    private static final String ARG_MODO_EDICION = "modoEdicion";
//    private static final String ARG_MATERIA = "materia";
//
//    private boolean modoEdicion;
//    private Materia materiaExistente;
//    private DialogAgregarMateriaBinding binding;
//    private MateriasViewModel materiasViewModel;
//
//    public static AgregarMateriasDialogFragment newInstance() {
//        return new AgregarMateriasDialogFragment();
//    }
//
//    public static AgregarMateriasDialogFragment newInstanceForEdit(Materia materia) {
//        AgregarMateriasDialogFragment fragment = new AgregarMateriasDialogFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(ARG_MODO_EDICION, true);
//        args.putSerializable(ARG_MATERIA, materia);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            modoEdicion = getArguments().getBoolean(ARG_MODO_EDICION, false);
//            materiaExistente = (Materia) getArguments().getSerializable(ARG_MATERIA);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = DialogAgregarMateriaBinding.inflate(inflater, container, false);
//        materiasViewModel = new ViewModelProvider(requireActivity()).get(MateriasViewModel.class);
//
//        // Configurar el Spinner
//        String[] periodos = {"1", "2", "3", "4"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, periodos);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.spPeriodo.setAdapter(adapter);
//
//        // Si estamos en modo edición, cargamos los datos
//        if (modoEdicion && materiaExistente != null) {
//            binding.etNombre.setText(materiaExistente.getNombre());
//            binding.spPeriodo.setSelection(materiaExistente.getPeriodo() - 1); // Los arrays son 0-based
//            binding.btnAgregar.setText("Actualizar");
//        }
//
//        binding.btnAgregar.setOnClickListener(v -> {
//            String nombre = binding.etNombre.getText().toString();
//            String periodoStr = (String) binding.spPeriodo.getSelectedItem();
//            int periodo = Integer.parseInt(periodoStr);
//
//            if (!nombre.isEmpty()) {
//                if (modoEdicion && materiaExistente != null) {
//                    // Modo edición
//                    materiaExistente.setNombre(nombre);
//                    materiaExistente.setPeriodo(periodo);
//                    materiasViewModel.actualizarMateria(materiaExistente, this::dismiss);
//                } else {
//                    // Modo creación
//                    Materia nuevaMateria = new Materia(nombre, periodo);
//                    materiasViewModel.agregarMateria(nuevaMateria);
//                    dismiss();
//                }
//            } else {
//                Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return binding.getRoot();
//    }

public class AgregarMateriasDialogFragment extends DialogFragment {
    private static final String ARG_MODO_EDICION = "modoEdicion";
    private static final String ARG_MATERIA = "materia";

    private boolean modoEdicion;
    private Materia materiaExistente;
    private DialogAgregarMateriaBinding binding;
    private MateriasViewModel materiasViewModel;
    private List<Profesor> listaProfesores;
    private List<Integer> profesoresSeleccionados = new ArrayList<>();
    private ArrayAdapter<Profesor> profesoresAdapter;

    public static AgregarMateriasDialogFragment newInstance() {
        return new AgregarMateriasDialogFragment();
    }

    public static AgregarMateriasDialogFragment newInstanceForEdit(Materia materia) {
        AgregarMateriasDialogFragment fragment = new AgregarMateriasDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_MODO_EDICION, true);
        args.putSerializable(ARG_MATERIA, materia);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modoEdicion = getArguments().getBoolean(ARG_MODO_EDICION, false);
            materiaExistente = (Materia) getArguments().getSerializable(ARG_MATERIA);

            if (modoEdicion && materiaExistente != null && materiaExistente.getProfesorMateria() != null) {
                for (ProfesorMateria pm : materiaExistente.getProfesorMateria()) {
                    profesoresSeleccionados.add(pm.getIdProfesor());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogAgregarMateriaBinding.inflate(inflater, container, false);
        materiasViewModel = new ViewModelProvider(requireActivity()).get(MateriasViewModel.class);

        // Configurar el Spinner de periodos
        String[] periodos = {"1", "2", "3", "4"};
        ArrayAdapter<String> periodoAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, periodos);
        periodoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spPeriodo.setAdapter(periodoAdapter);

        // Configurar la lista de profesores
        listaProfesores = new ArrayList<>();
        profesoresAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_multiple_choice, listaProfesores);
        binding.lvProfesores.setAdapter(profesoresAdapter);
        binding.lvProfesores.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Cargar lista de profesores
        cargarProfesores();

        // Si estamos en modo edición, cargamos los datos
        if (modoEdicion && materiaExistente != null) {
            binding.etNombre.setText(materiaExistente.getNombre());
            binding.spPeriodo.setSelection(materiaExistente.getPeriodo() - 1);
            binding.btnAgregar.setText("Actualizar");
        }

        binding.btnAgregar.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString();
            String periodoStr = (String) binding.spPeriodo.getSelectedItem();
            int periodo = Integer.parseInt(periodoStr);

            if (!nombre.isEmpty()) {
                if (modoEdicion && materiaExistente != null) {
                    // Modo edición
                    materiaExistente.setNombre(nombre);
                    materiaExistente.setPeriodo(periodo);
                    materiaExistente.setProfesores(getProfesoresSeleccionados());
                    materiasViewModel.actualizarMateria(materiaExistente, this::dismiss);
                    materiasViewModel.obtenerMaterias(); // Refrescar la lista
                } else {
                    // Modo creación
                    Materia nuevaMateria = new Materia(nombre, periodo);
                    nuevaMateria.setProfesores(getProfesoresSeleccionados());
                    materiasViewModel.agregarMateria(nuevaMateria);
                    materiasViewModel.obtenerMaterias();
                    dismiss();
                }
            } else {
                Toast.makeText(getContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void cargarProfesores() {
        String token = ApiClient.leerToken(requireContext());
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<List<Profesor>> call = api.obtenerProfes(token);

        call.enqueue(new Callback<List<Profesor>>() {
            @Override
            public void onResponse(Call<List<Profesor>> call, Response<List<Profesor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProfesores.clear();
                    listaProfesores.addAll(response.body());
                    profesoresAdapter.notifyDataSetChanged();

                    // Marcar los profesores seleccionados si estamos en modo edición
                    if (modoEdicion && materiaExistente != null) {
                        for (int i = 0; i < listaProfesores.size(); i++) {
                            Profesor profesor = listaProfesores.get(i);
                            if (profesoresSeleccionados.contains(profesor.getIdProfesor())) {
                                binding.lvProfesores.setItemChecked(i, true);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Profesor>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar profesores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Profesor> getProfesoresSeleccionados() {
        List<Profesor> seleccionados = new ArrayList<>();
        SparseBooleanArray checked = binding.lvProfesores.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                seleccionados.add(listaProfesores.get(checked.keyAt(i)));
            }
        }
        return seleccionados;
    }
}
