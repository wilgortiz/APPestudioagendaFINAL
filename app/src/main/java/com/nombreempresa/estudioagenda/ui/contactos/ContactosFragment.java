package com.nombreempresa.estudioagenda.ui.contactos;

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
import com.nombreempresa.estudioagenda.databinding.FragmentContactosBinding;
import com.nombreempresa.estudioagenda.databinding.FragmentMateriasBinding;
import com.nombreempresa.estudioagenda.databinding.FragmentProfesBinding;
import com.nombreempresa.estudioagenda.ui.materias.AgregarMateriasDialogFragment;
import com.nombreempresa.estudioagenda.ui.materias.MateriasAdapter;
import com.nombreempresa.estudioagenda.ui.materias.MateriasViewModel;

import java.util.ArrayList;

public class ContactosFragment extends Fragment {

    private FragmentContactosBinding binding;
    private ContactosViewModel contactosViewModel;
    private ContactosAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        contactosViewModel = new ViewModelProvider(requireActivity()).get(ContactosViewModel.class);

        binding = FragmentContactosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rvContactos;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ContactosAdapter(new ArrayList<>(), getContext(),contactosViewModel);
        recyclerView.setAdapter(adapter);


        contactosViewModel.obtenerContactos();
        contactosViewModel.getContactosList().observe(getViewLifecycleOwner(), contactos -> {
            if (contactos != null) {
                adapter.actualizarContactos (contactos);
            }
        });

        binding.fabAgregarContactos.setOnClickListener(v -> {
            DialogFragment dialog = new AgregarContactosDialogFragment();
            dialog.show(getChildFragmentManager(), "AgregarContactosDialog");
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        contactosViewModel.obtenerContactos();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}