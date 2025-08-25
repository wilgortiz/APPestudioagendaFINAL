package com.nombreempresa.estudioagenda.ui.faltas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.databinding.FragmentFaltasBinding;

import java.util.ArrayList;
import java.util.Map;

public class FaltasFragment extends Fragment {
    private FragmentFaltasBinding binding;
    private FaltasViewModel faltasViewModel;
    private FaltasAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        faltasViewModel = new ViewModelProvider(requireActivity()).get(FaltasViewModel.class);
        binding = FragmentFaltasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rvFaltasMaterias;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new FaltasAdapter(new ArrayList<>(), getContext(), faltasViewModel);
        recyclerView.setAdapter(adapter);

        // FAB para agregar faltas
        binding.fabAgregarFaltaMateria.setOnClickListener(v -> {
            DialogFragment dialog = new AgregarFaltasDialogFragment();
            dialog.show(getChildFragmentManager(), "AgregarFaltasDialog");
        });

        // Observar total de faltas
        faltasViewModel.getTotalFaltas().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer totalFaltas) {
                binding.tvFaltasTotales.setText(String.valueOf(totalFaltas));
            }
        });

        // Observar lista de materias con faltas
        faltasViewModel.getMateriasConFaltas().observe(getViewLifecycleOwner(), materias -> {
            adapter.actualizarFaltas(materias);
        });

        // Cargar datos
        faltasViewModel.obtenerFaltas();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        faltasViewModel.obtenerFaltas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

