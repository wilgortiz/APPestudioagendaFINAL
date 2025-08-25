package com.nombreempresa.estudioagenda.ui.faltas;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nombreempresa.estudioagenda.modelos.FaltaActualizacionDTO;
import com.nombreempresa.estudioagenda.modelos.Faltas;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



//anda
public class FaltasViewModel extends AndroidViewModel {
    private MutableLiveData<List<Materia>> materias;
    private MutableLiveData<Integer> totalFaltas;
    private MutableLiveData<List<Map<String, Object>>> materiasConFaltas;

    private MutableLiveData<List<Faltas>> faltasList;

    private Context context;


    public FaltasViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();
        materias = new MutableLiveData<>();
        totalFaltas = new MutableLiveData<>();
        faltasList = new MutableLiveData<>();
        //totalFaltas.setValue(0);
        materiasConFaltas = new MutableLiveData<>();
    }

    public void agregarMateria(String nombre) {
        List<Materia> lista = materias.getValue();
        if (lista == null) {
            lista = new ArrayList<>();
        }
        Materia materia = new Materia();
        materia.setNombre(nombre);
        lista.add(materia);
        materias.setValue(lista);
    }





    public void AgregarMateriaFalta(Faltas nuevaFalta, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Faltas> call = api.AgregarFaltaMateria(token, nuevaFalta);
        call.enqueue(new Callback<Faltas>() {
            @Override
            public void onResponse(@NonNull Call<Faltas> call, @NonNull Response<Faltas> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Add the new note to the current list immediately
                    List<Faltas> currentList = faltasList.getValue();

                    if (currentList != null) {
                        currentList.add(response.body());
                        faltasList.setValue(currentList); // This triggers observers
                    }

                    // Also refresh the full list from the server
                    obtenerFaltas();

                    // Execute callback on UI thread
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    Toast.makeText(context, "Error al crear la falta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Faltas> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void agregarFalta(Materia materia) {
        List<Materia> lista = materias.getValue();
        if (lista != null) {
            int index = lista.indexOf(materia);
            if (index != -1) {
                Materia m = lista.get(index);
                // Agrega la falta a la materia
                // ...
                totalFaltas.setValue(totalFaltas.getValue() + 1);
            }
        }
    }

    public void quitarFalta(Materia materia) {
        List<Materia> lista = materias.getValue();
        if (lista != null) {
            int index = lista.indexOf(materia);
            if (index != -1) {
                Materia m = lista.get(index);
                // Quita la falta a la materia
                // ...
                totalFaltas.setValue(totalFaltas.getValue() - 1);
            }
        }
    }

    public void eliminarFaltas(int id) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Void> call = api.eliminarFaltas(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    obtenerFaltas(); // Refrescar la lista
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
    public void obtenerFaltas() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        api.obtenerFaltas(token).enqueue(new Callback<List<Faltas>>() {
            @Override
            public void onResponse(Call<List<Faltas>> call, Response<List<Faltas>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Faltas> faltas = response.body();
                    procesarDatosFaltas(faltas);
                }
            }

            @Override
            public void onFailure(Call<List<Faltas>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarDatosFaltas(List<Faltas> faltas) {
        List<Map<String, Object>> materiasList = new ArrayList<>();
        int total = 0;

        // Sin agrupar - muestra cada registro individual
        for (Faltas falta : faltas) {
            Map<String, Object> item = new HashMap<>();
            item.put("idMateria", falta.getIdMateria());
            item.put("nombre", falta.getMateria().getNombre());
            item.put("cantidad", falta.getCantidad());
            item.put("id_falta", falta.getIdFalta());
            materiasList.add(item);
            total += falta.getCantidad();
        }

        // Procesar logs de datos (moviendo la lógica del Fragment al ViewModel)
        procesarLogsDatos(materiasList);

        materiasConFaltas.postValue(materiasList);
        totalFaltas.postValue(total);
    }

    private void procesarLogsDatos(List<Map<String, Object>> materias) {
        Log.d("OBSERVER_DATA", "Datos recibidos en UI: " + (materias != null ? materias.size() : 0));
        if (materias != null) {
            for (int i = 0; i < materias.size(); i++) {
                Map<String, Object> item = materias.get(i);
                Log.d("ITEM_" + i,
                        "Nombre: " + item.get("nombre") +
                                " | Cantidad: " + item.get("cantidad"));
            }
        }
    }


    public LiveData<Integer> getTotalFaltas() {
        return totalFaltas;
    }

    public void actualizarFaltas(List<Map<String, Object>> materias) {
        int totalFaltasCount = 0;
        for (Map<String, Object> materia : materias) {
            totalFaltasCount += (int) materia.get("cantidad");
        }
        totalFaltas.setValue(totalFaltasCount);
    }

    public void modificarCantidadFalta(int idFalta,int idMateria, int nuevaCantidad) {
        // Just call the API directly with the idFalta we already have
        actualizarFaltaEnBaseDeDatos(idFalta,idMateria, nuevaCantidad);

        // Optionally: Update the local list if you want
        List<Faltas> currentFaltas = faltasList.getValue();
        if (currentFaltas != null) {
            for (Faltas falta : currentFaltas) {
                if (falta.getIdFalta() == idFalta) {
                    falta.setCantidad(nuevaCantidad);
                    break;
                }
            }
            faltasList.postValue(currentFaltas);
        }
    }
    public void actualizarFaltaEnBaseDeDatos(int idFalta, int idMateria, int nuevaCantidad) {
        String token = ApiClient.leerToken(context);
        if (token == null || token.isEmpty()) {
            Log.e("API_ERROR", "Token is null or empty");
            return;
        }

        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        FaltaActualizacionDTO request = new FaltaActualizacionDTO(idFalta,idMateria, nuevaCantidad);

        api.actualizarCantidadFalta(token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Refresh the list after successful update
                    obtenerFaltas();
                } else {
                    Log.e("API_ERROR", "Error: " + response.code() + " - " + response.message());
                    if (response.code() == 404) {
                        Toast.makeText(context, "Falta no encontrada", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Error: " + t.getMessage(), t);
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public LiveData<List<Map<String, Object>>> getMateriasConFaltas() {
        return materiasConFaltas;
    }

}