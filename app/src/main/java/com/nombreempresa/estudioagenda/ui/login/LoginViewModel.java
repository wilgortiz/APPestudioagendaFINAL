package com.nombreempresa.estudioagenda.ui.login;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import com.nombreempresa.estudioagenda.MainActivity;
import com.nombreempresa.estudioagenda.modelos.Estudiante;
import com.nombreempresa.estudioagenda.modelos.LoginResponse;
import com.nombreempresa.estudioagenda.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class LoginViewModel extends AndroidViewModel {
    private Context context;
    private MutableLiveData<String> mMensaje;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
    }

    public LiveData<String> getmMensaje() {
        if (mMensaje == null) {
            mMensaje = new MutableLiveData<>();
        }
        return mMensaje;
    }

    public void Logueo(String email, String password) {

        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        Call<LoginResponse> call = api.login(email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    Log.d("salida", loginResponse.getToken());
                    Toast.makeText(context, "Login exitoso", Toast.LENGTH_LONG).show();

                    // Verificar si la respuesta es un token
                    String token = loginResponse.getToken();
                    if (token != null && !token.isEmpty()) {
                        String fullToken = "Bearer " + token;
                        Log.d("TOKEN_TEST", "Token recibido del servidor: " + token);
                        Log.d("TOKEN_TEST", "Token completo a guardar: " + fullToken);
                        
                        ApiClient.guardarToken(fullToken, context);
                        
                        // Verificar que se guardó correctamente
                        String savedToken = ApiClient.leerToken(context);
                        if (savedToken.equals(fullToken)) {
                            Log.d("TOKEN_TEST", "✅ Token guardado correctamente en SharedPreferences");
                        } else {
                            Log.e("TOKEN_TEST", "❌ Error al guardar token - Esperado: " + fullToken + ", Obtenido: " + savedToken);
                        }

                        // Llamar al endpoint para obtener el ID del estudiante logueado
                        obtenerIdEstudiante(token);

                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        Log.d("salida", "Token vacío");
                        Toast.makeText(context, "Error al intentar autenticar", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("salida", "Error: " + response.code());
                    Toast.makeText(context, "Error al intentar autenticar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("salida", "Error: " + t.getMessage());
                Toast.makeText(context, "Error al intentar autenticar", Toast.LENGTH_SHORT).show();
            }
        });

    }


    // Método para obtener los detalles del estudiante logueado:
    private void obtenerIdEstudiante(String token) {
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        Call<Estudiante> call = api.obtenerEstudiante(token);  // Suponiendo que tienes un endpoint para obtener los detalles del estudiante
        call.enqueue(new Callback<Estudiante>() {
            @Override
            public void onResponse(Call<Estudiante> call, Response<Estudiante> response) {
                if (response.isSuccessful()) {
                    Estudiante estudiante = response.body();
                    if (estudiante != null) {
                        // Guardar el idEstudiante en SharedPreferences o en un ViewModel
                        SharedPreferences prefs = context.getSharedPreferences("usuario", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("idEstudiante", estudiante.getIdEstudiante());  // Guarda el id del estudiante
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<Estudiante> call, Throwable t) {
                Log.d("salida", "Error: " + t.getMessage());
            }
        });

    }
}
