package com.nombreempresa.estudioagenda.modelos;

import java.io.Serializable;
import java.util.List;

public class Profesor implements Serializable  {
    private int idProfesor;
    private String nombre;
    private String apellido;
    private String email;
    private String celular;

    // Constructor
    public Profesor(String nombre, String apellido, String email, String celular) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.celular = celular;
    }

    //private List<ProfesorMateria> profesorMateria;

    // Constructores
    public Profesor() {}


    // Getters y setters
    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }


    @Override
    public String toString() {
        return

                nombre + ' ' + apellido

              ;
    }
}
