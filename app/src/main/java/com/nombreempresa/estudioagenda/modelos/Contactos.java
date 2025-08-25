package com.nombreempresa.estudioagenda.modelos;

import java.io.Serializable;

public class Contactos implements Serializable {
private int idContacto ;

private int Id_Estudiante;

private String nombre;

private String apellido;

private String celular;

private String email;

    public Contactos(String nombre, String apellido, String email, String celular) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.celular = celular;
        
    }

    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public int getId_Estudiante() {
        return Id_Estudiante;
    }

    public void setId_Estudiante(int id_Estudiante) {
        Id_Estudiante = id_Estudiante;
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

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Contactos{" +
                "Id_Contacto=" + idContacto +
                ", Id_Estudiante=" + Id_Estudiante +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", celular='" + celular + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
