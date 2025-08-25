package com.nombreempresa.estudioagenda.modelos;


import java.util.List;

public class MateriaDto {
    private int idMateria;
    private String nombre;
    private int periodo;
    private List<Integer> profesoresIds;

    // Getters y setters
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

    public List<Integer> getProfesoresIds() {
        return profesoresIds;
    }

    public void setProfesoresIds(List<Integer> profesoresIds) {
        this.profesoresIds = profesoresIds;
    }
}