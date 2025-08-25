package com.nombreempresa.estudioagenda.modelos;

public class RegistroEstudiante {
    private String nombre;
    private String apellido;
    private String email;
    private String clave;

    // Constructores
    public RegistroEstudiante() {}

    public RegistroEstudiante(String nombre, String apellido, String email, String clave) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.clave = clave;
    }

    // Getters y setters
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
}
