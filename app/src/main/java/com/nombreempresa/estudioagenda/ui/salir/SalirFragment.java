package com.nombreempresa.estudioagenda.ui.salir;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nombreempresa.estudioagenda.MainActivity;
import com.nombreempresa.estudioagenda.R;
import com.nombreempresa.estudioagenda.databinding.FragmentSalirBinding;


public class SalirFragment extends Fragment {
    private FragmentSalirBinding binding;
    private SalirViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding= FragmentSalirBinding.inflate(inflater,container,false);
        viewModel= new ViewModelProvider(this).get(SalirViewModel.class);
        View root = binding.getRoot();
        mostrarDialogo(root.getContext());
        return root;
    }


public static void mostrarDialogo(Context context) {
    new AlertDialog.Builder(context)
            .setTitle("Salir")
            .setMessage("¿Esta seguro que desea cerrar sesión?")
            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    SalirViewModel viewModel = new SalirViewModel((Application) context.getApplicationContext());
                    viewModel.salir();
                    ((Activity) context).finishAndRemoveTask();
                }
            })
            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }).show();
}
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}