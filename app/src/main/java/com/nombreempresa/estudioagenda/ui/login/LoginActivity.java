package com.nombreempresa.estudioagenda.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.databinding.ActivityLoginBinding;
import com.nombreempresa.estudioagenda.ui.registro.RegistroActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mv;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mv = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(LoginViewModel.class);

        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv.Logueo(
                        binding.etLoginEmail.getText().toString(),
                        binding.etLoginPassword.getText().toString()
                );
            }
        });

        // Asignar listener al botón de registro
        binding.btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la actividad de registro
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }
}