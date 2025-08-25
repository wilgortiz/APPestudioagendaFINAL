package com.nombreempresa.estudioagenda.ui.registro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.databinding.ActivityRegistroBinding;
import com.nombreempresa.estudioagenda.ui.login.LoginActivity;

public class RegistroActivity extends AppCompatActivity {

    private RegistroViewModel mv;
    private ActivityRegistroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mv = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(RegistroViewModel.class);

        mv.getmError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean error) {
                if (error) {
                    Toast.makeText(RegistroActivity.this, mv.getmMensaje().getValue(), Toast.LENGTH_LONG).show();
                    mv.resetError();
                }
            }
        });

        mv.getmMensaje().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String mensaje) {
                if (mensaje != null) {
                    Toast.makeText(RegistroActivity.this, mensaje, Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mv.registroUsuario(
                        binding.etRegistroNombre.getText().toString(),
                        binding.etRegistroApellido.getText().toString(),
                        binding.etRegistroEmail.getText().toString(),
                        binding.etRegistroPassword.getText().toString()
                );
            }
        });

        binding.btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la actividad de login
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        binding.btLimpiarCampos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etRegistroNombre.setText("");
                binding.etRegistroApellido.setText("");
                binding.etRegistroEmail.setText("");
                binding.etRegistroPassword.setText("");
            }
        });
    }
}