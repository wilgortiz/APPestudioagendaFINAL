package com.nombreempresa.estudioagenda.ui.contactos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.databinding.DialogAgregarContactosBinding;
import com.nombreempresa.estudioagenda.databinding.DialogAgregarProfesBinding;
import com.nombreempresa.estudioagenda.modelos.Contactos;
import com.nombreempresa.estudioagenda.modelos.Profesor;
import com.nombreempresa.estudioagenda.ui.profes.AgregarProfesDialogFragment;
import com.nombreempresa.estudioagenda.ui.profes.ProfesViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//
//public class AgregarContactosDialogFragment extends DialogFragment {
//
//    private static final String ARG_MODO_EDICION = "modoEdicion";
//    private static final String ARG_CONTACTO = "contacto";
//
//    private boolean modoEdicion;
//    private Contactos contactoExistente;
//
//
//    private DialogAgregarContactosBinding binding;
//    private ContactosViewModel contactosViewModel;
//
//
//    public static AgregarContactosDialogFragment newInstance() {
//        return new AgregarContactosDialogFragment();
//    }
//
//    public static AgregarContactosDialogFragment newInstanceForEdit(Contactos contacto) {
//        AgregarContactosDialogFragment fragment = new AgregarContactosDialogFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(ARG_MODO_EDICION, true);
//        args.putSerializable(ARG_CONTACTO, contacto);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            modoEdicion = getArguments().getBoolean(ARG_MODO_EDICION, false);
//            contactoExistente = (Contactos) getArguments().getSerializable(ARG_CONTACTO);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = DialogAgregarContactosBinding.inflate(inflater, container, false);
//        contactosViewModel = new ViewModelProvider(requireActivity()).get(ContactosViewModel.class);
//
//        // Si estamos en modo edición, cargamos los datos
//        if (modoEdicion && contactoExistente != null) {
//            binding.etNombre.setText(contactoExistente.getNombre());
//            binding.etApellido.setText(contactoExistente.getApellido());
//            binding.etEmail.setText(contactoExistente.getEmail());
//            binding.etCelular.setText(contactoExistente.getCelular());
//            binding.btnAgregarContacto.setText("Actualizar");
//        }
//
//        binding.btnAgregarContacto.setOnClickListener(v -> {
//            String nombre = binding.etNombre.getText().toString();
//            String apellido = binding.etApellido.getText().toString();
//            String email = binding.etEmail.getText().toString();
//            String celular = binding.etCelular.getText().toString();
//
//            if (!nombre.isEmpty() && !apellido.isEmpty()) {
//                if (modoEdicion && contactoExistente != null) {
//                    // Modo edición
//                    contactoExistente.setNombre(nombre);
//                    contactoExistente.setApellido(apellido);
//                    contactoExistente.setEmail(email);
//                    contactoExistente.setCelular(celular);
//                    contactosViewModel.actualizarContacto(contactoExistente, this::dismiss);
//                } else {
//                    // Modo creación
//                    Contactos nuevoContacto = new Contactos(
//                            nombre,
//                            apellido,
//                            email.isEmpty() ? null : email,
//                            celular.isEmpty() ? null : celular
//                    );
//                    contactosViewModel.agregarContacto(nuevoContacto);
//                    dismiss();
//                }
//            } else {
//                Toast.makeText(getContext(),
//                        "Nombre y apellido son obligatorios",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//        return binding.getRoot();
//    }
//
//    private boolean esEmailValido(String email) {
//        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
//        return email.matches(regex);
//    }
//
//    private boolean validarCampos(String nombre, String apellido, String celular, String email) {
//        return !nombre.isEmpty() && !apellido.isEmpty() && !celular.isEmpty() && !email.isEmpty()
//                && celular.length() >= 8 && celular.length() <= 15
//                && esEmailValido(email);
//    }
//
//    // Clase base para simplificar los TextWatcher
//    abstract class CampoWatcher implements TextWatcher {
//        public abstract void validar(String s);
//
//        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
//            validar(s.toString());
//        }
//        @Override public void afterTextChanged(Editable s) {}
//    }
//}



public class AgregarContactosDialogFragment extends DialogFragment {

    private static final String ARG_MODO_EDICION = "modoEdicion";
    private static final String ARG_CONTACTO = "contacto";

    private boolean modoEdicion;
    private Contactos contactoExistente;

    private DialogAgregarContactosBinding binding;
    private ContactosViewModel contactosViewModel;

    public static AgregarContactosDialogFragment newInstance() {
        return new AgregarContactosDialogFragment();
    }

    public static AgregarContactosDialogFragment newInstanceForEdit(Contactos contacto) {
        AgregarContactosDialogFragment fragment = new AgregarContactosDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_MODO_EDICION, true);
        args.putSerializable(ARG_CONTACTO, contacto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            modoEdicion = getArguments().getBoolean(ARG_MODO_EDICION, false);
            contactoExistente = (Contactos) getArguments().getSerializable(ARG_CONTACTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogAgregarContactosBinding.inflate(inflater, container, false);
        contactosViewModel = new ViewModelProvider(requireActivity()).get(ContactosViewModel.class);

        // Si estamos en modo edición, cargamos los datos
        if (modoEdicion && contactoExistente != null) {
            binding.etNombre.setText(contactoExistente.getNombre());
            binding.etApellido.setText(contactoExistente.getApellido());
            binding.etCelular.setText(contactoExistente.getCelular());
            binding.etEmail.setText(contactoExistente.getEmail());
            binding.btnAgregarContacto.setText("Actualizar");
        }

        // Validación en tiempo real para email
        binding.etEmail.addTextChangedListener(new CampoWatcher() {
            @Override
            public void validar(String s) {
                if (!s.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    binding.etEmail.setError("El email no es válido");
                } else {
                    binding.etEmail.setError(null);
                }
            }
        });

        // Validación en tiempo real para celular
        binding.etCelular.addTextChangedListener(new CampoWatcher() {
            @Override
            public void validar(String s) {
                if (!s.isEmpty() && !s.matches("\\d{8,15}")) {
                    binding.etCelular.setError("Solo números, entre 8 y 15 dígitos");
                } else {
                    binding.etCelular.setError(null);
                }
            }
        });

        // Acción del botón
        binding.btnAgregarContacto.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString().trim();
            String apellido = binding.etApellido.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String celular = binding.etCelular.getText().toString().trim();

            boolean valido = true;

            if (nombre.isEmpty()) {
                binding.etNombre.setError("El nombre es obligatorio");
                valido = false;
            }

            if (apellido.isEmpty()) {
                binding.etApellido.setError("El apellido es obligatorio");
                valido = false;
            }

            if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.etEmail.setError("El email no es válido");
                valido = false;
            }

            if (!celular.isEmpty() && !celular.matches("\\d{8,15}")) {
                binding.etCelular.setError("Solo números, entre 8 y 15 dígitos");
                valido = false;
            }

            if (!valido) {
                Toast.makeText(getContext(), "Por favor corrige los errores", Toast.LENGTH_SHORT).show();
                return;
            }

            if (modoEdicion && contactoExistente != null) {
                // Modo edición
                contactoExistente.setNombre(nombre);
                contactoExistente.setApellido(apellido);
                contactoExistente.setEmail(email.isEmpty() ? null : email);
                contactoExistente.setCelular(celular.isEmpty() ? null : celular);
                contactosViewModel.actualizarContacto(contactoExistente, this::dismiss);
            } else {
                // Modo creación
                Contactos nuevoContacto = new Contactos(
                        nombre,
                        apellido,
                        email.isEmpty() ? null : email,
                        celular.isEmpty() ? null : celular
                );
                contactosViewModel.agregarContacto(nuevoContacto);
                dismiss();
            }
        });

        return binding.getRoot();
    }

    // Clase base para simplificar los TextWatcher
    abstract class CampoWatcher implements TextWatcher {
        public abstract void validar(String s);

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            validar(s.toString());
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}
