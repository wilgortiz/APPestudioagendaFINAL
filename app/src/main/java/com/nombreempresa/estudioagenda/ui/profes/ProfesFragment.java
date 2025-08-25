package com.nombreempresa.estudioagenda.ui.profes;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.databinding.FragmentMateriasBinding;
import com.nombreempresa.estudioagenda.databinding.FragmentProfesBinding;
import com.nombreempresa.estudioagenda.ui.materias.AgregarMateriasDialogFragment;
import com.nombreempresa.estudioagenda.ui.materias.MateriasAdapter;
import com.nombreempresa.estudioagenda.ui.materias.MateriasViewModel;

import java.util.ArrayList;

public class ProfesFragment extends Fragment {

    private FragmentProfesBinding binding;
    private ProfesViewModel profesViewModel;
    private ProfesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profesViewModel = new ViewModelProvider(requireActivity()).get(ProfesViewModel.class);

        binding = FragmentProfesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rvProfes;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ProfesAdapter(new ArrayList<>(), getContext(),profesViewModel);
        recyclerView.setAdapter(adapter);



        //para los periodos, pero no hace falta por ahora
//        String[] periodos = {"Todos", "1", "2", "3", "4"};
//        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, periodos);
//        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


//        binding.spPeriodoProfes.setAdapter(adapterSpinner);
//
//        binding.spPeriodoProfes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String periodoSeleccionado = (String) parent.getItemAtPosition(position);
//                profesViewModel.filtrarProfesPorPeriodo(periodoSeleccionado);
//            }

//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }

//        profesViewModel.getProfesList().observe(getViewLifecycleOwner(), profes -> {
//            if (profes != null) {
//                Log.d("ProfesFragment", "Received updated list with " + profes.size() + " items");
//                profesViewModel.filtrarProfesPorPeriodo((String) binding.spPeriodoProfes.getSelectedItem());
//            }
//        });
//
//        profesViewModel.getProfesFiltrados().observe(getViewLifecycleOwner(), profes -> {
//            if (profes != null) {
//                adapter.actualizarProfes(profes);
//            }
//        });

        profesViewModel.obtenerProfes();
        profesViewModel.getProfesList().observe(getViewLifecycleOwner(), profes -> {
            if (profes != null) {
                adapter.actualizarProfes(profes);
            }
        });

        binding.fabAgregarProfes.setOnClickListener(v -> {
            DialogFragment dialog = new AgregarProfesDialogFragment();
            dialog.show(getChildFragmentManager(), "AgregarProfesDialog");
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        profesViewModel.obtenerProfes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}