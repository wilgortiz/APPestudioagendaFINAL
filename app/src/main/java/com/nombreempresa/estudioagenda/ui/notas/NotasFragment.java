package com.nombreempresa.estudioagenda.ui.notas;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
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

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.databinding.FragmentFaltasBinding;
import com.nombreempresa.estudioagenda.databinding.FragmentNotasBinding;
import com.nombreempresa.estudioagenda.modelos.Calificaciones;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.ui.faltas.AgregarFaltasDialogFragment;
import com.nombreempresa.estudioagenda.ui.faltas.FaltasAdapter;
import com.nombreempresa.estudioagenda.ui.faltas.FaltasViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotasFragment extends Fragment {
    private FragmentNotasBinding binding;
    private NotasViewModel notasViewModel;
    private NotasAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inicializa el ViewModel correctamente
        notasViewModel = new ViewModelProvider(this).get(NotasViewModel.class);

        binding = FragmentNotasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerView();
        setupFab();
        setupObservers();

        // Cargar datos
        Log.d("NotasFragment", "Llamando a obtenerNotas desde onCreateView");
        notasViewModel.obtenerNotas();

        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.rvNotasMaterias;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new NotasAdapter(new ArrayList<>(), requireContext(), notasViewModel);
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabAgregarNotaMateria.setOnClickListener(v -> {
            try {
                DialogFragment dialog = new AgregarNotasDialogFragment();
                dialog.show(getChildFragmentManager(), "AgregarNotasDialog");
            } catch (Exception e) {
                Log.e("NOTAS_ERROR", "Error al mostrar diálogo", e);
            }
        });
    }


    private void setupObservers() {
        notasViewModel.getNotasAgrupadas().observe(getViewLifecycleOwner(), listaParaAdapter -> {
            Log.d("NotasFragment", "Datos agrupados recibidos: " + (listaParaAdapter != null ? listaParaAdapter.size() : 0) + " elementos");
            adapter.actualizarNotas(listaParaAdapter);
        });
    }

    // En NotasFragment
    public void actualizarLista() {
        // Actualizar el adaptador con los datos actuales del ViewModel
        List<Map<String, Object>> listaParaAdapter = notasViewModel.getNotasAgrupadas().getValue();
        if (listaParaAdapter != null) {
            adapter.actualizarNotas(listaParaAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("NotasFragment", "Llamando a obtenerNotas desde onResume");
        notasViewModel.obtenerNotas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}