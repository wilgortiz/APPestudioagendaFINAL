package com.nombreempresa.estudioagenda.ui.calendario;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.nombreempresa.estudioagenda.modelos.Actividades;
import com.nombreempresa.estudioagenda.request.ApiClient;
import com.applandeo.materialcalendarview.EventDay;
import com.nombreempresa.estudioagenda.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarioViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Actividades>> actividadesList = new MutableLiveData<>(new ArrayList<>());
    private final Context context;

    public CalendarioViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }


    public LiveData<List<Actividades>> getActividadesList() {
        return actividadesList;
    }

    public LiveData<List<EventDay>> getEventDays() {
        MutableLiveData<List<EventDay>> eventDays = new MutableLiveData<>();
        List<Actividades> actividades = actividadesList.getValue();
        
        if (actividades == null || actividades.isEmpty()) {
            Log.d("Calendario", "No hay actividades para mostrar");
            eventDays.setValue(new ArrayList<>());
            return eventDays;
        }

        List<EventDay> events = new ArrayList<>();
        for (Actividades actividad : actividades) {
            if (actividad.getFechaEvento() != null) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(actividad.getFechaEvento());
                    events.add(new EventDay(calendar, R.drawable.event_indicator));
                } catch (Exception e) {
                    Log.e("Calendario", "Error al procesar fecha: " + e.getMessage(), e);
                }
            }
        }

        eventDays.setValue(events);
        return eventDays;
    }

    public List<Actividades> getActividadesDelDia(Calendar clickedDay) {
        List<Actividades> actividadesDelDia = new ArrayList<>();
        List<Actividades> actividadesList = this.actividadesList.getValue();
        
        if (actividadesList != null) {
            for (Actividades actividad : actividadesList) {
                if (actividad.getFechaEvento() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(actividad.getFechaEvento());

                    if (calendar.get(Calendar.YEAR) == clickedDay.get(Calendar.YEAR) &&
                            calendar.get(Calendar.MONTH) == clickedDay.get(Calendar.MONTH) &&
                            calendar.get(Calendar.DAY_OF_MONTH) == clickedDay.get(Calendar.DAY_OF_MONTH)) {
                        actividadesDelDia.add(actividad);
                    }
                }
            }
        }
        
        return actividadesDelDia;
    }

    public void crearActividad(Actividades nuevaActividad, final Runnable onComplete) {
        // Validación de la fecha antes de hacer la solicitud
        if (nuevaActividad.getFechaEvento() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Actividades> call = api.crearActividad(token, nuevaActividad);
        call.enqueue(new Callback<Actividades>() {
            @Override
            public void onResponse(@NonNull Call<Actividades> call, @NonNull Response<Actividades> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Programar la notificación
                    programarNotificacion(context, response.body());
                    // Actualizar la lista de actividades actual
                    List<Actividades> currentList = actividadesList.getValue();
                    if (currentList == null) {
                        currentList = new ArrayList<>();
                    }

                    // Agregar la nueva actividad a la lista
                    currentList.add(response.body());
                    actividadesList.setValue(currentList);

                    // Notificar que se completó la operación
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }

                    // Refrescar las actividades desde el servidor
                    obtenerActividades();
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        Log.e("apierror", "Error: " + errorMessage);
                    } catch (IOException e) {
                        Log.e("apierror", "Error al leer el cuerpo de la respuesta de error", e);
                    }
                    Toast.makeText(context, "Error al crear actividad", Toast.LENGTH_SHORT).show();
                }
                // Add this in CalendarioViewModel.java in the onResponse method
                try {
                    String jsonResponse = new Gson().toJson(response.body());
                    Log.d("Calendario", "Raw JSON response: " + jsonResponse);
                } catch (Exception e) {
                    Log.e("Calendario", "Error al serializar respuesta JSON", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Actividades> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void obtenerActividades() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<List<Actividades>> call = api.obtenerActividades(token);

        Log.d("Calendario", "Obteniendo actividades con token: " + token);
        call.enqueue(new Callback<List<Actividades>>() {
            @Override
            public void onResponse(@NonNull Call<List<Actividades>> call, @NonNull Response<List<Actividades>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("JSON", "Respuesta JSON completa: " + new Gson().toJson(response.body()));

                    List<Actividades> actividades = response.body();
                    Log.d("Calendario", "Recibidas " + actividades.size() + " actividades");

                    // Log de la respuesta raw
                    try {
                        // Este es un truco para ver el cuerpo bruto de la respuesta
                        // Solo útil para debugging - no es una práctica recomendada para producción
                        String rawResponse = response.raw().toString();
                        Log.d("Calendario", "Raw response headers: " + rawResponse);
                    } catch (Exception e) {
                        Log.e("Calendario", "Error al leer cabeceras de respuesta", e);
                    }

                    // Log details of each activity
                    for (Actividades act : actividades) {
                        if (act.getFechaEvento() != null) {
                            Log.d("Calendario", "Actividad: ID=" + act.getIdEvento()
                                    + ", Fecha=" + act.getFechaEvento().toString()
                                    + ", Desc=" + act.getDescripcion()
                                    + ",tipo=" + act.getTipo_Evento());

                            // Convertir a Calendar para verificar componentes de fecha
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(act.getFechaEvento());
                            Log.d("Calendario", "Fecha componentes: Año=" + calendar.get(Calendar.YEAR)
                                    + ", Mes=" + calendar.get(Calendar.MONTH)
                                    + " (0=Enero), Día=" + calendar.get(Calendar.DAY_OF_MONTH));
                        } else {
                            Log.e("Calendario", "Actividad con fecha nula: ID=" + act.getIdEvento());
                        }
                    }

                    actividadesList.setValue(actividades);
                } else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
                        Log.e("Calendario", "Error: " + errorMessage + ", Código: " + response.code());
                    } catch (IOException e) {
                        Log.e("Calendario", "Error al leer el cuerpo de la respuesta de error", e);
                    }
                    Toast.makeText(context, "Error al obtener actividades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Actividades>> call, @NonNull Throwable t) {
                Log.e("Calendario", "Fallo en la llamada API: " + t.getMessage(), t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void actualizarActividad(Actividades actividad, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Actividades> call = api.actualizarActividad(token, actividad);

        call.enqueue(new Callback<Actividades>() {
            @Override
            public void onResponse(@NonNull Call<Actividades> call, @NonNull Response<Actividades> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Reprogramar la notificación con los nuevos datos
                    programarNotificacion(context, response.body());
                    obtenerActividades(); // Refrescar la lista
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    Toast.makeText(context, "Error al actualizar actividad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Actividades> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void eliminarActividad(int id) {
        // Primero cancelar la notificación
        cancelarNotificacion(context, id);
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Void> call = api.eliminarActividad(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    obtenerActividades(); // Refrescar la lista
                } else {
                    Toast.makeText(context, "Error al eliminar actividad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
        //NOTIFICACIONES
        public void programarNotificacion(Context context, Actividades actividad) {

            // Verificar permisos primero
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Notificaciones", "Permisos de notificación no concedidos");
                    return;
                }
            }

            // Verificar permiso para alarmas exactas (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (!alarmManager.canScheduleExactAlarms()) {
                    // Pedir permiso al usuario
                    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Log.e("Notificaciones", "Se necesita permiso para alarmas exactas");
                    return;
                }
            }
            if (actividad == null || actividad.getFechaEvento() == null) {
                Log.e("Notificaciones", "Actividad o fecha es null");
                return;
            }

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(actividad.getFechaEvento());

                // Ya no necesitamos establecer hora fija (9 AM) porque ahora la hora viene del TimePicker
                // calendar.set(Calendar.HOUR_OF_DAY, 9);
                // calendar.set(Calendar.MINUTE, 0);
                // calendar.set(Calendar.SECOND, 0);

                // Verificar que la fecha no sea en el pasado
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    Log.w("Notificaciones", "Fecha de evento en el pasado: " + calendar.getTime());
                    return;
                }

                Intent intent = new Intent(context, notificaciones.class);
                intent.putExtra("actividad", actividad);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        actividad.getIdEvento(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                }

                Log.d("Notificaciones", "Notificación programada para: " + calendar.getTime());
            } catch (Exception e) {
                Log.e("Notificaciones", "Error al programar notificación", e);
            }
        }
            public void cancelarNotificacion(Context context, int idEvento) {
                Intent intent = new Intent(context, notificaciones.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        context,
                        idEvento,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                // También cancelar la notificación si ya fue mostrada
                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(idEvento);
            }
}
