package com.nombreempresa.estudioagenda.ui.Horarios;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nombreempresa.estudioagenda.modelos.Horarios;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HorariosViewModel extends AndroidViewModel {
    private MutableLiveData<List<Map<String, Object>>> horarios;
    private MutableLiveData<Calendar> currentWeek;
    private Context context;

    public HorariosViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        horarios = new MutableLiveData<>();
        currentWeek = new MutableLiveData<>();
        resetToCurrentWeek();
    }

    public void resetToCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        currentWeek.setValue(calendar);
    }

    public void nextWeek() {
        Calendar calendar = currentWeek.getValue();
        if (calendar != null) {
            calendar = (Calendar) calendar.clone();
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            currentWeek.setValue(calendar);
        }
    }

    public void previousWeek() {
        Calendar calendar = currentWeek.getValue();
        if (calendar != null) {
            calendar = (Calendar) calendar.clone();
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            currentWeek.setValue(calendar);
        }
    }

    public String getFormattedWeekRange() {
        Calendar calendar = currentWeek.getValue();
        if (calendar == null) return "";

        Calendar start = getWeekStart(calendar);
        Calendar end = getWeekEnd(start);

        return String.format("%02d/%02d - %02d/%02d",
                start.get(Calendar.DAY_OF_MONTH), start.get(Calendar.MONTH) + 1,
                end.get(Calendar.DAY_OF_MONTH), end.get(Calendar.MONTH) + 1);
    }

    Calendar getWeekStart(Calendar calendar) {
        Calendar start = (Calendar) calendar.clone();
        start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return start;
    }

    private Calendar getWeekEnd(Calendar start) {
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_WEEK, 6);
        return end;
    }


    public void agregarHorario(String nombreMateria, int idMateria, String diaSemana,
                               String horaInicio, String horaFin, final Runnable onComplete) {
        // Asegurarse que el formato sea hh:mm y agregar ":00"
        if (horaInicio.matches("^\\d{2}:\\d{2}$")) {
            horaInicio += ":00";
        }
        if (horaFin.matches("^\\d{2}:\\d{2}$")) {
            horaFin += ":00";
        }

        Horarios nuevoHorario = new Horarios();
        nuevoHorario.setIdMateria(idMateria);
        nuevoHorario.setDiaSemana(diaSemana);
        nuevoHorario.setHoraInicio(horaInicio);
        nuevoHorario.setHoraFin(horaFin);

        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Log.d("AgregarHorario", "Token: " + token);
        Log.d("AgregarHorario", "Datos enviados: idMateria=" + idMateria + ", dia=" + diaSemana
                + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin);

        Call<Horarios> call = api.AgregarHorariosMateria(token, nuevoHorario);
        call.enqueue(new Callback<Horarios>() {
            @Override
            public void onResponse(@NonNull Call<Horarios> call, @NonNull Response<Horarios> response) {
                Log.d("AgregarHorario", "onResponse - Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("AgregarHorario", "Horario creado correctamente: " + response.body().toString());
                    obtenerHorarios();
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin cuerpo de error";
                        Log.e("AgregarHorario", "Error al crear el horario - Código: " + response.code() + ", Error: " + errorBody);
                    } catch (Exception e) {
                        Log.e("AgregarHorario", "Excepción leyendo errorBody", e);
                    }
                    Toast.makeText(context, "Error al crear el horario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Horarios> call, @NonNull Throwable t) {
                Log.e("AgregarHorario", "Fallo en la llamada", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void actualizarHorario(int idHorario, Horarios horario, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        horario.setIdHorario(idHorario);
        // Asegurar formato correcto de horas
        if (horario.getHoraInicio().matches("^\\d{2}:\\d{2}$")) {
            horario.setHoraInicio(horario.getHoraInicio() + ":00");
        }
        if (horario.getHoraFin().matches("^\\d{2}:\\d{2}$")) {
            horario.setHoraFin(horario.getHoraFin() + ":00");
        }

        Call<Horarios> call = api.actualizarHorario(token, horario);

        call.enqueue(new Callback<Horarios>() {
            @Override
            public void onResponse(Call<Horarios> call, Response<Horarios> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerHorarios(); // Refrescar la lista
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    Log.e("API_ERROR", "Error al actualizar horario: " + response.code());
                    Toast.makeText(context, "Error al actualizar horario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Horarios> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void eliminarHorario(int id) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Void> call = api.eliminarHorario(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    obtenerHorarios(); // Refrescar la lista
                } else {
                    Log.e("API_ERROR", "Error al eliminar horario: " + response.code());
                    Toast.makeText(context, "Error al eliminar horario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void obtenerHorarios() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Log.d("API_DEBUG", "Iniciando solicitud para obtener horarios");
        Log.d("API_DEBUG", "Token utilizado: " + token);

        api.obtenerHorarios(token).enqueue(new Callback<List<Horarios>>() {
            @Override
            public void onResponse(Call<List<Horarios>> call, Response<List<Horarios>> response) {
                Log.d("API_DEBUG", "Respuesta recibida. Código: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_DEBUG", "Respuesta exitosa. Cantidad de horarios: " + response.body().size());

                    // Log detallado de cada horario recibido
                    for (Horarios horario : response.body()) {
                        Log.d("API_DATA", "Horario ID: " + horario.getIdHorario() +
                                " | Estudiante ID: " + horario.getIdEstudiante() +
                                " | Materia ID: " + horario.getIdMateria() +
                                " | Día: " + horario.getDiaSemana() +
                                " | Hora Inicio: " + horario.getHoraInicio() +
                                " | Hora Fin: " + horario.getHoraFin());
                    }

                    procesarDatosHorarios(response.body());
                } else {
                    Log.e("API_ERROR", "Respuesta no exitosa o body vacío");
                    if (response.errorBody() != null) {
                        try {
                            Log.e("API_ERROR", "Error body: " + response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("API_ERROR", "Error al leer errorBody", e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Horarios>> call, Throwable t) {
                Log.e("API_FAILURE", "Error en la solicitud: " + t.getMessage(), t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void procesarDatosHorarios(List<Horarios> horariosList) {
        List<Map<String, Object>> horariosMap = new ArrayList<>();

        for (Horarios horario : horariosList) {
            Map<String, Object> item = new HashMap<>();
            item.put("idHorario", horario.getIdHorario());
            item.put("idMateria", horario.getIdMateria());
            item.put("nombreMateria", horario.getMateria().getNombre());
            item.put("diaSemana", horario.getDiaSemana());
            // Formatear horas para quitar los segundos
            String horaInicio = horario.getHoraInicio();
            String horaFin = horario.getHoraFin();

            if (horaInicio != null && horaInicio.length() >= 8) {
                horaInicio = horaInicio.substring(0, 5); // Tomar solo HH:mm
            }

            if (horaFin != null && horaFin.length() >= 8) {
                horaFin = horaFin.substring(0, 5); // Tomar solo HH:mm
            }

            item.put("horaInicio", horaInicio);
            item.put("horaFin", horaFin);
            horariosMap.add(item);
        }

        horarios.postValue(horariosMap);
    }


    public LiveData<List<Map<String, Object>>> getHorarios() {
        return horarios;
    }

    public LiveData<Calendar> getCurrentWeek() {
        return currentWeek;
    }

    public String[] getDayNames() {
        return new String[]{"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
    }

    public int getHourRangeStart() {
        return 8;
    }

    public int getHourRangeEnd() {
        return 22;
    }

    public int convertDayToIndex(String diaSemana) {
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

    public List<Map<String, Object>> getHorariosForCalendar() {
        return horarios.getValue();
    }
}



