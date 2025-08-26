package com.nombreempresa.estudioagenda.ui.CambioClave;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nombreempresa.estudioagenda.databinding.FragmentCambioClaveBinding;
import com.nombreempresa.estudioagenda.request.ApiClient;
import com.nombreempresa.estudioagenda.ui.login.LoginActivity;



public class CambioClaveFragment extends Fragment {
    private FragmentCambioClaveBinding binding;
    private CambioClaveViewModel mv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCambioClaveBinding.inflate(inflater, container, false);
        mv = new ViewModelProvider(this).get(CambioClaveViewModel.class);


        binding.btCambiarClave.setOnClickListener(v -> {
            String actual = binding.etClaveActual.getText().toString();
            String nueva = binding.etClaveNueva.getText().toString();
            String confirmar = binding.etConfirmarClave.getText().toString();

            if (!nueva.equals(confirmar)) {
                Toast.makeText(getContext(), "La nueva clave y la de confirmación no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            mv.cambiarClave(actual, nueva);
        });

        mv.getMensaje().observe(getViewLifecycleOwner(), mensaje -> {
            if ("OK".equals(mensaje)) {
                // ✅ Borrar token
                ApiClient.borrarToken(requireContext());

                // Mostrar mensaje de éxito
                Toast.makeText(getContext(), "Clave cambiada con éxito ✅", Toast.LENGTH_LONG).show();

                // Retrasar la transición al login
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }, 2000); // 2000 milisegundos = 2 segundos
            } else {
                // Cualquier error o fallo se muestra
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }
}