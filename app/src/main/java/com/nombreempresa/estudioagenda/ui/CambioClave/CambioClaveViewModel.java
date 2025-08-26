package com.nombreempresa.estudioagenda.ui.CambioClave;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nombreempresa.estudioagenda.modelos.CambioClave;
import com.nombreempresa.estudioagenda.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CambioClaveViewModel extends AndroidViewModel {
    private MutableLiveData<String> mensaje = new MutableLiveData<>();

    public CambioClaveViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getMensaje() {
        return mensaje;
    }


    public void cambiarClave(String actual, String nueva) {
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();
        String token = ApiClient.leerToken(getApplication());

        CambioClave dto = new CambioClave(actual, nueva);

        Call<Void> call = api.cambiarClave(token, dto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mensaje.setValue("OK"); // 🔑 usamos un código simple
                } else {
                    mensaje.setValue("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mensaje.setValue("Fallo en la conexión: " + t.getMessage());
            }
        });
    }

    public String validarClave(String clave) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (!clave.matches(passwordRegex)) {
            return "La contraseña debe tener al menos 8 caracteres, una mayúscula y un número";
        }
        return null;
    }
}
