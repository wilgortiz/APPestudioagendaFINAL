package com.nombreempresa.estudioagenda.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Materia implements Serializable {

    private int idMateria;

    private int idEstudiante;

    private String nombre;

    private int periodo;

    private List<ProfesorMateria> profesorMateria;
    private List<Profesor> profesores; // Nuevo campo para profesores


    // Constructor por defecto
    public Materia() {}

    // Constructor con todos los campos
    public Materia(int idEstudiante, int idMateria, String nombre, int periodo) {
        this.idEstudiante = idEstudiante;
        this.idMateria = idMateria;
        this.nombre = nombre;
        this.periodo = periodo;
    }

    // Constructor sin idMateria
    public Materia(String nombre, int periodo) {
        this.nombre = nombre;
        this.periodo = periodo;
    }

    // Getters y setters
    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public List<ProfesorMateria> getProfesorMateria() {
        return profesorMateria;
    }

    public void setProfesorMateria(List<ProfesorMateria> profesorMateria) {
        this.profesorMateria = profesorMateria;
    }


    public List<Profesor> getProfesores() {
        return profesores;
    }

    public void setProfesores(List<Profesor> profesores) {
        this.profesores = profesores;
    }

    @Override
    public String toString() {
        return "Materia{" +
                "idMateria=" + idMateria +
                ", idEstudiante=" + idEstudiante +
                ", nombre='" + nombre + '\'' +
                ", periodo=" + periodo +
                ", profesorMateria=" + profesorMateria +
                '}';
    }

    // Sobreescribir equals y hashCode para asegurar que la agrupación funcione correctamente
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Materia materia = (Materia) o;
        return idMateria == materia.idMateria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMateria);
    }


}
