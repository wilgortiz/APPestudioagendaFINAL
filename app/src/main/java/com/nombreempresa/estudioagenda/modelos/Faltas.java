package com.nombreempresa.estudioagenda.modelos;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Faltas {
    private int idFalta;

    private int IdEstudiante;

    private Estudiante estudiante;

    private int idMateria;

    private Materia materia;

    private Date FechaFalta;

    private boolean justificada;

    private int cantidad;

    private int FaltasPermitidas;



    // Constructores
    public Faltas() {}

    public Faltas(int idFalta, int idEstudiante, Estudiante estudiante, int idMateria, Materia materia, Date fechaFalta, boolean justificada, int cantidad, int faltasPermitidas) {
        this.idFalta = idFalta;
        IdEstudiante = idEstudiante;
        this.estudiante = estudiante;
        this.idMateria = idMateria;
        this.materia = materia;
        FechaFalta = fechaFalta;
        this.justificada = justificada;
        this.cantidad = cantidad;
        FaltasPermitidas = faltasPermitidas;
    }
// Getters y setters

    public int getIdFalta() {
        return idFalta;
    }

    public void setIdFalta(int idFalta) {
        idFalta = idFalta;
    }

    public int getIdEstudiante() {
        return IdEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        IdEstudiante = idEstudiante;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Date getFechaFalta() {
        return FechaFalta;
    }

    public void setFechaFalta(Date fechaFalta) {
        FechaFalta = fechaFalta;
    }

    public boolean isJustificada() {
        return justificada;
    }

    public void setJustificada(boolean justificada) {
        this.justificada = justificada;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getFaltasPermitidas() {
        return FaltasPermitidas;
    }

    public void setFaltasPermitidas(int faltasPermitidas) {
        FaltasPermitidas = faltasPermitidas;
    }

    @Override
    public String toString() {
        return "Faltas{" +
                "idFalta=" + idFalta +
                ", IdEstudiante=" + IdEstudiante +
                ", estudiante=" + estudiante +
                ", idMateria=" + idMateria +
                ", materia=" + materia +
                ", FechaFalta=" + FechaFalta +
                ", justificada=" + justificada +
                ", cantidad=" + cantidad +
                ", FaltasPermitidas=" + FaltasPermitidas +
                '}';
    }
}
