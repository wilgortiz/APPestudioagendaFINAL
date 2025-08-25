package com.nombreempresa.estudioagenda.ui.apuntes;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.nombreempresa.estudioagenda.modelos.Apuntes;
import com.nombreempresa.estudioagenda.request.ApiClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//apuntesviewmodel
public class ApuntesViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<List<Apuntes>> apuntesList;
    private MutableLiveData<Apuntes> apunteSeleccionado;

    public ApuntesViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        apuntesList = new MutableLiveData<>();
        apunteSeleccionado = new MutableLiveData<>();
    }

    public LiveData<List<Apuntes>> getApuntesList() {
        return apuntesList;
    }

    public LiveData<Apuntes> getApunteSeleccionado() {
        return apunteSeleccionado;
    }


    public void obtenerApuntes() {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<List<Apuntes>> call = api.obtenerApuntes(token);

        Log.d("API_REQUEST", "Solicitando apuntes con token: " + token);

        call.enqueue(new Callback<List<Apuntes>>() {
            @Override
            public void onResponse(Call<List<Apuntes>> call, Response<List<Apuntes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Imprime el cuerpo de la respuesta en bruto para ver exactamente qué estás recibiendo
                    String responseBody = response.raw().body() != null ?
                            response.raw().body().toString() : "Cuerpo de respuesta vacío";
                    Log.d("API_RESPONSE_RAW", "Respuesta cruda: " + responseBody);

                    List<Apuntes> apuntes = response.body();
                    for (Apuntes a : apuntes) {
                        Log.d("APUNTE_DEBUG", "ID: " + a.getIdApunte() +
                                " | Estudiante: " + a.getIdEstudiante() +
                                " | Título: '" + a.getTitulo() + "'" +
                                " | Desc: '" + a.getDescripcion() + "'");
                    }

                    apuntesList.setValue(response.body());
                } else {
                    Log.e("API_ERROR", "Código: " + response.code() + " | Mensaje: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Apuntes>> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }

    // Obtener apunte por ID
    public void obtenerApuntePorId(int id) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Apuntes> call = api.obtenerApuntePorId(token, id);
        call.enqueue(new Callback<Apuntes>() {
            @Override
            public void onResponse(@NonNull Call<Apuntes> call, @NonNull Response<Apuntes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    apunteSeleccionado.setValue(response.body());
                } else {
                    Toast.makeText(context, "Error al obtener el apunte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Apuntes> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void crearApunte(Apuntes nuevoApunte, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Apuntes> call = api.crearApunte(token, nuevoApunte);
        call.enqueue(new Callback<Apuntes>() {
            @Override
            public void onResponse(@NonNull Call<Apuntes> call, @NonNull Response<Apuntes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Add the new note to the current list immediately
                    List<Apuntes> currentList = apuntesList.getValue();
                    if (currentList != null) {
                        currentList.add(response.body());
                        apuntesList.setValue(currentList); // This triggers observers
                    }

                    // Also refresh the full list from the server
                    obtenerApuntes();

                    // Execute callback on UI thread
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    Toast.makeText(context, "Error al crear apunte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Apuntes> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Eliminar apunte

    public void eliminarApunte(int id) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Void> call = api.eliminarApunte(token, id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    obtenerApuntes(); // Refrescar la lista
                } else {
                    Log.e("API_ERROR", "Error al eliminar el apunte: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_FAILURE", "Error: ", t);
            }
        });
    }



    public void actualizarApunte(Apuntes apunte, final Runnable onComplete) {
        String token = ApiClient.leerToken(context);
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Apuntes> call = api.actualizarApunte(token, apunte);

        call.enqueue(new Callback<Apuntes>() {
            @Override
            public void onResponse(@NonNull Call<Apuntes> call, @NonNull Response<Apuntes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerApuntes(); // Refrescar la lista
                    if (onComplete != null) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(onComplete);
                    }
                } else {
                    Toast.makeText(context, "Error al actualizar apunte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Apuntes> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
