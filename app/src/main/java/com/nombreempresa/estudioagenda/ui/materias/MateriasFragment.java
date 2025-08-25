package com.nombreempresa.estudioagenda.ui.materias;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.databinding.FragmentMateriasBinding;
import com.nombreempresa.estudioagenda.ui.apuntes.AgregarApunteDialogFragment;

import java.util.ArrayList;

public class MateriasFragment extends Fragment {

    private FragmentMateriasBinding binding;
    private MateriasViewModel materiasViewModel;
    private MateriasAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        materiasViewModel = new ViewModelProvider(requireActivity()).get(MateriasViewModel.class);

        binding = FragmentMateriasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rvMaterias;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MateriasAdapter(new ArrayList<>(), getContext(), materiasViewModel);
        recyclerView.setAdapter(adapter);

        String[] periodos = {"Todos", "1", "2", "3", "4"};
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, periodos);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spPeriodo.setAdapter(adapterSpinner);

        binding.spPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String periodoSeleccionado = (String) parent.getItemAtPosition(position);
                materiasViewModel.filtrarMateriasPorPeriodo(periodoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        materiasViewModel.getMateriasList().observe(getViewLifecycleOwner(), materias -> {
            if (materias != null) {
                Log.d("MateriasFragment", "Received updated list with " + materias.size() + " items");
                materiasViewModel.filtrarMateriasPorPeriodo((String) binding.spPeriodo.getSelectedItem());
            }
        });

        materiasViewModel.getMateriasFiltradas().observe(getViewLifecycleOwner(), materias -> {
            if (materias != null) {
                adapter.actualizarMaterias(materias);
            }
        });

        materiasViewModel.obtenerMaterias();

        binding.fabAgregarMateria.setOnClickListener(v -> {
            DialogFragment dialog = new AgregarMateriasDialogFragment();
            dialog.show(getChildFragmentManager(), "AgregarMateriaDialog");
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        materiasViewModel.obtenerMaterias();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}