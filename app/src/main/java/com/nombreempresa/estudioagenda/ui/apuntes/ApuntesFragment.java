package com.nombreempresa.estudioagenda.ui.apuntes;


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


import com.nombreempresa.estudioagenda.databinding.FragmentApuntesBinding;
import com.nombreempresa.estudioagenda.modelos.Apuntes;
import com.nombreempresa.estudioagenda.ui.apuntes.ApuntesAdapter;


import java.util.ArrayList;
import java.util.List;



/*
//apuntesfragment


public class ApuntesFragment extends Fragment {


    private FragmentApuntesBinding binding;
    private ApuntesViewModel apuntesViewModel;
    private ApuntesAdapter adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        apuntesViewModel = new ViewModelProvider(this).get(ApuntesViewModel.class);


        binding = FragmentApuntesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Configurar RecyclerView
        RecyclerView recyclerView = binding.rvApuntes;
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // grilla
        adapter = new ApuntesAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);


        // Observamos los cambios en los apuntes
        apuntesViewModel.getApuntesList().observe(getViewLifecycleOwner(), new Observer<List<Apuntes>>() {
            @Override
            public void onChanged(List<Apuntes> apuntes) {
                adapter.actualizarApuntes(apuntes);
                adapter.notifyDataSetChanged(); // Notificar a la vista para que se actualice
                apuntesViewModel.obtenerTodosLosApuntes();
            }
        });

        return root;
    }
}

*/
/*
        // Inicializar el botón para agregar apuntes
        binding.fabAgregarApunte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostrar el dialogo para ingresar el apunte
                DialogFragment dialog = new AgregarApunteDialogFragment();
                dialog.show(getChildFragmentManager(), "AgregarApunteDialog");
            }
        });

        // Cargar los apuntes al crear la vista
        apuntesViewModel.obtenerTodosLosApuntes();


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
*/




//version 2
public class ApuntesFragment extends Fragment {

    private FragmentApuntesBinding binding;
    private ApuntesViewModel apuntesViewModel;
    private ApuntesAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Use shared ViewModel instance with the activity scope to ensure
        // dialog and fragment use the same instance
        apuntesViewModel = new ViewModelProvider(requireActivity()).get(ApuntesViewModel.class);

        binding = FragmentApuntesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.rvApuntes;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize adapter with empty list
        adapter = new ApuntesAdapter( getContext(), apuntesViewModel);
        recyclerView.setAdapter(adapter);

        // Observe changes in the list of notes
        apuntesViewModel.getApuntesList().observe(getViewLifecycleOwner(), apuntes -> {
            if (apuntes != null) {
                Log.d("ApuntesFragment", "Received updated list with " + apuntes.size() + " items");
                adapter.actualizarApuntes(apuntes);
            }
        });

        // Initially load the data
        apuntesViewModel.obtenerApuntes();

        binding.fabAgregarApunte.setOnClickListener(v -> {
            DialogFragment dialog = new AgregarApunteDialogFragment();
            dialog.show(getChildFragmentManager(), "AgregarApunteDialog");
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment comes back to foreground
        apuntesViewModel.obtenerApuntes();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
