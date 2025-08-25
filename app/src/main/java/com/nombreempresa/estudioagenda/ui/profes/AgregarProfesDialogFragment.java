package com.nombreempresa.estudioagenda.ui.profes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.databinding.DialogAgregarMateriaBinding;
import com.nombreempresa.estudioagenda.databinding.DialogAgregarProfesBinding;
import com.nombreempresa.estudioagenda.modelos.Materia;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.ui.materias.MateriasViewModel;
public class AgregarProfesDialogFragment extends DialogFragment {
    private static final String ARG_MODO_EDICION = "modoEdicion";
    private static final String ARG_PROFESOR = "profesor";

    private boolean modoEdicion;
    private Profesor profesorExistente;
    private DialogAgregarProfesBinding binding;
    private ProfesViewModel profesViewModel;

    public static AgregarProfesDialogFragment newInstance() {
        return new AgregarProfesDialogFragment();
    }

    public static AgregarProfesDialogFragment newInstanceForEdit(Profesor profesor) {
        AgregarProfesDialogFragment fragment = new AgregarProfesDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_MODO_EDICION, true);
        args.putSerializable(ARG_PROFESOR, profesor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modoEdicion = getArguments().getBoolean(ARG_MODO_EDICION, false);
            profesorExistente = (Profesor) getArguments().getSerializable(ARG_PROFESOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogAgregarProfesBinding.inflate(inflater, container, false);
        profesViewModel = new ViewModelProvider(requireActivity()).get(ProfesViewModel.class);

        // Si estamos en modo edición, cargamos los datos
        if (modoEdicion && profesorExistente != null) {
            binding.etNombre.setText(profesorExistente.getNombre());
            binding.etApellido.setText(profesorExistente.getApellido());
            binding.etEmail.setText(profesorExistente.getEmail());
            binding.etContacto.setText(profesorExistente.getCelular());
            binding.btnAgregarProfe.setText("Actualizar");
        }

        binding.btnAgregarProfe.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString().trim();
            String apellido = binding.etApellido.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String contacto = binding.etContacto.getText().toString().trim();

            // Limpiar errores previos
            binding.etNombre.setError(null);
            binding.etApellido.setError(null);
            binding.etEmail.setError(null);
            binding.etContacto.setError(null);

            boolean valid = true;

            if (nombre.isEmpty()) {
                binding.etNombre.setError("El nombre es obligatorio");
                valid = false;
            }

            if (apellido.isEmpty()) {
                binding.etApellido.setError("El apellido es obligatorio");
                valid = false;
            }

            if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.setError("El email no es válido");
                valid = false;
            }

            if (!contacto.isEmpty() && !contacto.matches("\\d{8,}")) {
                binding.etContacto.setError("Debe tener al menos 8 dígitos numéricos");
                valid = false;
            }

            if (!valid) return;

            if (modoEdicion && profesorExistente != null) {
                profesorExistente.setNombre(nombre);
                profesorExistente.setApellido(apellido);
                profesorExistente.setEmail(email.isEmpty() ? null : email);
                profesorExistente.setCelular(contacto.isEmpty() ? null : contacto);
                profesViewModel.actualizarProfe(profesorExistente, this::dismiss);
            } else {
                Profesor nuevoProfe = new Profesor(
                        nombre,
                        apellido,
                        email.isEmpty() ? null : email,
                        contacto.isEmpty() ? null : contacto
                );
                profesViewModel.agregarProfe(nuevoProfe);
                dismiss();
            }
        });
        return binding.getRoot();
    }
}