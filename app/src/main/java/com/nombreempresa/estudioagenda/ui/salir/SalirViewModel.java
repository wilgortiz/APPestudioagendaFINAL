package com.nombreempresa.estudioagenda.ui.salir;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.nombreempresa.estudioagenda.request.ApiClient;


public class SalirViewModel extends AndroidViewModel {
    private Context context;
    public SalirViewModel(@NonNull Application application) {
        super(application);
        context= application.getApplicationContext();
    }
    public void salir(){
        String tokenAntes = ApiClient.leerToken(context);
        Log.d("LOGOUT_TEST", "🔍 Token antes de logout: " + (tokenAntes.isEmpty() ? "VACÍO" : tokenAntes));
        
        ApiClient.borrarToken(context);
        
        String tokenDespues = ApiClient.leerToken(context);
        if (tokenDespues.isEmpty()) {
            Log.d("LOGOUT_TEST", "✅ Token eliminado correctamente del SharedPreferences");
        } else {
            Log.e("LOGOUT_TEST", "❌ Error al eliminar token - Token aún presente: " + tokenDespues);
        }
        
        // Verificar SharedPreferences directamente
        SharedPreferences sp = context.getSharedPreferences("token.xml", 0);
        String tokenFromSP = sp.getString("token", "");
        Log.d("LOGOUT_TEST", "🔍 Token desde SharedPreferences después de borrar: " + (tokenFromSP.isEmpty() ? "VACÍO" : tokenFromSP));
    }


}
