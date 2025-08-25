package com.nombreempresa.estudioagenda.ui.Horarios;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.databinding.FragmentHorariosBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class HorariosFragment extends Fragment {
    private FragmentHorariosBinding binding;
    private HorariosViewModel horariosViewModel;
    private HorariosAdapter adapter;
    private ScrollView daysScrollView;
    private ScrollView hoursScrollView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        horariosViewModel = new ViewModelProvider(requireActivity()).get(HorariosViewModel.class);
        binding = FragmentHorariosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new HorariosAdapter(new ArrayList<>(), getContext(), horariosViewModel);

        // Obtener referencias a los ScrollViews
        daysScrollView = binding.daysScrollView;
        hoursScrollView = binding.hoursScrollView;

        // Configurar sincronización de scroll entre las horas y los días
        setupScrollSynchronization();

        // Configurar botones de navegación
        binding.btnPrevWeek.setOnClickListener(v -> horariosViewModel.previousWeek());
        binding.btnNextWeek.setOnClickListener(v -> horariosViewModel.nextWeek());
        binding.btnAgregarHorario.setOnClickListener(v -> showAddHorarioDialog());

        // Observar cambios en la semana actual
        horariosViewModel.getCurrentWeek().observe(getViewLifecycleOwner(), calendar -> {
            binding.txtWeekRange.setText(horariosViewModel.getFormattedWeekRange());
            updateCalendarVisuals();
        });

        // Observar lista de horarios
        horariosViewModel.getHorarios().observe(getViewLifecycleOwner(), horarios -> {
            adapter.actualizarHorarios(horarios);
            updateCalendarVisuals();
        });

        // Cargar datos iniciales
        horariosViewModel.obtenerHorarios();

        return root;
    }

    private void setupScrollSynchronization() {
        // Sincronizar el scroll entre la columna de horas y los días
        daysScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            hoursScrollView.scrollTo(0, scrollY);
        });

        hoursScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            daysScrollView.scrollTo(0, scrollY);
        });
    }

    private void showAddHorarioDialog() {
        DialogFragment dialog = new AgregarHorariosDialogFragment();
        dialog.show(getChildFragmentManager(), "AgregarHorarioDialog");
    }

    private void updateCalendarVisuals() {
        // Limpiar vistas anteriores
        binding.daysHeader.removeAllViews();
        binding.hoursColumn.removeAllViews();
        binding.daysContainer.removeAllViews();

        // Obtener la semana actual
        Calendar weekStart = horariosViewModel.getWeekStart(horariosViewModel.getCurrentWeek().getValue());

        // Configurar encabezados de días
        setupDaysHeader(weekStart);

        // Configurar filas de horas
        setupHoursGrid(weekStart);
    }

    private void setupDaysHeader(Calendar weekStart) {
        LinearLayout daysHeader = binding.daysHeader;
        daysHeader.setWeightSum(7); // Para 7 días de la semana

        String[] dayNames = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};

        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) weekStart.clone();
            day.add(Calendar.DAY_OF_WEEK, i);

            TextView dayView = new TextView(getContext());
            dayView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f));
            dayView.setGravity(Gravity.CENTER);
            dayView.setText(dayNames[i] + "\n" + day.get(Calendar.DAY_OF_MONTH));
            dayView.setTextColor(Color.WHITE);
            dayView.setPadding(8, 16, 8, 16);

            daysHeader.addView(dayView);
        }
    }

    private void setupHoursGrid(Calendar weekStart) {
        LinearLayout hoursColumn = binding.hoursColumn;
        LinearLayout daysContainer = binding.daysContainer;

        // Asegurar que la columna de horas tenga el mismo tamaño que el contenedor de días
        hoursColumn.removeAllViews();
        daysContainer.removeAllViews();

        // Crear horas del día (de 8:00 a 22:00)
        for (int hour = 8; hour <= 22; hour++) {
            // Añadir label de hora a la columna izquierda
            TextView hourLabel = new TextView(getContext());
            hourLabel.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(60)));
            hourLabel.setText(String.format("%02d:00", hour));
            hourLabel.setTextColor(Color.BLACK);
            hourLabel.setGravity(Gravity.CENTER);
            hourLabel.setBackgroundResource(R.drawable.cell_border);
            hoursColumn.addView(hourLabel);

            // Crear fila para los días
            LinearLayout dayRow = new LinearLayout(getContext());
            dayRow.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(60)));
            dayRow.setOrientation(LinearLayout.HORIZONTAL);
            dayRow.setWeightSum(7); // Para 7 días

            // Añadir celdas para cada día
            for (int day = 0; day < 7; day++) {
                FrameLayout cellFrame = new FrameLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f);
                params.setMargins(1, 1, 1, 1); // Pequeño margen para separar celdas
                cellFrame.setLayoutParams(params);
                cellFrame.setBackgroundResource(R.drawable.cell_border);
                dayRow.addView(cellFrame);
            }

            daysContainer.addView(dayRow);
        }

        // Añadir los horarios a las celdas correspondientes
        addScheduleItems(weekStart);

        // Resetear la posición de scroll
        daysScrollView.scrollTo(0, 0);
        hoursScrollView.scrollTo(0, 0);
    }


    private void addScheduleItems(Calendar weekStart) {
        List<Map<String, Object>> horariosList = horariosViewModel.getHorarios().getValue();
        if (horariosList == null) return;

        LinearLayout daysContainer = binding.daysContainer;

        for (Map<String, Object> horario : horariosList) {
            String diaSemana = (String) horario.get("diaSemana");
            String horaInicio = (String) horario.get("horaInicio");
            String horaFin = (String) horario.get("horaFin");
            String nombreMateria = (String) horario.get("nombreMateria");
            int idHorario = (int) horario.get("idHorario");
            int idMateria = (int) horario.get("idMateria");

            int dayIndex = convertDayToIndex(diaSemana);
            String[] timeParts = horaInicio.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            int rowIndex = hour - 8;
            if (rowIndex < 0 || rowIndex >= daysContainer.getChildCount()) continue;

            LinearLayout dayRow = (LinearLayout) daysContainer.getChildAt(rowIndex);
            if (dayRow.getChildCount() > dayIndex) {
                View cell = dayRow.getChildAt(dayIndex);

                TextView scheduleView = new TextView(getContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);
                scheduleView.setLayoutParams(params);

                scheduleView.setText(nombreMateria);
                scheduleView.setBackgroundColor(Color.parseColor("#3F51B5"));
                scheduleView.setTextColor(Color.WHITE);
                scheduleView.setGravity(Gravity.CENTER);
                scheduleView.setPadding(8, 4, 8, 4);
                scheduleView.setSingleLine(false);
                scheduleView.setEllipsize(TextUtils.TruncateAt.END);
                scheduleView.setMaxLines(2);

                // Agregar onClickListener
                scheduleView.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putInt("idHorario", idHorario);
                    args.putString("horaFin", horaFin);
                    args.putString("nombreMateria", nombreMateria);
                    args.putInt("idMateria", idMateria);
                    args.putString("diaSemana", diaSemana);
                    args.putString("horaInicio", horaInicio);
                    //args.putString("horaFin", (String) horario.get("horaFin"));

                    DialogFragment dialog = EditarHorarioDialogFragment.newInstance(horario);
                    dialog.show(getChildFragmentManager(), "OpcionesHorarioDialog");
                });

                if (cell instanceof ViewGroup) {
                    ((ViewGroup) cell).addView(scheduleView);
                }
            }
        }
    }



    private int convertDayToIndex(String diaSemana) {
        switch (diaSemana.toLowerCase()) {
            case "lunes": return 0;
            case "martes": return 1;
            case "miércoles": return 2;
            case "jueves": return 3;
            case "viernes": return 4;
            case "sábado": return 5;
            case "domingo": return 6;
            default: return 0;
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onResume() {
        super.onResume();
        horariosViewModel.obtenerHorarios();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}