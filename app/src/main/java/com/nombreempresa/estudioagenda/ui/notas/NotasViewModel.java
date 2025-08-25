package com.nombreempresa.estudioagenda.ui.notas;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nombreempresa.estudioagenda.modelos.Calificaciones;
import com.nombreempresa.estudioagenda.modelos.Faltas;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.request.ApiClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotasViewModel extends AndroidViewModel {
        private MutableLiveData<List<Calificaciones>> notasList = new MutableLiveData<>(new ArrayList<>());
        private MutableLiveData<Calificaciones> notaParaEditar = new MutableLiveData<>();
        private MutableLiveData<List<Map<String, Object>>> notasAgrupadas = new MutableLiveData<>(new ArrayList<>());
        private Context context;
        // Constructor simplificado

    public NotasViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        notasList = new MutableLiveData<>(new ArrayList<>());
        notaParaEditar = new MutableLiveData<>();
        notasAgrupadas = new MutableLiveData<>(new ArrayList<>());
    }

    // Métodos para operaciones con notas
    public void agregarNota(Calificaciones nuevaNota, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Call<Calificaciones> call = api.AgregarNotas(token, nuevaNota);
        call.enqueue(new Callback<Calificaciones>() {
            @Override
            public void onResponse(@NonNull Call<Calificaciones> call, @NonNull Response<Calificaciones> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Actualizar la lista local inmediatamente
                    List<Calificaciones> currentList = notasList.getValue();
                    if (currentList == null) {
                        currentList = new ArrayList<>();
                    }
                    currentList.add(response.body());
                    notasList.setValue(currentList);
                    procesarNotasAgrupadas(currentList);

                    // Opcional: Recargar datos del servidor para asegurar consistencia
                    obtenerNotas();

                    if (onComplete != null) {
                        new Handler(Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    String errorMessage = "Error al agregar calificación: ";
                    try {
                        errorMessage += "Código " + response.code() + " - ";
                        if (response.errorBody() != null) {
                            errorMessage += response.errorBody().string();
                        } else {
                            errorMessage += "Respuesta vacía";
                        }
                    } catch (IOException e) {
                        errorMessage += "Error al leer el mensaje de error";
                    }
                    Log.e("API_ERROR", errorMessage); // Log detallado en consola
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show(); // Toast más largo
                }
            }

            @Override
            public void onFailure(@NonNull Call<Calificaciones> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error al agregar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Método para actualizar una nota existente

    public void actualizarNotas(Calificaciones nota, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Calificaciones> call = api.actualizarNotas(token, nota);

        call.enqueue(new Callback<Calificaciones>() {
            @Override
            public void onResponse(Call<Calificaciones> call, Response<Calificaciones> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerNotas(); // Refrescar la lista
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else if (response.code() == 404) {
                    Log.e("API_ERROR", "Nota no encontrada");
                    Toast.makeText(context, "Nota no encontrada", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_ERROR", "Error al actualizar nota: " + response.code());
                    Toast.makeText(context, "Error al actualizar nota", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Calificaciones> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarListaLocal(Calificaciones notaActualizada) {
        List<Calificaciones> currentList = notasList.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }

        boolean encontrado = false;
        for (int i = 0; i < currentList.size(); i++) {
            if (currentList.get(i).getIdCalificacion() == notaActualizada.getIdCalificacion()) {
                currentList.set(i, notaActualizada);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            currentList.add(notaActualizada);
        }

        notasList.setValue(currentList);
    }

    // Métodos para obtener datos
    public void obtenerNotas() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Log.d("AUTH_DEBUG", "Token siendo enviado: " + token); // Verifica el token

        api.obtenerNotas(token).enqueue(new Callback<List<Calificaciones>>() {
            @Override
            public void onResponse(Call<List<Calificaciones>> call, Response<List<Calificaciones>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && !response.body().isEmpty()) {
                        notasList.setValue(response.body());
                        procesarNotasAgrupadas(response.body());
                    } else {
                        // Respuesta exitosa pero sin contenido
                        Log.d("API_RESPONSE", "Respuesta exitosa pero sin datos");
                        notasList.setValue(new ArrayList<>()); // Lista vacía
                        procesarNotasAgrupadas(new ArrayList<>());
                    }
                } else {
                    Log.d("API_RESPONSE", "Error en la respuesta: " + response.code());
                    Toast.makeText(context, "Error al obtener notas: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Calificaciones>> call, Throwable t) {
                Log.e("API_ERROR", "Error: " + t.getMessage());
                Toast.makeText(context, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarNotasAgrupadas(List<Calificaciones> calificaciones) {
        Log.d("NotasViewModel", "Procesando notas agrupadas: " + (calificaciones != null ? calificaciones.size() : 0) + " calificaciones");
        
        if (calificaciones != null && !calificaciones.isEmpty()) {
            // Agrupar notas por materia
            Map<Materia, List<Calificaciones>> notasAgrupadasMap = new HashMap<>();

            for (Calificaciones calificacion : calificaciones) {
                Materia materia = calificacion.getMateria();
                if (materia == null) continue;

                if (!notasAgrupadasMap.containsKey(materia)) {
                    notasAgrupadasMap.put(materia, new ArrayList<>());
                }
                notasAgrupadasMap.get(materia).add(calificacion);
            }

            // Convertir a lista para el adapter
            List<Map<String, Object>> listaParaAdapter = new ArrayList<>();
            for (Map.Entry<Materia, List<Calificaciones>> entry : notasAgrupadasMap.entrySet()) {
                Map<String, Object> map = new HashMap<>();
                map.put("materia", entry.getKey());
                map.put("calificaciones", entry.getValue());
                listaParaAdapter.add(map);
            }

            Log.d("NotasViewModel", "Notas agrupadas procesadas: " + listaParaAdapter.size() + " grupos");
            notasAgrupadas.setValue(listaParaAdapter);
        } else {
            Log.d("NotasViewModel", "No hay calificaciones para procesar, enviando lista vacía");
            notasAgrupadas.setValue(new ArrayList<>());
        }
    }

    //ELIMINAR NOTAS
    public void eliminarNota(int idCalificacion, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Call<Void> call = api.eliminarNotas(token, idCalificacion);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Eliminar de la lista local
                    List<Calificaciones> currentList = notasList.getValue();
                    if (currentList != null) {
                        currentList.removeIf(calificacion -> calificacion.getIdCalificacion() == idCalificacion);
                        notasList.setValue(currentList);
                        procesarNotasAgrupadas(currentList);
                    }

                    if (onComplete != null) {
                        new Handler(Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    String errorMessage = "Error al eliminar: ";
                    try {
                        errorMessage += "Código " + response.code();
                        if (response.errorBody() != null) {
                            errorMessage += " - " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorMessage += "Error al leer mensaje";
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public LiveData<List<Calificaciones>> getNotasList() {
        return notasList;
    }

    public LiveData<List<Map<String, Object>>> getNotasAgrupadas() {
        return notasAgrupadas;
    }
}