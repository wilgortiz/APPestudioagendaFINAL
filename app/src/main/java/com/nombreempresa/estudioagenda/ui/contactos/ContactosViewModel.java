package com.nombreempresa.estudioagenda.ui.contactos;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nombreempresa.estudioagenda.modelos.Contactos;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactosViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<List<Contactos>> contactosList;
    private MutableLiveData<List<Contactos>> contactosFiltrados;

    public ContactosViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        contactosList = new MutableLiveData<>();
        contactosFiltrados = new MutableLiveData<>();
    }

    public LiveData<List<Contactos>> getContactosList() {
        return contactosList;
    }
    public LiveData<List<Contactos>> getContactosFiltrados() {
        return contactosFiltrados;
    }


    public void obtenerContactos() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<List<Contactos>> call = api.obtenerContactos(token);

        Log.d("Contactos", "Solicitando Contactos con su token: " + token);

        call.enqueue(new Callback<List<Contactos>>() {
            @Override
            public void onResponse(Call<List<Contactos>> call, Response<List<Contactos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Contactos> contactos = response.body();
                    if (contactos.isEmpty()) {
                        Log.d("Contactos", "No hay contactos disponibles");
                    }
                    contactosList.setValue(contactos);
                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                }
            }


            @Override
            public void onFailure(Call<List<Contactos>> call, Throwable t) {
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

    public void agregarContacto(Contactos contacto) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Contactos> call = api.AgregarContactos(token, contacto);
        call.enqueue(new Callback<Contactos>() {
            @Override
            public void onResponse(Call<Contactos> call, Response<Contactos> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerContactos();
                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Contactos> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }


    public void actualizarContacto(Contactos contacto, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Contactos> call = api.actualizarContactos(token, contacto);

        call.enqueue(new Callback<Contactos>() {
            @Override
            public void onResponse(Call<Contactos> call, Response<Contactos> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerContactos(); // Refrescar la lista
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    Log.e("API_ERROR", "Error al actualizar contacto: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Contactos> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }



    public void eliminarContactos(int id) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Void> call = api.eliminarContacto(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    obtenerContactos(); // Refrescar la lista
                } else {
                    Log.e("API_ERROR", "Error al eliminar el contacto: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }
}