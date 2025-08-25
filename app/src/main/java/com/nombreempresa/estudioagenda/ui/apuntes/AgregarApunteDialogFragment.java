package com.nombreempresa.estudioagenda.ui.apuntes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.modelos.Apuntes;



//modo solo agregar apuntes
public class AgregarApunteDialogFragment extends DialogFragment {
    private static final String ARG_MODO_EDICION = "modoEdicion";
    private static final String ARG_APUNTE = "apunte";

    private boolean modoEdicion;
    private Apuntes apunteExistente;

    public static AgregarApunteDialogFragment newInstance() {
        return new AgregarApunteDialogFragment();
    }

    public static AgregarApunteDialogFragment newInstanceForEdit(Apuntes apunte) {
        AgregarApunteDialogFragment fragment = new AgregarApunteDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_MODO_EDICION, true);
        args.putSerializable(ARG_APUNTE, apunte);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modoEdicion = getArguments().getBoolean(ARG_MODO_EDICION, false);
            apunteExistente = (Apuntes) getArguments().getSerializable(ARG_APUNTE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View vista = inflater.inflate(R.layout.dialog_agregar_apunte, null);

        final EditText etTituloApunte = vista.findViewById(R.id.etTituloApunte);
        final EditText etApunte = vista.findViewById(R.id.etApunte);

        // Si estamos en modo edición, cargamos los datos existentes
        if (modoEdicion && apunteExistente != null) {
            etTituloApunte.setText(apunteExistente.getTitulo());
            etApunte.setText(apunteExistente.getDescripcion());
            builder.setTitle("Editar Apunte");
        } else {
            builder.setTitle("Agregar Apunte");
        }

        builder.setView(vista)
                .setPositiveButton("Guardar", (dialog, id) -> {
                    String textoApunte = etApunte.getText().toString();
                    String tituloApunte = etTituloApunte.getText().toString();

                    final ApuntesViewModel apuntesViewModel = new ViewModelProvider(requireActivity()).get(ApuntesViewModel.class);

                    if (modoEdicion && apunteExistente != null) {
                        // Modo edición
                        apunteExistente.setTitulo(tituloApunte);
                        apunteExistente.setDescripcion(textoApunte);
                        apuntesViewModel.actualizarApunte(apunteExistente, () -> {
                            apuntesViewModel.obtenerApuntes();
                            Log.d("APUNTES", "Apunte actualizado correctamente");
                        });
                    } else {
                        // Modo creación
                        Apuntes nuevoApunte = new Apuntes();
                        nuevoApunte.setTitulo(tituloApunte);
                        nuevoApunte.setDescripcion(textoApunte);
                        apuntesViewModel.crearApunte(nuevoApunte, () -> {
                            apuntesViewModel.obtenerApuntes();
                            Log.d("APUNTES", "Apunte guardado correctamente");
                        });
                    }
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}