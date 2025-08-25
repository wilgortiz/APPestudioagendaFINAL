package com.nombreempresa.estudioagenda.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Actividades implements Serializable {
    private int idEvento;

    private int idMateria;

    @SerializedName("Id_Estudiante")
    private int idEstudiante;


    private String Tipo_Evento;

    @SerializedName("fecha_Evento")
    private Date fechaEvento;

    @SerializedName("Fecha_Recordatorio")
    private Date fechaRecordatorio;


    private String descripcion;

    @SerializedName("Recordatorio")
    private boolean recordatorio;

    public Actividades() {
    }

    public Actividades(int idEvento, int idMateria, int idEstudiante, String tipo_Evento, Date fechaEvento, Date fechaRecordatorio, String descripcion, boolean recordatorio) {
        this.idEvento = idEvento;
        this.idMateria = idMateria;
        this.idEstudiante = idEstudiante;
        Tipo_Evento = tipo_Evento;
        this.fechaEvento = fechaEvento;
        this.fechaRecordatorio = fechaRecordatorio;
        this.descripcion = descripcion;
        this.recordatorio = recordatorio;
    }

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getTipo_Evento() {
        return Tipo_Evento;
    }

    public void setTipo_Evento(String tipo_Evento) {
        Tipo_Evento = tipo_Evento;
    }

    public Date getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(Date fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Date getFechaRecordatorio() {
        return fechaRecordatorio;
    }

    public void setFechaRecordatorio(Date fechaRecordatorio) {
        this.fechaRecordatorio = fechaRecordatorio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(boolean recordatorio) {
        this.recordatorio = recordatorio;
    }
}