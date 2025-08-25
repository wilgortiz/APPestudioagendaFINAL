package com.nombreempresa.estudioagenda.ui.registro;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nombreempresa.estudioagenda.modelos.RegistroEstudiante;
import com.nombreempresa.estudioagenda.modelos.RegistroResponse;
import com.nombreempresa.estudioagenda.request.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroViewModel extends AndroidViewModel {
    private MutableLiveData<String> mMensaje;
    private MutableLiveData<Boolean> mError;

    public RegistroViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getmMensaje() {
        if (mMensaje == null) {
            mMensaje = new MutableLiveData<>();
        }
        return mMensaje;
    }

    public LiveData<Boolean> getmError() {
        if (mError == null) {
            mError = new MutableLiveData<>();
        }
        return mError;
    }

    // Validaciones para registro
    public boolean validarCampos(String nombre, String apellido, String email, String password) {
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mMensaje.setValue("Todos los campos son obligatorios");
            return false;
        }

        if (!validarEmail(email)) {
            mMensaje.setValue("El correo electrónico no es válido");
            return false;
        }

        if (!validarPassword(password)) {
            mMensaje.setValue("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número");
            return false;
        }

        return true;
    }

    private boolean validarEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

    private boolean validarPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }

    public void registroUsuario(String nombre, String apellido, String email, String password) {
        if (validarCampos(nombre, apellido, email, password)) {
            Registro(nombre, apellido, email, password);
        } else {
            mError.setValue(true);
        }
    }

    public void Registro(String nombre, String apellido, String email, String password) {
        ApiClient.MisEndpoints api = ApiClient.getEndPoints();

        RegistroEstudiante request = new RegistroEstudiante(nombre, apellido, email, password);

        Call<RegistroResponse> call = api.registro(request);
        call.enqueue(new Callback<RegistroResponse>() {
            @Override
            public void onResponse(Call<RegistroResponse> call, Response<RegistroResponse> response) {
                if (response.isSuccessful()) {
                    RegistroResponse registroResponse = response.body();
                    Log.d("salida", registroResponse.getMensaje());
                    mMensaje.setValue(registroResponse.getMensaje());
                } else {
                    try {
                        String errorMensaje = response.errorBody().string();
                        Log.d("salida", "Error: " + errorMensaje);
                        mMensaje.setValue(errorMensaje);
                    } catch (Exception e) {
                        Log.d("salida", "Error al leer el mensaje de error: " + e.getMessage());
                        mMensaje.setValue("Error inesperado");
                    }
                }
            }

            @Override
            public void onFailure(Call<RegistroResponse> call, Throwable t) {
                Log.d("salida", "Error: " + t.getMessage());
                mMensaje.setValue("Error al intentar registrar");
            }
        });
    }


    public void resetError() {
        mError.setValue(false);
    }
}