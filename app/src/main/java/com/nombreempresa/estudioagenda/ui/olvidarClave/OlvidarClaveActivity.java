package com.nombreempresa.estudioagenda.ui.olvidarClave;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.databinding.ActivityOlvidarClaveBinding;
import com.nombreempresa.estudioagenda.ui.login.LoginViewModel;
public class OlvidarClaveActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private ActivityOlvidarClaveBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOlvidarClaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(LoginViewModel.class);

        // Observamos mensajes desde el ViewModel
        viewModel.getmMensaje().observe(this, mensaje -> {
            if (mensaje != null) {
                // Verificar si el mensaje indica éxito
                if (mensaje.startsWith("EXITO:")) {
                    // Mostrar mensaje de éxito
                    String mensajeExito = mensaje.substring(6); // Remover "EXITO:"
                    Toast.makeText(this, mensajeExito, Toast.LENGTH_LONG).show();

                    // Cerrar esta actividad y regresar a LoginActivity
                    finish();
                } else {
                    // Mostrar otros mensajes normalmente
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                }
            }
        });

        // Botón enviar recuperación
        binding.btEnviarRecuperacion.setOnClickListener(v -> {
            String email = binding.etEmailRecuperar.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.recuperarClave(email);
            }
        });
    }
}