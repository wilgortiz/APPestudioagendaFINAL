/*
package com.nombreempresa.estudioagenda.modelos;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Apuntes  {

    private int idApunte;

    private Integer idEstudiante;
    private Estudiante estudiante;

    private Integer idMateria;
    private Materia materia;

    private String titulo;
    private String descripcion;

    private Date fechaCreacion;
    //@SerializedName("fechaCreacion")
    //private Date fechaCreacion;

    // Constructores
    public Apuntes() {}

    public Apuntes(int idApunte, Integer idEstudiante, Integer idMateria, String titulo, String descripcion) {
        this.idApunte = idApunte;
        this.idEstudiante = idEstudiante;
        this.idMateria = idMateria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = new Date();
    }

    // Constructor sin idApunte (para cuando la base de datos lo asigna automáticamente)
    public Apuntes(Integer idEstudiante,  String titulo, String descripcion) {
        this.idEstudiante = idEstudiante;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = new Date();
    }

    // Getters y setters
    public int getIdApunte() {
        return idApunte;
    }

    public void setIdApunte(int idApunte) {
        this.idApunte = idApunte;
    }

    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

 */

package com.nombreempresa.estudioagenda.modelos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Apuntes implements Serializable {
    @SerializedName("idApunte")
    private int idApunte;

    @SerializedName("idEstudiante")
    private Integer idEstudiante;

    @SerializedName("Id_Materia")
    private Integer idMateria;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("fechaCreacion")
    private Date fechaCreacion;


    public Apuntes() {
    }

    // Constructor para crear nuevos apuntes (sin idApunte ni fecha)
    public Apuntes(Integer idEstudiante, String titulo, String descripcion) {
        this.idEstudiante = idEstudiante;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = new Date(); // Fecha actual por defecto
        this.idMateria = null; // Si la materia es opcional
    }

    public Apuntes(int idApunte, Integer idEstudiante, Integer idMateria, String titulo, String descripcion, Date fechaCreacion) {
        this.idApunte = idApunte;
        this.idEstudiante = idEstudiante;
        this.idMateria = idMateria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdApunte() {
        return idApunte;
    }

    public void setIdApunte(int idApunte) {
        this.idApunte = idApunte;
    }

    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Integer getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(Integer idMateria) {
        this.idMateria = idMateria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}

