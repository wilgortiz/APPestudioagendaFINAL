package com.nombreempresa.estudioagenda.ui.Horarios;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.nombreempresa.estudioagenda.R;

import java.util.Map;
public class EditarHorarioDialogFragment extends DialogFragment {
    private HorariosViewModel viewModel;
    private int idHorario;
    private String nombreMateria;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private int idMateria;

    public static EditarHorarioDialogFragment newInstance(Map<String, Object> horario) {
        EditarHorarioDialogFragment fragment = new EditarHorarioDialogFragment();
        Bundle args = new Bundle();
        args.putInt("idHorario", (int) horario.get("idHorario"));
        args.putString("nombreMateria", (String) horario.get("nombreMateria"));
        args.putInt("idMateria", (int) horario.get("idMateria"));
        args.putString("diaSemana", (String) horario.get("diaSemana"));
        args.putString("horaInicio", (String) horario.get("horaInicio"));
        args.putString("horaFin", (String) horario.get("horaFin"));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idHorario = getArguments().getInt("idHorario");
            nombreMateria = getArguments().getString("nombreMateria");
            idMateria = getArguments().getInt("idMateria");
            diaSemana = getArguments().getString("diaSemana");
            horaInicio = getArguments().getString("horaInicio");
            horaFin = getArguments().getString("horaFin");
        }
        viewModel = new ViewModelProvider(requireActivity()).get(HorariosViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_editar_horario, null);

        TextView tvMateria = view.findViewById(R.id.tvMateria);
        TextView tvDiaHora = view.findViewById(R.id.tvDiaHora);

        tvMateria.setText(nombreMateria);
        tvDiaHora.setText(String.format("%s, %s - %s", diaSemana, horaInicio, horaFin));

        builder.setView(view)
                .setTitle("Opciones de horario")
                .setPositiveButton("Editar", (dialog, id) -> mostrarDialogoEdicion())
                .setNegativeButton("Eliminar", (dialog, id) -> mostrarDialogoConfirmacion())

//                .setNegativeButton("Eliminar", (dialog, id) -> {
//                    viewModel.eliminarHorario(idHorario);
//                    Toast.makeText(getActivity(), "Horario eliminado", Toast.LENGTH_SHORT).show();
//                })
                .setNeutralButton("Cancelar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void mostrarDialogoEdicion() {
        AgregarHorariosDialogFragment dialog = new AgregarHorariosDialogFragment();

        Bundle args = new Bundle();
        args.putInt("idHorario", idHorario);
        args.putString("nombreMateria", nombreMateria);
        args.putInt("idMateria", idMateria);
        args.putString("diaSemana", diaSemana);
        args.putString("horaInicio", horaInicio);
        args.putString("horaFin", horaFin);
        dialog.setArguments(args);

        dialog.show(getParentFragmentManager(), "EditarHorarioDialog");
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este horario?")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    viewModel.eliminarHorario(idHorario);
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Horario eliminado", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

}