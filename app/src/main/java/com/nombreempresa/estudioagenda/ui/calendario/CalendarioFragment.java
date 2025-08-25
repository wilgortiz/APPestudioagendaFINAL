package com.nombreempresa.estudioagenda.ui.calendario;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.databinding.FragmentCalendarioBinding;
import com.nombreempresa.estudioagenda.modelos.Actividades;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarioFragment extends Fragment {
    private FragmentCalendarioBinding binding;
    private CalendarioViewModel calendarioViewModel;
    private CalendarView calendarView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarioViewModel = new ViewModelProvider(requireActivity()).get(CalendarioViewModel.class);

        binding = FragmentCalendarioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarView = binding.calendarView;

        // Load activities when fragment is created
        calendarioViewModel.obtenerActividades();

        calendarioViewModel.getActividadesList().observe(getViewLifecycleOwner(), actividades -> {
            calendarioViewModel.getEventDays().observe(getViewLifecycleOwner(), events -> {
                try {
                    calendarView.setEvents(events);
                } catch (Exception e) {
                    Log.e("Calendario", "Error al establecer eventos: " + e.getMessage(), e);
                }
            });
        });

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDay = eventDay.getCalendar();
            List<Actividades> actividadesDelDia = calendarioViewModel.getActividadesDelDia(clickedDay);

            if (!actividadesDelDia.isEmpty()) {
                // Mostrar modal con las actividades
                mostrarModalActividades(actividadesDelDia);
            } else {
                Toast.makeText(requireContext(), "No hay actividades para esta fecha", Toast.LENGTH_SHORT).show();
            }
        });

        binding.fabAgregarActividad.setOnClickListener(v -> {
            DialogFragment dialog = new AgregarActividadDialogFragment();
            dialog.show(getChildFragmentManager(), "AgregarActividadDialog");
        });

        return root;
    }

    private void mostrarModalActividades(List<Actividades> actividades) {
        DialogFragment dialog = new DetalleActividadDialogFragment(actividades);
        dialog.show(getChildFragmentManager(), "DetalleActividadDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


