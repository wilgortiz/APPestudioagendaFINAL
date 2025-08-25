package com.nombreempresa.estudioagenda.ui.profes;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfesViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<List<Profesor>> profesList;
    private MutableLiveData<List<Profesor>> profesFiltrados;

    public ProfesViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        profesList = new MutableLiveData<>();
        profesFiltrados = new MutableLiveData<>();
    }

    public LiveData<List<Profesor>> getProfesList() {
        return profesList;
    }

    public LiveData<List<Profesor>> getProfesFiltrados() {
        return profesFiltrados;
    }

    public void obtenerProfes() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<List<Profesor>> call = api.obtenerProfes(token);

        Log.d("profes", "Solicitando profes con su token: " + token);

        call.enqueue(new Callback<List<Profesor>>() {
            @Override
            public void onResponse(Call<List<Profesor>> call, Response<List<Profesor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profesList.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Profesor>> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }
//
//    public void filtrarProfesPorPeriodo(String periodo) {
//        if (profesList.getValue() != null) {
//            List<Profesor> profesFiltradosList;
//            if (periodo.equals("Todos")) {
//                profesFiltradosList = profesList.getValue();
//            } else {
//                profesFiltradosList = profesList.getValue().stream()
//                        .filter(profesor -> String.valueOf(profesor.getPeriodo).equals(periodo))
//                        .collect(Collectors.toList());
//            }
//            profesFiltrados.setValue(profesFiltradosList);
//        }
//    }

    public void agregarProfe(Profesor profesor) {
        // Verifica que el profesor tenga todos los datos
        Log.d("ProfesorDebug", "Creando profesor: " +
                "Nombre: " + profesor.getNombre() +
                ", Apellido: " + profesor.getApellido() +
                ", Email: " + profesor.getEmail() +
                ", Celular: " + profesor.getCelular());

        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Profesor> call = api.AgregarProfes(token, profesor);

        call.enqueue(new Callback<Profesor>() {
            @Override
            public void onResponse(Call<Profesor> call, Response<Profesor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Profesor profesorCreado = response.body();
                    Log.d("API_RESPONSE", "Profesor creado: " +
                            "ID: " + profesorCreado.getIdProfesor() +
                            ", Nombre: " + profesorCreado.getNombre() +
                            ", Apellido: " + profesorCreado.getApellido());
                    obtenerProfes();
                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("API_ERROR_BODY", response.errorBody().string());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Profesor> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }



    public void actualizarProfe(Profesor profesor, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Profesor> call = api.actualizarProfe(token, profesor);

        call.enqueue(new Callback<Profesor>() {
            @Override
            public void onResponse(Call<Profesor> call, Response<Profesor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerProfes(); // Refrescar la lista
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    Log.e("API_ERROR", "Error al actualizar profesor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Profesor> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }

    public void eliminarProfe(int id) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Void> call = api.eliminarProfe(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    obtenerProfes(); // Refrescar la lista
                } else {
                    Log.e("API_ERROR", "Error al eliminar profesor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }
}