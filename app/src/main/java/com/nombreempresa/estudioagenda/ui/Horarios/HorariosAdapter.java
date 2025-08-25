package com.nombreempresa.estudioagenda.ui.Horarios;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nombreempresa.estudioagenda.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HorariosAdapter extends RecyclerView.Adapter<HorariosAdapter.HorarioViewHolder> {
    private List<Map<String, Object>> horarios;
    private Context context;
    private HorariosViewModel viewModel;

    public HorariosAdapter(List<Map<String, Object>> horarios, Context context, HorariosViewModel viewModel) {
        this.horarios = horarios != null ? horarios : new ArrayList<>();
        this.context = context;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public HorarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_horario, parent, false);
        return new HorarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorarioViewHolder holder, int position) {
        Map<String, Object> horario = horarios.get(position);

        String nombreMateria = (String) horario.get("nombreMateria");
        String diaSemana = (String) horario.get("diaSemana");
        String horaInicio = (String) horario.get("horaInicio");
        String horaFin = (String) horario.get("horaFin");

        holder.tvMateria.setText(nombreMateria);
        holder.tvDiaHora.setText(String.format("%s, %s - %s", diaSemana, horaInicio, horaFin));
    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    public void actualizarHorarios(List<Map<String, Object>> nuevosHorarios) {
        this.horarios = nuevosHorarios != null ? nuevosHorarios : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class HorarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvMateria;
        TextView tvDiaHora;

        public HorarioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMateria = itemView.findViewById(R.id.tvMateriaHorario);
            tvDiaHora = itemView.findViewById(R.id.tvDiaHora);
        }
    }
}