package com.nombreempresa.estudioagenda.modelos;

import java.util.List;

public class Estudiante {

    //private int idEstudiante;
    private int Id_Estudiante; // Cambiado de int a Integer
    private String nombre;

    private String apellido;

    private String email;

    private String clave;

    // Relación con EstudiantesHorarios
    private List<EstudiantesHorarios> estudiantesHorarios;

    // Constructores
    public Estudiante() {
    }

    public Estudiante(int Id_Estudiante, String nombre, String apellido, String email, String clave) {
        this.Id_Estudiante = Id_Estudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.clave = clave;
    }

    // Getters y setters
    public int getIdEstudiante() {
        return Id_Estudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.Id_Estudiante = idEstudiante;
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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public List<EstudiantesHorarios> getEstudiantesHorarios() {
        return estudiantesHorarios;
    }

    public void setEstudiantesHorarios(List<EstudiantesHorarios> estudiantesHorarios) {
        this.estudiantesHorarios = estudiantesHorarios;
    }
}